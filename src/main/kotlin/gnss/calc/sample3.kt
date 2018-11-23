package gnss.calc

import aero.geosystems.formats.IGnssDataConsumer
import aero.geosystems.formats.rtcm3.Rtcm3Decoder
import aero.geosystems.formats.rtcm3.Rtcm3Message
import aero.geosystems.formats.rtcm3.messages.Rtcm1019
import aero.geosystems.formats.utils.asByteBuffer
import aero.geosystems.formats.utils.copyToArray
import aero.geosystems.formats.utils.flipDuplicate
import aero.geosystems.gnss.GnssUtils
import gnss.convert.toEph
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.pow

/*
 * Created by aimozg on 21.11.2018.
 * Confidential unless published on GitHub
 */


fun main(args: Array<String>) {
	if (args.size != 7) {
		println("Args:\n" +
				"\tCASTER_HOST CASTER_PORT MOUNTPOINT LOGIN:PASSWORD REF_X REF_Y REF_Z" +
				"\nExpects RTCM 3 msg 1019" +
				"\nOutputs GPS satellite positions at current GPS time, and distance (w/o account for Earth rotation) to reference point")
		return
	}
	Locale.setDefault(Locale.ENGLISH)
	val sch = SocketChannel.open(InetSocketAddress(args[0], args[1].toInt()))
	val request = ("GET /" + args[2] + " HTTP/1.0\r\n" +
			"User-Agent: NTRIP cmdline\r\n" +
			"Authorization: Basic " + Base64.getEncoder().encodeToString(args[3].toByteArray()) + "\r\n" +
			"\r\n").toByteArray().asByteBuffer()
	val refxyz = doubleArrayOf(args[4].toDouble(), args[5].toDouble(), args[6].toDouble())
	while (sch.isConnected && request.hasRemaining()) {
		val x = sch.write(request)
		if (x == -1) error("EOF")
	}
	val bb = ByteBuffer.allocate(1024)
	val decoder = Rtcm3Decoder(GnssUtils.gpstime(), object : IGnssDataConsumer<Rtcm3Message> {
		override fun consume(message: Rtcm3Message?, buffer: ByteBuffer, timestamp: Long?, type: Int) {
			when (message) {
				null -> println(String(buffer.copyToArray()))
				is Rtcm1019 -> {
					val date = LocalDateTime.now(ZoneOffset.UTC)
							.truncatedTo(ChronoUnit.SECONDS)
							.plusSeconds(GnssUtils.getLeapSeconds().toLong())
					val time = GnssTime(date)
					val eph = message.toEph(time.gpsWeek())
					println(message)
					println(eph)
					println("Time $time = ${time.gpsWeek()} ${time.gpsSecOfWeek()} = ${time.toDate()}")
					println("Time - Toc = ${time - eph.toc}, time - Toe = ${time - eph.toe}")
					val pos = eph.positionAt(time).point3D
					val range = (
							(pos.x - refxyz[0]).pow(2) +
									(pos.y - refxyz[1]).pow(2) +
									(pos.z - refxyz[2]).pow(2)
							).pow(0.5)

					println("%2d  %13.3f %13.3f %13.3f  %12.3f".format(eph.sat, pos.x, pos.y, pos.z, range))
				}
				// else -> println("Read ${message.message_id}")
			}
		}
	})
	val t0 = System.currentTimeMillis()
	while (sch.isConnected && System.currentTimeMillis() - t0 < 20_000L) {
		bb.position(0)
		val x = sch.read(bb)
		if (x == -1) error("EOF")
		if (x > 0) decoder.consume(bb.flipDuplicate())
	}
	sch.close()
}