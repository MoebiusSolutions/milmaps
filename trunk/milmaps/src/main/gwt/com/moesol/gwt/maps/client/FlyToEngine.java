package com.moesol.gwt.maps.client;

import com.google.gwt.animation.client.Animation;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class FlyToEngine extends Animation{
	private final MapView m_mapView;
	private int m_durationInSecs = 750;
	private double m_startLat;
	private double m_startLng;
	private double m_endLat;
	private double m_endLng;
	private double m_currentLat;
	private double m_currentLng;
	private double m_lngVector;
	private double m_latVector;
	private static double EPSILON = 0.00001;
	private double m_scaleFactor;
	private double m_projScale;
	private int    m_zoomCount = 0;
	private boolean m_zoomIn = true;
	private boolean m_panning = true;
	
	public FlyToEngine( MapView mv ) {
		m_mapView = mv;
	}
	/*
	public void flyTo( final double lat, final double lng, double projScale ) {
		m_endLat = lat;
		m_endLng = lng;
		GeodeticCoords gc = m_p.getVpGeoCenter();
		m_startLat = gc.getPhi(AngleUnit.DEGREES);
		m_startLng = gc.getLambda(AngleUnit.DEGREES);
		m_lngVector = getLngVector(m_startLng, lng);
		m_latVector = lat - m_startLat;
		m_mapView.setSuspendFlag(true);
		run(m_durationInSecs);
	}
	*/
	public void flyTo( final double lat, final double lng, final double scaleFactor) {
		m_panning = true;
		m_endLat = lat;
		m_endLng = lng;
		IProjection p = m_mapView.getProjection(); 
		m_projScale = p.getScale();
		m_scaleFactor = scaleFactor;
		m_zoomIn =  (m_scaleFactor < 1.0 ? false : true );
		GeodeticCoords gc = p.getViewGeoCenter();
		m_startLat = gc.getPhi(AngleUnit.DEGREES);
		m_startLng = gc.getLambda(AngleUnit.DEGREES);
		m_lngVector = getLngVector(p, m_startLng, lng);
		m_latVector = lat - m_startLat;
		m_mapView.setSuspendFlag(true);
		run(m_durationInSecs);
	}
	
	protected void moveTo( double lat, double lng, double scale ){ 
		m_mapView.centerOn(lat,lng,scale);
	}
	/*
	protected double getNewScale(double progress){
		if ( m_zoomCount == 0 ){
			return m_projScale;
		}
		if ( 0.5 <= progress && progress < 1.0 ){
			int i = (int)(progress/m_zoomInc) + 1;
			if ( m_zoomIn == false ){
				i = -1*i;
			}
			return m_projScale*Math.pow(2.0, i);
		}
		return m_projScale;
	}
	*/
	protected void moveMap( double progress ){
		if ( m_panning == true ){
			if ( 0 < progress  ){
				m_currentLat = m_startLat + progress*m_latVector;
				m_currentLng = m_startLng + progress*m_lngVector;
				m_mapView.centerOn(m_currentLat, m_currentLng, 0.0);	
			}
		}
	}
	
	protected void doFinalStep(){
		if ( Math.abs(m_endLat-m_currentLat) > EPSILON ||
			 Math.abs(m_endLng-m_currentLng) > EPSILON ){
			m_mapView.centerOn(m_endLat, m_endLng, 0.0);
		}
		m_mapView.setSuspendFlag(false);
		m_mapView.doUpdateView();
		if ( m_scaleFactor > 0 ){
			m_mapView.timerZoom(m_zoomIn,m_scaleFactor);
		}
	}

	@Override
	protected void onUpdate(double progress) {
		moveMap( progress );
	}
	
	@Override
	protected void onComplete(){
		super.onComplete();
		doFinalStep();
	}
	
	@Override
	protected void onCancel(){
		super.onCancel();
		doFinalStep();
	}

	public int getDurationInSecs() {
		return m_durationInSecs;
	}

	public void setDurationInSecs(int durationInSecs) {
		m_durationInSecs = durationInSecs;
	}
	
	/**
	 * This routine finds the shortest longitude distance 
	 * and direction between to longitude values.
	 * @param startLng ( start longitude )
	 * @param endLng (end longitude )
	 * @return shortest longitude distance
	 */
	private double getLngVector(IProjection p, double startLng, double endLng){
		double diff = endLng - startLng; 
		if ( (0.0 < startLng && 0.0 < endLng) || (startLng < 0.0 && endLng < 0.0 ) ){
			return diff;
		}
		if ( Math.abs(diff) > 180.0 ){
			return p.wrapLng(diff);
		}
		return diff;
	}
}
