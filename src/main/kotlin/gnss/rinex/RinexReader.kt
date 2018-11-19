package gnss.rinex

import gnss.calc.nav_t

/*
 * Created by aimozg on 19.11.2018.
 * Confidential unless published on GitHub
 */
class RinexReader(input: Sequence<String>,
                  maxHeaderLines: Int = AbstractRinexReader.DEFAULT_MAXPOSHEAD) :
		AbstractRinexReader(input, maxHeaderLines) {
	fun readNavFile():nav_t {
		readrnxh()
		when (type) {
			RinexType.GPS_NAV,
			RinexType.GLO_NAV,
			RinexType.GAL_NAV,
			RinexType.GEOSTATIONARY_GPSNAV,
			RinexType.QZSS_NAV -> readrnxbody()
			RinexType.SBAS -> TODO()
			RinexType.UNKNOWN,
			RinexType.METEO,
			RinexType.CLOCK,
			RinexType.SUMMARY,
			RinexType.OBS -> error("Not a nav RINEX")
		}
		return nav
	}
}