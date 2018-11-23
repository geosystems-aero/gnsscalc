package gnss.calc

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.math.pow

class nav_t {
	val eph = ArrayList<eph_t>()         /* GPS/QZS/GAL ephemeris */
	//    public final ArrayList<geph_t> geph;       /* GLONASS ephemeris */
	//    public seph_t seph;       /* SBAS ephemeris */
	//    public peph_t peph;       /* precise ephemeris */
	//    public pclk_t pclk;       /* precise clock */
	//    public alm_t alm;         /* almanac data */
	//    public tec_t tec;         /* tec grid data */
	//    public stec_t stec;       /* stec grid data */
	//    public erp_t  erp;         /* earth rotation parameters */
	val utc_gps = DoubleArray(4)  /* GPS delta-UTC parameters {A0,A1,T,W} */
	val utc_glo = DoubleArray(4)  /* GLONASS UTC GPS time parameters */
	val utc_gal = DoubleArray(4)  /* Galileo UTC GPS time parameters */
	val utc_qzs = DoubleArray(4)  /* QZS UTC GPS time parameters */
	val utc_cmp = DoubleArray(4)  /* BeiDou UTC parameters */
	val utc_sbs = DoubleArray(4)  /* SBAS UTC parameters */
	val ion_gps = DoubleArray(8)  /* GPS iono model parameters {a0,a1,a2,a3,b0,b1,b2,b3} */
	val ion_gal = DoubleArray(4)  /* Galileo iono model parameters {ai0,ai1,ai2,0} */
	val ion_qzs = DoubleArray(8)  /* QZSS iono model parameters {a0,a1,a2,a3,b0,b1,b2,b3} */
	val ion_cmp = DoubleArray(8)  /* BeiDou iono model parameters {a0,a1,a2,a3,b0,b1,b2,b3} */
	var leaps: Int = 0          /* leap seconds (s) */
	//   public final double lam[MAXSAT][NFREQ]; /* carrier wave lengths (m) */
	//   public final double cbias[MAXSAT][3];   /* code bias (0:p1-p2,1:p1-c1,2:p2-c2) (m) */
	//   public final double wlbias[MAXSAT];     /* wide-lane bias (cycle) */
	//   public final double[] glo_cpbias = new double[4];    /* glonass code-phase bias {1C,1P,2C,2P} (m) */
	//   public char glo_fcn; /* glonass frequency channel number + 8 */
	//   public final pcv_t pcvs[MAXSAT]; /* satellite antenna pcv */
	//   public sbssat_t sbssat;    /* SBAS satellite corrections */
	//   public final sbsion_t sbsion[MAXBAND+1]; /* SBAS ionosphere corrections */
	//   public final dgps_t dgps[MAXSAT]; /* DGPS corrections */
	//   public final ssr_t ssr[MAXSAT];  /* SSR corrections */
	//   public final lexeph_t lexeph[MAXSAT]; /* LEX ephemeris */
	//   public lexion_t lexion;    /* LEX ionosphere correction */
	internal fun add_eph(eph: eph_t): Boolean {
		return this.eph.add(eph)
	}
	//    boolean add_geph(geph_t geph){
	//        return this.geph.add(geph);
	//    }
	fun findBestEph(sat: Int, time: GnssTime): eph_t? {
		return eph.minBy {
			if (it.sat != sat) Double.MAX_VALUE else
			Math.abs(it.toc - time)
		}?.takeIf { it.sat == sat }
	}
	fun findBestPastEph(sat: Int, time: GnssTime): eph_t? {
		return eph.asSequence().filter {
			it.sat == sat
		}.filter {
			it.toc < time
		}.minBy {
			if (it.sat != sat || it.toc > time) Double.MAX_VALUE else
			Math.abs(it.toc - time)
		}?.takeIf { it.sat == sat }
	}
}

class eph_t(var toc: GnssTime, var sat: Int) {
    val sys = satsys(sat).sys

	constructor(versionMajor: Int, sat: Int, toc: GnssTime, data: ArrayList<Double>):this(toc,sat) {
		f0 = data[0]
		f1 = data[1]
		f2 = data[2]

		A = Math.pow(data[10], 2.0)
		e = data[8]
		i0 = data[15]
		OMG0 = data[13]
		omg = data[17]
		M0 = data[6]
		deln = data[5]
		OMGd = data[18]
		idot = data[19]

		crc = data[16]
		crs = data[4]
		cuc = data[7]
		cus = data[9]
		cic = data[12]
		cis = data[14]

		when (sys) {
			SYS_GPS, SYS_QZS -> {
				tgd[0] = data[25]
			}
			SYS_GAL, SYS_CMP -> {
				tgd[0] = data[25]
				tgd[1] = data[26]
			}
		}

		Adot = 0.0
		ndot = 0.0

		iode = data[3].toInt()
		iodc = when (sys) {
			SYS_GPS, SYS_QZS -> data[26].toInt()
			SYS_CMP -> data[28].toInt()
			else -> 0
		}
		val toes = data[11]
		week = data[21].toInt()
		toe = when (sys) {
			SYS_GPS, SYS_QZS, SYS_GAL -> adjweek(gps2time(week, toes), toc)
			else -> gps2time(week, toes) //fixme bdt2gpst
		}
		val ttrs = data[27]
		ttr = gps2time(week,ttrs)
		sva = 0
		svh = when (sys) {
			SYS_GPS, SYS_QZS, SYS_GAL -> data[24].toInt()
			else -> 0
		}
		code = when (sys) {
			SYS_GPS, SYS_QZS, SYS_GAL -> data[20].toInt()
			else -> 0
		}
		flag = when (sys) {
			SYS_GPS, SYS_QZS -> data[22].toInt()
			else -> 0
		}

		fit = when (sys) {
			SYS_GPS, SYS_QZS -> data[28]
			else -> 0.0
		}
	}

    var f0 = 0.0
    var f1 = 0.0
    var f2 = 0.0

    var A = 0.0
    var e = 0.0
    var i0 = 0.0
    var OMG0 = 0.0
    var omg = 0.0
    var M0 = 0.0
    var deln = 0.0
    var OMGd = 0.0
    var idot = 0.0

    var crc = 0.0
    var crs = 0.0
    var cuc = 0.0
    var cus = 0.0
    var cic = 0.0
    var cis = 0.0

    var tgd:Array<Double?> = arrayOfNulls(4)

    var Adot = 0.0
    var ndot = 0.0

    var iode = 0
    var iodc = 0
    var week = 0
    var toe: GnssTime = toc
	val toes get() = toe.gpsSecOfWeek()
    var ttr = toc
    var sva = 0
    var svh = 0
    var code = 0
    var flag = 0

    var fit = 0.0
	override fun toString(): String {
		return "eph_t(sat=$sat, sys=$sys, toc=$toc, toe=$toe, ttr=$ttr, week=$week, iode=$iode, iodc=$iodc, f0=$f0, f1=$f1, f2=$f2, A=$A, e=$e, i0=$i0, OMG0=$OMG0, omg=$omg, M0=$M0, deln=$deln, OMGd=$OMGd, idot=$idot, crc=$crc, crs=$crs, cuc=$cuc, cus=$cus, cic=$cic, cis=$cis, tgd=${Arrays.toString(tgd)}, Adot=$Adot, ndot=$ndot, sva=$sva, svh=$svh, code=$code, flag=$flag, fit=$fit)"
	}


}

fun eph_t.toRinexLikeStrings():String {
	val to = StringBuffer()
	val toc_date = LocalDateTime.ofEpochSecond(toc.time,toc.sec.times(1e9).toInt(), ZoneOffset.UTC)
	val toe_date = LocalDateTime.ofEpochSecond(toe.time,toe.sec.times(1e9).toInt(), ZoneOffset.UTC)
	to.appendln("%2d %2s %2s %2s %2s %2s%5.1f%19.12e%19.12e%19.12e".format(Locale.ENGLISH,
			sat,
			toc_date.year%100,toc_date.monthValue,toc_date.dayOfMonth,toc_date.hour,toc_date.minute,toc_date.second+toc.sec,
			f0,
			f1,
			f2
	) + "    // PRN / EPOCH / SV CLK")
	to.appendln("   %19.12e%19.12e%19.12e%19.12e".format(Locale.ENGLISH,
			iode.toDouble(),
			crs,
			deln,
			M0
	) + "    //  BROADCAST ORBIT - 1")
	to.appendln("   %19.12e%19.12e%19.12e%19.12e".format(Locale.ENGLISH,
			cuc,
			e,
			cus,
			A.pow(0.5)
	) + "    //  BROADCAST ORBIT - 2")
	to.appendln("   %19.12e%19.12e%19.12e%19.12e".format(Locale.ENGLISH,
			toe.gpsSecOfWeek(),
			cic,
			OMG0,
			cis
	) + "    //  BROADCAST ORBIT - 3")
	to.appendln("   %19.12e%19.12e%19.12e%19.12e".format(Locale.ENGLISH,
			i0,
			crc,
			omg,
			OMGd
	) + "    //  BROADCAST ORBIT - 4")
	to.appendln("   %19.12e%19.12e%19.12e%19.12e".format(Locale.ENGLISH,
			idot,
			code.toDouble(),
			week.toDouble(),
			flag.toDouble()
	) + "    //  BROADCAST ORBIT - 5")
	to.appendln("   %19.12e%19.12e%19.12e%19.12e".format(Locale.ENGLISH,
			sva.toDouble(),
			svh.toDouble(),
			tgd[0],
			iodc.toDouble()
	) + "    //  BROADCAST ORBIT - 6")
	to.appendln("   %19.12e%19.12e%19s%19s".format(Locale.ENGLISH,
			ttr.gpsSecOfWeek(),
			fit,
			"",
			""
	) + "    //  BROADCAST ORBIT - 7")
	return to.toString()
}

fun timediff(t1:GnssTime,t0: GnssTime):Double  = t1 - t0
fun timeadd(t1:GnssTime, sec:Double):GnssTime = t1 + sec

internal fun adjweek(t: GnssTime, refTime: GnssTime): GnssTime {
	val tt = timediff(t, refTime)
	if (tt < -302400.0) return timeadd(t, 604800.0)
	if (tt > 302400.0) return timeadd(t, - 604800.0)
	return t
}

class geph_t {
	var sat: Int = 0            /* satellite number */
	var iode: Int = 0           /* IODE (0-6 bit of tb field) */
	var frq: Int = 0            /* satellite frequency number */
	var svh: Int = 0
	var sva: Int = 0
	var age: Int = 0    /* satellite health, accuracy, age of operation */
	var toe: GnssTime = GnssTime(0,0.0)        /* epoch of epherides (gpst) */
	var tof: GnssTime = GnssTime(0,0.0)        /* message frame time (gpst) */
	val pos = DoubleArray(3)      /* satellite position (ecef) (m) */
	val vel = DoubleArray(3)      /* satellite velocity (ecef) (m/s) */
	val acc = DoubleArray(3)      /* satellite acceleration (ecef) (m/s^2) */
	var taun: Double = 0.0
	var gamn: Double = 0.0   /* SV clock bias (s)/relative freq bias */
	var dtaun: Double = 0.0       /* delay between L1 and L2 (s) */
}