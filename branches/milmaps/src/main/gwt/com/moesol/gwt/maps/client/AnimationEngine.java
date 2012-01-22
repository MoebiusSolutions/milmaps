package com.moesol.gwt.maps.client;

import com.google.gwt.animation.client.Animation;

public class AnimationEngine extends Animation {
	private final MapView m_mapView;
	private ViewPort m_viewPort;
	private IProjection m_proj;
	private ViewWorker m_vpWorker = null;
	private ZoomTagWorker m_ztWorker = new ZoomTagWorker();
	// Tag coordinates in View Coordinates
	private double m_oldScale;
	private double m_startEqScale;
	private double m_nextScale;
	private double m_scaleDiff;
	private boolean m_bZoomingIn;
	private int m_durationInSecs = 750;
	  
	public AnimationEngine( MapView mv ) {
	   m_mapView = mv;
	   m_proj = m_mapView.getProjection();
	} 
	
	public void animateZoomMap( final double tagX, final double tagY, final double scaleFactor ){
		m_bZoomingIn = ( scaleFactor > 1.0);
		m_proj = m_mapView.getProjection();
		m_viewPort = m_mapView.getViewport();
		m_vpWorker = m_viewPort.getVpWorker(); 
		m_ztWorker.setTagInVC((int)tagX, (int)tagY);
		m_startEqScale = m_proj.getEquatorialScale();
		m_oldScale = m_startEqScale;
		m_scaleDiff = (scaleFactor - 1.0)*m_startEqScale;
		m_ztWorker.setViewOffsets(m_vpWorker.getOffsetInWcX(), m_vpWorker.getOffsetInWcY());
	    run(m_durationInSecs);
	}
	
	protected void zoom( double nextScale ){
		double f = nextScale/m_oldScale;
		m_ztWorker.compViewOffsets(f);
		double ox = m_ztWorker.getOffsetX();
		double oy = m_ztWorker.getOffsetY();
		m_ztWorker.setViewOffsets(ox, oy);
		m_mapView.zoomAndMove(f, (int)ox, (int)oy);
		m_oldScale = nextScale;
		// TODO fix this soon.
		//m_mapView.drawIcons();
	}
	
	@Override
	protected void onUpdate(double progress) {
		m_nextScale = m_startEqScale + m_scaleDiff*Math.min(1.0, progress);
		if ( progress != 0.0 )
			zoom( m_nextScale );
	}
	
	protected void onStart() {
		super.onStart();
		m_mapView.setSuspendFlag(true);
	}
	
	@Override
	protected void onComplete(){
		try {
			super.onComplete();
			double fudge = 2.000000001;
			if ( m_bZoomingIn && ( m_nextScale < fudge*m_startEqScale ) ){
				zoom( fudge*m_startEqScale );
			}
			m_mapView.doUpdateView();
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
		m_mapView.doUpdateView();
	}

	public int getDurationInSecs() {
		return m_durationInSecs;
	}

	public void setDurationInSecs(int durationInSecs) {
		m_durationInSecs = durationInSecs;
	}
}
