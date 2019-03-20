package gnss.calc

import gnss.rinex.AbstractRinexReader
import junit.framework.TestCase
import java.math.BigDecimal

/*
 * Created by aimozg on 20.03.2019.
 * Confidential unless published on GitHub
 */
class GenPrcTest : TestCase() {
	fun testReferencePrc() {
		//A	9 Feb 2018 12:00:00 GMT	7	4.311	5	-7.709	27	0.536	30	2.995	8	4.303	28	2.502	20	-11.043	9	-2.074	13	-0.445	11	-3.616
		/*
		 * Reference pseudorange correction.
		 * Date: 9 Feb 2018
		 * Receiver location (XYZ): 1056163.8308 3518134.1561 5196804.4640
		 *
		 * Reference ephemeris:
		 * IODC: 8
		 * ToC: 10:00:00.0 GMT
		 * IODE=IODC, ToE=ToC
		 *
		 * Reference observation:
		 * Observation time: 12:00:00 GMT
		 * Sat PRN: 7
		 * L1 Pseudorange: 20304751.797000 m
		 * L2 Pseudorange: 20304745.836000 m
		 * L1 PhaseRange: 106702234.656250 cy
		 * L2 PhaseRange:  83144558.117188 cy
		 *
		 * Reference NovAtel-generated RTCM2.3 MT1 pseudorange correction = 4.020 (m)
		 * Reference pseudorange correction we should generate = 4.311 (m)
		 */
		val rcv = doubleArrayOf(1056163.8308, 3518134.1561, 5196804.4640)
		val epoch = GnssTime.fromGpsWeek(1987, 475200.0)
		val psr = 20304751.797000

		val rinexData = """ 7 18 02 09 10 00  0.0 2.393638715148D-04-5.570655048359D-12 0.000000000000D+00
    8.000000000000D+00 6.796875000000D+01 4.285178434316D-09-2.718520354085D+00
    3.345310688019D-06 1.115239202045D-02 8.910894393921D-06 5.153695716858D+03
    4.680000000000D+05-5.587935447693D-09 2.409917318592D+00-1.061707735062D-07
    9.595521429126D-01 2.076250000000D+02-2.544397853641D+00-7.758537812208D-09
   -3.571577408823D-11 1.000000000000D+00 1.987000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.117587089539D-08 8.000000000000D+00
    4.607400000000D+05 0.000000000000D+00"""
		val eph = AbstractRinexReader.readrnxnavb(BigDecimal("2.10"), SYS_GPS, rinexData.split('\n'))
		assertEquals(7, eph.sat)
		assertEquals(8, eph.iodc)
		assertEquals(8, eph.iode)
		assertEquals(GnssTime.fromGpsWeek(1987, 468000.0), eph.toc)
		assertEquals(eph.toc, eph.toe)

		val prc = genprc(epoch, psr, rcv, eph)
		assertEquals(4.311, prc, 0.001)
	}
}