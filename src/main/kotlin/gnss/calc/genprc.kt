package gnss.calc

import kotlin.math.pow

/*
 * Created by aimozg on 15.11.2018.
 * Confidential unless published on GitHub
 */

/**
 * Calculates pseudorange correction - the difference between computed geometric range (cr) and adjusted pseudorange (adjr):
 * cr - distance between the base and the satellite at the time of transmission (ttr = time - range / c) corrected for the Earth's rotation,
 * adjr = range + Δtsv(ttr) * c - Tgd * c,
 * @param time - time of the pseudorange measurement,
 * @param range - pseudorange (m),
 * @param base - base coordinates (ecef) (m) (double[3]),
 * @param eph - ephemeris parameters for the correction calculation.
 * @return the calculated pseudorange correction.
 */
fun genprc(time: GnssTime, range: Double, base: DoubleArray, eph: eph_t): Double {
	val dt = -range / CLIGHT
	val time1 = GnssTime(time.time, time.sec + dt)
	val pos = eph.positionAt(time1)
	val x = pos.point3D.x
	val y = pos.point3D.y
	val z = pos.point3D.z
	val adjr = range + pos.dts * CLIGHT - (eph.tgd[0] ?: 0.0) * CLIGHT
	val cr = ((base[0] - x).pow(2) + (base[1] - y).pow(2) + (base[2] - z).pow(2)).pow(0.5)
	val dr = OMGE / CLIGHT * (x * base[1] - y * base[0])
	return cr - adjr + dr
}

/*
double gengprc(gtime_t time,double range,const double * base,const geph_t * eph)
{
 double rs[3];
 double adjr;
 double cr;
 double dr;
 double dts;
 double var;
 time.sec-=range/CLIGHT;
 if(time.sec<0)
 {
  --time.time;
  time.sec=1.0+time.sec;
 }
 geph2pos(time,eph,rs,&dts,&var);
 adjr=range+dts*CLIGHT;
 cr=sqrt(pow(base[0]-rs[0],2)+pow(base[1]-rs[1],2)+pow(base[2]-rs[2],2));
 dr=OMGE/CLIGHT*(rs[0]*base[1]-rs[1]*base[0]);
 return cr-adjr+dr;
}

 */