package gnss.rinex

import gnss.calc.*
import gnss.utils.nextOrNull
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by aimozg on 19.11.2018.
 * Confidential unless published on GitHub
 */
abstract class AbstractRinexReader(
		private val input: Sequence<String>,
		private val maxHeaderLines: Int = DEFAULT_MAXPOSHEAD
		) {
	/*
	 * [see RINEX 2.11, "4. THE EXCHANGE OF RINEX FILES"]
	 * O: Observation file
	 * N: GPS Navigation file
	 * M: Meteorological data file
	 * G: GLONASS Navigation file
	 * L: Future Galileo Navigation file
	 * H: Geostationary GPS payload nav mess file
	 * B: Geo SBAS broadcast data file
	 * C: Clock file (separate documentation)
	 * S: Summary file (used e.g., by IGS, not a standard!)
	 */
	enum class RinexType(val charCode:Char) {
		UNKNOWN(' '),
		OBS('O'),
		GPS_NAV('N'),
		METEO('M'),
		GLO_NAV('G'),
		GAL_NAV('L'),
		GEOSTATIONARY_GPSNAV('H'),
		SBAS('B'),
		CLOCK('C'),
		SUMMARY('S'),
		QZSS_NAV('J');
		// TODO other types
		companion object {
			fun byCharCode(cc:Char) = values().firstOrNull { it.charCode == cc } ?: UNKNOWN
		}
	}

	var version:BigDecimal = BigDecimal("2.10")
		protected set
	var type:RinexType = RinexType.UNKNOWN
		protected set
	var sys:Int = SYS_GPS
		protected set
	var tsys:Int = TSYS_GPS
		protected set
	val nav:nav_t = nav_t()

	protected val inputLines = input.iterator()
	protected fun hasNext() = inputLines.hasNext()
	protected fun nextLine() = inputLines.nextOrNull()

	/**
	 * Read rinex header
	 */
	protected open fun readrnxh() {
		var i = 0
		var block = false
		loop@ while (++i < maxHeaderLines) {
			val line = nextLine() ?: return
			if (line.length <= 60) {
				continue
			}
			val typeCode = line[20]
			val headerName:String = line.substring(60).trim()
			val headerValue:String = line.substring(0,60)
			when {
				headerName.contains("RINEX VERSION / TYPE") -> {
					version = line.substring(0, 9).trim().toBigDecimal()
					type = RinexType.byCharCode(typeCode)
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
					continue@loop
				}
				headerName.contains("PGM / RUN BY / DATE") -> continue@loop
				headerName.contains("COMMENT") -> {
					if (line.contains("WIDELANE SATELLITE FRACTIONAL BIASES") ||
							line.contains("WIDELANE SATELLITE FRACTIONNAL BIASES")) {
						block = true
					} else if (block) {
						//todo
					}
					continue@loop
				}
				// if (i >= maxHeaderLines && type == RinexType.UNKNOWN) error("Iteration limit exceeded")
			}

			when (type) {
				RinexType.OBS -> decode_obsh(headerName,headerValue)
				RinexType.GPS_NAV -> decode_navh(headerName,headerValue)
				RinexType.GLO_NAV -> decode_gnavh(headerName,headerValue)
				RinexType.GEOSTATIONARY_GPSNAV -> decode_hnavh(headerName,headerValue)
				RinexType.QZSS_NAV -> decode_navh(headerName,headerValue)
				RinexType.GAL_NAV -> decode_navh(headerName,headerValue)
				RinexType.UNKNOWN -> error("Unknown RINEX file type $typeCode")
				else -> error("Not supported RINEX file type $type")
			}
			if (line.substring(60).contains("END OF HEADER")) {
				return
			}
			// if (i >= maxHeaderLines && type == RinexType.UNKNOWN) error("Iteration limit exceeded")
		}
		error("Iteration limit exceeded")
	}

	protected open fun decode_obsh(name:String,value:String) {
		TODO("not implemented")
	}

	protected open fun decode_hnavh(name:String,value:String) {
		TODO("not implemented")
	}

	protected open fun decode_gnavh(name:String,value:String) {
		TODO("not implemented")
	}

	protected open fun decode_navh(name:String,value:String) {
		when (name) {
			"ION ALPHA",
			"ION BETA" -> {
				val values = value.substring(2..50).replace('d', 'E', true)
				val shift = if (name == "ION_ALPHA") 0 else 4
				for (i in 0..3) nav.ion_gps[i + shift] = values.substring(i * 12, (i + 1) * 12).toDouble()
			}
			"DELTA-UTC: A0,A1,T,W" -> {
				val values = value.substring(3, 60).replace('d', 'E', true)
				nav.utc_gps[0] = values.substring(0, 19).toDouble()
				nav.utc_gps[1] = values.substring(19, 38).toDouble()
				nav.utc_gps[2] = values.substring(38, 47).toDouble()
				nav.utc_gps[3] = values.substring(47, 56).toDouble()
			}
			"IONOSPHERIC CORR" -> {
				println("IONOSPHERIC CORR") // TODO
			}
			"TIME SYSTEM CORR" -> {
				println("TIME SYSTEM CORR") // TODO
			}
			"LEAP SECONDS" -> {
				nav.leaps = value.substring(0, 6).trim().toInt()
			}
		}
	}

	protected open fun readrnxbody() {
		when (type) {
			RinexType.UNKNOWN -> error("Cannot read unknown body")
			RinexType.OBS -> TODO()
			RinexType.GPS_NAV -> readrnxnav()
			RinexType.METEO -> TODO()
			RinexType.GLO_NAV -> TODO()
			RinexType.GAL_NAV -> TODO()
			RinexType.GEOSTATIONARY_GPSNAV -> TODO()
			RinexType.SBAS -> TODO()
			RinexType.CLOCK -> TODO()
			RinexType.SUMMARY -> TODO()
			RinexType.QZSS_NAV -> TODO()
		}
	}

	private fun readrnxnav() {
		var batchSize = 8
		if (sys == SYS_GLO || sys == SYS_SBS) batchSize = 4
		while (hasNext()) {
			val lines = (1..batchSize).mapNotNull { nextLine() }
			if (lines.size == batchSize) {
				readrnxnavb(lines)
			}
		}
	}

	private fun readrnxnavb(lines: List<String>) {
		nav.add_eph(Companion.readrnxnavb(version, sys, lines))
	}

	companion object {
		const val DEFAULT_MAXPOSHEAD = 1024

		fun readrnxnavb(version: BigDecimal, sys: Int, lines: List<String>): eph_t {
			if (lines.size != 8) error("Expected 8 rnxnav lines, got ${lines.size}")

			val spacing = if (version.toInt() >= 3) 4 else 3

			val sat: Int
			if (version.toInt() >= 3 || sys == SYS_GAL || sys == SYS_QZS) {
				TODO()
				// sat = satIDtoNo(lines[0].substring(0,3))
			} else {
				val prn = lines[0].substring(0, 2).trim().toInt()
				sat = when {
					sys == SYS_SBS -> satno(SYS_SBS, prn + 100)
					sys == SYS_GLO -> satno(SYS_GLO, prn)
					prn in 93..97 -> satno(SYS_QZS, prn + 100)
					else -> satno(SYS_GPS, prn)
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
					if (end > lines[i].length) {
						data.add(0.0)
					} else {
						val sub = lines[i].substring(start, end).replace('d', 'E', true).trim()
						if (sub.isEmpty()) data.add(0.0)
						else data.add(sub.toDouble())
					}
				}
			}
			return eph_t(version.toInt(), sat, toc, data)
		}
	}
}