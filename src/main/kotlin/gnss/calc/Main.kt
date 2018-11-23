package gnss.calc

import gnss.rinex.RinexReader
import javafx.geometry.Point3D
import java.io.File
import java.util.*

const val SYS_NONE = 0x00
const val SYS_GPS = 0x01
const val SYS_SBS = 0x02
const val SYS_GLO = 0x04
const val SYS_GAL = 0x08
const val SYS_QZS = 0x10
const val SYS_CMP = 0x20
const val SYS_IRN = 0x40
const val SYS_LEO = 0x80
const val SYS_ALL = 0xFF

const val TSYS_GPS = 0
const val TSYS_UTC = 1
const val TSYS_GLO = 2
const val TSYS_GAL = 3
const val TSYS_QZS = 4
const val TSYS_CMP = 5

fun main(args: Array<String>) {
    if (args.size != 4) {
        println("Arguments:\n\tRINEX_NAV_FILE SAT_ID GPS_WEEK GPS_SEC")
        return
    }
    Locale.setDefault(Locale.ENGLISH)
    val nav = RinexReader.readNavFile(File(args[0]))
    val satID = args[1].toInt()
    val time = gps2time(args[2].toInt(), args[3].toDouble())
    println("For ${args.joinToString(" ")} (time = $time = ${time.toDate().toGMTString()})")
    val sat = nav.findBestEph(satID, time) ?: error("Ephemeris not found")
    println("Using ephemerides:")
    println(sat.toRinexLikeStrings())
    val pos = sat.positionAt(time)
    println("Result:")
    println("%12.3f %12.3f %12.3f".format(pos.point3D.x, pos.point3D.y, pos.point3D.z))
}


const val CLIGHT = 299792458.0       /* speed of light (m/s) */
const val RE_GLO = 6378136.0        /* radius of earth (m)            ref [2] */
const val MU_GPS = 3.9860050E14     /* gravitational constant         ref [1] */
const val MU_GLO = 3.9860044E14     /* gravitational constant         ref [2] */
const val MU_GAL = 3.986004418E14   /* earth gravitational constant   ref [7] */
const val MU_CMP = 3.986004418E14   /* earth gravitational constant   ref [9] */
const val J2_GLO = 1.0826257E-3     /* 2nd zonal harmonic of geopot   ref [2] */

const val OMGE = 7.2921151467E-5  /* earth angular velocity (IS-GPS) (rad/s) */
const val OMGE_GLO = 7.292115E-5      /* earth angular velocity (rad/s) ref [2] */
const val OMGE_GAL = 7.2921151467E-5  /* earth angular velocity (rad/s) ref [7] */
const val OMGE_CMP = 7.292115E-5      /* earth angular velocity (rad/s) ref [9] */

const val SIN_5 = -0.0871557427476582 /* sin(-5.0 deg) */
const val COS_5 = 0.9961946980917456 /* cos(-5.0 deg) */

const val ERREPH_GLO = 5.0            /* error of glonass ephemeris (m) */
const val TSTEP = 60.0             /* integration step glonass ephemeris (s) */
const val RTOL_KEPLER = 1E-14         /* relative tolerance for Kepler equation */

const val DEFURASSR = 0.15            /* default accurary of ssr corr (m) */
const val MAXECORSSR = 10.0           /* max orbit correction of ssr (m) */
const val MAXCCORSSR = (1E-6 * CLIGHT)  /* max clock correction of ssr (m) */
const val MAXAGESSR = 90.0            /* max age of ssr orbit and clock (s) */
const val MAXAGESSR_HRCLK = 10.0      /* max age of ssr high-rate clock (s) */
const val STD_BRDCCLK = 30.0          /* error of broadcast clock (m) */

const val MAX_ITER_KEPLER = 30        /* max number of iteration of Kelpler */

fun gps2time(week: Int, sec: Double): GnssTime {
    return GnssTime.fromGpsWeek(week,sec)
}

class Pos(val point3D: Point3D, val dts: Double, val variance: Double)