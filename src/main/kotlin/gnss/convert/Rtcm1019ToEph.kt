package gnss.convert

import aero.geosystems.formats.rtcm3.messages.Rtcm1019
import gnss.calc.GnssTime
import gnss.calc.adjweek
import gnss.calc.eph_t
import gnss.calc.gps2time
import kotlin.math.PI
import kotlin.math.pow

/*
 * Created by aimozg on 21.11.2018.
 * Confidential unless published on GitHub
 */

fun Rtcm1019.toEph(ref_week:Int): eph_t {
	val weekdiff = ref_week - week_number
	val rollovers = weekdiff/1024 +
			(if (weekdiff%1024 > 600) 1 else 0)
	val week_full = week_number + rollovers*1024
	return eph_t(GnssTime.fromGpsWeek(week_full, toc.toDouble()), sat_id).also { eph ->
		eph.f0 = af0
		eph.f1 = af1
		eph.f2 = af2
		eph.iode = iode
		eph.crs = c_rs
		eph.deln = delta_n * PI
		eph.M0 = m0 * PI
		eph.cuc = c_uc
		eph.e = eccentricity_e
		eph.cus = c_us
		eph.A = a12.pow(2.0)
		eph.toe = adjweek(gps2time(week_full,toe.toDouble()),eph.toc)
		eph.cic = c_ic
		eph.OMG0 = omega0 * PI
		eph.cis = c_is
		eph.i0 = i0 * PI
		eph.crc = c_rc
		eph.omg =omega * PI
		eph.OMGd = omegadot * PI
		eph.idot = idot * PI
		eph.code = code_on_l2
		eph.week = week_full
		eph.flag = if (l2_p_data_flag) 1 else 0
		eph.sva = sv_accuracy
		eph.svh = sv_health
		eph.tgd[0] = tgd
		eph.iodc = iodc
		eph.ttr = eph.toc
		eph.fit =  if (fit_interval) 4.0 else 1.0
	}
}