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
	private double m_endEqScale;
	private double m_nextScale;
	private double m_scaleDiff;
	private int m_durationInSecs = 250;
	  
	public AnimationEngine( MapView mv ) {
	   m_mapView = mv;
	   m_proj = m_mapView.getProjection();
	} 
	
	
	public void animateZoomMap( final double tagX, final double tagY, final double scaleFactor ){
		m_proj = m_mapView.getProjection();
		m_viewPort = m_mapView.getViewport();
		m_vpWorker = m_viewPort.getVpWorker(); 
		m_ztWorker.setTagInVC((int)tagX, (int)tagY);
		m_startEqScale = m_proj.getEquatorialScale();
		m_oldScale = m_startEqScale;
		m_endEqScale = getClosestLevelScale(m_startEqScale*scaleFactor);	
		m_scaleDiff = m_endEqScale - m_startEqScale;
		m_ztWorker.setViewOffsets(m_vpWorker.getOffsetInWcX(), m_vpWorker.getOffsetInWcY());
	    run(m_durationInSecs);
	}
	
	protected void zoom(double nextScale) {
		double f = nextScale/m_oldScale;
		if (f == 1.0) {
			return;
		}
		m_ztWorker.compViewOffsets(f);
		double ox = m_ztWorker.getOffsetX();
		double oy = m_ztWorker.getOffsetY();
		m_ztWorker.setViewOffsets(ox, oy);
		zoomAndMove(f, (int)ox, (int)oy);
		m_oldScale = nextScale;
	}
	
	//TODO This routine shouldn't do a full update ???+
	public void zoomAndMove( final double factor, final int offsetX, final int offsetY ) {
		m_proj.zoomByFactor(factor);
		final ViewDimension vd = m_vpWorker.getDimension();
		final WorldCoords wc = new WorldCoords(offsetX + vd.getWidth()/2, offsetY - vd.getHeight()/2);
		m_vpWorker.setCenterInWc(wc);
		m_mapView.partialUpdateView();
	}
	
	@Override
	protected void onUpdate(double progress) {
		m_nextScale = m_startEqScale + m_scaleDiff*Math.min(1.0, progress);
		if (progress != 0.0) {
			zoom( m_nextScale );
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		m_mapView.setSuspendFlag(true);
	}
	
	private double getClosestLevelScale(double eqScale) {
		DivManager divMgr = m_mapView.getDivManager();
		return divMgr.getClosestLevelScale(eqScale);
	}
	
	@Override
	protected void onComplete() {
		try {
			super.onComplete();
			IProjection proj = m_mapView.getProjection();
			double scale = proj.getEquatorialScale();
			zoom(getClosestLevelScale(scale));
			m_mapView.dumbUpdateView();
		} finally {
			m_mapView.setSuspendFlag(false);
		}
	}
	
	@Override
	protected void onCancel() {
		// Don't call super onCancel or it will call onComplete.
		m_mapView.setSuspendFlag(false);
		m_mapView.dumbUpdateView();
	}

	public int getDurationInSecs() {
		return m_durationInSecs;
	}

	public void setDurationInSecs(int durationInSecs) {
		m_durationInSecs = durationInSecs;
	}
}
