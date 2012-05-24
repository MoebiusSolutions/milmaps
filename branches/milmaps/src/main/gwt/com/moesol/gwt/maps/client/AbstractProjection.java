/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.moesol.gwt.maps.client.events.ProjectionChangedEvent;
import com.moesol.gwt.maps.client.events.ProjectionChangedHandler;


public abstract class AbstractProjection implements IProjection, HasHandlers {
    
	public static double RadToDeg = 57.29577951;
	public static double DegToRad = 0.017453293;
	public static int DOTS_PER_INCH = 96;
	private final HandlerManager m_handlerManager = new HandlerManager(this);
	protected IProjection.T m_projType;
	protected final WorldDimension m_wdSize = new WorldDimension(); // Whole world
																  // map size
	public double EarthCirMeters  = 2.0*Math.PI*IProjection.EARTH_RADIUS_MEERS;
	public double MeterPerDeg  = EarthCirMeters/360.0;
	
	protected int m_scrnDpi = DOTS_PER_INCH;   // screen dot per inch
	protected double m_scrnMpp = 2.54/m_scrnDpi*100.0; // screen meter per pixel
	
	protected double m_eqScale = 0; // map scale
	
	protected int m_origMapWidthSize;
	
	protected double m_baseEqScale = 0;
	//protected int 	 m_orgTilePixSize = 0;
	//protected double m_origTileDegHeight = 0;

	@Override
	public IProjection.T getType() { return m_projType; }
	
	public AbstractProjection() {
		this(DOTS_PER_INCH);
	}
	public AbstractProjection(int dpi) {
		m_scrnDpi = dpi;
		m_scrnMpp = 2.54/(dpi*100); // meters per pixel for physical screen
	}
	
	public abstract int lngDegToPixX( double deg );
	public abstract double xPixToDegLng( double pix );
	
	public abstract int latDegToPixY( double deg );
	public abstract double yPixToDegLat( double pix );
	
    protected double clip( double n, double minValue, double maxValue ) {
        return Math.min( Math.max( n, minValue ), maxValue );
    }
	
	public abstract WorldDimension getWorldDimension();
	
	// Map size in pixels
	public double mapSize() {
		double pixels = (EarthCirMeters*m_eqScale/m_scrnMpp);
		return pixels;
	}
	
	@Override
	public int iMapWidth() {
		return (int)(mapSize() + 0.5);
	}
	
	@Override
	public double wrapLng(double lng) {
		int k = (int)Math.abs((lng/360));
		if (lng > 180.0) {
			lng -= k*360;
			if (lng > 180.0)
				lng -= 360.0;
		} else if (lng < -180.0) {
			lng += k*360;
			if (lng < -180.0)
				lng += 360.0;
		}
		return lng;
	}

	@Override
	public void initialize(int tileSize, double degWidth, double degHeight) {
		double earth_mpp = (degWidth * MeterPerDeg)/tileSize;
		// meters per pixel for physical screen
		m_scrnMpp = 2.54 / (m_scrnDpi * 100.0); 
		m_eqScale = (m_scrnMpp / earth_mpp);
		m_baseEqScale = m_eqScale;
		//m_origMapWidthSize = (int)( tileSize*(360/degWidth) + 0.5);
		computeWorldSize();
	}

	@Override
	public void copyFrom(IProjection p) {
		m_scrnDpi = p.getScrnDpi();
		m_baseEqScale = p.getBaseEquatorialScale();
		setEquatorialScale(p.getEquatorialScale());
	}

	@Override
	public int getScrnDpi() {
		return m_scrnDpi;
	}
	
	@Override
	public void setBaseEquatorialScale(double dScale) {
		if (dScale <= 0.0) {
			throw new IllegalArgumentException("scale Factor less than zero");
		}
		m_baseEqScale = dScale;
	}
	
    @Override
    public double getBaseEquatorialScale(){ 
    	return m_baseEqScale; 
    }
	
	@Override
	public double getEquatorialScale() {
		return m_eqScale;
	}

	@Override
	public void setEquatorialScale(double dScale) {
		if (dScale <= 0.0) {
			throw new IllegalArgumentException("scale Factor less than zero");
		}
		int level = getLevelFromScale(dScale, 0);
		if (level < 0) {
			dScale = getScaleFromLevel(0);
		}
		else if ( level > DivManager.NUMDIVS-1){
			dScale = getScaleFromLevel(DivManager.NUMDIVS-1);
		}
		m_eqScale = dScale;
		computeWorldSize();
	}

	@Override
	public void zoomByFactor( double scaleFactor ) {
		if ( scaleFactor <= 0.0 ){
			throw new IllegalArgumentException("scale Factor less than zero");
		}
		setEquatorialScale(scaleFactor*m_eqScale);
	}
	
	protected int modX( int x, int size ) {
		if ( x < 0 ) {
			return (size+x);
		}
		if ( x > size ) {
			return (x - size);
		}
		return x;
	}
	
	protected void computeWorldSize() {
		ProjectionChangedEvent pce = new ProjectionChangedEvent(this);
		fireEvent(pce);
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
    public int getLevelFromScale( double eqScale, double roundValue ){
		double level = ( Math.log(eqScale) - Math.log(m_baseEqScale))/Math.log(2);
		return (int)(level + roundValue);
    }
  
    @Override
    public double getScaleFromLevel(int level) {
		return m_baseEqScale * (1 << level); 
    }

	@Override
	public void fireEvent(GwtEvent<?> event) {
		m_handlerManager.fireEvent(event);
	}
	
	@Override
	public HandlerRegistration addProjectionChangedHandler(ProjectionChangedHandler handler) {
		return m_handlerManager.addHandler(ProjectionChangedEvent.TYPE, handler);
	}

	@Override
	public String toString() {
		return "AbstractProjection ["
				+ "m_projType=" + m_projType + ", m_wdSize=" + m_wdSize
				+ ", EarthRadius=" + IProjection.EARTH_RADIUS_MEERS + ", EarthCirMeters="
				+ EarthCirMeters + ", MeterPerDeg=" + MeterPerDeg
				+ ", m_scrnDpi=" + m_scrnDpi + ", m_scrnMpp=" + m_scrnMpp
				+ ", m_eqScale=" + m_eqScale + ", m_origMapWidthSize="
				+ m_origMapWidthSize + ", m_origEqScale=" + m_baseEqScale + "]";
	}
    
}
