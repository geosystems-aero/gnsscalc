import java.util.Date;

public class geph_t {
    int sat;            /* satellite number */
    int iode;           /* IODE (0-6 bit of tb field) */
    int frq;            /* satellite frequency number */
    int svh,sva,age;    /* satellite health, accuracy, age of operation */
    Date toe;        /* epoch of epherides (gpst) */
    Date tof;        /* message frame time (gpst) */
    double[] pos = new double[3];      /* satellite position (ecef) (m) */
    double[] vel = new double[3];      /* satellite velocity (ecef) (m/s) */
    double[] acc = new double[3];      /* satellite acceleration (ecef) (m/s^2) */
    double taun,gamn;   /* SV clock bias (s)/relative freq bias */
    double dtaun;       /* delay between L1 and L2 (s) */
}
