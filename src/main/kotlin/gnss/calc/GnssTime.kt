package gnss.calc

import aero.geosystems.gnss.GnssUtils
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/*
 * Created by aimozg on 15.11.2018.
 * Confidential unless published on GitHub
 */
class GnssTime(
		/** time (s) expressed by standard time_t */
		time: Long,
		/** fraction of second under 1 s */
		sec: Double): Comparable<GnssTime> {
	constructor(date: Date) : this(date.time / 1000, (date.time % 1000) / 1000.0)
	constructor(ldt: LocalDateTime): this(ldt.toEpochSecond(ZoneOffset.UTC),ldt.nano*1e-9)
	constructor(ldt: ZonedDateTime): this(ldt.toEpochSecond(),ldt.nano*1e-9)
	constructor(ldt: OffsetDateTime): this(ldt.toEpochSecond(),ldt.nano*1e-9)

	/** time (s) expressed by standard time_t */
	val time:Long
	/** fraction of second under 1 s */
	val sec:Double
	init {
		if (sec >= 0.0 && sec < 1.0) {
			this.time = time
			this.sec = sec
		} else {
			val i = Math.floor(sec)
			this.time = time + i.toLong()
			this.sec = sec - i
			/* case A:
			   sec = -2.4
			   i = -3.0
			   time = time - 3
			   sec = -2.4 - (-3.0) = 0.6

			   case B:
			   sec = 2.6
			   i = 2.0
			   time = time + 2
			   sec = 2.6 - 2.0 = 0.6
			 */
		}
	}

	fun toDate():Date {
		return Date(time*1000L + sec.toLong())
	}

	operator fun minus(other: GnssTime):Double {
		return (this.time - other.time).toDouble() + (this.sec - other.sec)
	}
	operator fun minus(sec:Double):GnssTime {
		return GnssTime(this.time,this.sec-sec)
	}
	operator fun plus(sec:Double):GnssTime {
		return GnssTime(this.time,this.sec+sec)
	}

	override operator fun compareTo(other: GnssTime) = when {
		this.time < other.time -> -1
		this.time > other.time -> +1
		this.sec < other.sec -> -1
		this.sec > other.sec -> +1
		else -> 0
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as GnssTime

		if (time != other.time) return false
		if (sec != other.sec) return false

		return true
	}

	override fun hashCode(): Int {
		var result = time.hashCode()
		result = 31 * result + sec.hashCode()
		return result
	}

	override fun toString(): String {
		return "GnssTime{${gpsWeek()} ${gpsIntSecOfWeek()}${sec.toString().drop(1)}}"
	}

	companion object {
		fun fromGpsWeek(week:Int,sec:Double):GnssTime {
			val gpsec = week.toLong()*7*24*60*60
			return GnssTime(
					gpsec + GnssUtils.GPS_UNIX_DIFF_S,
					sec
			)
		}

		fun fromGpsEpochMs(gpstime: Long): GnssTime {
			val week = GnssUtils.extractGpsWeek(gpstime).toInt()
			val ms = GnssUtils.extractMs(gpstime)
			return fromGpsWeek(week, ms / 1000.0)
		}
	}
}
fun GnssTime.gpsWeek() = ((time-GnssUtils.GPS_UNIX_DIFF_S)/GnssUtils.SEC_IN_WEEK).toInt()
fun GnssTime.gpsIntSecOfWeek() = ((time - GnssUtils.GPS_UNIX_DIFF_S) % GnssUtils.SEC_IN_WEEK).toInt()
fun GnssTime.gpsSecOfWeek() = ((time-GnssUtils.GPS_UNIX_DIFF_S)%GnssUtils.SEC_IN_WEEK) + sec
fun GnssTime.gpsEpochMs() = (time * 1000 + sec.times(1000).toLong() - GnssUtils.GPS_UNIX_DIFF)
