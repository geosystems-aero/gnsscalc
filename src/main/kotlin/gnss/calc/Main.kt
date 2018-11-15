package gnss.calc

import javafx.geometry.Point3D
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val MAXPOSHEAD = 1024

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

var ver = 2.10
var type = ' '
var sys = SYS_GPS
var tsys = TSYS_GPS
var nav = nav_t()

fun main(args: Array<String>) {
    val byteStream: ByteArray
    try {
        byteStream = File(args[0]).readBytes()
    } catch (e: Exception) {
        return
    }
    val inStream = BufferedReader(InputStreamReader(byteStream.inputStream()))
    readrnxh(inStream)
    when (type) {
        'O' -> {
        }
        'N' -> {
            readrnxnav(inStream)
        }
        'G' -> {
        }
        'H' -> {
        }
        'J' -> {
        }
        'L' -> {
        }
        'C' -> {
        }
    }
    val satID = 32
    val time = gps2time(1922, 292229.000)
    val sat = select_sat(satID, time)
    val pos = sat.positionAt(time)
    println("$satID:${sat.toc.toDate().toGMTString()}:${time.toDate().toGMTString()}: ${pos.point3D.x}, ${pos.point3D.y}, ${pos.point3D.z}")
}

fun select_sat(sat:Int, time: GnssTime): eph_t {
    val list = nav.eph.filter { it.sat == sat }
    if(list.isEmpty()){
        throw Throwable("Satellite $sat not found.")
    }
    val sorted = list.sortedBy { Math.abs(it.toc - time) }
    return sorted[0]
}

/**
 * Read rinex header
 */
fun readrnxh(inStream: BufferedReader): Boolean {
    var i = 0
    var block = false
    while (true) {
        val line = inStream.readLine()
        if (line.length <= 60) {
            continue
        }
        if (line.substring(60).contains("RINEX VERSION / TYPE")) {
            ver = line.substring(0, 9).toDouble()
            type = line[20]
            sys = when (line[40]) {
                ' ' -> SYS_GPS
                'G' -> SYS_GPS
                'R' -> SYS_GLO
                'E' -> SYS_GAL
                'S' -> SYS_SBS
                'J' -> SYS_QZS
                'C' -> SYS_CMP
                'M' -> SYS_NONE
                else -> throw error("Not supported satellite system ${line[40]}")
            }
            tsys = when (line[40]) {
                ' ' -> TSYS_GPS
                'G' -> TSYS_GPS
                'R' -> TSYS_UTC
                'E' -> TSYS_GAL
                'S' -> TSYS_GPS
                'J' -> TSYS_QZS
                'C' -> TSYS_CMP
                'M' -> TSYS_GPS
                else -> throw error("Not supported satellite system ${line[40]}")
            }
            continue
        } else if (line.substring(60).contains("PGM / RUN BY / DATE")) continue
        else if (line.substring(60).contains("COMMENT")) {
            if (line.contains("WIDELANE SATELLITE FRACTIONAL BIASES") ||
                    line.contains("WIDELANE SATELLITE FRACTIONNAL BIASES")) {
                block = true
            } else if (block) {
                //todo
            }
            continue
        }

        when (type) {
            'O' -> decode_obsh()
            'N' -> decode_navh(line)
            'G' -> decode_gnavh()
            'H' -> decode_hnavh()
            'J' -> decode_navh(line)
            'L' -> decode_navh(line)
        }
        if (line.substring(60).contains("END OF HEADER")) {
            return true
        }
        if (++i >= MAXPOSHEAD && type == ' ') return false
    }
}

fun decode_hnavh() {
    TODO("not implemented")
}

fun decode_gnavh() {
    TODO("not implemented")
}

fun decode_navh(line: String) {
    fun ion_gps(shift: Int = 0) {
        val values = line.substring(2..50).replace('d', 'E', true)
        for (i in 0..3) nav.ion_gps[i + shift] = values.substring(i * 12, (i + 1) * 12).toDouble()
    }
    when (line.substring(60).trim()) {
        "ION ALPHA" -> {
            ion_gps(0)
        }
        "ION BETA" -> {
            ion_gps(4)
        }
        "DELTA-UTC: A0,A1,T,W" -> {
            val values = line.substring(3, 60).replace('d', 'E', true)
            nav.utc_gps[0] = values.substring(0, 19).toDouble()
            nav.utc_gps[1] = values.substring(19, 38).toDouble()
            nav.utc_gps[2] = values.substring(38, 47).toDouble()
            nav.utc_gps[3] = values.substring(47, 56).toDouble()
        }
        "IONOSPHERIC CORR" -> {
            println("IONOSPHERIC CORR")
        }
        "TIME SYSTEM CORR" -> {
            println("TIME SYSTEM CORR")
        }
        "LEAP SECONDS" -> {
            nav.leaps = line.substring(0, 6).trim().toInt()
        }
    }
}

fun readrnxnav(inStream: BufferedReader) {
    var spacing = 3
    var max = 8
    if (ver >= 3.0) {
        spacing = 4
    }
    if (sys == SYS_GLO || sys == SYS_SBS) max = 4
    while (true) {
        try {
            val lines = Array<String>(max) { _ -> inStream.readLine() }
            readrnxnavb(lines, spacing)
        } catch (e: Exception) {
            return
        }
    }
}

fun readrnxnavb(lines: Array<String>, spacing: Int) {
    var sat = 0
    if (ver >= 3.0 || sys == SYS_GAL || sys == SYS_QZS) {
        //todo
//        sat = satIDtoNo(lines[0].substring(0,3))
    } else {
        val prn = lines[0].substring(0, 2).trim().toInt()
        if (sys == SYS_SBS) {
            sat = satno(SYS_SBS, prn + 100)
        } else if (sys == SYS_GLO) {
            sat = satno(SYS_GLO, prn)
        } else if (prn in 93..97) {
            sat = satno(SYS_QZS, prn + 100)
        } else {
            sat = satno(SYS_GPS, prn)
        }
    }
    val df = SimpleDateFormat("yy MM dd HH mm ssss")
    df.timeZone = TimeZone.getTimeZone("GMT")
    val toc = GnssTime(df.parse(lines[0].substring(spacing, spacing + 19)))
    val data = ArrayList<Double>()
    for (j in 1..3) {
        val start = spacing + (j * 19)
        val end = start + 19
        data.add(lines[0].substring(start, end).replace('d', 'E', true).toDouble())
    }
    for (i in 1 until lines.size) {
        for (j in 0..3) {
            val start = spacing + (j * 19)
            val end = start + 19
            data.add(lines[i].substring(start, end).replace('d', 'E', true).toDouble())
        }
    }
    if (lines.size >= 8) {
        nav.add_eph(eph_t(ver, sat, toc, data))
    }
}

fun decode_obsh() {
    TODO("not implemented")
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