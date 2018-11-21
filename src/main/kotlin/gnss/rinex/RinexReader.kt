package gnss.rinex

import gnss.calc.nav_t
import java.io.File
import java.io.InputStream
import java.io.Reader

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
	companion object {
		fun readNavFile(input:Sequence<String>):nav_t = RinexReader(input).readNavFile()
		fun readNavFile(input:Iterable<String>):nav_t = RinexReader(input.asSequence()).readNavFile()
		fun readNavFile(input: Reader):nav_t = RinexReader(input.buffered().lineSequence()).readNavFile()
		fun readNavFile(input: InputStream):nav_t = RinexReader(input.bufferedReader().lineSequence()).readNavFile()
		fun readNavFile(input: File):nav_t = RinexReader(input.bufferedReader().lineSequence()).readNavFile()
	}
}