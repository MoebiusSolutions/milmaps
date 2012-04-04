package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.stats.Stats;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;
import com.moesol.gwt.maps.shared.BoundingBox;


/**
 * Handles the conversion of geodetic coordinates to world coordinates. The
 * world is an image of the earth with pixel 0,0 at -180,-90. If the zoom factor
 * goes negative fileTile will return a tile whose width and height have been
 * scaled.
 * 
 * @author Moebius Solutions, Inc.
 */

public class CylEquiDistProj extends AbstractProjection {
	private static final BoundingBox BOUNDS 
		= BoundingBox.builder().bottom(-90).top(90).left(-180).right(180).degrees().build();

	public CylEquiDistProj( ){
		super();
		m_projType = IProjection.T.CylEquiDist;
		this.initialize(512, 180, 180);	
	}
	
	public CylEquiDistProj( int pixWidth, double degWidth, double degHeight ) {
		super();
		m_projType = IProjection.T.CylEquiDist;
		this.initialize(pixWidth, degWidth, degHeight);
	}
	
	@Override
	public boolean doesSupport( String srs ){
		if ( srs.equals("EPSG:4326")){
			return true;
		}
		return false;
	}

	@Override
	public IProjection cloneProj(){
		IProjection proj = new CylEquiDistProj();
		proj.copyFrom(this);
		return proj;
	}

	protected double lngDegToMcX( double deg ){
		deg = clip(deg, getDegreeBoundingBox().left(), getDegreeBoundingBox().right());
		double x = (deg/360) + 0.5;
		double mapSize = mapSize();
		// add 0.5 for roundoff error
		return (x*mapSize);	
	}

	@Override
	public int lngDegToPixX( double deg ){
		return (int)(lngDegToMcX(deg)+0.5);
	}

	@Override
	public double xPixToDegLng( double pix  )
	{
        double mapSize = mapSize();
        double x = (pix / mapSize) - 0.5;   
        return (x*360);                 
	} 
	
	protected double latDegToMcY( double deg ){
		deg = clip(deg, getDegreeBoundingBox().bottom(), getDegreeBoundingBox().top());
		double y = (deg + 90)/180;
		double mapHeightSize = mapSize()/2.0;
		return (y * mapHeightSize);		
	}
	
	@Override
	public int latDegToPixY( double deg ){
		return (int)(latDegToMcY( deg )+0.5);
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
		int wdX = (int) (m_eqScale * (360 * MeterPerDeg) / m_scrnMpp + 0.5);
		int wdY = (int) (m_eqScale * (180 * MeterPerDeg) / m_scrnMpp + 0.5);

		m_wdSize.setWidth(wdX);
		m_wdSize.setHeight(wdY);
		return m_wdSize;
	}
	
	
	@Override
	public GeodeticCoords worldToGeodetic(WorldCoords w) {
		Stats.incrementWorldToGeodetic();
		
		// we have to mod by world size in case we had
		// to extend the whole world
		double lng = xPixToDegLng(w.getX() % m_wdSize.getWidth());
		if (w.getX() > 10 && lng == getDegreeBoundingBox().left()) {
			lng = getDegreeBoundingBox().right();
		}
		lng = wrapLng(lng);
		double lat = yPixToDegLat(w.getY());
		lat = Math.max(Math.min(lat,BOUNDS.getTopLat()),BOUNDS.getBotLat());
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
	
	@Override
	public GeodeticCoords mapCoordsToGeodetic(MapCoords w) {
		// we have to mod by world size in case we had
		// to extend the whole world
		
		double lng = xPixToDegLng(w.getX() % m_wdSize.getWidth());
		if (w.getX() > 10 && lng == getDegreeBoundingBox().left()) {
			lng = getDegreeBoundingBox().right();
		}
		lng = wrapLng(lng);
		double lat = yPixToDegLat(w.getY());
		return Degrees.geodetic(lat, lng);
	}
	

	@Override
	public MapCoords geodeticToMapCoords(GeodeticCoords g) {
		return MapCoords.builder()
			.setX(lngDegToMcX(g.getLambda(AngleUnit.DEGREES)))
			.setY(latDegToMcY(g.getPhi(AngleUnit.DEGREES))).build();
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
    public int getNumYtiles(double tileDegHeight){
    	return (int)(180.0/tileDegHeight);
    }

	@Override
	public BoundingBox getDegreeBoundingBox() {
		return BOUNDS;
	}

}
