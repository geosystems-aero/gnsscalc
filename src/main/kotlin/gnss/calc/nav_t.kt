package gnss.calc

import java.util.*

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
}

class eph_t(versionMajor: Int, var sat: Int, var toc: GnssTime, data: ArrayList<Double>) {
    private val sys = satsys(sat).sys

    var f0 = data[0]
    var f1 = data[1]
    var f2 = data[2]

    var A = Math.pow(data[10], 2.0)
    var e = data[8]
    var i0 = data[15]
    var OMG0 = data[13]
    var omg = data[17]
    var M0 = data[6]
    var deln = data[5]
    var OMGd = data[18]
    var idot = data[19]

    var crc = data[16]
    var crs = data[4]
    var cuc = data[7]
    var cus = data[9]
    var cic = data[12]
    var cis = data[14]

    var tgd = when (sys) {
        SYS_GPS, SYS_QZS -> {
            arrayOf(data[25], null, null, null)
        }
        SYS_GAL, SYS_CMP -> {
            arrayOf(data[25], data[26], null, null)
        }
        else -> arrayOfNulls(4)
    }

    var Adot = 0.0
    var ndot = 0.0

    var iode = data[3].toInt()
    var iodc = when (sys) {
        SYS_GPS, SYS_QZS -> data[26].toInt()
        SYS_CMP -> data[28].toInt()
        else -> 0
    }
    var toes = data[11]
    var week = data[21].toInt()
    var toe: GnssTime = when (sys) {
        SYS_GPS, SYS_QZS, SYS_GAL -> adjweek(gps2time(week, toes), toc)
        else -> gps2time(week, toes) //fixme bdt2gpst
    }
    lateinit var ttr: GnssTime
    var sva = 0
    var svh = when (sys) {
        SYS_GPS, SYS_QZS, SYS_GAL -> data[24].toInt()
        else -> 0
    }
    var code = when (sys) {
        SYS_GPS, SYS_QZS, SYS_GAL -> data[20].toInt()
        else -> 0
    }
    var flag = when (sys) {
        SYS_GPS, SYS_QZS -> data[22].toInt()
        else -> 0
    }

    var fit = when (sys) {
        SYS_GPS, SYS_QZS -> data[28]
        else -> 0.0
    }

}

fun timediff(t1:GnssTime,t0: GnssTime):Double  = t1 - t0
fun timeadd(t1:GnssTime, sec:Double):GnssTime = t1 + sec

internal fun adjweek(t: GnssTime, t0: GnssTime): GnssTime {
	val tt = timediff(t, t0)
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