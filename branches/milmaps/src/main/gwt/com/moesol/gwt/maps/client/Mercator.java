package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.WorldCoords.Builder;
import com.moesol.gwt.maps.client.stats.Stats;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;
import com.moesol.gwt.maps.shared.BoundingBox;

// EPSG:3857 Mercator projection

public class Mercator extends AbstractProjection {	
	private static final BoundingBox BOUNDS 
		= BoundingBox.builder().bottom(-85.05113).top(85.05113).left(-180).right(180).build();

	public Mercator( ){
		super();
		m_projType = IProjection.T.Mercator;
		this.initialize(256, 360.0,  2*85.05113);
	}
	
	public Mercator( int pixWidth, double degWidth, double degHeight) {
		super();
		m_projType = IProjection.T.Mercator;
		this.initialize(pixWidth, degWidth, degHeight);
	}
	
	@Override
	public boolean doesSupport( int espg ){
		switch ( espg ){
		case 900913:
			return true;
		}
		return false;
	}

	@Override
	public IProjection cloneProj(){
		IProjection proj = new Mercator( );
		proj.copyFrom(this);
		return proj;
	}


	@Override
	public WorldDimension getWorldDimension() {
		int wdX  = (int)(mapSize()+0.5);
		double height = verticalDist(getDegreeBoundingBox().bottom(), getDegreeBoundingBox().top());
		int wdY = (int) (height+ 0.5);

		m_wdSize.setWidth(wdX);
		m_wdSize.setHeight(wdY);
		return m_wdSize;
	}
	
	// TODO can this method be pulled up?
	@Override
	public GeodeticCoords worldToGeodetic(WorldCoords w) {
		Stats.incrementWorldToGeodetic();
		
		// we have to mod by world size in case we had
		// to extend the whole world
		double pix = w.getX() % m_wdSize.getWidth();
		double lng = xPixToDegLng(pix);
		if (w.getX() > 10 && lng == getDegreeBoundingBox().left()) {
			lng = getDegreeBoundingBox().right();
		}
		double lat = yPixToDegLat( w.getY() );
		return Degrees.geodetic(lat, lng);
	}
	

	@Override
	public WorldCoords geodeticToWorld(GeodeticCoords g) {
		Stats.incrementGeodeticToWorld();
		
		Builder builder = WorldCoords.builder();
		
		double deg = g.getLambda(AngleUnit.DEGREES);
		builder.setX((int)(lngDegToPixX(deg)+0.5));

		deg = g.getPhi(AngleUnit.DEGREES);
		builder.setY((int)(latDegToPixY(deg)+0.5));
		return builder.build();
	}
	
	@Override
	public GeodeticCoords mapCoordsToGeodetic(MapCoords w) {
		// we have to mod by world size in case we had
		// to extend the whole world
		
		double lng = xPixToDegLng(w.getX() % m_wdSize.getWidth());
		if (w.getX() > 10 && lng == getDegreeBoundingBox().left()) {
			lng = getDegreeBoundingBox().right();
		}
		double lat = yPixToDegLat(w.getY());
		return Degrees.geodetic(lat, lng);
	}
	

	@Override
	public MapCoords geodeticToMapCoords(GeodeticCoords g) {
		return MapCoords.builder()
				.setX(lng2PixX(g.getLambda(AngleUnit.DEGREES)))
				.setY(lat2PixY(g.getPhi(AngleUnit.DEGREES))).build();
	}
	
	//protected double degPixToScale(double deg, int pix) {
	//	double mpp = deg*(111120.0 / pix);
	//	return (m_scrnMpp / mpp);
	//}

	/**
	 * This routine finds the whole world pixel value, based on zero pixel at 
	 * the top of the map
	 * Converts lat in degrees to the world coordinate pixel y value in doubles
	 * @param dPhi : Geodetic Coordinate
	 * @return a double (the y value).
	 */
	protected double lat2PixY( double dLat ){
		dLat = clip(dLat, getDegreeBoundingBox().bottom(), getDegreeBoundingBox().top());
        double sinLat = Math.sin(DegToRad*dLat);
        double y = 0.5 - Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI);
        return (y * mapSize());
	}
        
	/**
	 * Converts Longitude in degs to the pixel coordinate X value
	 * @param dLng : Geodetic longitude Coordinate
	 * @return a double (the x pixel value).
	 */
	protected double lng2PixX( double dLng )
	{
		dLng = clip(dLng, getDegreeBoundingBox().left(), getDegreeBoundingBox().right());
		double x = (dLng + 180)/360;
		return (x * mapSize());
	}
	
	/**
	 * Convert the x value of a world coordinate into longitude
	 * @param dX : World pixel coordinate x value
	 * @return a double (longitude)
	 */
	protected double pixX2Lng( double dX )
	{
        double mapSize = mapSize();
        double x = (dX / mapSize) - 0.5;   
        return (x*360);
	}

	/**
	 * This routine is based on pixel zero at the top of the map.
	 * Convert the y pixel component of a world coordinate into a latitude in degs
	 * @param dY : word coordinate y value
	 * @return a double, the y value. If an error occurs it returns DEG_ERROR
	 */
	protected double  pixY2Lat( double dY )
	{
        double mapSize = mapSize();
        double y = 0.5 - (clip(dY, 0, mapSize ) / mapSize);
        double lat = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        return lat;
	}
	
	@Override
	public int lngDegToPixX( double deg ){
		return (int)(lng2PixX(deg)+0.5);
	}
	
	@Override
	public double xPixToDegLng( double pix ){
		return pixX2Lng(pix);
	}
	
	@Override
	public int latDegToPixY( double deg ){
		// First we find the whole world pixel value based on the 
		// top of the map having y value equal to zero.
		double pixY = lat2PixY(deg);
		// Next we have to translate it for a map with zero y-value
		// at the bottom of the map.
		pixY =  mapSize() - pixY;
		return (int)( pixY+0.5);
	}
	
	/**
	 * This routine converts pixY to a latitude with whole world pixel zero
	 * at the bottom of the map (not the top)
	 */
	@Override
	public double yPixToDegLat( double pix ){
		// First we need to translate the pixel since the y direction is reversed
		double mapSize = mapSize();
		pix = mapSize - pix;
		return pixY2Lat(pix);
	}
	
	/**
	 * Computes the horizontal distance between two longitudes.
	 * @param lng1 is the first longitude
	 * @param lng2 is the second longitude.
	 * @return
	 * 		pixels in double value.
	 */
	protected double horizontalDist( double lng1, double lng2 ){
		double x1 = lng2PixX( lng1 );
		double x2 = lng2PixX( lng2 );
		return Math.abs(x2 - x1);
	}
	
	/**
	 * Computes the vertical distance in pixels between two latitudes.
	 * @param phi1 is the first latitude
	 * @param phi2 is the second latitude.
	 * @return
	 */
	protected double verticalDist( double lat1, double lat2 ){
		double y1 = latDegToPixY( lat1 );
		double y2 = latDegToPixY( lat2 );
		return Math.abs(y2 - y1);
	}

	/// <summary>
    /// Determines the ground resolution (in meters per pixel) at a specified
    /// latitude and level of detail.
    /// </summary>
    /// <param name="latitude">Latitude (in degrees) at which to measure the
    /// ground resolution.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <returns>The ground resolution, in meters per pixel.</returns>
    //protected  double groundResolution( double lat, int levelOfDetail )
    //{
    //    lat = clip(lat, m_minLat, m_maxLat);
    //    int mapSize = (int) (m_orgTilePixSize << levelOfDetail);
    //    //return Math.cos(lat * Math.PI / 180) * EarthCirMeters / mapSize;
    //    return (EarthCirMeters/mapSize);
    //}
    
	@Override
    public int getNumXtiles(double tileDegWidth){
    	//int mapSize = (int)( mapSize() + 0.5);
    	//return Math.max(mapSize/m_orgTilePixSize, 2);
		return (int)(360.0 / tileDegWidth);
    }
    
	@Override
    public int getNumYtiles(double tileDegWidth){
    	return getNumXtiles(tileDegWidth);
    }

	@Override
	public BoundingBox getDegreeBoundingBox() {
		return BOUNDS;
	}

}
