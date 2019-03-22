package gnss.calc

/*
 * Created by aimozg on 14.11.2018.
 * Confidential unless published on GitHub
 */

private fun var_uraeph(ura: Int): Double {
	val ura_value = arrayOf(
			2.4, 3.4, 4.85, 6.85, 9.65, 13.65, 24.0, 48.0, 96.0, 192.0, 384.0, 768.0, 1536.0,
			3072.0, 6144.0
	)
	if ((ura < 0 || 15 < ura)) {
		return Math.pow(6144.0, 2.0)
	}
	return ura_value[ura]
}

fun eph_t.positionAt(time: GnssTime): Pos {
    if (A <= 0.0) {
        return Pos(Point3D.ZERO, 0.0, 0.0)
    }
    var tk = timediff(time, toe)
    val satinfo = satsys(sat)
    val sys = satinfo.sys
    val prn = satinfo.prn
    val mu = when (sys) {
        SYS_GAL -> MU_GAL
        SYS_CMP -> MU_CMP
        else -> MU_GPS
    }
    val omge = when (sys) {
        SYS_GAL -> OMGE_GAL
        SYS_CMP -> OMGE_CMP
        else -> OMGE
    }
    val M = M0 + (Math.sqrt(mu / (A * A * A)) + deln) * tk
    var n = 0
    var E = M
    var Ek = 0.0
    while (Math.abs(E - Ek) > RTOL_KEPLER && n < MAX_ITER_KEPLER) {
        Ek = E;
        E -= (E - e * Math.sin(E) - M) / (1.0 - e * Math.cos(E))
        n++
    }
    if (n >= MAX_ITER_KEPLER) {
        throw Exception("kepler iteration overflow sat=$sat")
    }
    val sinE = Math.sin(E)
    val cosE = Math.cos(E)

    var u = Math.atan2(Math.sqrt(1.0 - e * e) * sinE, cosE - e) + omg
    var r = A * (1.0 - e * cosE)
    var i = i0 + idot * tk
    val sin2u = Math.sin(2.0 * u)
    val cos2u = Math.cos(2.0 * u)

    u += cus * sin2u + cuc * cos2u
    r += crs * sin2u + crc * cos2u
    i += cis * sin2u + cic * cos2u

    val x = r * Math.cos(u)
    val y = r * Math.sin(u)

    val cosi = Math.cos(i)

    var point3D: Point3D
    if (sys == SYS_CMP && prn <= 5) {
        val O = OMG0 + OMGd * tk - omge * toes
        val sinO = Math.sin(O)
        val cosO = Math.cos(O)
        val xg = x * cosO - y * cosi * sinO
        val yg = x * sinO + y * cosi * cosO
        val zg = y * Math.sin(i)
        val sino = Math.sin(omge * tk)
        val coso = Math.cos(omge * tk)
        point3D = Point3D(
		        xg * coso + yg * sino * COS_5 + zg * sino * SIN_5,
		        -xg * sino + yg * coso * COS_5 + zg * coso * SIN_5,
		        -yg * SIN_5 + zg * COS_5
        )
    } else {
        val O = OMG0 + (OMGd - omge) * tk - omge * toes
        val sinO = Math.sin(O)
        val cosO = Math.cos(O)
        point3D = Point3D(
		        x * cosO - y * cosi * sinO,
		        x * sinO + y * cosi * cosO,
		        y * Math.sin(i)
        )
    }
    tk = timediff(time, toc)
    var dts = f0 + f1 * tk + f2 * tk * tk
    dts -= 2.0 * Math.sqrt(mu * A) * e * sinE / Math.pow(CLIGHT, 2.0)
    val variance = var_uraeph(sva)
    return Pos(point3D, dts, variance)
}