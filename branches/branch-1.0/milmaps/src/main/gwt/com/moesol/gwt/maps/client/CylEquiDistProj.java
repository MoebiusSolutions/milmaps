package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.stats.Stats;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;


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
	public boolean doesSupport( int espg ){
		switch ( espg ){
		case 4326:
			return true;
		}
		return false;
	}
	
	@Override
	public IProjection cloneProj(){
		IProjection proj = new CylEquiDistProj( m_orgTilePixSize,
												m_origTileDegWidth, 
												m_origTileDegHeight );
		proj.setViewGeoCenter(this.getViewGeoCenter());
		return proj;
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
		int result = (int)((y * mapHeightSize)+0.5); 
		return result;
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
		Stats.incrementWorldToGeodetic();
		
		// we have to mod by world size in case we had
		// to extend the whole world
//		double lng = xPixToDegLng(w.getX() % m_wdSize.getWidth());
//		if (w.getX() > 10 && lng == m_minLng)
//			lng = m_maxLng;
//		double lat = yPixToDegLat(w.getY());
//		m_returnedGeodeticCoords.set(lng, lat, AngleUnit.DEGREES);
//		return m_returnedGeodeticCoords;
		
		double lng = xPixToDegLng(w.getX() % m_wdSize.getWidth());
		if (w.getX() > 10 && lng == m_minLng) {
			lng = m_maxLng;
		}
		double lat = yPixToDegLat(w.getY());
		
		return Degrees.geodetic(lat, lng);
	}
	

	@Override
	public WorldCoords geodeticToWorld(GeodeticCoords g) {
		Stats.incrementGeodeticToWorld();
		
		int x = lngDegToPixX(g.getLambda(AngleUnit.DEGREES));
		int y = latDegToPixY(g.getPhi(AngleUnit.DEGREES));
		return new WorldCoords(x, y);
//		m_returnedWorldCoords
//				.setX(lngDegToPixX(g.getLambda(AngleUnit.DEGREES)));
//		m_returnedWorldCoords
//				.setY(latDegToPixY(g.getPhi(AngleUnit.DEGREES)));
//		return m_returnedWorldCoords;
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
    	return Math.max(1,getNumXtiles(tileDegWidth)/2);
    }
	
	@Override
	public TileXY geoPosToTileXY( int level, int pixWidth, 
								  double degW, double degH, GeodeticCoords g  )
	{
		// This computes the tile (x,y) with y = 0 as the bottom tile.
		int tLevel = Math.max(0,level);
		double degCellWidth = degW/Math.pow(2.0,tLevel);
		double degCellHeight = degH/Math.pow(2.0,tLevel);
		double degMag = Math.abs(g.getLambda(AngleUnit.DEGREES)+180);
		m_tile.m_x = (int)(degMag/degCellWidth);
		degMag = Math.abs(g.getPhi(AngleUnit.DEGREES)+90);
		m_tile.m_y = (int)(degMag/degCellHeight);
		return m_tile;
	}

	protected double orig_yPixToDegLat( int level, double pix ){
        double mapSize = (m_origMapWidthSize/2) << level;
        double y = (pix / mapSize); 
		double dLat = y*180.0;
		return ( -90 + dLat);
	}
	
	protected double orig_xPixToDegLng( int level, double pix ){
        double mapSize = m_origMapWidthSize << level;
        double x = (pix / mapSize) - 0.5;   
        return (x*360);		
	}	
    
    @Override
    public WorldCoords tileXYToTopLeftXY(  int level, int pixSize, TileXY tile  ){
    	int topLeftX = tile.m_x*pixSize;
    	int topLeftY = (tile.m_y+1)*pixSize;
    	int tLevel = Math.max(0, level);
    	double lat = orig_yPixToDegLat(tLevel, topLeftY);
    	double lng = orig_xPixToDegLng(tLevel, topLeftX);
    	m_tileGeoPos = Degrees.geodetic(lat, lng);
    	return geodeticToWorld(m_tileGeoPos);
    }
    
    @Override
	public int adjustSize(  int size ){   
    	// scale = origScale*2^z  so z = log(scale/origScale)/log(2)
    	int z = (int)Math.max(0,(( Math.log(m_scale)- Math.log(m_origScale))/Math.log(2)));
    	double mapSize =  m_origMapWidthSize<<z;
    	double scaledMapSize =  mapSize();
    	double factor = scaledMapSize/mapSize;
    	return (int)(factor*size);
    }

	@Override
	public String toString() {
		return "CylEquiDistProj [m_vpGeoCenter=" + m_vpGeoCenter
				+ ", m_wdSize=" + m_wdSize + ", m_vpSize=" + m_vpSize
				+ ", m_returnedViewCoords=" + m_returnedViewCoords
				+ ", m_returnedWorldCoords=" + m_returnedWorldCoords
				+ ", m_returnedGeodeticCoords=" + m_returnedGeodeticCoords
				+ ", m_viewToGeoPos=" + m_viewToGeoPos + ", m_tileGeoPos="
				+ m_tileGeoPos + ", m_pixel=" + m_pixel + ", m_tile=" + m_tile
				+ ", EarthRadius=" + EarthRadius + ", EarthCirMeters="
				+ EarthCirMeters + ", MeterPerDeg=" + MeterPerDeg
				+ ", m_scrnDpi=" + m_scrnDpi + ", m_scrnMpp=" + m_scrnMpp
				+ ", m_scale=" + m_scale + ", m_prevScale=" + m_prevScale
				+ ", m_wholeWorldScale=" + m_wholeWorldScale
				+ ", m_origMapWidthSize=" + m_origMapWidthSize + ", m_minLat="
				+ m_minLat + ", m_minLng=" + m_minLng + ", m_maxLat="
				+ m_maxLat + ", m_maxLng=" + m_maxLng + ", m_origScale="
				+ m_origScale + ", m_orgTilePixSize=" + m_orgTilePixSize
				+ ", m_origTileDegWidth=" + m_origTileDegWidth
				+ ", m_origTileDegHeight=" + m_origTileDegHeight
				+ ", m_zoomFlag=" + m_zoomFlag + "]";
	}
    
    
}

