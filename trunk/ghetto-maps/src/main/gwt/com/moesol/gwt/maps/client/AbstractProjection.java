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
	protected final GeodeticCoords m_returnedGeodeticCoords = new GeodeticCoords();

	protected int m_scrnDpi = 0; // screen dot per inch
	protected double m_scrnMpp = 0; // screen meter per pixel
	protected double m_scale = 0; // map scale
	protected double m_prevScale;
	protected double m_wholeWorldScale;
	
	protected double m_minLat = -90.0;
	protected double m_minLng = -180.0;
	protected double m_maxLat = 90.0;
	protected double m_maxLng = 180.0;
	
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
	
	public abstract WorldDimension getWorldDimension();
	
	@Override
	public void initScale( int tileWidth, double degWidth ){
		double l_mpp = degWidth * (111120.0 / tileWidth);
		// meters per pixel for physical screen
		double mpp = 2.54 / (m_scrnDpi * 100); 
		m_scale = (mpp / l_mpp);
		m_prevScale = m_scale;		
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
		double lng = pixToLngDegMag(xDiff);
		lng = wrapLng(gc.getLambda(AngleUnit.DEGREES) + lng );
		double lat = pixToLatDegMag(0,yDiff);
		lat = gc.getPhi(AngleUnit.DEGREES) + lat;
		m_vpGeoCenter.set(lng, lat,AngleUnit.DEGREES );
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
	

	@Override
	public GeodeticCoords viewToGeodetic( ViewCoords v ) {
		double lngDiff = pixToLngDegMag(v.getX() - m_vpSize.getWidth()/2);
		double lng = wrapLng(m_vpGeoCenter.getLambda(AngleUnit.DEGREES)+lngDiff);
		
		double latDiff = pixToLatDegMag( 0,m_vpSize.getHeight()/2 - v.getY());
		double lat = m_vpGeoCenter.getPhi(AngleUnit.DEGREES) + latDiff;
		
		m_returnedGeodeticCoords.set( lng, lat, AngleUnit.DEGREES );
		return m_returnedGeodeticCoords;
	}
	
	/*
	@Override
	public ViewCoords geodeticToView(GeodeticCoords g) {
		double lng = g.getLambda(AngleUnit.DEGREES);
		double lngDiff = wrapLng(lng - m_vpGeoCenter.getLambda(AngleUnit.DEGREES));
		int vpX = m_vpSize.getWidth()/2 + lngDegToPixMag(lngDiff);
		
		double lat = g.getPhi(AngleUnit.DEGREES);
		double latDiff = m_vpGeoCenter.getPhi(AngleUnit.DEGREES) - lat;
		
		int vpY = m_vpSize.getHeight()/2 + latDegToPixMag(0,latDiff);
		
		m_returnedViewCoords.setX(vpX);
		m_returnedViewCoords.setY(vpY);
		
		return m_returnedViewCoords;
	}
	*/
	protected void computeWorldSize() {
		int width  = 2*lngDegToPixMag(m_maxLng);
		int height = 2*latDegToPixMag(m_maxLat/2, m_maxLat);
		m_wdSize.setWidth(width);
		m_wdSize.setHeight(height);
	}
}
