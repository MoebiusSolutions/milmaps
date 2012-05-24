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

import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;

public class CylProj {
	
	public enum ZoomFlag {
		OUT, NONE, IN
	}
	
	protected GeodeticCoords m_vpGeoCenter = new GeodeticCoords(); // viewport Center
	protected final WorldDimension m_wdSize = new WorldDimension(); // Whole world map size
	protected ViewDimension m_vpSize = new ViewDimension(); // viewPort size
	protected final ViewCoords m_returnedViewCoords = new ViewCoords();
	protected final WorldCoords m_returnedWorldCoords = new WorldCoords();
	protected final WorldCoords m_wc = new WorldCoords();
	protected final GeodeticCoords m_returnedGeodeticCoords = new GeodeticCoords();
	
	protected ZoomFlag m_zoomFlag = ZoomFlag.NONE;
	
	public double EarthRadius = 6378137;
	public double EarthCirMeters  = 2.0*Math.PI*6378137;
	public double MeterPerDeg  = EarthCirMeters/360.0;
	
	protected int m_scrnDpi = AbstractProjection.DOTS_PER_INCH;   // screen dot per inch
	protected double m_scrnMpp = 2.54/(m_scrnDpi*100.0); // screen meter per pixel
	protected double m_scale = 0;   // map scale
	protected double m_prevScale;
	
	protected double m_minLat = -90.0;
	protected double m_minLng = -180.0;
	protected double m_maxLat = 90.0;
	protected double m_maxLng = 180.0;
	
	
	public CylProj( int tileWidth, double degWidth ){
		double l_mpp = degWidth * (111120.0 / tileWidth);
		// meters per pixel for physical screen
		double mpp = 2.54 / (m_scrnDpi * 100); 
		m_scale = (mpp / l_mpp);
		m_prevScale = m_scale;	
		computeWorldSize();
	}
	
	public int getScrnDpi(){
		return m_scrnDpi;
	}
	public double getScale() {
		return m_scale;
	}
	
	public void setScale(double dScale) {
		m_prevScale = m_scale;
		m_scale = dScale;
		computeWorldSize();
	}
	
	public void setViewGeoCenter(GeodeticCoords c) {
		m_vpGeoCenter = c;
	}
	
	public GeodeticCoords getVpGeoCenter() {
		return m_vpGeoCenter;
	}

	public double pixToLngDegMag(int pix){
		return ((pix * m_scrnMpp / m_scale) / 111120.0);
	}


	public int lngDegToPixMag( double deg ){
		return (int)( (m_scale * (deg * 111120.0) / m_scrnMpp) + 0.5 );
	}
	
	
	public double pixToLatDegMag( int pix ){
		return ((pix * m_scrnMpp / m_scale) / 111120.0);
	}
	

	public int latDegToPixMag( double deg ){
		return (int)( (m_scale * (deg * 111120.0) / m_scrnMpp) + 0.5 );
	}
	
	public WorldDimension getWorldDimension() {
		int wdX = (int) (m_scale * (360 * 111120.0) / m_scrnMpp + 0.5);
		int wdY = (int) (m_scale * (180 * 111120.0) / m_scrnMpp + 0.5);

		m_wdSize.setWidth(wdX);
		m_wdSize.setHeight(wdY);
		return m_wdSize;
	}
	
	public void setViewSize(ViewDimension vp) {
		m_vpSize = vp;
	}

	public GeodeticCoords worldToGeodetic(WorldCoords w) {
		// we have to mod by world size in case we had
		// to extend the whole world
		double lng = pixToLngDegMag(w.getX() % m_wdSize.getWidth()) - 180.0;
		if (w.getX() > 10 && lng == m_minLng)
			lng = m_maxLng;
		double lat = pixToLatDegMag(w.getY()) + m_minLat;

		return Degrees.geodetic(lat, lng);
	}
	
	public WorldCoords geodeticToWorld(GeodeticCoords g) {
		int x = lngDegToPixMag(g.getLambda(AngleUnit.DEGREES) + m_maxLng);
		int y = latDegToPixMag(g.getPhi(AngleUnit.DEGREES) + m_maxLat);
		return new WorldCoords(x, y);
	}
	
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

	
	public void zoomByFactor( double scaleFactor ) {
		if ( scaleFactor <= 0.0 ){
			throw new IllegalArgumentException("scale Factor less than zero");
		}
		zoom(scaleFactor*m_scale);
	}
	
	protected double computeScale(double deg, int pix) {
		double mpp = deg*(111120.0 / pix);
		return (m_scrnMpp / mpp);
	}
	
	protected void computeWorldSize() {
		int width  = 2*lngDegToPixMag(m_maxLng);
		int height = 2*latDegToPixMag(m_maxLat);
		m_wdSize.setWidth(width);
		m_wdSize.setHeight(height);
	}
}
