package gnss.calc

import aero.geosystems.gnss.GnssConstants
import gnss.rinex.RinexReader
import java.io.File
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

/*
 * Created by aimozg on 15.11.2018.
 * Confidential unless published on GitHub
 */

private data class Observation(
		val satId:Int,
		val satSystem:Int,
		val psrL1:Double,
		val phaseL1:Double,
		val psrL2:Double?,
		val phaseL2:Double?
)
private val GPS_GAMMA = (GnssConstants.GPS_L1_FREQUENCY.toDouble()/ GnssConstants.GPS_L2_FREQUENCY.toDouble()).pow(2)
private fun Observation.adjP1(tgd:Double,ionofree: Boolean):Double {
	if (ionofree && psrL2 != null) {
		/* iono-free combination */
		return (GPS_GAMMA * psrL1 - psrL1) / (GPS_GAMMA - 1.0)
	} else {
		val P1_P2 = (1.0 - GPS_GAMMA) * aero.geosystems.gnss.GnssConstants.C * tgd
		return psrL1 - P1_P2 / (1.0 - GPS_GAMMA)
	}
}
private data class ObsEpoch(
		val time:GnssTime,
		val observations:List<Observation>
)

// See "Novatel Binary Protocol, RANGECMP"

private fun parseRangecmp(rangecmp:String):ObsEpoch {
	val (sheader, sbody) = rangecmp.split(';')
	// #RANGECMP,2,96,0,37.0,180,1986,479644.000,00100000,9691,9603
	val header = sheader.split(',')
	val time = GnssTime.fromGpsWeek(header[6].toInt(),header[7].toDouble())
	/*
	 38,
	 30,20229393.930,0.050,-106306226.378906,0.006,-533.672,51.0,12756.500,08109c04,
	 30,20229393.805,0.050,-82836042.914063,0.006,-415.852,47.0,12750.219,01309c0b,
	 7,21007073.852,0.050,-110392964.074219,0.006,-2026.422,50.0,16423.375,18109c24,
	 7,21007071.508,0.050,-86020483.261719,0.006,-1579.035,42.0,16050.594,11309c2b
	 drop first (obs count)
	 group by index/9
	 convert to SubObs
	 then group by satellite
	 */
	data class SubObs(
			val satId: Int,
			val psr:Double,
			val phase:Double,
			val system:Int,
			val signal:Int)
	val body =
			sbody.split(',').drop(1).asSequence()
					.withIndex().groupBy({it.index/9},{it.value}).values
					.map {
						// [30,20229393.930,0.050,-106306226.378906,0.006,-533.672,51.0,12756.500,08109c04]
						val chtrst = it[8].toLong(16)
						/*
						0 = GPS
						1= GLONASS
						2 = SBAS
						3 = Galileo
						4 = BeiDou
						5 = QZSS
						6 = Reserved
						7 = Other
						 */
						val system = 0x000f_0000L.and(chtrst).ushr(16).toInt()
						val signal = 0x03e0_0000L.and(chtrst).ushr(21).toInt()
						SubObs(
								it[0].toInt(),
								it[1].toDouble(),
								-it[3].toDouble(),
								system,
								signal
						)
					}
					.groupBy { it.satId }
	val obs = body.values.mapNotNull { group ->
		val l1 = group.find { it.signal == 0 }
		if (l1 != null && l1.system == 0) {
			val l2 = when (l1.system) {
				0 -> group.find { it.signal in listOf(5,9,17) }
				else -> null
			}
			Observation(l1.satId,l1.system,l1.psr,l1.phase,l2?.psr,l2?.phase)
		} else null
	}//.sortedBy { it.satId }
	return ObsEpoch(time,obs)
}


fun main(args: Array<String>) {
	Locale.setDefault(Locale.ENGLISH)
	val nav = RinexReader.readNavFile(File("sample/brdc0400.18n"))
	val rcvxyz = doubleArrayOf(
			1056163.8308, 3518134.1561, 5196804.4640 // novdgps040.txt
	)
	File("sample/novdgps040.txt").forEachLine { line ->
		if (line.startsWith("#RANGECMP")) {
			print("A\t")
			val epoch = parseRangecmp(line)
			print(epoch.time.toDate().toGMTString())
			for (o in epoch.observations) {
				print("\t")
				print(o.satId)
				val eph = nav.findBestPastEph(o.satId,epoch.time)
				print("\t")
				if (eph != null) {
					val psr = o.psrL1//o.adjP1(eph.tgd[0]?:0.0, true)
					val prc = genprc(epoch.time, psr, rcvxyz, eph)
					print("%.3f".format(prc,prc))
				} else {
					print("N/A")
				}
			}
			println()
		} else if (line.startsWith("#RTCMDATA1A")) {
			val (sheader,sbody) = line.split(';')
			val header = sheader.split(',')
			val time = GnssTime.fromGpsWeek(header[5].toInt(),header[6].toDouble())
			print("B\t")
			print(time.toDate().toGMTString())
			val parts = sbody.split(',','*').dropLast(1)
			val mzc = parts[2].toInt()*0.6
			val dtz = mzc-mzc.roundToInt()
			val corrparts = parts.drop(7).chunked(6)
			val corrs = corrparts.map { corr ->
				val scale = if (corr[0].toInt() == 0) 0.02 else 0.32
				val udre = corr[1].toInt()
				val sat = corr[2].toInt()
				val pc0 = corr[3].toInt() * scale
				val rrc = corr[4].toInt() * 0.1 * scale
				val iodc = corr[5]
				val pc = pc0 + rrc*dtz
				sat to pc
			}//.sortedBy { it.first }
			for ((sat,pc) in corrs) {
				print("\t%d\t%.3f".format(sat, pc))
			}
			println()
		}
	}

}