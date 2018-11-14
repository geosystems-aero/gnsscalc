package gnss.calc;

import java.util.ArrayList;

public class nav_t {
    ArrayList<eph_t> eph = new ArrayList<>();         /* GPS/QZS/GAL ephemeris */
//    ArrayList<geph_t> geph;       /* GLONASS ephemeris */
//    seph_t seph;       /* SBAS ephemeris */
//    peph_t peph;       /* precise ephemeris */
//    pclk_t pclk;       /* precise clock */
//    alm_t alm;         /* almanac data */
//    tec_t tec;         /* tec grid data */
//    stec_t stec;       /* stec grid data */
//    erp_t  erp;         /* earth rotation parameters */
    double[] utc_gps = new double[4];  /* GPS delta-UTC parameters {A0,A1,T,W} */
    double[] utc_glo = new double[4];  /* GLONASS UTC GPS time parameters */
    double[] utc_gal = new double[4];  /* Galileo UTC GPS time parameters */
    double[] utc_qzs = new double[4];  /* QZS UTC GPS time parameters */
    double[] utc_cmp = new double[4];  /* BeiDou UTC parameters */
    double[] utc_sbs = new double[4];  /* SBAS UTC parameters */
    double[] ion_gps = new double[8];  /* GPS iono model parameters {a0,a1,a2,a3,b0,b1,b2,b3} */
    double[] ion_gal = new double[4];  /* Galileo iono model parameters {ai0,ai1,ai2,0} */
    double[] ion_qzs = new double[8];  /* QZSS iono model parameters {a0,a1,a2,a3,b0,b1,b2,b3} */
    double[] ion_cmp = new double[8];  /* BeiDou iono model parameters {a0,a1,a2,a3,b0,b1,b2,b3} */
    int leaps;          /* leap seconds (s) */
//    double lam[MAXSAT][NFREQ]; /* carrier wave lengths (m) */
//    double cbias[MAXSAT][3];   /* code bias (0:p1-p2,1:p1-c1,2:p2-c2) (m) */
//    double wlbias[MAXSAT];     /* wide-lane bias (cycle) */
//    double[] glo_cpbias = new double[4];    /* glonass code-phase bias {1C,1P,2C,2P} (m) */
//    char glo_fcn; /* glonass frequency channel number + 8 */
//    pcv_t pcvs[MAXSAT]; /* satellite antenna pcv */
//    sbssat_t sbssat;    /* SBAS satellite corrections */
//    sbsion_t sbsion[MAXBAND+1]; /* SBAS ionosphere corrections */
//    dgps_t dgps[MAXSAT]; /* DGPS corrections */
//    ssr_t ssr[MAXSAT];  /* SSR corrections */
//    lexeph_t lexeph[MAXSAT]; /* LEX ephemeris */
//    lexion_t lexion;    /* LEX ionosphere correction */
    boolean add_eph(eph_t eph){
        return this.eph.add(eph);
    }
//    boolean add_geph(geph_t geph){
//        return this.geph.add(geph);
//    }

}

