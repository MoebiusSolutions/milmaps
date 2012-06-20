/*   
   All Rights Reserved.
   This material may be reproduced by or for the U.S. Government pursuant to
   the copyright license under the clause at DFARS 252.227-7013 (NOV 1995).
*/

package mil.usmc.mgrs.milGrid;

import mil.geotransj.*;
import mil.usmc.mgrs.objects.Point;

public class Madtran {
	protected static double RadToDeg = 57.29577951;
	protected static double DegToRad = 0.017453293;
	
	protected static int eNO_SOLUTION = 0;
	protected static int eOUTSIDE_RANGE = 1;
	protected static int eGOOD_SOLUTION = 2;
	
	protected static int eSOUTH_HEM = -1;
	protected static int eNORTH_HEM = 1;
	
	//protected Utm m_utm = new Utm();
	protected double[] m_phi = new double[1];
	protected double[] m_lam = new double[1];
	protected double[] m_easting = new double[1];
	protected double[] m_northing = new double[1];
	protected int[] m_zone = new int[1];
	protected char[] m_hem = new char[1];
	protected Geodetic m_geo = new Geodetic();
	protected Point m_geoPt = new Point();
	protected UtmPoint m_utmPt = new UtmPoint();
	protected int[] m_override = new int[1];
	protected int[] m_iLtrLow = new int[1];
	protected int[] m_iLtrHi = new int[1];
	protected double[] m_dFyltr = new double[1];
	protected int[] m_iErr = new int[1];
	protected int[] m_iLTR1 = new int[1];
	protected double[] m_dSpsou = new double[1];
	protected double[] m_dSpnor = new double[1]; 
	protected double[] m_dSleast = new double[1]; 
	protected double[] m_dSlwest = new double[1];
	protected double[] m_dYltr = new double[1];
	protected double[] m_dXltr = new double[1];
	
	protected int[] m_izone = new int[1];
	
	protected String[] m_mgrs = new String[1];
	//protected double[] m_a = new double[1]; // Semi-major axis
	//protected double[] m_f = new double[1]; //flattening
	
	// For old madtran code ..................
	// WGS84 parameters
	protected double m_dA = 6378137.0;      
	protected double m_dRecF = 298.257223563;
	protected int m_iEllipIndex = 21; //wgs84
	
	
	//void SetSouthHem() { m_iHem = eSOUTH_HEM; };
	//void SetNorthHem() { m_iHem = eNORTH_HEM; };
	//int GetHemisphere() { return m_iHem; };
	// ........................................
	
	protected String m_ALBET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public Madtran(){
		
	}
	
	public double getSemiMajorAxis(){
		return m_dA;
	}
	
	public Point utm2Gp(char cHem, int iZone, double dNs, double dEw){
		m_geoPt.flag = false;
		int iHem = ( cHem == 'S' ? -1 : 1 );
		utmToGp(m_dA, m_dRecF, iZone, iHem * dNs, dEw, m_phi, m_lam );
		m_geoPt.m_lat = RadToDeg*m_phi[0];
		m_geoPt.m_lng = RadToDeg*m_lam[0];
		m_geoPt.flag = true;
		return m_geoPt;
	}
	
	public UtmPoint gp2Utm( double lat, double lng ){
		double phi = DegToRad*lat;
		double lam = DegToRad*lng;
		m_utmPt.eHem = ( phi < 0.0 ?  UtmPoint.EHemisphere.eSouth :
									  UtmPoint.EHemisphere.eNorth );
		if ( DegToRad*(-80) <= phi && phi <= DegToRad*84 ){
			gpToUtm( m_dA, m_dRecF, phi, lam, m_zone, m_northing, m_easting, 0 ); 
			m_utmPt.iEasting = (int)(m_easting[0] + 0.5);
			m_utmPt.iNorthing = (int)(m_northing[0] + 0.5);
			m_utmPt.iZone = m_zone[0];
			return m_utmPt;			
		}
		return null;
	}
	
	public String utm2Units( int iHem, int iZone, double dNs, double dEw, Point geo ){
		  //AFX_MANAGE_STATE(AfxGetStaticModuleState());
		  String csMgrs = null;
		  int k;
		  if ( iHem != -1 && iHem != 1 )
		    return null;
		  //csMgrs = "Invalid Position";
		  utmToGp(m_dA, m_dRecF, iZone, iHem * dNs, dEw, m_phi, m_lam );
		  if ( geo != null ){
			  geo.m_lat = RadToDeg*m_phi[0];
			  geo.m_lng = RadToDeg*m_lam[0];
		  }
		  k =  yxTMgr( m_dA, m_dRecF, m_phi[0], m_lam[0], 
		               iZone, dNs, dEw, m_iEllipIndex, m_mgrs);
		  if( k != 0 )
		    csMgrs = m_mgrs[0];
		  return csMgrs;
	}
	
	public int validUtm( int iHem, int iZone, double dNs, double dEw){
		  if ( iHem != -1 && iHem != 1 )
		    return 0;
		  //csMgrs = "Invalid Position";
		  utmToGp(m_dA, m_dRecF, iZone, iHem * dNs, dEw, m_phi, m_lam );
		  int k =  yxTMgr( m_dA, m_dRecF, m_phi[0], m_lam[0], 
		               iZone, dNs, dEw, m_iEllipIndex, null);
		  return k;
	}
	
	public String gp2Units( double lat, double lng, UtmPoint utmPt ){
		UtmPoint p =  gp2Utm( lat, lng );
		if ( p != null ){
			if ( utmPt != null ){
				utmPt.copy(p);
			}
			
		}
		double dPhi = DegToRad*lat;
		double dLam = DegToRad*lng;
		String csMgrs = null;
		int k =  yxTMgr( m_dA, m_dRecF, dPhi, dLam,  p.iZone, 
		     	         p.iNorthing, p.iEasting, m_iEllipIndex, m_mgrs);
		if( k != 0 )
			csMgrs = m_mgrs[0];
		return csMgrs;
	}
	
	///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	// USE MADTRAN CODE BELOW THIS LINE
	////////////////////////////////////////////////////////////////////
	
	protected double pow( double a, double b ){
		return Math.pow(a,b);
	}
	
	protected double sin( double theta ){
		return Math.sin( theta);
	}
	
	protected double cos( double theta ){
		return Math.cos( theta);
	}
	
	protected double sqrt( double x){
		return Math.sqrt(x);
	}
	
	protected double abs ( double x ){
		return ( x < 0 ? -1*x : x);
	}
	
	protected int SGN(double x) 
	{ 
		if ( x == 0 )
		    return 0;
		return ( x < 0 ? -1 : 1 ); 
	}
	
	protected double fnDENOM( double es,double sphi ){
		return ( sqrt( 1.0-(es)*pow(sin((sphi)),2.0)) );
	}

	protected double fnSPHSR( double a, double es, double sphi){
		return ((a)*(1.0-(es))/pow(fnDENOM((es),(sphi)),3.0));
	}
	
	protected double fnSPHTMD(double ap, double bp, double cp, double dp, double ep,double sphi){
		return ((ap)*(sphi)-(bp)*sin(2.0*(sphi))+ 
				(cp)*sin(4.0*(sphi))-(dp)*sin(6.0*(sphi))+
				(ep)*sin(8.0*(sphi)));
	}
	
	//	     *** RADIUS OF CURVATURE IN THE PRIME VERTICAL FROM LATITUDE ***
	protected double  fnSPHSN( double a,double es, double sphi ){
		return ( (a)/sqrt(1.0 - (es)*pow(sin((sphi)),2.0)) );
	}
	
	
	public int Utm2Gp( int iHem, int iZone, double dNs, double dEw, 
			 double[] pdPhi, double[] pdLambda ){
		if ( iHem != eSOUTH_HEM && iHem != eNORTH_HEM )
			return 0;
		utmToGp(m_dA, m_dRecF, iZone, iHem * dNs, dEw, pdPhi, pdLambda );

		return 1;
	}
	/////////////////////////////////////////////////////////////////////
	// Utm2Gp.cpp
	//
	// FILE NAME: utm2gp.bas       VERSION : 9102.07      DESIGN DATE:  7/06/88
	// STRUCTURE: Subroutine       AUTHOR  : K. B. Taylor
	// LANGUAGE : C++              COMPILER: VisualC++    VERSION: 2.10a
	// PURPOSE  : Convert UTM to GP

	/////////////////////////////////////////////////////////////////////
	//	                            DOCUMENTATION
	/////////////////////////////////////////////////////////////////////
	// FUNCTIONAL DESCRIPTION: CONVERTS GRID COORDINATES (ZONE, NORTHING, EASTING ) ***
	//  TO GEOGRAPHIC COORDINATES (LATITUDE, LONGITUDE) ON THE UNIVERSAL TRANSVERSE
	//  MERCATOR (UTM) GRID.
	//
	// CALLING SEQUENCE: UTMTOGP(a, f, iZone, y, x, *pSphi, *pSlam)
	//
	// INPUT ARGUEMENTS:
	//  Variable Name       Type  Description
	//  a                   dbl   Ellipsoid semi-major axis, meters
	//		f                   dbl   Ellipsoid flattening
	//		iZone               int   UTM zone
	//		y                   dbl   UTM northing, meters
	//		x                   dbl   UTM easting, meters
	//
	// OUTPUT ARGUEMENTS:
	//  Variable Name      Type  Description
	//  *pSphi              dbl   Latitude, radians
	//		*pSlam              dbl   Longitude, radians
	//
	// FILES/DEVICES: none


	protected  void utmToGp(
		double  a, 
	    double  recF,  
	    int     iZone, 
	    double  y, 
	    double  x,
	    double[] pSphi, 
	    double[] pSlam
	)
	{
	    double fe = 500000.0;
	    double ok = 0.9996;
	    double dDegRad = Utmhelp.M_PI/180.0;

	    //
	    //    *************************************************
	    //    *****   DERIVE OTHER ELLIPSOID PARAMTERS    *****
	    //    *****         FROM INPUT VARIABLES          *****
	    //    *****    A = SEMI-MAJOR AXIS OF ELLIPSOID   *****
	    //    ***** RECF = RECIPROCAL OF FLATTENING (1/F) *****
	    //    *************************************************

	    //     ** SEMI MAJOR AXIS - B **
	    //double recF = 1.0 / f;

		double b = a*( recF -1. )/recF;

	    //     ** ECCENTRICITY SQUARED **
	    //double es = (a*a - b*b)/(a*a);
		double es = 1.0 - (b*b)/(a*a);

	    //     ** SECOND ECCENTRICITY SQUARED **
		//double ebs = (a*a - b*b)/(b*b);
	    double ebs = (a*a)/(b*b)- 1.0;
	  
	    //     ** TRUE MERIDIONAL DISTANCE CONSTANTS **
	    double tn, ap, bp, cp, dp, ep;
		tn = ( a - b )/( a + b );
	    double tn2 = tn*tn;
	    double tn3 = tn*tn2;
	    double tn4 = tn*tn3;
	    double tn5 = tn*tn4;
		//ap = a*( 1. - tn + 5.0*(tn^2 -tn^3)/4. +81.*(tn^4 -tn^5 )/64. )
	    ap = a*( 1.0 - tn + 5.0*(tn2 - tn3)/4.0 +81.0*(tn4 - tn5)/64.0);
		//bp = 3.*a*( tn -tn^2  +7.*( tn^3 -tn^4)/8. +55.*tn^5/64. )/2.
	    bp = 3.0*a*( tn - tn2 + 7.0*(tn3 - tn4)/8.0 + 55.0*tn5/64.0)/2.0;
		//cp = 15.*a*( tn^2 -tn^3 +3.*( tn^4 -tn^5)/4.  )/16.
	    cp = 15.0*a*( tn2 - tn3 + 3.0*(tn4 - tn5)/4.0 )/16.0;
		//dp = 35.*a*( tn^3 -tn^4 +11.*tn^5/16. )/48.
	    dp = 35.0*a*( tn3 - tn4 +11.0*tn5/16.0 )/48.0;
		//ep = 315.*a*( tn^4 -tn^5 )/512.
	    ep = 315.0*a*( tn4 - tn5 )/512.0;

	    //     *** HEMISPHERE ADJUSTMENT TO FALSE NORTHING & POINT NORTHING ***
	    //     *   NORTHERN HEMISPHERE
		double nfn = 0.0;
		if( SGN(y) < 0 ){
			nfn = 10000000.0;
			y = abs( y );
		}

	    //     ** TRUE MERIDIONAL DISTANCE FOR FOOTPOINT LATITUDE **
		double tmd = ( y - nfn )/ok;

	    //     ***** FOOTPOINT LATITUDE *****

	    //     ** 1ST ESTIMATE **
		double sr = fnSPHSR(a,es, 0.0 );
		double ftphi = tmd/sr;

	    //     ******************************************
	    //     ** ITERATE TO OBTAIN FOOTPOINT LATITUDE **
	    double t10, t11, t12, t13, t14, t15, t16, t17;

		for( int i = 0; i < 5; i++ ){
	    //     * COMPUTED TRUE MERIDIONAL *
			t10 = fnSPHTMD( ap,bp,cp,dp,ep,ftphi );
	    //     * COMPUTED RADIUS OF CURVATURE IN THE MERIDIAN *
			sr = fnSPHSR( a,es,ftphi );
	    //     * CORRECTED FOOTPOINT LATITUDE *
	    //     * NEW FTPOINT = LAST FTPOINT +(TMDACTUAL -TMDCOMP)/RADIUS
			ftphi = ftphi + ( tmd - t10 )/sr;
	    }

	    //     ******************************************

	    //     ** RADIUS OF CURVATURE IN THE MERIDIAN **
		sr = fnSPHSR( a,es,ftphi );
	  
	    //     ** RADIUS OF CURVATURE IN PRIME VERTICAL **
		double sn = fnSPHSN( a,es,ftphi );

	    //     ** OTHER COMMON TERMS **
		double 	s = Math.sin( ftphi);
		double 	c = Math.cos( ftphi);
		double 	t = s/c;
		double 	eta = ebs*c*c;

	    //     ** DELTA EASTING - DIFFERENCE IN EASTING **
		double 	de = x - fe;
	    //     *******************************
	    //     ***** LATITUDE *****
	    //     *** ACCURACY NOTE ***
	    //     *** TERMS T12, T13, T16 & T17 MAY NOT BE NEEDED IN
	    //     *** APPLICATIONS REQUIRING LESS ACCURACY
	    //

	    double t2 = t*t;
	    double t4 = t2*t2;
	    double eta2 = eta*eta;
	    double eta3 = eta*eta2;
	    double eta4 = eta*eta3;
			t10 = t/( 2.0*sr*sn*ok*ok );
			t11 = t*( 5.0 +3.0*t2 +eta -4.0*eta2 -9.0*t2*eta )/( 24.0*sr*sn*sn*sn*pow(ok,4.0) );
			t12 = t*( 61.0 +90.0*t2 +46.0*eta  +45.0*t4 -252.0*t2*eta -3.0*eta2 +100.0*eta3 
	          - 66.0*t2*eta2 -90.0*t4*eta + 88.*eta4 +225.*t4*eta2 + 84.*t2*eta3 
	          - 192.*t2*eta4 )/( 720.*sr*Math.pow(sn,5.0)*Math.pow(ok,6.0) );
			t13 = t*( 1385. +3633.*t2 +4095.*t4 +1575.*t4*t2 )/( 40320.*sr*pow(sn,7.0)*pow(ok,8.0) );
	    //
	    //	     ** LATITUDE **
	    double de2 = de*de;
	    double de4 = de2*de2;

	    //SPHI# = FTPHI# -DE#^2*T10# +DE#^4*T11# -DE#^6*T12# +DE#^8*T13#
	    pSphi[0] = ftphi - de2*t10 +de4*t11 -de2*de4*t12 +de4*de4*t13;
	    //  
	    //	     ***** LONGITUDE *****
	    //
		t14 = 1.0/( sn*c*ok );
		t15 = ( 1.0 + 2.0*t2 + eta )/( 6.0*pow(sn,3)*c*pow(ok,3) );
		t16 = ( 5.0 + 6.0*eta + 28.0*t2 - 3.0*eta2 + 8.0*t2*eta +
	            24.0*t4 - 4.0*eta3 + 4.0*t2*eta2 +24.0*t2*eta3 )/( 120.0*pow(sn,5.0)*c*pow(ok,5.0) );
		t17 = ( 61.0 +662.0*t2 +1320.0*t4 +720.0*t2*t4 )/( 5040.0*pow(sn,7.0)*c*pow(ok,7.0) );
		//
		//	     ** DIFFERENCE IN LONGITUDE **
		double DLAM = de*t14 - de*de2*t15 + de*de4*t16 - de*de2*de4*t17;
	    //
		//	     ** CENTRAL MERIDIAN **
		double OLAM = ( iZone*6 - 183 )*dDegRad;
	    //
        //	     ** LONGITUDE **
		pSlam[0] = OLAM +DLAM;
	}
	
	//--------------  GpToUtm -----------------------------
	protected double sphsn( double sphi, double a, double es){
		return (a/sqrt(1.0-es*pow(sin((sphi)),2.0)));
	}
	
	//  *** TRUE MERIDIONAL DISTANCE FROM LATITUDE ***
	protected double sphtmd(double sphi, double ap, double bp, double cp, double dp, double ep ){
		return (ap*(sphi)-bp*sin(2.0*(sphi))+ 
				cp*sin(4.0*(sphi))- dp*sin(6.0*(sphi))+
				ep*sin(8.0*(sphi)));
	}


	protected int AdjustZone( int iZone, double sphi, double slam ){
		// Need to check for row V and cell 31-32
		if ( DegToRad*56 < sphi && sphi <= DegToRad*64 ){ 
			// This is row V 
			if ( DegToRad*3 < slam && iZone == 31)
				iZone = 32;
		}
		// Next we need to check for row X and cells 31-37.
		else if ( DegToRad*72 < sphi ){
			if ( iZone == 32 ){
				if ( slam < DegToRad*9 )
					iZone = 31;
				else
					iZone = 33;
			}
			else if ( iZone == 34 ){
				if( slam < DegToRad*21 )
					iZone = 33;
				else
					iZone = 35;
			}
			else if ( iZone == 36 ){
				if ( slam < DegToRad*33 )
					iZone = 35;
				else
					iZone = 37;
			}
		}
		return iZone;
	}

	public void gpToUtm (
		double  a, 
		double  recF, //f, 
		double  sphi, 
		double  slam, 
		int[]   piZone, 
		double[] y, 
		double[] x, 
		int     iFixz
	)
	{
		double fe = 500000.0;	// FALSE EASTING (500,000 - UTM)
		double ok = 0.9996;		// ORIGIN SCALE FACTOR (.9996 UTM) 
		double ap;				    // TRUE MERIDIONAL DISTANCE CONSTANT
		double b;				      // SEMI-MINOR AXIS OF ELLIPSOID
		double bp;				    // TRUE MERIDIONAL DISTANCE CONSTANT
		double c;				      // COSINE OF LATITUDE
		double cp;				    // TRUE MERIDIONAL DISTANCE CONSTANT
		double dlam;			    // DELTA LONGITUDE - DIFFERENCE IN LONGITUDE
		double dp;				    // TRUE MERIDIONAL DISTANCE CONSTANT
		double ebs;				    // SECOND ECCENTRICITY SQUARED - ELLIPSOID
		double ep;				    // TRUE MERIDIONAL DISTANCE CONSTANT
		double es;				    // ECCENTRICITY SQUARED (E**2) - ELLIPSOID
		double eta;				    // CONSTANT - EBS*C*C
		double fn;		        // FALSE NORTHING (0 NORTH OF EQUATOR, 10MILLION SOUTH)
		double olam;			    // CENTRAL MERIDIAN - LONGITUDE OF ORIGIN
		double s;				      // SINE OF LATITUDE
		double sn;				    // RADIUS OF CURVATURE IN THE PRIME VERTICAL
		double t;				      // TANGENT OF LATITUDE
		// TERM IN COORDINATE CONVERSION FORMULA - GP TO Y
		double t2, t3, t4, t5, t6;
		double tmd;				    // TRUE MERIDIONAL DISTANCE
		double tn;				    // TRUE MERIDIONAL DISTANCE CONSTANT
		double degrad;			  // DEGREE TO RADIAN CONVERSION FACTOR (PI/180)
		double c3, c5, c7;

		degrad = Utmhelp.M_PI/180.0;

		//    ***************************************************
		//    *****   DERIVE OTHER ELLIPSOID PARAMTERS      *****
		//    *****         FROM INPUT VARIABLES            *****
		//    *****    A = SEMI-MAJOR AXIS OF ELLIPSOID     *****
		//    ***** dRecF = RECIPROCAL OF FLATTENING (1/F)  *****
		//    ***************************************************


		//    ** SEMI MAJOR AXIS - B **
		//double dRecF = 1.0 / f;
	
		b = a*( recF - 1.0 )/recF;

		es = 1.0 - (b/a)*(b/a);

		//   ** SECOND ECCENTRICITY SQUARED **

		ebs = (a/b)*(a/b) - 1.0;

		//   ** TRUE MERIDIONAL DISTANCE CONSTANTS **
		tn = (a - b) / (a + b);
		double tn2 = tn*tn;
		double tn3 = tn*tn2;
		double tn4 = tn*tn3;
		double tn5 = tn*tn4;
		ap = a*( 1.0 - tn + 5.0*( tn2 - tn3)/4.0 + 81.0*( tn4 - tn5 )/64.0);
		bp = 3.0*a*( tn - tn2  + 7.0*( tn3 - tn4)/8.0 + 55.0*tn5/64.0)/2.0;
		cp = 15.0*a*( tn2 - tn3 + 3.0*( tn4 -tn5)/4.0)/16.0;
		dp = 35.0*a*( tn3 - tn4 + 11.0*tn5/16.0)/48.0;
		ep = 315.0*a*( tn4 - tn5 )/512.0;

		//     ***** ZONE - CENTRAL MERIDIAN *****

		//     *** ZONE ***

		//     *** HOLD FIXED IF IFIXZ IS SET TO ONE
		//     *** DETERMINE ZONE NUMBER IF IFIXZ IS ZERO
		if(   iFixz == 0 ){
			//*piZone = 31 + int(slam/degrad/6.0);
			piZone[0] = (int)( 31.0 + slam/degrad/6.0 ); // KBT
			// Adjust the zone for the weird cases.
			piZone[0] = AdjustZone(piZone[0],sphi,slam);
			//         ** ZONE TRAP - AT HEMISPHERE LIMITS **
			if( piZone[0] > 60 ) 
				piZone[0] = 60;
			if( piZone[0] < 1 )
				piZone[0] = 1;
		}

		//     *** CENTRAL MERIDIAN ***
		olam = (piZone[0]*6.0 - 183.0 )*degrad;

		//     *** DELTA LONGITUDE ***
		//     *** DIFFERENCE BETWEEN LONGITUDE AND CENTRAL MERIDIAN ***
		dlam = slam - olam;

		//     *** OTHER COMMON TERMS ***
		s = sin(sphi);
		c = cos(sphi);
		t = s / c;
		eta = ebs * c*c;
		//     ** RADIUS OF CURVATURE IN PRIME VERTICAL **
		sn = sphsn( sphi, a, es);
		//     ** TRUE MERIDIONAL DISTANCE **
		tmd = sphtmd( sphi, ap, bp, cp, dp, ep );
		//     ***** NORTHING *****
	    c3 = c*c*c;
	    c5 = c3*c*c;
	    c7 = c5*c*c;
	    t2 = t*t;
	    t3 = t*t2;
	    t4 = t*t3;
	    t5 = t*t4;
	    t6 = t*t5;

	    double eta2 = eta*eta;
	    double eta3 = eta*eta2;
	    double eta4 = eta*eta3;

	    double T1 = tmd*ok;
	    double T2 = sn*s*c*ok/2.0;
	    double T3 = sn*s*c3*ok*( 5.0 - t2 + 9.0*eta + 4.0*eta*eta )/24.0;
	    double T4 = sn*s*c5*ok*(  61.0 - 58.0*t2 + t4 + 270.0*eta - 330.0*t2*eta + 445.0*eta2 + 
                         		  324.0*eta3  - 680.0*t2*eta2 + 88.0*eta4 - 600.0*t2*eta3  - 
                                  192.0*t2*eta4 )/720.0;
	    double T5 = sn*s*c7*ok*( 1385.0 - 3111.0*t2 + 543.0*t4 - t6 )/40320.0;

	    //     ** FALSE NORTHING **
	    fn = 0.0;
	    if ( sphi < 0.0 )
	    	fn = 10000000.0;

	    y[0] = fn + T1 + dlam*dlam*(T2 + dlam*dlam*(T3 + dlam*dlam*(T4 + dlam*dlam*T5)));


	    //     ***** EASTING *****

	    double  T6 = sn*c*ok;
	    double  T7 = sn*c3*ok*( 1.0 - t2 +eta )/6.0;
	    double  T8 = sn*c5*ok*( 5.0 - 18.0*t2 + t4 + 14.0*eta - 58.0*t2*eta + 
                       			13.0*eta2 + 4.0*eta3 - 64.0*t2*eta2 - 
                       			24.0*t2*eta3 )/120.0;
	    double  T9 = sn*c7*ok*( 61.0 - 479.0*t2 + 179.0*t4 - t6)/5040.0;
	
	    x[0] = fe + dlam*(T6 + dlam*dlam*(T7 + dlam*dlam*(T8 + dlam*dlam*T9)));

	    //   *** ACCURACY NOTE ***
	    //   *** TERMS T4, T5, T8 & T9 MAY NOT BE NEEDED IN
	    //   *** APPLICATIONS REQUIRING LESS ACCURACY
	}
	
	// --------------------- MGRS string support ------------------------------
	///////////////////////////////////////////////////////////////////////////
	protected void  utmSet(
		int      iZone, 
		int[]    piLtrLow, 
		int[]    piLtrHi, 
		double[] pdFyltr, 
		int      iSph, 
		int[]    piErr  
	)
	{
		//     *** ELLIPSOID CODE (iSph) LIST                           ***
		//     ***                                                      ***
		//     *** iSph  FIGURE  ELLIPSOID                              ***
		//     *** 15 1   B-3    INTERNATIONAL                          ***
		//     ***  4 2   B-4    BESSEL                                 ***
		//     ***  5 3   B-4    CLARKE 1866 - NAD AREA                 ***
		//     ***  5 3   B-3    CLARKE 1866 - ZONES 47-55              ***
		//     ***  6 4   B-4    CLARKE 1880                            ***
		//     ***  7 5   B-3    EVEREST                                ***
		//     ***  3 6   B-3    AUSTRALIAN NATIONAL                    ***
		//     *** 17 7   B-3    GRS 1967 (SAD 69)                      ***
		//     *** 20 8   B-3    WGS-72                                 ***
		//     *** 21 9   B-3    WGS-84                                 ***
		//     *** 12 10  B-3    GRS 1980 (NAD-83)                      ***
		//     ***                                                      ***
		//     *** iGroup = 1 IF FIGURE B-3 APPLIES                     ***
		//     ***        = 2 IF FIGURE B-4 APPLIES                     ***
		//     ***                                                      ***
		//
		//
		//     **************************************
		//     *** DETERMINE SET FROM ZONE NUMBER ***
		int iGroup;
		int iSet = 1;
	    while ( iSet < 7 ){
		    if ( (((iZone - iSet)/6 )*6 + iSet )  ==  iZone )
			    break;
			iSet++;
		}

		//     **  SET ELLIPSOID FLAG TO VALID ELLIPSOID CODE
		piErr[0] = 0;
		//     *** SET GROUP NUMBER BASED ON ELLIPSOID CODE ***
		if ( iSph == 4 || iSph == 5 || iSph == 6 || iSph == 22 ){
		    iGroup = 2;
		}
		else if ( iSph == 15  || iSph == 7  || iSph == 3  || iSph == 17 ||
		    iSph == 20  || iSph == 21 || iSph == 12 || iSph == 8  ||
		    iSph == 10  || iSph == 18 || iSph == 19 || iSph == 23 ){
		    iGroup = 1;
		}
		else if ( iSph == 24 ){
		    //        *** THIS NUMBER IS USED TO SET THE GROUP T0 1
		    //        *** FOR A GENERIC ELLIPSE.
		    iGroup = 1;
		}
		else if ( iSph == 25 ){
		    //        *** THIS NUMBER IS USED TO SET THE GROUP T0 2
		    //        *** FOR A GENERIC ELLIPSE.
		    iGroup = 2;
		}
		else {
		    //        *** NON VALID ELLIPSOID FOR MGRS TM8358 VERSION
		    //        *** SET ERROR FLAG TO ON    
		    piErr[0] = 1;
		    iGroup = 1;
		}

		//     *** CLARKE 1866 SPHEROID FOR PHILIPPINES & MARIANA ISLANDS ***
		if ( iSph == 5 && ( iZone >= 47 && iZone <= 55 ) )
		    iGroup = 1;

		//     *** SET 'LOW' AND 'HIGH' (2ND) LETTER OF ZONE BASED ON iSet ***
		if( iSet == 1 || iSet == 4 ){
		    piLtrLow[0] = 1;
		    piLtrHi[0] = 8;
		}
		else if( iSet == 2 || iSet == 5 ){
		   piLtrLow[0] = 10;
		   piLtrHi[0] = 18;
	    }
		else if( iSet == 3 || iSet == 6 ){
		   piLtrLow[0] = 19;
		   piLtrHi[0] = 26;
		}

		//     *** SET FALSE NORTHINGS FOR 3RD LETTER ***

		if( iGroup == 1 ){
		    // * GROUP 1
		    pdFyltr[0] = ( iSet % 2 == 0 ? 1500000.0 : 0 );
		}
		else if( iGroup == 2 ){
		   // * GROUP 2
		   pdFyltr[0] = ( iSet % 2 == 0 ? 500000.0 : 1000000.0 );
		}
	}
	
	protected void utmLim( 
	    int[]     N, 
		double    sphi, 
		int       iZone, 
		double[]  pSpsou, 
		double[]  pSpnor, 
		double[]  pSleast, 
		double[]  pSlwest 
	)
	{
	    //     *** 1ST LETTER NUMBER IS EITHER INPUT OR DERIVED        ***
		//     ***                                                     ***
		//     *** WHERE:  *N = 1ST LETTER NUMBER                       ***
		//     ***           = 3-24 (C-X) IF KNOWN                     ***
		//     ***           = 0 = UNKNOW TO BE DERIVED                ***
		//     ***      sphi = LATITUDE OF POINT IN RADIANS           ***
		//     ***     iZone = ZONE NUMBER                             ***
		//     ***     *pSpsou = SOUTH LATITUDE OF AREA                 ***
		//     ***     *pSpnor = NORTH LATITUDE OF AREA                 ***
		//     ***    *pSleast = EAST LONGITUDE OF AREA                 ***
		//     ***    *pSlwest = WEST LONGITUDE OF AREA                 ***
		//     ***********************************************************
        //     ** CONSTANT DEG TO RADIANS
		double dDegRad = Utmhelp.M_PI/180.0;

		//     *** 1ST LETTER NUMBER FOR MGRS ***

		if(  N[0] == 0 ){
		    //        *** DERIVE 1ST LETTER NUMBER OF MGRS FROM ***
			//        *** LATITUDE AS BROKEN DOWN INTO 8 DEGREE BANDS ***
			//        *** STARTING WITH LETTER #3 (C) AT 80 DEGREES SOUTH LATITUDE ***
			N[0] = (int)( ( sphi +80*dDegRad)/( 8*dDegRad ) ) +3;

			//        ** CORRECT FOR LETTERS I & O **
			if( N[0] >  8 )
			    N[0] = N[0] + 1;
			if( N[0] > 14 )
			    N[0] = N[0] + 1;
			//        ** LETTER 'Y' (25) WILL BE DERIVED FOR LATITUDES 80-84N
			//        ** RESET TO LETTER 'X' (24) WHICH HAS BEEN MADE
			//        ** 12 DEGREES IN SIZE IN THE NORTH-SOUTH DIRECTION
			if( N[0] >=  25 )  
			    N[0] = 24;
			//        ** LATITUDE OF ZERO SHOULD BE MADE LETTER '*N' (14) **
			if( N[0] == 13  &&  sphi ==  0 )
			    N[0] = 14;
		}
		//     *************************
		//     **** LATITUDE LIMITS ****
		//     *************************
		//     ** SOUTH LATITUDE LIMIT **

		int iSphi = ( N[0] - 3)*8 - 80;
		//     ** CORRECTION FOR LETTERS I (9) & O (15) **
		if( N[0] >  8 ) 
		    iSphi = iSphi  - 8;
		if( N[0] > 14 ) 
		    iSphi = iSphi  - 8;
		pSpsou[0] = iSphi*dDegRad;
		//     ** NORTH LATITUDE LIMIT - 8 DEGREES NORTH **
		pSpnor[0] = pSpsou[0] + 8*dDegRad;
		//     ** LETTER 'X' (24) LIMIT IS 12 DEGREES NORTH **
		if( N[0] == 24 )
		    pSpnor[0] = pSpsou[0] + 12*dDegRad;
		//     ****************************
		//     ***** LONGITUDE LIMITS *****
		//     ****************************

		//     *** SET LONGITUDE LIMITS FOR 'STANDARD' ZONES ***
			  
		//     ** CENTRAL MERIDIAN
		int     iCm   = iZone*6 -183;
		double  dSLCM = iCm*dDegRad;

		//     ** EAST & WEST LONGITUDE LIMITS **
		pSleast[0] = dSLCM + 3*dDegRad;
		pSlwest[0] = dSLCM - 3*dDegRad;
		//
		//     ** ADJUST LONGITUDE LIMITS FOR ODD SIZED AREAS **
		//     ** ALL IN ZONES 31 - 37
		//     ** LETTER NUMBER 22 (V) AND ABOVE
		if( iZone < 31  ||  iZone > 37 ) 
		    return;
		if(  N[0] < 22 )
			return;
		//     ** ZONES 31 AND 32 AT LETTER NUMBER 22 (V) **
		if( N[0] == 22  &&  iZone == 31 )
		    pSleast[0] = 3*dDegRad;
		if( N[0] == 22  &&  iZone == 32 )
		    pSlwest[0] = 3*dDegRad;

		// ZONES 32, 34, AND 36 and LETTER NUMBER (X);
		// ADDED BY KBT.
		if ( iZone == 32 || iZone == 34 || iZone == 36 ){
		    if ( N[0] == 24 )
			    N[0] = 23;
		}

		//     ** REMAINDER OF ODD LONGITUDE ZONES IN LETTER NUMBER 24 (X) **
		if( N[0] < 24 )
		    return;
	    if( iZone == 31 )
		    pSleast[0] = 9*dDegRad;
		if( iZone == 33 )
		    pSlwest[0] = 9*dDegRad;
		if( iZone == 33 )
		    pSleast[0] = 21*dDegRad;
		if( iZone == 35 )
		    pSlwest[0] = 21*dDegRad;
		if( iZone == 35 )
		    pSleast[0] = 33*dDegRad;
		if( iZone == 37 )
		    pSlwest[0] = 33*dDegRad;
		return;
	}
	
	public int milRef( 
		double  a, 
		double  recF, 
		double  sphi, 
		double  slam, 
		int     iZone, 
		double  y, 
		double  x,  
		int     iSph,
		String[] mgrs
	)
	{

		//     ** SET CERTAIN CONSTANTS
		//     * ROUND OFF, ALPAHABET, BIG NUMBER, 100K, 2 MILLION
		
		double dRND1    = 0.1;
		double dBIGN    = 10000000.0;
		double dONEHT   = 100000.0;
		double dTWOMIL  = dONEHT*20;
		double dZERO    = 0;

		//        ** DEG TO RADIANS **
		double dDegRad = Utmhelp.M_PI/180.0;

		//      *** BRANCH OFF ACCORDING TO AREA CODE - IAREA ***
		//      *** IAREA = 1 = UTM  *** (ONLY AVAILABLE OPTION ***
		//      ***         2 = UPS - NORTH ZONE (N/A THIS VERSION)
		//      ***         3 = UPS - SOUTH ZONE (N/A THIS VERSION)

		//      *** UTM ***
		//      *** FROM ZONE NUMBER AND SPHEROID CODES(S) ***
		//      *** DETERMINE THE FOLLOWING USEING SUBPROGRAM UTMSET
		//      *** 2ND LETTER RANGE (LTRLOW, LTRHI)
		//      *** FALSE NORTHING FOR 3RD LETTER (FNLTR)
		//      *** IF ELLIPSOID CODE IS APPLICABLE TO MGRS (TM8358)
		//      ***    IERR = 0 - IF APPLICABLE
		//      ***         = 1 - IF *NOT* APPLICABLE - PUT MSG IN MGRS AND EXIT

		utmSet( iZone, m_iLtrLow, m_iLtrHi, m_dFyltr, iSph, m_iErr );
		//     *******************************************************
		if(  m_iErr[0] == 1 ){
		    // *** NON MGRS ELLIPSOID
		    return 0;
		}

		//     *** DETERMINE 1ST LETTER NUMBER AND
		//     *** LATITUDE & LONGITUDE LIMITS OF UTM GEOGRAPHIC AREA
		//     *** USING LATITUDE AND ZONE NUMBER
		//     ***

		m_iLTR1[0] = 0;
		utmLim( m_iLTR1, sphi, iZone,  m_dSpsou, m_dSpnor, m_dSleast, m_dSlwest  );

		//     *** DETERMINE LOWEST NORTHING OF GEOGRAPHIC AREA ***
		//         USING LATITUDE SPSOU AT CENTRAL MERIDIAN

		//     ** CENTRAL MERIDIAN **
		double dSlcm = ( iZone*6 -183 )*dDegRad;
		m_izone[0] = iZone;
		//     ** CONVERT TO GRID COORDINATES **
		gpToUtm( a, recF, m_dSpsou[0], dSlcm, m_izone, m_dYltr, m_dXltr, 1 );
		iZone = m_izone[0];

		// ********** LINES BELOW NOT NEEDED FOR THIS VERSION ******************
		// ******* SCALE DOWN TO NEAREST 100,000 UNIT **
		// *****YLOW# = int( CDBL( int( dYltr/dONEHT ) )*dONEHT +.5*SGN(dYltr) )
		// *****
		// ******* SCALE NUMBER DOWN TO LESS THAN 2 MILLION **
		// *****YSLOW# = YLOW#
		// *****
		// *****
		// *****WHILE  YSLOW# > dTWOMIL
		// *****    YSLOW# = YSLOW# -dTWOMIL
		// *****WEND
		// *****
		// *****SLOW# = int( YSLOW# +.5*SGN(YSLOW#) )
		// *****
		// *********** LINES ABOVE NOT NEEDED FOR THIS VERSION ***************
        //     *** UTM *** 3RD LETTER ***

		//     ** OBTAIN NEAREST WHOLE NUMBER **
		m_dYltr[0] = (int)( y +.5*SGN(y) );

		//     ***********************************************
		//     ***   SPECIAL CASE - SOUTHERN HEMISPHERE    ***
		//     ***     10 MILLION NORTHING (EQUATOR)       ***
		//     *** NOT POSSIBLE IN MGRS - SUBTRACT 1 METER ***
		//     ***********************************************

		if( (int)( m_dYltr[0] + .5 ) == (int)( 1.E7 + dRND1 ) )
			m_dYltr[0] = (int)( m_dYltr[0] -1 );

		//     ** SCALE DOWN NUMBER UNTIL LESS THAN 2 MILLION **
		while( m_dYltr[0] >=  dTWOMIL )
		    m_dYltr[0] = m_dYltr[0] -dTWOMIL;

		//     ** SUBTRACT FALSE NORTHING **
		m_dYltr[0] = m_dYltr[0] - m_dFyltr[0];

		//     ** IF LESS THAN ZERO CORRECT BY ADDING 2 MILLION **
		if( m_dYltr[0] < dZERO )
		    m_dYltr[0] = m_dYltr[0] +dTWOMIL;

		//     *** DETERMINE 3RD LETTER NUMBER ***

		int iLTR3 = (int)( ( m_dYltr[0] +dRND1)/dONEHT ) + 1;

		//     ** CORRECT FOR LETTERS 'I' (9) AND 'O' (15)  **
		if( iLTR3 >  8  )
		    iLTR3 = iLTR3 +1;
		if( iLTR3 > 14  )
		    iLTR3 = iLTR3 +1;

		//     *** UTM - 2ND LETTER ***

		//     ** OBTAIN NEAREST WHOLE NUMBER **
		m_dXltr[0] = (int)( x +.5*SGN(x) );

		//     ******************************************************
		//     ***   SPECIAL CASE - ZONE 31                       ***
		//     *** 56-64 DEGREES NORTH LATITUDE - LETTER 'V' (22) ***
		//     *** 500,000 EASTING NOT POSSIBLE IN MGRS           ***
		//     *** SUBTRACT 1 METER                               ***
		//     ******************************************************

		if( ( m_iLTR1[0] == 22 && iZone == 31) && ( (int)( m_dXltr[0] +.5 ) == (int)(5.E5 + dRND1) ) )
			m_dXltr[0] = (int)( m_dXltr[0] -1 );

		int iLTR2 = m_iLtrLow[0] + (int)( ( m_dXltr[0] +dRND1)/dONEHT ) -1;

		//     ** CORRECT FOR LETTER 'O' (15) **
		//     ** WHEN LTRLOW IS 'J' (10)     **
		if( m_iLtrLow[0] == 10  &&  iLTR2 > 14 )
		    iLTR2 = iLTR2 +1;

		//     ***** NUMBER SECTION *****
		//     *** EASTING PART ***
		//     ** OBTAIN NEAREST WHOLE NUMBER **
		double dXNUM = (int)( x +.5*SGN( x)  );
		//
		//     ******************************************************
		//     ***   SPECIAL CASE - ZONE 31                       ***
		//     *** 56-64 DEGREES NORTH LATITUDE - LETTER 'V' (22) ***
		//     *** 500,000 EASTING NOT POSSIBLE IN MGRS           ***
		//     *** SUBTRACT 1 METER                               ***
		//     ******************************************************

		if( ( m_iLTR1[0] == 22   &&   iZone == 31 ) &&
			( (int)( dXNUM +.5 ) == (int)( 5.E5 +dRND1 ) ) )
			    dXNUM = (int)( dXNUM -1 );
			  
		//     *** NORTHING PART ***

		//     ** OBTAIN NEAREST WHOLE NUMBER **
		double dYNUM = (int)( y + 0.5*SGN(y) );

		//     ***********************************************
		//     ***   SPECIAL CASE - SOUTHERN HEMISPHERE    ***
		//     ***     10 MILLION NORTHING (EQUATOR)       ***
		//     *** NOT POSSIBLE IN MGRS - SUBTRACT 1 METER ***
		//     ***********************************************

		if( (int)( dYNUM + 0.5 ) == (int)( 10000000.0 + dRND1 ))
			dYNUM = (int)( dYNUM -1 );
		//		dYNUM = dYNUM  -int( ( dYNUM  +dRND1)/dONEHT )*dONEHT
		//
		//     *** MGRS TEMPLATE
		//    "ZZL LL EEEEE NNNNN";

		// SET THE STRING
		dXNUM =  dXNUM + dBIGN;
		dYNUM  = dYNUM + dBIGN; 
		String sEasting = Integer.toString((int)(dXNUM));
		String sNorthing = Integer.toString((int)(dYNUM));
		int L1 = sEasting.length();
		int L2 = sNorthing.length();
		///////////////////////////////////////////////////////////////////
		// Added by KBT.
		double dYnum = 1.5e-7;
		double dXnum = dYnum/cos(sphi);
		if ( sphi + dYnum < m_dSpsou[0] || sphi - dYnum > m_dSpnor[0] )
			 m_iLTR1[0]--;
		else if ( slam + dXnum < m_dSlwest[0] || slam - dXnum > m_dSleast[0] )
			 m_iLTR1[0]--;
		// End of add by KBT
		///////////////////////////////////////////////////////////////////

		// Note we need to subtract 1 from the itr1, itrl2 and itrl3 since the 
		// ALBET array starts at 0 and not 1.
		//
		// Note we need to subtract 1 because ALBET starts with a zero index.
		if ( mgrs != null ){
			mgrs[0] = String.format( "%02d%c %c%c %s %s", 
							 		 iZone, m_ALBET.charAt(m_iLTR1[0]-1), // UTM
							         m_ALBET.charAt(iLTR2-1), m_ALBET.charAt(iLTR3-1), // MGRS Characters     
							         sEasting.substring(L1-5),	  // Easting
							         sNorthing.substring(L2-5) );  //sNorthing.
		}
		return  1;        
	}
	
	public int yxTMgr( 
		double   a, 
		double   recF, 
		double   sphi, 
		double   slam, 
		int      iZone, 
		double   dY, 
		double   dX,  
		int      isph,   // Set to 21 for WGS-84
		String[] pcMGRS
	)
	{
		  
		//     ** DEGREES TO RADIANS
		double dDegRad = Utmhelp.M_PI/180.0;

		//     *** SET ROUND OFF ALLOWANCE FOR GEOGRAPHIC COORDINATES ***
		//         - TO APPROXIMATELY 1 METER AT EQUATOR -
		//         - .032 SECONDS IN RADIANS -

		double dRSL = 0.032*dDegRad/3600;
		//     *** USE iZone TO DETERMINE GRID COORDINATE SYSTEM ***
		//     *** iZone = 1-60 = UTM (0 FOR UPS IN OTHER APPLICATIONS)
		//     NOTE: THIS APPLICATION - UTM ONLY
		//     NOTE: iZone .EQ. 0 TEST WENT HERE IN OTHER APPLICATIONS
		//
		//     ***** UTM (ONLY) SET AREA = 1  *****

		//     *** SPECIAL CASE AT EQUATOR FOR SOUTHERN HEMISPHERE ***
		//     *** WHERE Y = -10 MILLION
		//     *** REDUCE VALUE BY .001 METERS

		if(  abs(  dY +1.E7 ) < 0.001 )
			dY = dY + 0.001;
		//     *** CONVERT GRID COORDINATES TO GEOGRAPHIC COORDINATES ***

		// Commented out this next line. Don't see why we need it.
		// We already know what sphi and slam are since they are inputs. 
		//Utm2Gp( a, recF, iZone, *pY, *pX, &sphi, &slam ); // KBT

		//     *** VALIDITY CHECK ON LATITUDE LIMITS ***

		if( sphi - dRSL  >   84*dDegRad )
		    return 0;
		if( sphi + dRSL  <  -80*dDegRad )
		    return 0;

		//     *** VALIDITY CHECKS ***
		//     *** CHECK FOR NON EXISTENT ZONES
		//     *** ZONES 32, 34 & 36
		//     *** FOR AREAS ALL ABOVE 72 DEGREES NORTH LATITUDE

		if( sphi >  72*dDegRad ){
		//     *** ZONES ABOVE 72 DEG NOT ON UTM ***
		    if( iZone == 32 )
			    iZone = 31;
		   else if ( iZone == 34 )
		      iZone = 33;
		   else if ( iZone == 36)
		      iZone = 35;
	    }

		//    *** DETERMINE LONGITUDE LIMITS OF AREA (SLEAST & SLWEST) ***
		//    *** (1ST LTR NUM OF MGRS (N) ALSO DETERMINED)
		//    *** (LATITUDE LIMITS OF AREA (SPSOU &SPNOR) ALSO DETERMINED

		//    ** SET 1ST LTR NUMBER OF MGRS (N) TO ZERO SO IT WILL BE DERIVED
		//    ** BY SUBROUTINE UTMLIM

		int[] N = new int[1];             
		utmLim( N, sphi, iZone,  m_dSpsou, m_dSpnor, m_dSleast, m_dSlwest );

		//     ***** VALIDITY CHECK ON LONGITUDE LIMITS *****

		if( slam + dRSL/cos( sphi )  >=  m_dSlwest[0] ){
			if( slam - dRSL/cos( sphi )  <=  m_dSleast[0] ){
			    //     ***** VALID - GEOGRAPHIC & UTM GRID COORDINATES - GET MGRS *****
			    //     ***** GET MILITARY GRID REFERENCE SYSTEM (MGRS) COORDINATE *****
				milRef( a, recF, sphi, slam, iZone, dY, dX, isph, pcMGRS );
			    return 1;
			}
		}
		//     ****** ADJUSTMENT & ERROR CONDITION MESSAGE ***** 
		//     ** OUTSIDE LIMITS - UTM - LATITUDE, LONGITUDE OR ZONE  **
		//     ** CHECK FOR POSSIBLE 1ST LETTER BOUNDARY CONDITION **
		//        - ODD SIZED ZONE -

		if( abs( sphi - m_dSpnor[0] ) <=  dRSL ){
		    //   *** NORTH LIMIT OF 1ST LTR AREA - GO UP ONE LETTER
			N[0] = N[0] + 1;
			//   * LTR 25 & 26 - NORTH ZONE - UPS
			if(  N[0] > 24 ){
			     return 0;
			}
		}
		else if( abs( sphi - m_dSpsou[0] ) <=  dRSL ){
		    // *** SOUTH LIMIT OF 1ST LTR AREA - GO DOWN ONE LETTER
			N[0] = N[0] -1;
			// * LTR 1 & 2 - SOUTH ZONE - UPS
			if(  N[0] < 3  ){
			    return 0;
			}
		}
		else{
		    //     **** STILL OUTSIDE MGRS ****
			//sprintf(pcMGRS,"UTM NOT IN MGRS");
			return 0;
		}
		//     *** TRY AGAIN WITH FIXED 1ST LETTER NUMBER (N) ***

		utmLim( N, sphi, iZone, m_dSpsou, m_dSpnor, m_dSleast, m_dSlwest );

		//     ***** VALIDITY CHECK ON LONGITUDE LIMITS *****

		if( slam + dRSL/cos(sphi)  <  m_dSlwest[0] ){
		    //sprintf(pcMGRS,"UTM NOT IN MGRS");
		    return 0;
		}
		
		if( slam - dRSL/cos( sphi )  >  m_dSleast[0] ){
		    //sprintf(pcMGRS,"UTM NOT IN MGRS");
		    return 0;
		}

		//     *** POINT NOW VALID ***
		milRef( a, recF, sphi, slam, iZone, dY, dX, isph, pcMGRS );
		return 1;
	}
}
