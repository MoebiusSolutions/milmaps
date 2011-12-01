package com.moesol.gwt.maps.client;

import java.util.ArrayList;

import com.google.gwt.animation.client.Animation;

public class AnimationEngine extends Animation {
	private final MapView m_mapView;
	private ViewPort m_viewPort;
	private IProjection m_proj;
	private ViewWorker m_vpWorker = null;
	private ZoomTagWorker m_ztWorker = new ZoomTagWorker();
	// Tag coordinates in View Coordinates

	private double m_oldScale;
	private double m_startScale;
	private double m_scaleDiff;
	private int m_durationInSecs = 750;
	  
	public AnimationEngine( MapView mv ) {
	   m_mapView = mv;
	   m_proj = m_mapView.getProjection();
	} 
	
	public void animateZoomMap( final double tagX, final double tagY, final double scaleFactor ) {
		m_proj = m_mapView.getProjection();
		m_viewPort = m_mapView.getViewport();
		m_vpWorker = m_viewPort.getVpWorker(); 
		m_ztWorker.setTagInVC((int)tagX, (int)tagY);
		m_startScale = m_proj.getScale();
		m_oldScale = m_startScale;
		m_scaleDiff = (scaleFactor - 1.0)*m_startScale;

		m_mapView.setSuspendFlag(true);
		run(m_durationInSecs);
	}
	
	protected void zoom( double nextScale ){
		int oWcX = m_vpWorker.getOffsetInWcX();
		int oWcY = m_vpWorker.getOffsetInWcY();
		m_ztWorker.setViewOffsets(oWcX, oWcY);
		double f = nextScale/m_oldScale;
		m_ztWorker.compViewOffsets(f);
		m_mapView.ZoomAndMove(f, m_ztWorker.getOffsetX(), m_ztWorker.getOffsetY());
		m_oldScale = nextScale;
		// TODO fix this soon.
		//m_mapView.drawIcons();
	}
	
	@Override
	protected void onUpdate(double progress) {
		double nextScale = m_startScale + m_scaleDiff*Math.min(1.0, progress);
		if ( progress != 0.0 )
			zoom( nextScale );
	}
	
	
	@Override
	protected void onComplete(){
		super.onComplete();
		m_mapView.setSuspendFlag(false);
		m_mapView.updateDivPanel();
		m_mapView.doUpdateView();
	}
	
	@Override
	protected void onCancel(){
		super.onCancel();
		m_mapView.setSuspendFlag(false);
		m_mapView.updateDivPanel();
		m_mapView.doUpdateView();
	}

	public int getDurationInSecs() {
		return m_durationInSecs;
	}

	public void setDurationInSecs(int durationInSecs) {
		m_durationInSecs = durationInSecs;
	}
}