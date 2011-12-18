package com.moesol.gwt.maps.client;

import com.google.gwt.animation.client.Animation;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;

public class FlyToEngine extends Animation {
	static final double ZOOM_OUT_TARGET_SCALE = MapScale.parse("1:20M").asDouble();
	static double ZOOM_OUT_UNTIL = 0.50;
	static double PAN_OUT_UNTIL = 0.66;
	private final IMapView m_mapView;
	private int m_durationInMilliSecs = 5000;
	private double m_startLat;
	private double m_startLng;
	private double m_startScale;
	private double m_deltaOutScale;
	private double m_deltaLat;
	private double m_deltaLng;
	private double m_deltaInScale;
	
	public FlyToEngine(IMapView mv) {
		m_mapView = mv;
	}
	
	void initEngine(double endLat, double endLng, double projectionScale) {
		m_startLat = m_mapView.getProjection().getViewGeoCenter().getPhi(AngleUnit.DEGREES);
		m_startLng = m_mapView.getProjection().getViewGeoCenter().getLambda(AngleUnit.DEGREES);
		m_startScale = m_mapView.getProjection().getScale();
		// TODO TARGET OUT SCALE should be based on pan distance...
		m_deltaOutScale = ZOOM_OUT_TARGET_SCALE - m_startScale;
		m_deltaLat = endLat - m_startLat;
		m_deltaLng = endLng - m_startLng;
		// faster to go the other way
		if (m_deltaLng > 180.0) {
			m_deltaLng -= 360.0;
		} else if (m_deltaLng < -180.0) {
			m_deltaLng += 360.0;
		}
		m_deltaInScale = projectionScale - ZOOM_OUT_TARGET_SCALE;
	}
	
	/**
	 * We divide the animation progress into three parts.  
	 * <ol>
	 * <li>Zoom out
	 * <li>Pan
	 * <li>Zoom in
	 * </ol>
	 * 
	 * We pan the entire time. We zoom in for the first half and zoom in for the second half.
	 */
	@Override
	protected void onUpdate(double progress) {
		pan(progress);
		
		if (progress <= ZOOM_OUT_UNTIL) {
			double outProgress = progress / ZOOM_OUT_UNTIL;
			zoomOut(outProgress);
//		} else if (progress <= PAN_OUT_UNTIL) {
//			double deltaPan = PAN_OUT_UNTIL - ZOOM_OUT_UNTIL;
//			double panProgress = (progress - ZOOM_OUT_UNTIL) / deltaPan;
//			pan(panProgress);
		} else {
//			double deltaZoomIn = 1.0 - PAN_OUT_UNTIL;
//			double inProgress = (progress - PAN_OUT_UNTIL) / deltaZoomIn;
//			pan(1.0); // The animation might jump past pan(1.0) during second phase.
			double deltaZoomIn = 1.0 - ZOOM_OUT_UNTIL;
			double inProgress = (progress - ZOOM_OUT_UNTIL) / deltaZoomIn;
			zoomIn(inProgress);
		}
		m_mapView.doUpdateView();
	}
	
	void pan(double progress) {
		double newLat = m_startLat + m_deltaLat * progress;
		double newLng = m_startLng + m_deltaLng * progress;
		if (newLng < -180) {
			newLng = newLng + 360.0;
		} else if (newLng > 180) {
			newLng = newLng -360.0;
		}
		
		m_mapView.setCenter(Degrees.geodetic(newLat, newLng));
	}
	
	void zoomOut(double progress) {
		double newScale = m_startScale + m_deltaOutScale * progress;
		m_mapView.getProjection().setScale(newScale);
	}
	void zoomIn(double progress) {
		double newScale = ZOOM_OUT_TARGET_SCALE + m_deltaInScale * progress;
		m_mapView.getProjection().setScale(newScale);
	}

	/**
	 * Start a flyTo animation.
	 * 
	 * @param endLat
	 * @param endLng
	 * @param projectionScale
	 */
	public void flyTo(double endLat, double endLng, double projectionScale) {
		initEngine(endLat, endLng, projectionScale);
		
	    run(m_durationInMilliSecs);
	}

	@Override
	protected void onStart() {
		super.onStart();
		m_mapView.setSuspendFlag(true);
	}
	@Override
	protected void onComplete() {
		try {
			super.onComplete();
		} finally {
			m_mapView.setSuspendFlag(false);
		}
	}
	@Override
	protected void onCancel() {
		try {
			super.onCancel();
		} finally {
			m_mapView.setSuspendFlag(false);
		}
	}

	public int getDurationInSecs() {
		return m_durationInMilliSecs;
	}

	public void setDurationInSecs(int durationInSecs) {
		m_durationInMilliSecs = durationInSecs;
	}
	
}
