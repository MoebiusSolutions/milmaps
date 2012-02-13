package com.moesol.gwt.maps.client;

import com.google.gwt.animation.client.Animation;
import com.moesol.gwt.maps.client.timing.interpolators.SplineInterpolator;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;

public class FlyToEngine {
	public enum Dir {
		LEFT, RIGHT, STAY
	}
	//static final double ZOOM_OUT_TARGET_SCALE = MapScale.parse("1:100M").asDouble();
	static final SplineInterpolator EASE_IN_OUT = new SplineInterpolator(1.0, 0, 0.0, 1.0);
	static double ZOOM_OUT_UNTIL = 0.30;
	static double ZOOM_PAUSE = 0.45;
	static double ZOOM_IN_AT = 0.55;
	static double ZOOM_OUT_FACTOR = 1.0/1.5;
	private final IMapView m_mapView;
	private int m_durationInMilliSecs = 2000;
	private double m_startLat;
	private double m_startLng;
	private double m_teleportLat;
	private double m_teleportLng;
	private double m_teleportScale;
	private double m_startScale;
	private double m_deltaOutScale;
	private double m_preTeleportDeltaLat;
	private double m_preTeleportDeltaLng;
	private double m_postTeleportDeltaLat;
	private double m_postTeleportDeltaLng;
	private double m_deltaInScale;
	private boolean m_teleported = false;
	private Dir m_move = Dir.STAY;
	private Animation m_animationAdaptor;
	
	public FlyToEngine(IMapView mv) {
		m_mapView = mv;
	}
	
	private class AnimationAdaptor extends Animation {

		@Override
		protected void onUpdate(double progress) {
			FlyToEngine.this.onUpdate(progress);
		}

		@Override
		protected void onCancel() {
			FlyToEngine.this.onCancel();
		}
		
		@Override
		protected void onComplete(){
			FlyToEngine.this.onComplete();
		}
	}
	
	public Animation getAnimation() {
		// Instead of extending Animation we use an adaptor and lazy initialization
		// so that we can create FlyToEngine in unit test.
		if (m_animationAdaptor == null) {
			m_animationAdaptor = new AnimationAdaptor();
		}
		return m_animationAdaptor;
	}
	
	
	private GeodeticCoords viewToGeo(int x, int y) {
		ViewWorker vw = m_mapView.getViewport().getVpWorker();
		WorldCoords wc = vw.viewToWorld(new ViewCoords(x,y));
		IProjection proj = m_mapView.getProjection();
		return proj.worldToGeodetic(wc);
	}
	
	Dir getDirection(double leftLng, double centLng,  double rightLng, double endLng){
		boolean goodOrder = (leftLng < centLng && centLng < rightLng);
		if (goodOrder == false) {
			if (centLng > 0) {
				rightLng = (rightLng >= 0 ? rightLng : 360 + rightLng);
				endLng = (endLng >= 0 ? endLng : 360 + endLng);
			}
			else if (centLng < 0) {
				leftLng = (leftLng <= 0 ? leftLng : leftLng - 360);
				endLng = (endLng <= 0 ? endLng : endLng - 360);
			}
		}
		else {
			double diff = endLng - centLng;
			endLng += (diff > 180 ? -360 : (diff < -180 ? 360 : 0));
		}
		if (endLng < leftLng) {
			return Dir.LEFT;
		}
		if (rightLng < endLng) {
			return Dir.RIGHT;
		}
		return Dir.STAY;
	}

	double getPreTeleportLng(double leftLng, double centLng, 
							 double rightLng, double endLng) {
		
		m_move = getDirection(leftLng, centLng, rightLng, endLng);
		if (m_move == Dir.RIGHT) {
			return rightLng;
		}
		if (m_move == Dir.LEFT) {
			return leftLng;
		}
		return endLng;
	}
	
	private double getPreTeleportLat(double botLat, double topLat, double endLat) {
		if (endLat < botLat) {
			return botLat;
		}
		if (topLat < endLat) {
			return topLat;
		}
		return (m_startLat+endLat)/2.0;
	}
	
	protected void computeTeleportPt( GeodeticCoords endPt, double projScale){
		IProjection proj = m_mapView.getProjection().cloneProj();
		proj.setEquatorialScale(projScale);
		int level = proj.getLevelFromScale(projScale, 0);
		
		WorldCoords wc = proj.geodeticToWorld(endPt);
		ViewWorker vw = m_mapView.getViewport().getVpWorker();
		// Edge position
		if (m_move == Dir.STAY) {
			m_teleportLat = endPt.getPhi(AngleUnit.DEGREES);
			m_teleportLng = endPt.getLambda(AngleUnit.DEGREES);
		}
		else {
			ViewDimension vd = vw.getDimension();
			int offset = vd.getWidth()/(m_move == Dir.RIGHT ? -2 : 2);
			wc = new WorldCoords(wc.getX()+offset, wc.getY());
			GeodeticCoords gc = proj.worldToGeodetic(wc);
			m_teleportLat = gc.getPhi(AngleUnit.DEGREES);
			m_teleportLng = gc.getLambda(AngleUnit.DEGREES);
		}
		m_teleportScale = proj.getScaleFromLevel(level);
		m_postTeleportDeltaLat = endPt.getPhi(AngleUnit.DEGREES) - m_teleportLat;
		m_postTeleportDeltaLng = endPt.getLambda(AngleUnit.DEGREES) - m_teleportLng;
	}
	
	protected void computePreTeleportPt( GeodeticCoords endPt, double projScale){
		ViewPort vp = m_mapView.getViewport();
		GeodeticCoords center = vp.getVpWorker().getGeoCenter();
		double centLng = center.getLambda(AngleUnit.DEGREES);
		ViewWorker vw = m_mapView.getViewport().getVpWorker();
		// Edge position
		ViewDimension vd = vw.getDimension();
		GeodeticCoords tl = viewToGeo(0,0);
		GeodeticCoords br = viewToGeo(vd.getWidth(), vd.getHeight());
		double preTelePortLng = getPreTeleportLng(tl.getLambda(AngleUnit.DEGREES), 
										   		  centLng,
										   		  br.getLambda(AngleUnit.DEGREES), 
										   		  endPt.getLambda(AngleUnit.DEGREES));
		double preTelePortLat = getPreTeleportLat(br.getPhi(AngleUnit.DEGREES), 
							   			   		  tl.getPhi(AngleUnit.DEGREES),
							   			   		  endPt.getPhi(AngleUnit.DEGREES));
		// Near end position
		m_preTeleportDeltaLat = preTelePortLat - m_startLat;
		m_preTeleportDeltaLng = preTelePortLng - m_startLng;
		computeTeleportPt(endPt, projScale);
	}
	
	void initEngine( GeodeticCoords endPt, double projectionScale) {
		ViewPort vp = m_mapView.getViewport();
		GeodeticCoords gcP = vp.getVpWorker().getGeoCenter();
		m_startLat = gcP.getPhi(AngleUnit.DEGREES);
		m_startLng = gcP.getLambda(AngleUnit.DEGREES);
		IProjection proj = m_mapView.getProjection();
		m_startScale = proj.getEquatorialScale();
		double outScale = m_startScale*ZOOM_OUT_FACTOR;
		double baseScale = m_mapView.getProjection().getBaseEquatorialScale();
		outScale = Math.max(outScale, baseScale);
		m_deltaOutScale = outScale - m_startScale;
		computePreTeleportPt(endPt, projectionScale);
		// faster to go the other way	
		m_deltaInScale = projectionScale - m_teleportScale;
		m_teleported = false;
	}
	
	/**
	 * We divide the animation progress into three parts.  
	 * <ol>
	 * <li>Zoom out
	 * <li>Pan
	 * <li>Zoom in
	 * </ol>
	 * 
	 * We pan the entire time. We zoom out for the first half and zoom in for the second half.
	 */
	protected void onUpdate(double progress) {
		if (progress <= ZOOM_OUT_UNTIL) {
			double outProgress = progress/ZOOM_OUT_UNTIL;
			//outProgress = EASE_IN_OUT.interpolate(outProgress);
			panPreTeleport(outProgress);
			zoomOut(outProgress);
		} 
		else if (m_teleported == false) {
			m_teleported = true;
			teleport();	
		}
		if (progress > ZOOM_IN_AT) {
			double deltaZoomIn = 1.0 - ZOOM_IN_AT;
			double inProgress = (progress - ZOOM_IN_AT) / deltaZoomIn;
			panPostTeleport(inProgress);
			zoomIn(inProgress);
		}
		m_mapView.updateView();
	}
	
	public void onCancel() {
		// Overriden to prevent onComplete. We just stop the flyTo
		m_mapView.doUpdateView();
	}
	
	public void onComplete(){
		m_mapView.doUpdateView();
	}
	
	void panPreTeleport(double progress) {
		if (m_move != Dir.STAY) {
			double newLat = m_startLat + m_preTeleportDeltaLat * progress;
			double newLng = m_startLng + m_preTeleportDeltaLng * progress;
			if (newLng < -180) {
				newLng = newLng + 360.0;
			} else if (newLng > 180) {
				newLng = newLng -360.0;
			}
			setGeoCenter(Degrees.geodetic(newLat, newLng));
		}
	}
	
	void teleport(){
		setGeoCenter(Degrees.geodetic(m_teleportLat, m_teleportLng));
		m_mapView.getProjection().setEquatorialScale(m_teleportScale);
	}
	
	void panPostTeleport(double progress) {
		double newLat = m_teleportLat + m_postTeleportDeltaLat * progress;
		double newLng = m_teleportLng + m_postTeleportDeltaLng * progress;
		if (newLng < -180) {
			newLng = newLng + 360.0;
		} else if (newLng > 180) {
			newLng = newLng -360.0;
		}
		setGeoCenter(Degrees.geodetic(newLat, newLng));
	}
	
	void setGeoCenter(GeodeticCoords center){
		ViewPort vp = m_mapView.getViewport();
		vp.getVpWorker().setGeoCenter(center);		
	}
	
	void zoomOut(double progress) {
		//progress = EASE_IN_OUT.interpolate(progress);
		
		double newScale = m_startScale + m_deltaOutScale * progress;
		m_mapView.getProjection().setEquatorialScale(newScale);
	}
	void zoomIn(double progress) {
		//progress = EASE_IN_OUT.interpolate(progress);
		
		double newScale = m_teleportScale + m_deltaInScale * progress;
		m_mapView.getProjection().setEquatorialScale(newScale);
	}

	/**
	 * Start a flyTo animation.
	 * 
	 * @param endLat
	 * @param endLng
	 * @param projectionScale
	 */
	public void flyTo( GeodeticCoords endPt, double projectionScale) {
		getAnimation().cancel();
		
		initEngine( endPt, projectionScale);
		
		getAnimation().run(m_durationInMilliSecs);
	}

	public int getDurationInSecs() {
		return m_durationInMilliSecs;
	}

	public void setDurationInSecs(int durationInSecs) {
		m_durationInMilliSecs = durationInSecs;
	}

}
