package com.moesol.gwt.maps.client;

import java.util.Date;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.moesol.gwt.maps.client.stats.Sample;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;
import com.moesol.gwt.maps.shared.BoundingBox;

public class MapView extends Composite implements IMapView, SourcesChangeEvents {
	private static final long ONE_YEAR = 365 * 24 * 60 * 60 * 1000;
	
	private final AbsolutePanel m_viewPanel = new AbsolutePanel();
	private final FocusPanel m_focusPanel = new FocusPanel();
	private final MapController m_mapEventListener;

	private IProjection m_mapProj = null;
	private final ViewPort m_viewPort = new ViewPort();
	private final DivManager m_divMgr = new DivManager(this);
	
	private final AnimationEngine m_animateEngine = new AnimationEngine(this);
	private final FlyToEngine m_flyToEngine = new FlyToEngine(this);
	private final IconEngine m_iconEngine = new IconEngine(this);
	private DeclutterEngine m_declutterEngine; // null unless needed
	private final ChangeListenerCollection m_changeListeners = new ChangeListenerCollection();

	private final IconLayer m_iconLayer = new IconLayer();

	private int m_dpi = AbstractProjection.DOTS_PER_INCH;
	private boolean m_bSuspendMapAction = false;
	private boolean m_bDeclutterLabels = false;

	private double m_mapBrightness = 1.0;
	private boolean m_bProjSet = false;
	private double m_previousEqScale;
	private final EventBus m_eventBus;
	private final DynamicUpdateEngine m_dynamicUpdateEngine;

	public MapView() {
		this(new SimpleEventBus());
	}
	public MapView(final EventBus eventBus) {
		this(new CylEquiDistProj(), eventBus);
	}
	public MapView(final IProjection projection) {
		this(projection, new SimpleEventBus());
	}
	public MapView(final IProjection projection, final EventBus eventBus) {
		this(projection, eventBus, Degrees.geodetic(0, 0));
	}
	public MapView(final IProjection projection, final double defLat, GeodeticCoords defaultCenter) {
		this(projection, new SimpleEventBus(), defaultCenter);
	}
	public MapView(final IProjection projection, final EventBus eventBus, GeodeticCoords defaultCenter) {
		setProjection(projection);
		m_eventBus = eventBus;
		m_mapEventListener = new MapController(this, m_eventBus);
		m_dynamicUpdateEngine = new DynamicUpdateEngine(this, m_eventBus);
		initialize(defaultCenter);
	}

	
	private void initialize(GeodeticCoords defaultCenter) {
		ProjectionValues.readCookies(m_mapProj);
		
		setCenter(recoverCenter(defaultCenter.getLambda(AngleUnit.DEGREES), defaultCenter.getPhi(AngleUnit.DEGREES)));
		updateSize(m_viewPort.getWidth(), m_viewPort.getHeight());
	
		m_dynamicUpdateEngine.initDynamicRefreshTimer();

		m_focusPanel.setStyleName("moesol-MapView");
		m_focusPanel.getElement().setId("FocusPanelId");
		m_mapEventListener.bindHandlers(m_focusPanel);
		
		ViewDimension v = m_viewPort.getVpWorker().getDimension();
		m_viewPanel.setPixelSize(v.getWidth(), v.getHeight());
		m_viewPanel.getElement().getStyle().setZIndex(2000);
		m_viewPanel.getElement().setId("ViewPanelId");
		
		m_divMgr.setViewWorker(m_viewPort.getVpWorker());
		m_divMgr.attachDivsTo(m_viewPanel);
		m_focusPanel.setWidget(m_viewPanel);
		//placeDivPanels();
		initWidget(m_focusPanel);
		//doUpdateView();
	}
	
	public AbsolutePanel getViewPanel(){
		return m_viewPanel;
	}
	
	public IconEngine getIconEngine() { return m_iconEngine; }
	
	public EventBus getEventBus() {
		return m_eventBus;
	}
	
	public DivManager getDivManager() {
		return m_divMgr;
	}

	public void setProjection(IProjection proj) {
		m_mapProj = proj;
		m_previousEqScale = proj.getEquatorialScale()*8.0;
		m_viewPort.setProjection(proj);
	}
	
	private boolean setProjFromLayerSet(LayerSet ls) {
		if (!ls.isActive()) {
			return false;
		}
		if (m_mapProj != null) {
			if ( m_mapProj.doesSupport(ls.getSrs()) && m_bProjSet )
				return true;
		}

		IProjection proj = Projection.getProj(ls);
		if (proj != null) {
			setProjection(proj);
			return true;
		}

		return false;
	}

	public void addEventListener(EventListener eventListener) {
		this.addEventListener(eventListener);
	}

	public void setSuspendFlag(boolean flag) {
		this.m_bSuspendMapAction = flag;
	}
	public boolean isMapActionSuspended() {
		return this.m_bSuspendMapAction;
	}
	
	/**
	 * @return true when label declutter is enabled.
	 */
	public boolean isDeclutterLabels() {
		return m_bDeclutterLabels;
	}
	/**
	 * Set declutter labels flag. When declutter labels is enabled the map view will
	 * move the labels so that they do not overlap each other.
	 * @param bDeclutterLabels
	 */
	public void setDeclutterLabels(boolean bDeclutterLabels) {
		m_bDeclutterLabels = bDeclutterLabels;
	}
	
	public DeclutterEngine getDeclutterEngine() {
		if (m_declutterEngine == null) {
			m_declutterEngine = new DeclutterEngine(this);
		}
		return m_declutterEngine;
	}

	public MapController getController() {
		return m_mapEventListener;
	}

	public ViewPort getViewport() {
		return m_viewPort;
	}

	public void setTilePixDimensions(int width, int height) {
		m_viewPort.setTilePixWidth(width);
		m_viewPort.setTilePixHeight(height);
	}

	public void setDpi(int dpi) {
		m_dpi = dpi;
	}

	public int getDpi() {
		return m_dpi;
	}
	
	private GeodeticCoords recoverCenter(double defLng, double defLat) {
		String centerLng = Cookies.getCookie("centerLng");
		String centerLat = Cookies.getCookie("centerLat");
		if (centerLng == null || centerLat == null) {
			return new GeodeticCoords(defLng, defLat, AngleUnit.DEGREES);
		}
		try {
			double lng = Double.parseDouble(centerLng);
			double lat = Double.parseDouble(centerLat);
			return new GeodeticCoords(lng, lat, AngleUnit.DEGREES);
		} catch (NumberFormatException e) {
			return new GeodeticCoords(defLng, defLat, AngleUnit.DEGREES);
		}
	}
	
	/**
	 * Called when map changed and idle, when this method is called onIdle is not called.
	 */
	void onChangeAndIdle() {
		// Map went idle force suspend flag to off, work around bug in IE
		setSuspendFlag(false);
		
		// Things changed, save cookies
		recordCenter();
		ProjectionValues.writeCookies(getProjection());

		// View changed re-declutter
		if (isDeclutterLabels()) {
			getDeclutterEngine().incrementalDeclutter(
					getIconLayer().getIcons(), 
					m_iconEngine, 
					m_divMgr.getCurrentDiv().getDivWorker());
		}
	}
	
	/**
	 * Called when map goes idle. When this method is called onChangeAndIdle is not called.
	 */
	long m_oldIconVersion = 0L;
	void onIdle() {
		// Map went idle force suspend flag to off, work around bug in IE
		setSuspendFlag(false);
		
		if (!isDeclutterLabels()) {
			return;
		}
		if (m_oldIconVersion == getIconLayer().getVersion()) {
			return;
		}
		m_oldIconVersion = getIconLayer().getVersion();
		
		if (isDeclutterLabels()) {
			getDeclutterEngine().incrementalDeclutter(
					getIconLayer().getIcons(), 
					m_iconEngine, 
					m_divMgr.getCurrentDiv().getDivWorker());
		}
	}
	
	private void recordCenter() {
		String centerLng = Double.toString(getCenter().getLambda(
				AngleUnit.DEGREES));
		String centerLat = Double.toString(getCenter()
				.getPhi(AngleUnit.DEGREES));
		Date expire = new Date(new Date().getTime() + ONE_YEAR);
		Cookies.setCookie("centerLng", centerLng, expire);
		Cookies.setCookie("centerLat", centerLat, expire);
	}

	public void addLayersFromLayerConfig(ILayerConfigAsync config) {
		config.getLayerSets(new AsyncCallback<LayerSet[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get layer config " + caught);
			}

			@Override
			public void onSuccess(LayerSet[] result) {
				LayerSet[] layers = result;
				for (int i = 0; i < layers.length; i++) {
					addLayer(layers[i]);
				}
				updateView();
			}
		});
	}

	public void addLayer(LayerSet layerSet) {
		if (m_bProjSet == false) {
			m_bProjSet = setProjFromLayerSet(layerSet);
			if ( m_bProjSet ){
				m_divMgr.setProjFromLayerSet(layerSet);
			}
		}
		m_divMgr.addLayer(layerSet);
		return;
	}

	public void addLayers(LayerSet[] layers) {
		for (int i = 0; i < layers.length; i++) {
			addLayer(layers[i]);
		}
	}
	
	public void removeLayer(LayerSet layerSet) {
		m_divMgr.removeLayer(layerSet);
	}


	public void clearLayers() {
		m_divMgr.clearLayers();
	}

	public IconLayer getIconLayer() {
		return m_iconLayer;
	}

	public IProjection getProjection() {
		return m_mapProj;
	}

	/**
	 * Sets the map center
	 *
	 * @param latDegrees
	 *            (latitude in degrees)
	 * @param lngDegrees
	 *            (longitudes in degrees)
	 */
	// TODO remove this method use setCenter(Degrees.geodetic(lat, lng)) instead
	public void setCenter(double latDegrees, double lngDegrees) {
		GeodeticCoords center = Degrees.geodetic(latDegrees, lngDegrees);
		setCenter(center);
	}

	/**
	 * Center on a position and scale
	 *
	 * @param latDegrees
	 * @param lngDegrees
	 * @param scale
	 */
	// TODO remove this method does not properly encapsulate DEGREES/RADIANS
	public void setCenterScale(double latDegrees, double lngDegrees, double scale) {
		GeodeticCoords center = Degrees.geodetic(latDegrees, lngDegrees);
		if (scale > 0) {
			m_mapProj.setEquatorialScale(scale);
		}
		setCenter(center);
	}

	/**
	 * Sets the map center
	 *
	 * @param center
	 */
	public void setCenter(GeodeticCoords center) {
		m_viewPort.getVpWorker().setGeoCenter(center);
	}

	public GeodeticCoords getCenter() {
		return m_viewPort.getVpWorker().getGeoCenter();
	}

	public ViewCoords getViewCenter() {
		return m_viewPort.worldToView(getWorldCenter(), true);
	}

	public WorldCoords getWorldCenter() {
		return m_viewPort.getVpWorker().getVpCenterInWc();
	}

	/**
	 * This routine sets the viewport's center in WCs
	 * Is does not force an update of the map tiles.
	 * @param worldCenter
	 */
	public WorldCoords setWorldCenter(WorldCoords worldCenter) {
		WorldCoords wc = m_viewPort.constrainAsWorldCenter(worldCenter);
		m_viewPort.getVpWorker().setCenterInWc(wc);
		return wc;
	}

	private final Timer m_updateTimer = new Timer() {
		@Override
		public void run() {
			m_isUpdateTimerScheduled = false;
			doUpdateView();
		}
	};
	private boolean m_isUpdateTimerScheduled = false;
	private boolean m_resized = false;

	/**
	 * Match the view to the model data.
	 */
	public void updateView() {
		if (m_declutterEngine != null) {
			m_declutterEngine.cancelIncrementalDeclutter();
		}
		m_animateEngine.cancel();

		if (m_isUpdateTimerScheduled) {
			return;
		}
		m_updateTimer.schedule(1000/30);
		m_isUpdateTimerScheduled = true;
	}
	
	void cancelAnimations() {
		m_animateEngine.cancel();
		m_flyToEngine.getAnimation().cancel();
	}

	private boolean hasLevelChanged() {
		int prevLevel = m_mapProj.getLevelFromScale(m_previousEqScale, 0);
		double scale = m_mapProj.getEquatorialScale();
		int currentLevel = m_mapProj.getLevelFromScale(scale, 0);
		m_previousEqScale = m_mapProj.getEquatorialScale();
		return (prevLevel != currentLevel);
	}
	
	
	public void doUpdateView() {
		if (m_resized 
                        || hasLevelChanged() 
                        || m_divMgr.hasDivMovedToFar()) {
			m_resized = false;
			//System.out.println("fullUpdate");
			fullUpdateView();
		} else {
			//System.out.println("partialUpdate");
			partialUpdateView();
		}
                
		m_mapEventListener.fireMapViewChangeEventWithMinElapsedInterval(500);
		m_changeListeners.fireChange( this );
		
// TODO move to idle handling...
//		recordCenter();
//		ProjectionValues.writeCookies(m_proj);
	}
	
	public void fullUpdateView() {
		Sample.MAP_FULL_UPDATE.beginSample();
		// Do Not change the order of the next two
		// methods are called in
		m_divMgr.doUpdateDivsVisibility();
		m_divMgr.doUpdateDivsCenterScale( m_mapProj.getEquatorialScale() );
		m_divMgr.placeDivsInViewPanel( m_viewPanel );
		m_divMgr.positionIcons();
		Sample.MAP_FULL_UPDATE.endSample();
	}
	
	public void partialUpdateView() {
		Sample.MAP_PARTIAL_UPDATE.beginSample();
		m_divMgr.placeDivsInViewPanel( m_viewPanel );
		m_divMgr.positionIcons();
		Sample.MAP_PARTIAL_UPDATE.endSample();
	}
	
	public boolean hasAutoRefreshOnTimerLayers() {
		return m_divMgr.hasAutoRefreshOnTimerLayers();
	}

	@Override
	public String toString() {
		return getCenter().toString();
	}

	public void setFocus( boolean bFocus) {
		m_focusPanel.setFocus(bFocus);
	}

	@Override
	public void setPixelSize( int width, int height ) {
		super.setPixelSize(width, height);
		updateSize(width, height);
	}

	@Override
	public void setSize(String width, String height) {
		super.setSize(width, height);
                m_viewPanel.setSize(width, height);
		// below fails in IE
        //updateSize(getOffsetWidth(), getOffsetHeight());
	}

	private void updateSize(int width, int height) {
		m_viewPort.setSize(width, height);
		//m_iconsOverTilesPanel.setPixelSize(width, height);
		m_viewPanel.setPixelSize(width, height);
		m_focusPanel.setPixelSize(width, height);
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		m_changeListeners.add(listener);
	}

	@Override
	public void removeChangeListener(ChangeListener listener) {
		m_changeListeners.remove(listener);
	}
	
	// /////////////////////////////////////////////////////////////////
	// / User action calls /////////////////////////////////////////////
	/**
	 * Resize the map view to the width and height. Call this method when the
	 * map view is resized so that the internal view engine know what size to
	 * use.
	 */
	public void resizeMap(int w, int h) {
		m_resized  = true;
		setPixelSize(w, h);
		m_divMgr.resizeDivs(w,h);
		WorldCoords wc = m_viewPort.getVpWorker().getVpCenterInWc();
		setWorldCenter(wc);
		updateView();
	}

	/**
	 * Pan map by dx, dy;
	 *
	 * @param dx
	 * @param dy
	 * @return
	 */
	public void moveMapByPixels(int dx, int dy) {
		cancelAnimations();
		
		ViewWorker vpWorker = this.getViewport().getVpWorker();
		WorldCoords centerInWc = vpWorker.getVpCenterInWc();
		setWorldCenter(centerInWc.translate(dx, dy));
		updateView();
	}
	
	/**
	 * Animate zoom map, but keep the latitude and longitude under x, y 
	 * during the zoom and after its completion.
	 *
	 * @param x
	 * @param y
	 * @param bZoomIn
	 * @return
	 */
	public boolean zoomOnPixel(int x, int y, double scaleFactor) {
		if (m_bSuspendMapAction == false) {
			double scale = m_mapProj.getEquatorialScale();
			int level = m_mapProj.getLevelFromScale(scale*scaleFactor, 0.05);
			if (level < DivManager.NUMDIVS) {
				m_animateEngine.animateZoomMap( x, y, scaleFactor);
			}
		}
		return (m_bSuspendMapAction == false);
	}

	/**
	 * Non-animated zoom in.
	 */
	public void zoomByFactor(double zoomFactor) {
		m_mapProj.zoomByFactor(zoomFactor);
		// TODO confirm next two lines are needed.
		ViewWorker vp = m_viewPort.getVpWorker();
		vp.setCenterInWc(m_mapProj.geodeticToWorld(vp.getGeoCenter()));
		//updateView();
		doUpdateView();
	}
	
	//TODO
	public void zoomAndMove( final double factor, final int offsetX, final int offsetY ) {
		final ViewWorker vpWorker = this.getViewport().getVpWorker();
		m_mapProj.zoomByFactor(factor);
		final ViewDimension vd = vpWorker.getDimension();
		final WorldCoords wc = new WorldCoords(offsetX + vd.getWidth()/2, offsetY - vd.getHeight()/2);
		vpWorker.setCenterInWc(wc);
		doUpdateView();
	}

	public void zoom(double dScale) {
		m_mapProj.setEquatorialScale(dScale);
		updateView();
	}

	public void animateZoom(double scaleFactor) {
		ViewDimension v = m_viewPort.getVpWorker().getDimension();
		int x = v.getWidth() / 2;
		int y = v.getHeight() / 2;
		zoomOnPixel(x, y, scaleFactor);
	}

	/**
	 * Fly to center/scale.
	 * @param center
	 * @param scale
	 */
	public void flyTo(GeodeticCoords center, MapScale scale) {
		m_flyToEngine.flyTo(center, scale.asDouble());
	}
	
	/**
	 * Clients will call this routine to fly to a center lat, lng and scale
	 * based on a bounding box.
	 *
	 * @param box
	 */
	public void flyTo(BoundingBox box) {
		GeodeticCoords gc = new GeodeticCoords( box.getCenterLng(),
												box.getCenterLat(), 
												AngleUnit.DEGREES);
		m_flyToEngine.flyTo(gc, Projections.findScaleFor(getViewport(), box));
	}

	public int getDynamicRefreshMillis() {
		return m_dynamicUpdateEngine.getDynamicRefreshMillis();
	}

	public void setDynamicRefreshMillis(int dynamicRefreshMillis) {
		m_dynamicUpdateEngine.setDynamicRefreshMillis(dynamicRefreshMillis);
	}

	public long getDynamicCounter() {
		return m_dynamicUpdateEngine.getDynamicCounter();
	}

	public void setMapBrightness(double val) {
		this.m_mapBrightness = Math.min(1.0, Math.max(0.0, val));
	}

	public double getMapBrightness() {
		return m_mapBrightness;
	}

	public void incrementMapBrightness(double val) {
		if ((m_mapBrightness == 1.0 && val > 0)
				|| (m_mapBrightness == 0.0 && val < 0)) {
			return;
		}
		m_mapBrightness += val;
		setMapBrightness(m_mapBrightness);
		m_divMgr.setOpacity(getMapBrightness());
	}

	public void setAnimationDurationSeconds(int v) {
		m_animateEngine.setDurationInSecs(v);
	}

	public int getAnimationDurationSeconds() {
		return m_animateEngine.getDurationInSecs();
	}

	public FocusPanel getFocusPanel() {
		return m_focusPanel;
	}
	
	@Override
	public IProjection getTempProjection() {
		return m_mapProj.cloneProj();
	}
	
	public WidgetPositioner getWidgetPositioner() {
		return m_divMgr.getWidgetPositioner();
	}

//        private boolean iconsHaveMoved() {
//                return m_iconLayer.iconsHaveMoved();
//        }
	
}
