package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.units.AngleUnit;

public abstract class AbstractProjection implements IProjection {
	// private final GeodeticCoords m_lowerLeft;
	protected final GeodeticCoords m_vpGeoCenter = new GeodeticCoords(); // viewport
																		// center
	protected final WorldDimension m_wdSize = new WorldDimension(); // Whole world
																  // map size
	protected final ViewDimension m_vpSize = new ViewDimension(); // viewPort size
	protected final ViewCoords m_returnedViewCoords = new ViewCoords();
	protected final WorldCoords m_returnedWorldCoords = new WorldCoords();
	protected final WorldCoords m_wc = new WorldCoords();
	protected final GeodeticCoords m_returnedGeodeticCoords = new GeodeticCoords();
	protected final GeodeticCoords m_viewToGeoPos = new GeodeticCoords();
	protected GeodeticCoords m_tileGeoPos = new GeodeticCoords();
	
	
    protected PixelXY m_pixel = new PixelXY();
    protected TileXY m_tile = new TileXY();

	public double EarthRadius = 6378137;
	public double EarthCirMeters  = 2.0*Math.PI*6378137;
	public double MeterPerDeg  = EarthCirMeters/360.0;
	
	protected int m_scrnDpi = 75;   // screen dot per inch
	protected double m_scrnMpp = 2.54/7500.0; // screen meter per pixel
	
	protected double m_scale = 0; // map scale
	protected double m_prevScale;
	protected double m_wholeWorldScale;
	
	protected double m_minLat = -90.0;
	protected double m_minLng = -180.0;
	protected double m_maxLat = 90.0;
	protected double m_maxLng = 180.0;
	
	protected int m_orgTilePixSize = 0;
	protected double m_origTileDegWidth = 0;
	protected double m_origTileDegHeight = 0;
	
	protected ZoomFlag m_zoomFlag = ZoomFlag.NONE;
	
	public static double M_PI = Math.PI;
	public static double M_PI_2 = Math.PI/2.0;
	public static double M_PI_4 = Math.PI/4.0;
	public static double MaxLatInRadians = Math.PI*(80.0/180.0);
	public static double NMtoMeters  = 1852.0;
	public static double RadToDeg = 57.29577951;
	public static double DegToRad = 0.017453293;
	
	
	public AbstractProjection(){
		m_scrnDpi = 75;
		m_scrnMpp = 2.54 / (75.0 * 100); // meters per pixel for physical screen
		m_vpGeoCenter.setLambda(0.0, AngleUnit.DEGREES);
		m_vpGeoCenter.setPhi(0.0, AngleUnit.DEGREES);
	}
	
	public AbstractProjection(int dpi) {
		m_scrnDpi = dpi;
		m_scrnMpp = 2.54 / (dpi * 100); // meters per pixel for physical screen
		m_vpGeoCenter.setLambda(0.0, AngleUnit.DEGREES);
		m_vpGeoCenter.setPhi(0.0, AngleUnit.DEGREES);
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
	
	@Override
	public void initialize( int tileSize, double degWidth, double degHeight ){
		double earth_mpp = degWidth * (MeterPerDeg / tileSize);
		// meters per pixel for physical screen
		m_scrnMpp = 2.54 / (m_scrnDpi * 100); 
		m_scale = (m_scrnMpp / earth_mpp);
		m_prevScale = m_scale;	
		m_orgTilePixSize = tileSize;
		m_origTileDegWidth = degWidth;
		m_origTileDegHeight = degHeight;
		computeWorldSize();
	}
	
	@Override
	public void copyFrom(IProjection orig) {
		m_scrnDpi = orig.getScrnDpi();
		m_vpGeoCenter.copyFrom(orig.getVpGeoCenter());
		setScale(orig.getScale());
		computeWorldSize();
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
	public void setViewGeoCenter(GeodeticCoords c) {
		m_vpGeoCenter.copyFrom(c);
	}
	

	@Override
	public void setCenterFromViewPixel( ViewCoords vc ){
		GeodeticCoords gc = viewToGeodetic(vc);
		if ( m_wdSize.getHeight() <= m_vpSize.getHeight() )
			gc.setPhi(0.0, AngleUnit.DEGREES);
		m_vpGeoCenter.copyFrom(gc);
	}
	

	@Override
	public void tagPositionToPixel( GeodeticCoords gc, ViewCoords vc ){
		int xDiff = m_vpSize.getWidth()/2 - vc.getX();
		int yDiff = vc.getY() - m_vpSize.getHeight()/2;
		WorldCoords tagWc = geodeticToWorld(gc);
		m_wc.setX(tagWc.getX()+xDiff);
		m_wc.setY(tagWc.getY() + yDiff);
		GeodeticCoords c = worldToGeodetic(m_wc);
		m_vpGeoCenter.copyFrom(c);
	}

	@Override
	public GeodeticCoords getVpGeoCenter() {
		return m_vpGeoCenter;
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
	public WorldCoords getWcCenter() {
		return geodeticToWorld( m_vpGeoCenter );
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

	@Override
	public ViewDimension getViewSize() {
		return m_vpSize;
	}
	
	
	protected int modX(int x, int size){
		if ( x < 0 )
			return (size+x);
		if ( x > size )
			return (x - size);
		return x;
	}
	

	@Override
	public GeodeticCoords viewToGeodetic( ViewCoords v ) {
		int wcX = lngDegToPixX( m_vpGeoCenter.getLambda(AngleUnit.DEGREES) );
		int wcY = latDegToPixY( m_vpGeoCenter.getPhi(AngleUnit.DEGREES));
		wcX += (v.getX() - m_vpSize.getWidth()/2);
		wcX = modX(wcX,(int)(mapSize()+ 0.5));
		wcY += (m_vpSize.getHeight()/2 - v.getY());
		double lng = wrapLng(xPixToDegLng(wcX));
		double lat = clip(yPixToDegLat(wcY),m_minLat,m_maxLat);
		
		m_viewToGeoPos.set( lng, lat, AngleUnit.DEGREES );
		return m_viewToGeoPos;
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
		return (int)(Math.abs(y2 - y1));
    }
    
}
