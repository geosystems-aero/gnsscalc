package gnss.calc

const val MINPRNGPS = 1                   /* min satellite PRN number of GPS */
const val MAXPRNGPS = 32                  /* max satellite PRN number of GPS */
const val NSATGPS = (MAXPRNGPS - MINPRNGPS + 1) /* number of GPS satellites */
const val NSYSGPS = 1
const val MINPRNGLO = 0
const val MAXPRNGLO = 0
const val NSATGLO = 0
const val NSYSGLO = 0
const val MINPRNGAL = 0
const val MAXPRNGAL = 0
const val NSATGAL = 0
const val NSYSGAL = 0
const val MINPRNQZS = 0
const val MAXPRNQZS = 0
const val MINPRNQZS_S = 0
const val MAXPRNQZS_S = 0
const val NSATQZS = 0
const val NSYSQZS = 0
const val MINPRNCMP = 0
const val MAXPRNCMP = 0
const val NSATCMP = 0
const val NSYSCMP = 0
const val MINPRNLEO = 0
const val MAXPRNLEO = 0
const val NSATLEO = 0
const val NSYSLEO = 0
const val MINPRNSBS = 120                 /* min satellite PRN number of SBAS */
const val MAXPRNSBS = 142                 /* max satellite PRN number of SBAS */
const val NSATSBS = (MAXPRNSBS - MINPRNSBS + 1) /* number of SBAS satellites */
const val MAXSAT = (NSATGPS + NSATGLO + NSATGAL + NSATQZS + NSATCMP + NSATSBS + NSATLEO)

fun satno(sys: Int, prn: Int): Int {
    when (sys) {
        SYS_GPS -> {
            if (prn in MINPRNGPS..MAXPRNGPS) {
                return prn - MINPRNGPS + 1
            }
        }
        SYS_GLO -> {
            if (prn in MINPRNGLO..MAXPRNGLO) {
                return NSATGPS + prn - MINPRNGLO + 1
            }
        }
        SYS_GAL -> {
            if (prn in MINPRNGAL..MAXPRNGAL) {
                return NSATGPS + NSATGLO + prn - MINPRNGAL + 1
            }
        }
        SYS_QZS -> {
            if (prn in MINPRNQZS..MAXPRNQZS) {
                return NSATGPS + NSATGLO + NSATGAL + prn - MINPRNQZS + 1
            }
        }
        SYS_CMP -> {
            if (prn in MINPRNCMP..MAXPRNCMP) {
                return NSATGPS + NSATGLO + NSATGAL + NSATQZS + prn - MINPRNCMP + 1
            }
        }
        SYS_LEO -> {
            if (prn in MINPRNLEO..MAXPRNLEO) {
                return NSATGPS + NSATGLO + NSATGAL + NSATQZS + NSATCMP + prn - MINPRNLEO + 1
            }
        }
        SYS_SBS -> {
            if (prn in MINPRNSBS..MAXPRNSBS) {
                return NSATGPS + NSATGLO + NSATGAL + NSATQZS + NSATCMP + NSATLEO + prn - MINPRNSBS + 1
            }
        }
    }
    return 0
}

fun satsys(sat: Int): satinfo {
    var sys = SYS_NONE
    var msat = sat
    var loc = false
    if (sat <= 0 || MAXSAT < sat) msat = 0
    val systems = arrayOf(
            arrayOf(NSATGPS, SYS_GPS, MINPRNGPS),
            arrayOf(NSATGLO, SYS_GLO, MINPRNGLO),
            arrayOf(NSATGAL, SYS_GAL, MINPRNGAL),
            arrayOf(NSATQZS, SYS_QZS, MINPRNQZS),
            arrayOf(NSATCMP, SYS_CMP, MINPRNCMP),
            arrayOf(NSATLEO, SYS_LEO, MINPRNLEO),
            arrayOf(NSATSBS, SYS_SBS, MINPRNSBS)
    )
    for (i in systems) {
        if (msat <= i[0]) {
            sys = i[1]
            msat += i[2] - 1
            break
        } else {
            msat -= i[0]
        }
    }
    if (sys == SYS_NONE) msat = 0
    return satinfo(sys, msat)
}

data class satinfo(val sys: Int, val prn: Int)