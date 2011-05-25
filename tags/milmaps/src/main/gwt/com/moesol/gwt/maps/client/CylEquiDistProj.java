package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.units.AngleUnit;


/**
 * Handles the conversion of geodetic coordinates to world coordinates. The
 * world is an image of the earth with pixel 0,0 at -180,-90. If the zoom factor
 * goes negative fileTile will return a tile whose width and height have been
 * scaled.
 * 
 * @author Moebius Solutions, Inc.
 */
public class CylEquiDistProj extends AbstractProjection {

	public CylEquiDistProj( int pixWidth, double degWidth, double degHeight ) {
		super();
		this.initialize(pixWidth, degWidth, degHeight);
		m_minLat = -90.0;
		m_maxLat = 90.0;
	}

	@Override
	public int lngDegToPixX( double deg ){
		deg = clip(deg,m_minLng,m_maxLng);
		double x = (deg + 180)/360;
		return (int)((x * mapSize())+0.5);
		//return (int)( (m_scale * (( 180 + deg) * MeterPerDeg) / m_scrnMpp) + 0.5 );
	}
	
	@Override
	public double xPixToDegLng( double pix  )
	{
        double mapSize = mapSize();
        double x = (pix / mapSize) - 0.5;   
        return (x*360);                 
	} 
	
	@Override
	public int latDegToPixY( double deg ){
		deg = clip(deg,m_minLat,m_maxLat);
		double y = (deg + 90)/180;
		double mapHeightSize = mapSize()/2.0;
		return (int)((y * mapHeightSize)+0.5);
	}
	
	@Override
	public double yPixToDegLat( double pix  )
	{
		double mapSize =  mapSize()/2.0;
        double x = (pix / mapSize) - 0.5;   
        return (x*180);               
	} 
	
	public CylEquiDistProj(IProjection orig) {
		copyFrom(orig);
	}

	@Override
	public WorldDimension getWorldDimension() {
		int wdX = (int) (m_scale * (360 * MeterPerDeg) / m_scrnMpp + 0.5);
		int wdY = (int) (m_scale * (180 * MeterPerDeg) / m_scrnMpp + 0.5);

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
		double lng = xPixToDegLng(w.getX() % m_wdSize.getWidth());
		if (w.getX() > 10 && lng == m_minLng)
			lng = m_maxLng;
		double lat = yPixToDegLat(w.getY());
		m_returnedGeodeticCoords.set(lng, lat, AngleUnit.DEGREES);
		return m_returnedGeodeticCoords;
	}
	

	@Override
	public WorldCoords geodeticToWorld(GeodeticCoords g) {
		m_returnedWorldCoords
				.setX(lngDegToPixX(g.getLambda(AngleUnit.DEGREES)));
		m_returnedWorldCoords
				.setY(latDegToPixY(g.getPhi(AngleUnit.DEGREES)));
		return m_returnedWorldCoords;
	}

	protected double computeScale(double deg, int pix) {
		double mpp = deg*(MeterPerDeg / pix);
		return (m_scrnMpp / mpp);
	}
	
    
	@Override
    public int getNumXtiles(double tileDegWidth){
    	return (int)(360.0 / tileDegWidth);
    }
	
	@Override
    public int getNumYtiles(double tileDegWidth){
    	return getNumXtiles(tileDegWidth)/2;
    }
	
	@Override
	public TileXY geoPosToTileXY( int level, GeodeticCoords g  )
	{
		double degCellWidth = m_origTileDegWidth/Math.pow(2.0,level);
		double degCellHeight = m_origTileDegHeight/Math.pow(2.0,level);
		double degMag = Math.abs(g.getLambda(AngleUnit.DEGREES)+180);
		m_tile.m_x = (int)(degMag/degCellWidth);
		degMag = Math.abs(g.getPhi(AngleUnit.DEGREES)+90);
		m_tile.m_y = (int)(degMag/degCellHeight);
		return m_tile;
	}

	protected double orig_yPixToDegLat( int level, double pix ){
        double mapSize = m_orgTilePixSize << (level);
        double y = (pix / mapSize); 
		double dLat = y*180.0;
		return ( -90 + dLat);
	}
	
	protected double orig_xPixToDegLng( int level, double pix ){
        double mapSize = m_orgTilePixSize << (level+1);
        double x = (pix / mapSize) - 0.5;   
        return (x*360);		
	}	
    
    @Override
    public WorldCoords tileXYToTopLeftXY(  int level, TileXY tile  ){
    	int topLeftX = tile.m_x*m_orgTilePixSize;
    	int topLeftY = (tile.m_y+1)*m_orgTilePixSize;
    	double lat = orig_yPixToDegLat( level, topLeftY);
    	double lng = orig_xPixToDegLng( level, topLeftX);
    	m_tileGeoPos.set(lng, lat, AngleUnit.DEGREES);
    	return geodeticToWorld(m_tileGeoPos);
    }
	
    @Override
	public int adjustSize( int level, int size){    	
    	double mapSize =  (m_orgTilePixSize << (level+1));
    	double scaledMapSize =  (m_scale * EarthCirMeters / m_scrnMpp );
    	double factor = scaledMapSize/mapSize;
    	return (int)(factor*size);
    }
}

