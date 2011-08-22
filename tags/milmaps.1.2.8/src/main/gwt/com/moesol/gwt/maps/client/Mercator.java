package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.units.AngleUnit;

// EPSG:3857 Mercator projection

public class Mercator extends AbstractProjection {	
	private static double DEG_ERROR = 10000;

	
	public Mercator( int pixWidth, double degWidth, double degHeight) {
		super();
		this.initialize(pixWidth, degWidth, degHeight);
		m_minLat = -85.05113;
		m_maxLat = 85.05113;
	}
    

	@Override
	public WorldDimension getWorldDimension() {
		int wdX  = (int)(mapSize()+0.5);
		double height = verticalDist( m_minLat, m_maxLat);
		int wdY = (int) (height+ 0.5);

		m_wdSize.setWidth(wdX);
		m_wdSize.setHeight(wdY);
		return m_wdSize;
	}
	
	@Override
	public void setViewSize(ViewDimension vp) {
		m_vpSize.copyFrom(vp);
	}
	
	@Override
	public GeodeticCoords worldToGeodetic(WorldCoords w) {
		// we have to mod by world size in case we had
		// to extend the whole world
		double pix = w.getX() % m_wdSize.getWidth();
		double lng = xPixToDegLng(pix);
		if (w.getX() > 10 && lng == m_minLng)
			lng = m_maxLng;
		double lat = yPixToDegLat( w.getY() );
		m_returnedGeodeticCoords.set(lng, lat, AngleUnit.DEGREES);
		return m_returnedGeodeticCoords;
	}
	

	@Override
	public WorldCoords geodeticToWorld(GeodeticCoords g) {
		double deg = g.getLambda(AngleUnit.DEGREES);
		m_returnedWorldCoords.setX((int)(lngDegToPixX(deg)+0.5));

		deg = g.getPhi(AngleUnit.DEGREES);
		m_returnedWorldCoords.setY((int)(latDegToPixY(deg)+0.5));
		return m_returnedWorldCoords;
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
		dLat = clip(dLat, m_minLat, m_maxLat);
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
		dLng = clip(dLng, m_minLng, m_maxLng);
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
        double y = 0.5 - (clip(dY, 0, mapSize -1 ) / mapSize);
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
		double dR   = EarthRadius*m_scale;
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
		double y1 = lat2PixY( lat1 );
		double y2 = lat2PixY( lat2 );
		return Math.abs(y2 - y1);
	}

	// This is based on the microsoft version.
	// This routine is based on 
	@Override
	public TileXY geoPosToTileXY( int level, GeodeticCoords g ){
        double lat = clip(g.getPhi(AngleUnit.DEGREES),   m_minLat, m_maxLat);
        double lng = clip(g.getLambda(AngleUnit.DEGREES), -180.0, 180.0);

        double x = (lng + 180) / 360; 
        double sinLat = Math.sin(lat * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI);

        int mapSize = (int) ( m_orgTilePixSize << level );
        double pixX = clip(x * mapSize + 0.5, 0, mapSize);
        double pixY = clip(y * mapSize + 0.5, 0, mapSize);
		m_tile.m_x =  (int)Math.floor(pixX/ m_orgTilePixSize);
		m_tile.m_y =  (int)Math.floor(pixY/m_orgTilePixSize);
		// translate so it is based on row zero at the bottom of the map
		int j = (1<<level) - 1;
		m_tile.m_y = j - m_tile.m_y;
		return m_tile;
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
    protected  double groundResolution( double lat, int levelOfDetail )
    {
        lat = clip(lat, m_minLat, m_maxLat);
        int mapSize = (int) (m_orgTilePixSize << levelOfDetail);
        //return Math.cos(lat * Math.PI / 180) * EarthCirMeters / mapSize;
        return (EarthCirMeters/mapSize);
    }
    
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
  
    
	protected double orig_yPixToDegLat( int level, double pix ){
		// First we need to translate the pixel since the y direction is reversed
		double mapSize = m_orgTilePixSize << level;
		pix = mapSize - pix;
        double y = 0.5 - (clip(pix, 0, mapSize -1 ) / mapSize);
        double lat = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        return lat;
	}
	
	protected double orig_xPixToDegLng( int level, double pix ){
        double mapSize = m_orgTilePixSize << level;
        double x = (pix / mapSize) - 0.5;   
        return (x*360);		
	}
    
    @Override
    public WorldCoords tileXYToTopLeftXY( int level, TileXY tile  ){
    	int topLeftX = tile.m_x*m_orgTilePixSize;
    	int topLeftY = (tile.m_y+1)*m_orgTilePixSize;
    	double lat = orig_yPixToDegLat( level, topLeftY);
    	double lng = orig_xPixToDegLng( level, topLeftX);
    	m_tileGeoPos.set(lng, lat, AngleUnit.DEGREES);
    	return geodeticToWorld(m_tileGeoPos);
    }
    
    @Override
	public int adjustSize( int level, int size){    	
    	double mapSize =  (m_orgTilePixSize << level);
    	double scaledMapSize =  (m_scale * EarthCirMeters / m_scrnMpp );
    	double factor = scaledMapSize/mapSize;
    	return (int)(factor*size);
    }
}
