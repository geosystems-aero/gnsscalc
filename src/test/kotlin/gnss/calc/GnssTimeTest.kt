package gnss.calc

import junit.framework.TestCase

/*
 * Created by aimozg on 20.03.2019.
 * Confidential unless published on GitHub
 */
class GnssTimeTest : TestCase() {
	fun testComparison() {
		val a = GnssTime(1518170400, 0.0)
		val b = GnssTime(1518177600, 0.0)
		assertFalse(a < a)
		assertFalse(a > a)
		assertTrue(a == GnssTime(a.time, a.sec))

		assertTrue(a < b)
		assertFalse(a > b)
		assertFalse(a == b)
	}

	fun testDate() {
		val a = GnssTime(1518170400, 0.0)
		assertEquals(1518170400000L, a.toDate().time)
		assertEquals(a, GnssTime(a.toDate()))
		assertEquals(a.toDate(), GnssTime(a.toDate()).toDate())
	}
}