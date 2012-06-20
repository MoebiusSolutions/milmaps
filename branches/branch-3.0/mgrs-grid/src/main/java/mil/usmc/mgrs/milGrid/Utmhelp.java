package mil.usmc.mgrs.milGrid;

import java.awt.Graphics2D;
import java.util.ArrayList;

import mil.usmc.mgrs.IProjection;
import mil.usmc.mgrs.milGrid.UtmPoint.EHemisphere;
import mil.usmc.mgrs.objects.MapUnits;
import mil.usmc.mgrs.objects.Point;
import mil.usmc.mgrs.objects.R2;


public class Utmhelp {
	protected static int WINDEDGE = 10;
	protected static int MAX_NS            =   10000000;
	protected static int FALSE_NORTHING    =  10000000;
	protected static int FALSE_EASTING	   =   500000; 
	protected static int LEFT_EASTING0     =  166021;
	protected static int RIGHT_EASTING0    = 833978;
	protected static int MIN_LEFT_EASTING  = 100000;
	protected static int MAX_LEFT_EASTING  = FALSE_EASTING;
	protected static int MIN_RIGHT_EASTING = FALSE_EASTING;
	protected static int MAX_RIGHT_EASTING = 999999;
	protected static int MAX_ITER          = 200;
	protected static double TOL            = 0.000001;
	public static double M_PI = 3.14159265358979323;
	public static double RadToDeg = (180.0/M_PI);
	public static double DegToRad = (M_PI/180.0);	
	
	public static Point utmToLatLng( 
	  Madtran  cm,
	  UtmPoint uPt
	)
	{
	  int     iZone;
	  char    cHem  = (uPt.eHem == UtmPoint.EHemisphere.eNorth ? 'N' : 'S');
	  double  dEw, dNs;
	  iZone = uPt.iZone;
	  dEw   = uPt.iEasting;
	  dNs   = uPt.iNorthing;
	  return cm.utm2Gp(cHem,iZone,dNs,dEw);
	}

	public static String utmToMgr( 
	  Madtran  cm,
	  UtmPoint uPt,
	  MapUnits mp
	)
	{
	  int     iZone;
	  double  dEw, dNs;
	  int iHem  = (uPt.eHem == UtmPoint.EHemisphere.eNorth ? 1 : -1);
	  iZone = uPt.iZone;
	  dEw   = uPt.iEasting;
	  dNs   = uPt.iNorthing;
	  return cm.utm2Units(iHem,iZone,dNs,dEw,null);
	}

	public static UtmPoint latLngToUtm(
	  Madtran  cm,
	  double    lat,
	  double    lng
	)
	{
	  return cm.gp2Utm(lat,lng);
	}

	public static String latLngToMgr(
	  Madtran  cm,
	  double    lat,
	  double    lng
	)
	{
	  return cm.gp2Units(lat,lng,null);
	}


	public static String worldPixToMgr(
	  Madtran     cm,
	  IProjection  proj,
	  R2           pix
	)
	{
	  Point gp = proj.xyPixelToLatLng(pix.m_x, pix.m_y);
	  if ( gp == null )
		  return null;
	  return latLngToMgr(cm,gp.m_lat,gp.m_lng);
	}

	public static String boxPixToMgr(
		Madtran        cm,
		IProjection    proj,
		PixBoundingBox box,
		R2             pix
	){
		R2 p = box.boxPtToWordPix(pix);
		return worldPixToMgr( cm, proj, p );
	}

	public static UtmPoint worldPixToUtm(
	  Madtran     cm,
	  IProjection  pProj,
	  R2           pt
	)
	{
		Point gp = pProj.xyPixelToLatLng(pt.m_x, pt.m_y);
		//cm.Convert(gp, false);
		return latLngToUtm(cm, gp.m_lat,gp.m_lng);
	}

	public static R2 utmToWorldPix(
	  Madtran      cm,
	  IProjection  pProj,
	  UtmPoint     uPt
	)
	{
		Point geo = utmToLatLng(cm,uPt);
		//cm.Convert(geo, true);
		if ( geo != null )
			return pProj.latLngToPixelXY(geo.m_lat, geo.m_lng);
		return null;
	}
	
	public static UtmPoint boxPixToUtm(
		Madtran      	cm,
		IProjection  	proj,
		PixBoundingBox 	box,
		R2           	pt
	){
		R2 p = box.boxPtToWordPix(pt);
		return worldPixToUtm(cm,proj,p);
	}
	
	

	// This is based on a map with zero y pixel at the bottom of the map
	public static boolean getUtmCornerPts(
	  Madtran     	 cm,  
	  IProjection  	 pProj,
	  PixBoundingBox box,
	  UtmPoint     	 bl,
	  UtmPoint       tl,
	  UtmPoint       tr,
	  UtmPoint       br
	)
	{
		R2 pt = new R2();
		pt.m_x = box.getLeftX()-WINDEDGE;
		pt.m_y = box.getBottomY() - WINDEDGE;
		UtmPoint p;
		p = worldPixToUtm(cm, pProj, pt);
		if ( p != null ) {
			bl.copy(p);
			pt.m_y = box.getTopY() + WINDEDGE;
			p = worldPixToUtm(cm, pProj, pt);
			if ( p != null ) {
				tl.copy(p);
				pt.m_x = box.getRightX() + WINDEDGE;
				p = worldPixToUtm(cm, pProj, pt);
				if ( p != null) {
					tr.copy(p);
					pt.m_y = box.getBottomY() - WINDEDGE;
					p = worldPixToUtm(cm, pProj, pt);
					if ( p != null ) {
						br.copy(p);
						return true;
					}
				}
			}
		}
		return false;
	}


	public static void collectZones(
	  UtmPoint bl,
	  UtmPoint tl,
	  UtmPoint tr,
	  UtmPoint br,
	  ArrayList<Integer> m_caZones
	)
	{
		// Need to compute the zones.
		int iZone  = Math.min(bl.iZone,tl.iZone);
		int iEndZone = Math.max(br.iZone,tr.iZone);
		// Next count the zone and save them in a CString. 
		// We can do this since 1 <= iZone <= 60.
		m_caZones.clear();
		m_caZones.add(iZone);
		while( iZone != iEndZone ){
			iZone = nextZone(iZone,'N');
			m_caZones.add(iZone);
		}
	}



	public static UtmLocSize findUtmCell(
		Madtran    cm,
		char	    cHem,
		int		    iZone,
		int		    iNs
	)
	{
		UtmLocSize cell = null;
		int iHem  = (cHem == 'N' ? 1 : -1);
		String csMgrs = cm.utm2Units(iHem, iZone, iNs, FALSE_EASTING, null );
		if(  csMgrs != null ){
			char cLatBand = csMgrs.charAt(2);
			cell = zoneCharToCell(iZone, cLatBand);
		}
		return cell;
	}


	public static int leftEasting(
		Madtran    cm,
		UtmLocSize cell,
		char	   cHem,
		int		   iZone,
		int		   iNs
	)
	{
		if ( iNs == 0 ) {
			return LEFT_EASTING0;
		}
		
		double dA = cm.getSemiMajorAxis();
		double[] dPhi = new double[1];
		double[] dLam = new double[1];
		if ( dA <= 0.0 ) {
			dA = (FALSE_EASTING - LEFT_EASTING0) / DegToRad*3;
		}
	    int tNs = ( cHem == 'N' ? iNs : MAX_NS - iNs );
	    dPhi[0] = tNs / dA;
	    double dArc = DegToRad*(cell.dLeftLng - cell.dOriginLng);
	    int iEw = (int)(0.5 + dArc * dA * Math.cos(dPhi[0]));
	    iEw += FALSE_EASTING;
	 
	    dLam[0] = 99999.99;
	    double dLeftLam = DegToRad*(cell.dLeftLng); 
	    int iHem = ( cHem == 'N' ? 1 : -1 );
		int iCount = 0;
		while ( Math.abs(dLam[0] - dLeftLam) > TOL && iCount < MAX_ITER ) {
	    cm.Utm2Gp(iHem, iZone, iNs, iEw, dPhi, dLam);
	    iEw -= (int)(0.5 + (dLam[0] - dLeftLam) * dA * Math.cos(dPhi[0]));
			iCount++;
		}
	    // Do a final check to make sure utm is valid for mgrs.
	    iCount = 0;
	    while( cm.validUtm(iHem,iZone,iNs,iEw++) != 0 && iCount++ < 6 );

	    return iEw;
	}

	public static int rightEasting(
		Madtran    cm,
		UtmLocSize  cell,
		char		cHem,
		int		    iZone,
		int		    iNs
	)
	{
		if ( iNs == 0 ) {
			return RIGHT_EASTING0;
		}

	    double dA = cm.getSemiMajorAxis();
		double[] dPhi = new double[1];
		double[] dLam = new double[1];
		
	    int tNs = ( cHem == 'N' ? iNs : MAX_NS - iNs );
	    if ( dA <= 0.0 ) {
	      dA = (FALSE_EASTING - LEFT_EASTING0) / DegToRad*3;
	    }
		dPhi[0] = tNs / dA;
	   double dArc = DegToRad*(cell.dLeftLng + cell.iWidth - cell.dOriginLng);
	   int iEw = (int)(0.5 + dArc * dA * Math.cos(dPhi[0]));
	   iEw += FALSE_EASTING;

	  dLam[0] = 99999.99;
	  double dRightLam = DegToRad*(cell.dLeftLng + cell.iWidth);

	  int iHem = ( cHem == 'N' ? 1 : -1 );
	  int iCount = 0;
	  while ( Math.abs(dLam[0] - dRightLam) > TOL && iCount < MAX_ITER ) {
	    cm.Utm2Gp(iHem, iZone, iNs, iEw, dPhi, dLam);
	    iEw += (int)(0.5 + (dRightLam - dLam[0]) * dA * Math.cos(dPhi[0]));
	    iCount++;
	  }
	  // Do a final check to make sure utm is valid for mgrs.
	  iCount = 0;
	  while(  cm.validUtm(iHem,iZone,iNs,iEw--) != 0 && iCount++ < 6);
	  return iEw;
	}

	//TODO
	//public static Point getCenterLatLng(
	//  IProjection pProj,
	//  int tileSize,
	//  int levelOfDetail,
	//  PixBoundingBox pixBox
	//)
	//{
	//	//pProj.initUsingSize_Level(tileSize, levelOfDetail);
	//	int centX = pixBox.getCenterX();
	//	int centY = pixBox.getCenterY();
	//	// Get Center Lat lng.
	//	Point gp = pProj.xyPixelToLatLng(centX, centY);
	//	return gp.clone();
	//}

	public static int getNextInc(int iCurrent, int iEnd, int iInc)
	{
	  // First we need to Get the next multiple of iInc.
	  int iNext = iInc*( iCurrent/iInc + 1 );
	  // Next check to see if current is already a multiple of iInc
	  // and if so we need to add iInc to get the next one.
	  if ( iCurrent == iNext )
	    iNext += iInc;
	  return Math.min(iNext,iEnd);
	}

	public static int adjustEw(
	  int iNextEw,
	  int iMatchToEw,
	  int iStartEw,
	  int iEndEw,
	  int iInc
	)
	{
	  int iEw = iNextEw;
	  if ( iNextEw < iMatchToEw - iInc/4 ){
	    while( iEw < iEndEw ){
	      iEw = Math.min(iEndEw,(iEw+iInc));
	      if ( iEw == iMatchToEw )
	        break;
	    }
	  }
	  else if ( iNextEw > iMatchToEw + iInc/2 ) {
	    iEw = iMatchToEw;
	  }
	  else if ( iNextEw > iMatchToEw + iInc/4 ){
	    while ( iEw > iStartEw ){
	      iEw = Math.max(iStartEw,(iEw-iInc));
	      if ( iEw == iMatchToEw )
	        break;
	    }
	  }
	  return iEw;
	}


	public static double getLeftMost(
	  double dCentLng,
	  double dLeftLng,
	  double dLng
	)
	{
	  if ( dCentLng < 0 ){
	    if ( dLeftLng < 0 ){
	      if ( dLng < dLeftLng || dLng > 0 ) 
	        dLeftLng = dLng;
	    }
	    else {
	      if ( dLng < dLeftLng )
	        dLeftLng = dLng;
	    }
	  }
	  else {
	    if ( Math.abs( dLeftLng-dCentLng ) < Math.abs( dLng-dCentLng ) )
	      dLeftLng = dLng; 
	  }
	  return dLeftLng;
	}

	public static double getRightMost(
	  double dCentLng,
	  double dRightLng,
	  double dLng
	)
	{
	  if ( dCentLng > 0 ){
	    if ( dRightLng > 0 ){
	      if ( dLng > dRightLng || dLng < 0 ) 
	        dRightLng = dLng;
	    }
	    else {
	      if ( dLng < dRightLng )
	        dRightLng = dLng;
	    }
	  }
	  else {
	    if (Math.abs( dRightLng-dCentLng ) < Math.abs( dLng-dCentLng ) )
	      dRightLng = dLng; 
	  }
	  return dRightLng;
	}

	// Computes the longitudes furthest from the center
	// but still on the map.
	/*
	public static void furthestLng(
	  IProjection 	 pProj,
	  int 			 tileSize,
	  int 			 levelOfDetail,
	  PixBoundingBox pixBox,
	  double[]   	 lng
	)
	{
	  double dLng;
	  double dLeftLng, dCentLng, dRightLng;
	  
	  int x = pixBox.getCenterX();
	  int y = pixBox.getCenterY();
	  pProj.initUsingSize_Level(tileSize, levelOfDetail);
	  // Get Center lng.
	  Point fgp = pProj.xyPixelToLatLng(x, y);
	  dCentLng = fgp.getLng();
	  // Get left lng.
	  x = pixBox.getLeftX();
	  y = pixBox.getCenterY();
	  fgp = pProj.xyPixelToLatLng(x, y);
	  dLeftLng = fgp.getLng();
	  
	  y = pixBox.getTopY();
	  fgp = pProj.xyPixelToLatLng(x, y);
	  dLng = fgp.getLng();
	  
	  dLeftLng = getLeftMost(dCentLng,dLeftLng,dLng);
	  y = pixBox.getBottomY();
	  fgp = pProj.xyPixelToLatLng(x, y);
	  dLng = fgp.getLng();
	  dLeftLng = getLeftMost(dCentLng,dLeftLng,dLng);
	  
	  // Get right lng.
	  x = pixBox.getRightX();
	  y = pixBox.getCenterY();
	  fgp = pProj.xyPixelToLatLng(x, y);
	  dRightLng = fgp.getLng();
	  
	  y = pixBox.getTopY();
	  fgp = pProj.xyPixelToLatLng(x, y);
	  dLng = fgp.getLng();
	  
	  dRightLng = getRightMost(dCentLng,dRightLng,dLng);
	  
	  y = pixBox.getBottomY();
	  fgp = pProj.xyPixelToLatLng(x, y);
	  dLng = fgp.getLng();  
	  dRightLng = getRightMost(dCentLng,dRightLng,dLng);
	  /////
	  lng[0] = dLeftLng;
	  lng[1] = dRightLng;
	}
	*/
	// Computes the latitude furthest from the center
	// but still on the map.
	public static void furthestLat(
		IProjection 	pProj,
		int 			levelOfDetail,
		PixBoundingBox  pixBox,
		double[]   		lat
	)
	{
	  double dLat;
	  double dBotLat, dCentLat, dTopLat;

	  int x = pixBox.getCenterX();
	  int y = pixBox.getCenterY();
	  // Get Center lat.
	  Point fgp = pProj.xyPixelToLatLng(x, y);
	  dCentLat = fgp.getLat();

	  // Get Bottom lat.
	  x = pixBox.getLeftX();
	  y = pixBox.getBottomY();
	  fgp = pProj.xyPixelToLatLng(x, y);
	  dBotLat = fgp.getLat();
	  
	  x = pixBox.getCenterX();
	  fgp = pProj.xyPixelToLatLng(x, y);
	  dLat = fgp.getLat();
	  
	  if( Math.abs( dBotLat - dCentLat ) < Math.abs( dLat - dCentLat ) )
		  dBotLat = dLat;
	  
	  // Get Top lat.
	  x = pixBox.getLeftX();
	  y = pixBox.getTopY();
	  fgp = pProj.xyPixelToLatLng(x, y);
	  dTopLat = fgp.getLat();
	  x = pixBox.getCenterX();
	  fgp = pProj.xyPixelToLatLng(x, y);
	  dLat = fgp.getLat();
	  if ( Math.abs(dTopLat-dCentLat) < Math.abs(dLat - dCentLat) )
		    dTopLat = dLat;
	  x = pixBox.getRightX();
	  fgp = pProj.xyPixelToLatLng(x, y);
	  dLat = fgp.getLat();	  
	  if ( Math.abs(dTopLat-dCentLat) < Math.abs(dLat - dCentLat) )
	    dTopLat = dLat;	  
	  lat[0] = dTopLat;
	  lat[1] = dBotLat;
	}

	// Given a zone and a row this returns the zone to the right.
	public static int nextZone( int iZone, int iRow )
	{
	  if ( 19 == iRow && ( 31 == iZone || 33 == iZone || 35 == iZone ) ){
	    iZone += 2;
	  }
	  else{
	    iZone++;
	    if ( iZone > 60 )
	      iZone -= 60;
	  }
	  return iZone;
	}

	// Given a zone and a row this returns the zone to the left.
	public static int prevZone( int iZone, int iRow )
	{
	  if ( 19 == iRow && ( 33 == iZone || 35 == iZone ) ){
	    iZone -= 2;
	  }
	  else{
	    iZone--;
	    if ( iZone < 1 )
	      iZone = 60;
	  }
	  return iZone;
	}

	// Given a zone and a row this returns the zone to the right.
	public static int nextZone( int iZone, char cNs )
	{
	  if ( 'X' == cNs && ( 31 == iZone || 33 == iZone || 35 == iZone ) ){
	    iZone += 2;
	  }
	  else{
	    iZone++;
	    if ( iZone > 60 )
	      iZone = 1;
	  }
	  return iZone;
	}

	// Given a zone and a row this returns the zone to the left.
	public static int prevZone( int iZone, char cNs ){
	  if ( 'X' == cNs && ( 33 == iZone || 35 == iZone ) ){
	    iZone -= 2;
	  }
	  else{
	    iZone--;
	    if ( iZone < 1 )
	      iZone = 60;
	  }
	  return iZone;
	}


	public static UtmLocSize zoneCharToCell( int iZone, char desig )
	{
	  int[] zoneSpecialLeftLng  = { 9, 0, 21, 0, 33 };
	  int[] zoneSpecialWidth    = { 12, 0, 12, 0, 9 };

	  UtmLocSize zone = new UtmLocSize();
	  zone.dLeftLng  = 0.0;
	  zone.dLowerLat = 0.0;
	  zone.iWidth    = 0;
	  zone.iHeight   = 0;
	  zone.dOriginLng = -177 + 6 * (iZone - 1);
	  if ( 'X' == desig ) {
	    zone.dLowerLat = 72.0;
	    zone.iHeight   = 12;
	    zone.iWidth    = 6;
	    if ( iZone == 31 ) zone.iWidth = 9;
	    if ( iZone < 32 ){
	      zone.dLeftLng = -180.0 + (iZone-1)*6.0;
	    }
	    else if ( iZone < 38 ){
	      if ( iZone != 32 && iZone != 34 && iZone != 36 ){
	        zone.dLeftLng = zoneSpecialLeftLng[iZone - 33];
	        zone.iWidth   = zoneSpecialWidth[iZone - 33];
	      }
	      else {
	        zone.iWidth = 0;
	      }
	    }
	    else {
	      zone.dLeftLng = 42 + (iZone-38)*6.0;
	    }
	  }
	  else {
		int C = Character.getNumericValue('C');
		int I = Character.getNumericValue('I');
		int O = Character.getNumericValue('O');
		int nDesig = Character.getNumericValue(desig);
	    int n = ( Character.getNumericValue(desig) - C);
	    if ( nDesig > I ){
	      n--; // We don't want to include the letter I
	      if ( nDesig > O )
	        n--; // We don't want to include the letter O
	    }
	    zone.dLowerLat = -80 + n*8.0;
	    zone.dLeftLng  = -180.0 + (iZone-1)*6.0;
	    zone.iHeight   = 8;
	    zone.iWidth    = 6;
	    if ( desig == 'V' ){
	      if ( iZone == 31 ) {
	        zone.iWidth = 3;
	      }
	      else if ( iZone == 32 ){
	        zone.dLeftLng = 3.0;
	        zone.iWidth   = 9;
	      }
	    }
	  }
	  return zone;
	}

	public static boolean getCellBotNorthing(
	  Madtran cm, 
	  UtmLocSize cell, 
	  int[] piBotNs
	)
	{
	  boolean bRet = false;

	  if ( cell.iHeight == 0 || cell.iWidth == 0 )
	    return false;

	  UtmPoint pt = cm.gp2Utm( cell.dLowerLat, cell.dOriginLng );
	  if (  pt != null ){
	    piBotNs[0] = (int)(pt.iNorthing + 0.5);
	    bRet = true;
	  }
	  return bRet;
	}

	public static boolean getCellTopNorthing( 
			Madtran cm, 
			UtmLocSize cell,  
			int[] piTopNs
	)
	{
	  boolean bRet = false;

	  if ( cell.iHeight == 0 || cell.iWidth == 0 )
	    return false;

	  double dLat = (cell.dLowerLat + cell.iHeight);
	  double dLng = (cell.dOriginLng);
	  
	  UtmPoint pt = cm.gp2Utm( dLat, dLng );
	  if ( pt != null ) {
	    piTopNs[0] = (int)(pt.iNorthing + 0.5);
	    bRet = true;
	  }
	  return bRet;
	}

	public static int getZone( double degLat, double degLng ){
		
	  int iZone = (int)( 31.0 + degLng/6.0 );
	  if ( iZone == 61 )
	    iZone = 1;
	  // Need to check for row V and cell 31-32
	  if ( 56 < degLat && degLat <= 64 ){ 
	    // This is row V 
	    if ( 3 < degLng && iZone == 31)
	      iZone = 32;
	  }
	  // Next we need to check for row X and cells 31-37.
	  else if ( 72 < degLat ){
	    if ( iZone == 32 ){
	      if ( degLng < 9 )
	        iZone = 31;
	      else
	        iZone = 33;
	    }
	    else if ( iZone == 34 ){
	      if( degLng < 21 )
	        iZone = 33;
	      else
	        iZone = 35;
	    }
	    else if ( iZone == 36 ){
	      if ( degLng < 33 )
	        iZone = 35;
	      else
	        iZone = 37;
	    }
	  }
	  if ( iZone < 0 )
	    iZone += 60;
	  return iZone;
	}

	public static int getClosestZone( double degLat, double degLng )
	{
	  char[] ALBET = {'C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X'};
	  int iZone = getZone(degLat,degLng);
	  char cNs = ALBET[getCellRow(degLat)];
	  UtmLocSize ls = zoneCharToCell( iZone, cNs );

	  if ( Math.abs(degLng-ls.dLeftLng) > Math.abs(degLng-(ls.dLeftLng+ls.iWidth)) )
	    iZone = nextZone(iZone, cNs);

	  return iZone;
	}

	public static int getCellRow( double degLat )
	{
	  int iRow = 19;
	  iRow = (int)( Math.abs(degLat+80.0)/8.0);
	  //iRow = int( Abs(degLat+80.0)/8.0 + 0.5 );
	  return Math.min(iRow,19);
	}

	public static int getClosestRow ( double degLat )
	{
	  int iRow;
	  if ( degLat < 72 ){
	    iRow = (int)( Math.abs(degLat+84.0)/8.0 );
	  }
	  else if ( degLat < 84 ){
	    iRow = (int)( Math.abs(degLat+86.0)/8.0 );
	  }
	  else {
	    iRow = 20;
	  }
	  return iRow;
	}

	public static double lamSpace(
	  double dLam_1, 
	  double dLam_2
	)
	{
	  double dLam = Math.abs(dLam_2 - dLam_1);
	  return ( dLam <= M_PI ? dLam : 2*M_PI - dLam );
	}

	public static double WrapLam(double dLam )
	{
	  if ( dLam > M_PI )
	    return dLam - 2*M_PI;
	  if ( dLam < -M_PI )
	    return 2*M_PI + dLam;
	  return dLam;
	}
	// Utm grid methods
	public static double rowToLat( int row )
	{
	  row = Math.max ( row, 0 );
	  if ( row < 20 )
	    return (-80 + row*8);
	  return 72;//84;
	}
	
	public static double rowToPhi( int row )
	{
	  return DegToRad*rowToLat(row);
	}

	public static double zoneToLng( int iRow, int iZone )
	{
	  double dLng;
	  
	  iZone  = correctZone(iZone);

	  if ( iRow < 19 ){
	    dLng = ( ( iZone == 32 && iRow == 17 ) ? 3 : (iZone-1)*6  - 180 );
	  }
	  else{
	    if ( iZone < 31 )
	      dLng =  (iZone-1)*6  - 180;
	    else if ( iZone < 32 )
	      dLng = 0;
	    else if ( iZone < 34 )
	      dLng = 9;
	    else if ( iZone < 36 )
	      dLng = 21;
	    else if ( iZone < 38 )
	      dLng = 33;
	    else
	      dLng = (iZone-1)*6 - 180;
	  }
	  return dLng;
	}

	public static double zoneToLng( double dLat, int iZone )
	{
	  double dLng;
	  
	  iZone  = correctZone(iZone);

	  if ( dLat <= 72 ){
	    dLng = (iZone-1)*6  - 180;
	  }
	  else{
	    if ( iZone < 31 )
	      dLng =  (iZone-1)*6  - 180;
	    else if ( iZone < 32 )
	      dLng = 0;
	    else if ( iZone < 34 )
	      dLng = 9;
	    else if ( iZone < 36 )
	      dLng = 21;
	    else if ( iZone < 38 )
	      dLng = 33;
	    else
	      dLng = (iZone-1)*6 - 180;
	  }
	  return dLng;
	}

	public static double zoneToLam( double dPhi, int iZone )
	{
	  return DegToRad*zoneToLng(RadToDeg*dPhi,iZone);
	}
	
	public static int correctZone(int iZone)
	{
		
	  iZone = ( iZone > 60 ? iZone - 60 : iZone );
	  iZone = ( iZone < 1 ? 60 + iZone : iZone );

		return iZone;
	}
	
	public static int GetNumRows(
		UtmCell tlCell,
		UtmCell trCell,
		UtmCell brCell,
		UtmCell blCell
	)
	{
		int iLeftNum  = tlCell.m_iRow - blCell.m_iRow;
		int iRightNum = trCell.m_iRow - brCell.m_iRow;
		return Math.max(iLeftNum,iRightNum) + 1;
	}
	
	public static int ComptuteNumSidePts( int cellJump )
	{
	  return ( (cellJump-1)*5 );
	}
	
	public static char getHemChar( EHemisphere eHem ){
		return ( eHem == EHemisphere.eNorth ? 'N' : 'S' );
	}
	
    public static void drawPolyline( Graphics2D g, R2[] pts, int numPts){
    	for ( int i = 0; i < numPts-1; i++ ){
    		g.drawLine(pts[i].m_x, pts[i].m_y, pts[i+1].m_x, pts[i+1].m_y);
    	}
    }
}
