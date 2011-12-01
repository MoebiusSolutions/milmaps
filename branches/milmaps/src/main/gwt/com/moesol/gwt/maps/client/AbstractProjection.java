package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.units.AngleUnit;

public abstract class AbstractProjection implements IProjection {
	
	protected IProjection.T m_projType;
	// private final GeodeticCoords m_lowerLeft;
	protected final WorldDimension m_wdSize = new WorldDimension(); // Whole world
																  // map size
	protected final ViewCoords m_returnedViewCoords = new ViewCoords();
	protected final WorldCoords m_returnedWorldCoords = new WorldCoords();
	protected final MapCoords m_returnedMapCoords = new MapCoords();
	protected final WorldCoords m_wc = new WorldCoords();
	protected final MapCoords m_mc = new MapCoords();
	protected final GeodeticCoords m_returnedGeodeticCoords = new GeodeticCoords();

	public double EarthRadius = 6378137;
	public double EarthCirMeters  = 2.0*Math.PI*6378137;
	public double MeterPerDeg  = EarthCirMeters/360.0;
	
	protected int m_scrnDpi = 75;   // screen dot per inch
	protected double m_scrnMpp = 2.54/7500.0; // screen meter per pixel
	
	protected double m_scale = 0; // map scale
	protected double m_prevScale;
	protected double m_wholeWorldScale;
	
	protected int m_origMapWidthSize;
	
	double m_minLat = -90.0;
	double m_minLng = -180.0;
	double m_maxLat = 90.0;
	double m_maxLng = 180.0;
	
	protected double m_origScale = 0;
	//protected int 	 m_orgTilePixSize = 0;
	//protected double m_origTileDegHeight = 0;
	
	protected ZoomFlag m_zoomFlag = ZoomFlag.NONE;
	
	public static double RadToDeg = 57.29577951;
	public static double DegToRad = 0.017453293;
	
	public void setType( IProjection.T type ){
		m_projType = type;
	}
	
	public IProjection.T getType(){ return m_projType; }
	
	public AbstractProjection(){
		m_scrnDpi = 75;
		m_scrnMpp = 2.54 / (75.0 * 100); // meters per pixel for physical screen
	}
	
	public AbstractProjection(int dpi) {
		m_scrnDpi = dpi;
		m_scrnMpp = 2.54 / (dpi * 100); // meters per pixel for physical screen
	}
	
    protected double clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }
	
	
	public abstract WorldDimension getWorldDimension();
	
	// Map size in pixels
	public double mapSize(){
		double pixels = (EarthCirMeters*m_scale/m_scrnMpp);
		return pixels;
	}
	
	public int iMapSize(){
		return (int)(mapSize() + 0.5);
	}
	
	@Override
	public void initialize( int tileSize, double degWidth, double degHeight ){
		double earth_mpp = degWidth * (MeterPerDeg / tileSize);
		// meters per pixel for physical screen
		m_scrnMpp = 2.54 / (m_scrnDpi * 100); 
		m_scale = (m_scrnMpp / earth_mpp);
		m_origScale = m_scale;
		m_prevScale = m_scale;	
		//m_origMapWidthSize = (int)( tileSize*(360/degWidth) + 0.5);
		computeWorldSize();
	}
	/*
	@Override
	public void synchronize( int tileSize, double degWidth, double degHeight ){
		// meters per pixel for physical screen
		m_scrnMpp = 2.54 / (m_scrnDpi * 100); 	
		m_origMapWidthSize = (int)( tileSize*(360/degWidth) + 0.5);
		computeWorldSize();
	}
	*/
	@Override
	public void setGeoBounds(double minLat, double minLng, double maxLat, double maxLng ){
		m_minLat = minLat;
		m_minLng = minLng;
		m_maxLat = maxLat;
		m_maxLng = maxLng;
	}
	@Override
	public double getMinLat(){ return m_minLat;}
	@Override
	public double getMaxLat(){ return m_maxLat; }
	@Override
	public double getMinLng(){ return m_minLng; }
	@Override
	public double getMaxLng(){ return m_maxLng; }
	@Override
	public void copyFrom(IProjection p) {
		m_scrnDpi = p.getScrnDpi();
		m_minLat = p.getMinLat();
		m_minLng = p.getMinLng();
		m_maxLat = p.getMaxLat();
		m_maxLng = p.getMaxLng();
		setScale(p.getScale());
	}

	@Override
	public int getScrnDpi() {
		return m_scrnDpi;
	}
	
	@Override
	public double getScale() {
		return m_scale;
	}
	

	@Override
	public void setScale(double dScale) {
		m_prevScale = m_scale;
		m_scale = dScale;
		computeWorldSize();
	}

	@Override
	public double getPrevScale() {
		return m_prevScale;
	}
	

	@Override
	public void setZoomFlag(ZoomFlag flag) {
		m_zoomFlag = flag;
	}

	@Override
	public ZoomFlag getZoomFlag() {
		return m_zoomFlag;
	}
	
	@Override
	public void zoom( double scale) {
		if ( scale <= 0.0 ){
			throw new IllegalArgumentException("scale Factor less than zero");
		}
		m_prevScale = m_scale;
		m_scale = scale;
		if ( scale == m_prevScale ){
			m_zoomFlag = ZoomFlag.NONE;
		}
		else{
			m_zoomFlag = ( scale < m_prevScale ? ZoomFlag.OUT : ZoomFlag.IN );
		}
		computeWorldSize();
	}

	
	@Override
	public void zoomByFactor( double scaleFactor ) {
		if ( scaleFactor <= 0.0 ){
			throw new IllegalArgumentException("scale Factor less than zero");
		}
		zoom(scaleFactor*m_scale);
	}
	

	@Override
	public double wrapLng(double lng) {
		if (lng > 180.0) {
			while (lng > 180.0)
				lng -= 360.0;
		} else if (lng < -180.0) {
			while (lng < -180.0)
				lng += 360.0;
		}
		return lng;
	}
	
	
	protected int modX(int x, int size){
		if ( x < 0 )
			return (size+x);
		if ( x > size )
			return (x - size);
		return x;
	}
	
	protected void computeWorldSize() {
		getWorldDimension();
	}
	
	public WorldDimension getMapSize(){
		return m_wdSize;
	}
	
    
    @Override
	public int compWidthInPixels(double lng1, double lng2){
    	double mapSize = mapSize();
		double x1 = lngDegToPixX( lng1 );
		double x2 = lngDegToPixX( lng2 );
		double  dist = Math.abs(x2 - x1);
		if ( dist > mapSize/2 )
			dist = mapSize - dist;
		return (int)(dist + 0.5 );
    }
	
    @Override
	public int compHeightInPixels(double lat1, double lat2){
		double y1 = latDegToPixY( lat1 );
		double y2 = latDegToPixY( lat2 );
		return (int)(Math.abs(y2 - y1) + 0.5);
    }
    
    @Override
    public double getOrigScale(){ 
    	return m_origScale; 
    }
}
