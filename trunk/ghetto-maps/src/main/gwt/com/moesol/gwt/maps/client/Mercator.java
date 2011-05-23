package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.units.AngleUnit;

public class Mercator extends AbstractProjection {	
	private static double DEG_ERROR = 10000;
	// Radius of earth in meters.
	public static double EarthRadius = NMtoMeters*(21600/(2*M_PI));

	
	public Mercator() {
		super();
		m_minLat = -85.05113;
		m_maxLat = 85.05113;
	}
	
	public Mercator(int dpi) {
		super(dpi);
		m_minLat = -80.0;
		m_maxLat = 80.0;
	}

	@Override
	public double pixToLngDegMag(int pix) {
		double dR = EarthRadius*m_scale;//*cos(m_dPhi);
		double meters = pix*m_scrnMpp;
		double dLam = wcxToGeoLam( meters, 0.0, dR );
		return RadToDeg*dLam;
	}

	@Override
	public int lngDegToPixMag( double deg ) {
		double lam = DegToRad*deg;
		double dR = EarthRadius*m_scale;
		double dX = geoLamToWcX( lam, 0.0, dR );
		dX = dX/m_scrnMpp;
		return (int)( dX + 0.5 );
	}
	
	@Override
	public double pixToLatDegMag( int centY, int pix ) {
		double dR = EarthRadius*m_scale;//*cos(m_dPhi);
		double meters = pix*m_scrnMpp;
		double dPhi = wcyToGeoPhi(meters, dR);
		return RadToDeg*dPhi;
	}

	@Override
	public int latDegToPixMag(double centLat, double deg) {
		double botLat = centLat - deg/2;
		double topLat = centLat + deg/2;
		double dY = verticalDist(botLat,topLat);
		dY = dY/m_scrnMpp;
		return (int)( dY + 0.5 );
	}


	@Override
	public WorldDimension getWorldDimension() {
		double width  = 4.0*lngDegToPixMag(90);
		double height = verticalDist( m_minLat, m_maxLat);
		int wdX = (int) (width / m_scrnMpp + 0.5);
		int wdY = (int) (height / m_scrnMpp + 0.5);

		m_wdSize.setWidth(wdX);
		m_wdSize.setHeight(wdY);
		return m_wdSize;
	}
	
	@Override
	public void setViewSize(ViewDimension vp) {
		m_vpSize.copyFrom(vp);
		m_wholeWorldScale = Math.max( computeScale(2*m_maxLng, m_vpSize.getWidth()),
				  					  computeScale(2*m_maxLat, m_vpSize.getHeight()) );
	}
	
	@Override
	public GeodeticCoords worldToGeodetic(WorldCoords w) {
		// we have to mod by world size in case we had
		// to extend the whole world
		double lng = pixToLngDegMag(w.getX() % m_wdSize.getWidth()) - 180.0;
		if (w.getX() > 10 && lng == m_minLng)
			lng = m_maxLng;
		double lat = pixToLatDegMag(0,w.getY()-m_wdSize.getHeight()/2);
		m_returnedGeodeticCoords.set(lng, lat, AngleUnit.DEGREES);
		return m_returnedGeodeticCoords;
	}
	

	@Override
	public WorldCoords geodeticToWorld(GeodeticCoords g) {
		double deg = g.getLambda(AngleUnit.DEGREES);
		double width = horizontalDist(m_minLng,deg);
		width = width/m_scrnMpp;
		m_returnedWorldCoords.setX((int)(width+0.5));

		deg = g.getPhi(AngleUnit.DEGREES);
		double height= verticalDist(m_minLat,deg);
		height = height/m_scrnMpp;
		m_returnedWorldCoords.setY((int)(height+0.5));
		return m_returnedWorldCoords;
	}
	
	protected double computeScale(double deg, int pix) {
		double mpp = deg*(111120.0 / pix);
		return (m_scrnMpp / mpp);
	}

	/**
	 * Converts Phi in radians to the world coordinate y value
	 * @param dPhi : Geodetic Coordinate
	 * @param dR : Spherical Radius
	 * @return a double (the y value).
	 */
	protected double geoPhiToWcY(
	  double dPhi,
	  double dR
	)
	{
	  if ( Math.abs(dPhi) > M_PI_2 ){ 
			return -1;
	  }
	  double pY;
	  if ( 0.0 == dPhi ){
	    pY = 0.0;
	  }
	  else {
	    pY = dR * Math.log( Math.tan( M_PI_4 + dPhi/2.0 ) );
	  }
	  return pY;
	}
	/**
	 * Converts Lamda in radians to the world coordinate X value
	 * @param dLam : Geodetic Coordinate
	 * @param dLambda : Central Meridian
	 * @param dR : Spherical Radius
	 * @return a double (the x value in meters).
	 */
	protected double geoLamToWcX( double dLam, double dLambda, double dR )
	{
		return dR * (dLam - dLambda); 
	}
	
	/**
	 * Convert the x value of a world coordinate into lambda
	 * @param dX : World coordinate x value
	 * @param dLambda : Central Meridian
	 * @param dR : Spherical Radius
	 * @return a double (lambda)
	 */
	protected double wcxToGeoLam( double dX, double dLambda, double dR )
	{
		return dX / dR + dLambda;                     
	}

	/**
	 * Convert the y component of a world coordinate into a latitude in radians
	 * @param dY : word coordinate y value
	 * @param dR : Spherical Radius
	 * @return a double, the y value. If an error occurs it returns DEG_ERROR
	 */
	protected double  wcyToGeoPhi( double  dY, double  dR )
	{
	    double dPhi = M_PI_2 - 2.0 * Math.atan( Math.exp( -dY/dR ) );  

	    if ( Math.abs(dPhi) >  M_PI_2 ) {
	        return DEG_ERROR;
	    }
	    return dPhi;
	}
	
	/**
	 * Computes the horizontal distance between two longitudes.
	 * @param phi1 is the first latitude
	 * @param phi2 is the second latitude.
	 * @return
	 */
	protected double horizontalDist( double lng1, double lng2 ){
		double dR   = EarthRadius*m_scale;
		double x1 = geoLamToWcX( DegToRad*lng1,0.0, dR );
		double x2 = geoLamToWcX( DegToRad*lng2,0.0, dR );
		return (Math.abs(x1 - x2));
	}
	
	/**
	 * Computes the vertical distance between two latitudes.
	 * @param phi1 is the first latitude
	 * @param phi2 is the second latitude.
	 * @return
	 */
	protected double verticalDist( double lat1, double lat2 ){
		double dR   = EarthRadius*m_scale;
		double y1 = geoPhiToWcY( DegToRad*lat1, dR );
		double y2 = geoPhiToWcY( DegToRad*lat2, dR );
		return (Math.abs(y1 - y2));
	}

}
