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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;
import com.moesol.gwt.maps.shared.BoundingBox;

public class MapView extends Composite implements IMapView, SourcesChangeEvents {
	private static final Logger logger = Logger.getLogger(MapView.class.getName());
	private static final double BBOX_ZOOM_BUFFER = 2.0;
	private static final long ONE_YEAR = 365 * 24 * 60 * 60 * 1000;
	//private final AbsolutePanel m_iconsOverTilesPanel = new AbsolutePanel();
	
	private final AbsolutePanel m_viewPanel = new AbsolutePanel();
	private final FocusPanel m_focusPanel = new FocusPanel();
	private final MapController m_mapEventListener;

	private IProjection m_proj = null;
	private IProjection m_tempProj;
	private final ViewPort m_viewPort = new ViewPort();
	private final DivManager m_divMgr = new DivManager(this);
	
	private final AnimationEngine m_animateEngine = new AnimationEngine(this);
	private final FlyToEngine m_flyToEngine = new FlyToEngine(this);
	private final IconEngine m_iconEngine = new IconEngine(this);
	private DeclutterEngine m_declutterEngine; // null unless needed
	private final ChangeListenerCollection m_changeListeners = new ChangeListenerCollection();
	// private final MapControls mapControls = new MapControls(this);

	private final IconLayer m_iconLayer = new IconLayer();

	private int m_dpi = 75;
	private boolean m_bSuspendMapAction = false;
	private boolean m_bDeclutterLabels = false;

	private double m_mapBrightness = 1.0;
	private boolean m_bProjSet = false;
	private double m_previousEqScale;
	private final EventBus m_eventBus;
	private final DynamicUpdateEngine m_dynamicUpdateEngine;
	private WidgetPositioner m_widgetPositioner;

	MapsClientBundle clientBundle = GWT.create(MapsClientBundle.class);

	public MapView() {
		this(new SimpleEventBus());
	}
	public MapView(final EventBus eventBus){
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

	void initialize(GeodeticCoords defaultCenter) {
		ProjectionValues.readCookies(m_proj);
		
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
	
	public EventBus getEventBus() {
		return m_eventBus;
	}

	public void setProjection(IProjection proj) {
		m_proj = proj;
		m_previousEqScale = proj.getEquatorialScale()*8.0;
		m_viewPort.setProjection(proj);
		m_tempProj = proj.cloneProj();
	}
	
	public boolean setProjFromLayerSet( LayerSet ls ){
		if (!ls.isActive()) {
			return false;
		}
		if ( m_proj != null ){
			if ( m_proj.doesSupport(ls.getEpsg()) && m_bProjSet )
				return true;
		}

		IProjection proj = Projection.getProj(ls);
		if ( proj != null ){
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
			getDeclutterEngine().incrementalDeclutter(getIconLayer().getIcons(), m_iconEngine);
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
		
		getDeclutterEngine().incrementalDeclutter(getIconLayer().getIcons(), m_iconEngine);
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
		return m_proj;
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
			m_proj.setEquatorialScale(scale);
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
	public void setWorldCenter(WorldCoords worldCenter) {
		WorldCoords wc = m_viewPort.constrainAsWorldCenter(worldCenter);
		m_viewPort.getVpWorker().setCenterInWc(wc);
	}

	private Timer m_updateTimer = null;

	/**
	 * Match the view to the model data.
	 */
	public void updateView() {
		if (m_declutterEngine != null) {
			m_declutterEngine.cancelIncrementalDeclutter();
		}
		
		if (m_updateTimer != null) {
			return;
		}
		m_updateTimer = new Timer() {
			@Override
			public void run() {
				m_updateTimer = null;
				doUpdateView();
			}
		};
		m_updateTimer.schedule(1000 / 30);
	}


	private boolean shouldUpdateDivs() {
		int prevLevel = m_proj.getLevelFromScale(m_previousEqScale);
		double scale = m_proj.getEquatorialScale();
		int currentLevel = m_proj.getLevelFromScale(scale);
		m_previousEqScale = m_proj.getEquatorialScale();
		return true;//( prevLevel != currentLevel);

	}
	
	public void doUpdateView() {
		if ( shouldUpdateDivs() ) {
			m_divMgr.doUpdateDivs( 2, m_proj.getEquatorialScale() );
		}
		m_divMgr.placeDivPanels( m_viewPanel, 2 );
		//positionIcons();
		m_changeListeners.fireChange(this);
		// TODO move to idle handling...
//		recordCenter();
		ProjectionValues.writeCookies(m_proj);
	}
	
	public void moveDivPanelsOffset( int deltaX, int deltaY ){
		m_divMgr.moveDivPanelsOffset( m_viewPanel, 2, deltaX, deltaY );
	}

	public void udateAfterZooming(){
		updateView();
	}

	public void hideAnimatedTiles() {
		if (m_mapBrightness < 1.0) {
			m_divMgr.hideAnimatedTiles();
		}
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
	public void setPixelSize(int width, int height) {
		super.setPixelSize(width, height);
		updateSize(width, height);
	}

	@Override
	public void setSize(String width, String height) {
		super.setSize(width, height);
		// below fails in IE
		updateSize(getOffsetWidth(), getOffsetHeight());
	}

	private void updateSize(int width, int height) {
		m_viewPort.setSize(width, height);
		//m_iconsOverTilesPanel.setPixelSize(width, height);
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
	 * Resize the map view to the width and height. Call this method with the
	 * map view is resized so that the internal view engine know what size to
	 * use.
	 */
	public void resizeMap(int w, int h) {
		m_focusPanel.setPixelSize(w, h);
		m_viewPanel.setPixelSize(w, h);
		m_viewPort.getVpWorker().setDimension(new ViewDimension(w, h));
		m_divMgr.resizeDivs(w,h);
		setPixelSize(w, h);
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
		m_flyToEngine.getAnimation().cancel();
		// TODO replace suspend flag with cancel animation.
		if (m_bSuspendMapAction == false) {
			return;
		}
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
			m_animateEngine.animateZoomMap( x, y, scaleFactor);
		}
		return (m_bSuspendMapAction == false);
	}

	/**
	 * Non-animated zoom in.
	 */
	public void zoomByFactor(double zoomFactor) {
		m_proj.zoomByFactor(zoomFactor);
		ViewWorker vp = m_viewPort.getVpWorker();
		vp.setCenterInWc(m_proj.geodeticToWorld(vp.getGeoCenter()));
		updateView();
	}
	
	//TODO
	public void zoomAndMove( final double factor, final int offsetX, final int offsetY ) {
		final ViewWorker vpWorker = this.getViewport().getVpWorker();
		m_proj.zoomByFactor(factor);
		final ViewDimension vd = vpWorker.getDimension();
		final WorldCoords wc = new WorldCoords(offsetX + vd.getWidth()/2, offsetY - vd.getHeight()/2);
		vpWorker.setCenterInWc(wc);
		
		m_divMgr.placeDivPanels( m_viewPanel, 2 );
	}

	public void zoom(double dScale) {
		m_proj.setEquatorialScale(dScale);
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
		m_flyToEngine.flyTo(center.getPhi(AngleUnit.DEGREES), center.getLambda(AngleUnit.DEGREES), scale.asDouble());
	}
	
	/**
	 * Clients will call this routine to fly to a center lat, lng and scale
	 * based on a bounding box.
	 *
	 * @param box
	 */
	public void flyTo(BoundingBox box) {
		m_flyToEngine.flyTo(box.getCenterLat(), box.getCenterLng(), Projections.findScaleFor(getViewport(), box));
	}

	/**
	 * This routine centers on a position with a given scale.
	 *
	 * @param lat
	 * @param lng
	 * @param scale
	 * 
	 * @deprecated use GeodeticCoords instead.
	 */
	public void centerOn(double lat, double lng, double scale) {
		setCenter(lat, lng);
		updateView();
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

	// TODO move to div manager.
//	public ArrayList<TiledImageLayer> getActiveLayers() {
//		ArrayList<TiledImageLayer> result = new ArrayList<TiledImageLayer>();
//		for (TiledImageLayer layer : m_tiledImageLayers) {
//			if (layer.getLayerSet().isActive()) {
//				result.add(layer);
//			}
//		}
//		return result;
//	}

	public FocusPanel getFocusPanel() {
		return m_focusPanel;
	}
	
	@Override
	public IProjection getTempProjection() {
		return m_tempProj;
	}
	
	@Override
	public WidgetPositioner getWidgetPositioner() {
		return null;
	}
	
//	@Override
//	public WidgetPositioner getWidgetPositioner() {
//		if (m_widgetPositioner == null) {
//			m_widgetPositioner = new WidgetPositioner() {
//				@Override
//				public void place(Widget widget, int x, int y) {
//					if (widget.getParent() == null) {
//						getIconPanel().add(widget, x, y);
//					} else {
//						getIconPanel().setWidgetPosition(widget, x, y);
//					}
//				}
//				@Override
//				public void remove(Widget widget) {
//					getIconPanel().remove(widget);
//				}
//			};
//		}
//		return m_widgetPositioner;
//	}
	
}
