package com.moesol.gwt.maps.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.Widget;
import com.moesol.gwt.maps.client.IProjection.ZoomFlag;
import com.moesol.gwt.maps.client.stats.Sample;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;
import com.moesol.gwt.maps.shared.BoundingBox;

public class MapView extends Composite implements IMapView, SourcesChangeEvents {
	private static final Logger logger = Logger.getLogger(MapView.class.getName());
	private static final double BBOX_ZOOM_BUFFER = 2.0;
	private static final long ONE_YEAR = 365 * 24 * 60 * 60 * 1000;
	//private final AbsolutePanel m_iconsOverTilesPanel = new AbsolutePanel();
	private final AbsolutePanel m_tileLayersPanel = new AbsolutePanel();
	private final FocusPanel m_focusPanel = new FocusPanel();
	private final MapController m_mapEventListener;

	private IProjection m_projection = null;
	private IProjection m_tempProj;
	private final ViewPort m_viewPort = new ViewPort();
	private final AnimationEngine m_animateEngine = new AnimationEngine(this);
	private final FlyToEngine m_flyToEngine = new FlyToEngine(this);
	private final IconEngine m_iconEngine = new IconEngine(this);
	private DeclutterEngine m_declutterEngine; // null unless needed
	private final ChangeListenerCollection m_changeListeners = new ChangeListenerCollection();
	// private final MapControls mapControls = new MapControls(this);

	private GeodeticCoords m_center = new GeodeticCoords();
	private GeodeticCoords m_gc = new GeodeticCoords();

	private final IconLayer m_iconLayer = new IconLayer();
	private final ArrayList<TiledImageLayer> m_tiledImageLayers = new ArrayList<TiledImageLayer>();
	private int m_dpi = 75;
	private boolean m_bSuspendMapAction = false;
	private boolean m_bDeclutterLabels = false;

	private double m_mapBrightness = 1.0;
	private boolean m_firstSearch = true;
	private boolean m_bProjSet = false;
	private double m_defLat = 0;
	private double m_defLng = 0;
	private final EventBus m_eventBus;
	private final DynamicUpdateEngine m_dynamicUpdateEngine;
	private WidgetPositioner m_widgetPositioner;

	MapsClientBundle clientBundle = GWT.create(MapsClientBundle.class);
	

	public MapView() {
		this(Projection.T.CylEquiDist, new SimpleEventBus());
	}
	public MapView(final EventBus eventBus){
		this(Projection.T.CylEquiDist, eventBus);
	}
	public MapView(final Projection.T type) {
		this(type, new SimpleEventBus());
	}
	public MapView(final Projection.T type, final EventBus eventBus) {
		this(type, eventBus, 0, 0);
	}
	public MapView(final Projection.T type, final double defLat, final double defLng) {
		this(type, new SimpleEventBus(), defLat, defLng);
	}
	public MapView(final Projection.T type, final EventBus eventBus, final double defLat, final double defLng) {
		m_defLat = defLat;
		m_defLng = defLng;
		if (type == Projection.T.Mercator ) {
			setProjection(new Mercator(256,360.0,170.10226));
		} else {
			setProjection(new CylEquiDistProj(512,180.0,180.0));
		}
		m_eventBus = eventBus;
		m_mapEventListener = new MapController(this, m_eventBus);
		m_dynamicUpdateEngine = new DynamicUpdateEngine(this, m_eventBus);
		initialize();
	}

	public  void initialize() {
		ProjectionValues.readCookies(m_projection);
		setCenter(recoverCenter(m_defLng, m_defLat));
		initSize();
		updateView();
		m_dynamicUpdateEngine.initDynamicRefreshTimer();

		m_focusPanel.setStyleName("moesol-MapView");
		m_mapEventListener.bindHandlers(m_focusPanel);

		//m_iconsOverTilesPanel.add(m_tileLayersPanel);
		//m_focusPanel.setWidget(m_iconsOverTilesPanel);
		m_focusPanel.setWidget(m_tileLayersPanel);
		initWidget(m_focusPanel);
	}
	
	public EventBus getEventBus() {
		return m_eventBus;
	}

	public void setProjection(IProjection proj) {
		m_projection = proj;
		m_viewPort.setProjection(proj);
		m_tempProj = proj.cloneProj();
	}

	private boolean computeProjFromLayerSet(LayerSet ls) {
		if (!ls.isActive()) {
			return false;
		}
		if (m_projection != null) {
			if ( m_projection.doesSupport(ls.getEpsg()) && m_bProjSet )
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

	public TiledImageLayer addLayer(LayerSet layerSet) {
		if (m_bProjSet == false) {
			m_bProjSet = computeProjFromLayerSet(layerSet);
		}
		TiledImageLayer tiledImageLayer = new TiledImageLayer(this, layerSet, m_tileLayersPanel);
		m_tiledImageLayers.add(tiledImageLayer);
		return tiledImageLayer;
	}

	public void removeLayer(LayerSet layerSet) {
		int i = 0;
		for (TiledImageLayer layer : m_tiledImageLayers) {
			if (layer.getLayerSet().equals(layerSet)) {
				m_tiledImageLayers.remove(i);
				break;
			}
			i++;
		}
	}

	public void addLayers(LayerSet[] layers) {
		for (int i = 0; i < layers.length; i++) {
			addLayer(layers[i]);
		}
	}

	public void clearLayers() {
		for (TiledImageLayer layer : m_tiledImageLayers) {
			layer.destroy();
		}
		m_tiledImageLayers.clear();
	}

	private void initSize() {
		updateSize(m_viewPort.getWidth(), m_viewPort.getHeight());
	}

	public IconLayer getIconLayer() {
		return m_iconLayer;
	}

	public IProjection getProjection() {
		return m_projection;
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
			m_projection.setScale(scale);
		}
		setCenter(center);
	}

	/**
	 * Sets the map center
	 *
	 * @param center
	 */
	// TODO evaluate why we have duplicate state for center in MapView and Projection
	public void setCenter(GeodeticCoords center) {
		m_center = center;
		m_projection.setViewGeoCenter(center);
	}

	public GeodeticCoords getCenter() {
		return m_center;
	}

	public ViewCoords getViewCenter() {
		return m_viewPort.worldToView(getWorldCenter(), true);
	}

	public WorldCoords getWorldCenter() {
		return m_projection.geodeticToWorld(m_center);
	}

	public void setWorldCenter(WorldCoords worldCenter) {
		WorldCoords constrained = m_viewPort.constrainAsWorldCenter(worldCenter);
		GeodeticCoords center = m_projection.worldToGeodetic(constrained);
		setCenter(center);
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

	// This will change when we add tile coords for each layer set

	private void placeTiles(int level, TiledImageLayer layer) {
		TileCoords[] tileCoords = m_viewPort.arrangeTiles(layer, level);
		
		if (layer.getLayerSet().isAlwaysDraw() || layer.isPriority()) {
			layer.setTileCoords(tileCoords);
			layer.setLevel(level);
			
			layer.updateView();
		}
	}

	private void computeLevelsAndTileCoords() {
		int dpi = m_projection.getScrnDpi();
		double projScale = m_projection.getScale();
		for (TiledImageLayer layer : m_tiledImageLayers) {
			LayerSet ls = layer.getLayerSet();
			if (!ls.isActive()) {
				layer.updateView(); // Force hiding inactive layers, but skip placing their tiles.
				continue;
			}
			if (ls.isAlwaysDraw() || layer.isPriority()) {
				int level = layer.findLevel(dpi, projScale);
				placeTiles(level, layer);
			}
		}
	}

	void preUpdateView() {
		computeLevelsAndTileCoords();
	}

/*
	private double syncProjScale(ZoomFlag zoomFlag, double scale) {
		double tScale = m_projection.getScale();
		if (zoomFlag == ZoomFlag.IN) {// zooming in
			tScale = (scale >= tScale ? scale : scale * 2.0);
		} else if (zoomFlag == ZoomFlag.OUT) { // zooming out
			tScale = (scale <= tScale ? scale : scale / 2.0);
		}
		return tScale;
	}
*/

	/**
	 * Marks the tiled image layer that is the best for this projection as
	 * priority.
	 *
	 * @param zoomFlag
	 */
	public void setLayerBestSuitedForScale(ZoomFlag zoomFlag) {

		if (zoomFlag == ZoomFlag.NONE && !m_firstSearch) {
			// optimize pans etc.
			return;
		}

		m_firstSearch = false;
		int dpi = m_projection.getScrnDpi();
		double projScale = m_projection.getScale();

		TiledImageLayer bestLayerSoFar = null;
		int LevelWithBestScaleSoFar = -10000;
		double bestScaleSoFar = 0.0;
		for (TiledImageLayer layer : getActiveLayers()) {
			layer.setPriority(false);

			int level = layer.findLevel(dpi, projScale);
			if (!isLayerCandidateForScale(layer, level)) {
				layer.setPriority(false);
				continue;
			}
			double layerScale = layer.findScale(dpi, level);
			if (bestLayerSoFar == null) {
				bestLayerSoFar = layer;
				bestScaleSoFar = layerScale;
				LevelWithBestScaleSoFar = level;
				continue;
			}
			if (bestScaleSoFar == layerScale) {
				if (level >= 0 && level < LevelWithBestScaleSoFar) {
					bestLayerSoFar = layer;
					bestScaleSoFar = layerScale;
					LevelWithBestScaleSoFar = level;
				}
				continue;
			}
			double oldDistance = Math.abs(projScale - bestScaleSoFar);
			double newDistance = Math.abs(projScale - layerScale);
			if (newDistance < oldDistance) {
				bestLayerSoFar = layer;
				bestScaleSoFar = layerScale;
				LevelWithBestScaleSoFar = level;
			}
		}
		if (bestLayerSoFar != null) {
			bestLayerSoFar.setPriority(true);
			LayerSet ls = bestLayerSoFar.getLayerSet();
			int tileWidth = ls.getPixelWidth();
			double degWidth = ls.getStartLevelTileWidthInDeg();
			double degHeight = ls.getStartLevelTileHeightInDeg();
			m_projection.synchronize(tileWidth, degWidth, degHeight);
		}
	}

	private boolean isLayerCandidateForScale(TiledImageLayer layer, int level) {
		LayerSet layerSet = layer.getLayerSet();
		if (layerSet.isAlwaysDraw()) {
			return false;
		}
		if (!layerSet.useToScale()) {
			return false;
		}
		if (!layerSet.levelIsInRange(level)) {
			return false;
		}

		return true;
	}

	/**
	 * Internal method, use updateView from clients.
	 */
	public void doUpdateView() {
		Sample.MAP_UPDATE_VIEW.beginSample();

		ZoomFlag zoomFlag = m_projection.getZoomFlag();
		m_projection.setZoomFlag(ZoomFlag.NONE);
		Sample.BEST_LAYER.beginSample();
		setLayerBestSuitedForScale(zoomFlag);
		Sample.BEST_LAYER.endSample();
		
		computeLevelsAndTileCoords();

		m_iconEngine.positionIcons();
		
		Sample.MAP_UPDATE_VIEW.endSample();
		
		m_changeListeners.fireChange(this); // TODO remove this and all uses of ChangeListener use event below instead which only happens after map idle for a while...
		m_mapEventListener.fireMapViewChangeEventWithMinElapsedInterval(500);
	}

	public void hideAnimatedTiles() {
		if (m_mapBrightness < 1.0) {
			for (TiledImageLayer layer : getActiveLayers()) {
				layer.hideAnimatedTiles();
			}
		}
	}

	boolean hasAutoRefreshOnTimerLayers() {
		for (TiledImageLayer layer : getActiveLayers()) {
			if (layer.getLayerSet().isAutoRefreshOnTimer()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		// TODO show width too
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
		m_tileLayersPanel.setPixelSize(width, height);
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
		// Since this method is used to resize the widget
		// I'm not sure how it can be "suspended".
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
	public boolean moveMapByPixels(int dx, int dy) {
		m_flyToEngine.getAnimation().cancel();
		// TODO replace suspend flag with cancel animation.
		if (m_bSuspendMapAction == false) {
			ViewDimension v = m_projection.getViewSize();
			ViewCoords vc = new ViewCoords(v.getWidth() / 2 + dx, v.getHeight() / 2 + dy);
			m_projection.setCenterFromViewPixel(vc);
			setCenter(m_projection.getViewGeoCenter());
			updateView();
		}
		return (m_bSuspendMapAction == false);
	}

	/**
	 * Animate zoom map, but keep the latitude and longitude under x, y at x, y
	 * during the zoom and after its completion.
	 *
	 * @param x
	 * @param y
	 * @param bZoomIn
	 * @return
	 */
	public boolean zoomOnPixel(int x, int y, double scaleFactor) {
		boolean bZoomIn = (scaleFactor < 1.0 ? false : true);
		if (m_bSuspendMapAction == false) {
			ViewCoords vc = ViewCoords.builder().setX(x).setY(y).build();
			m_gc = m_projection.viewToGeodetic(vc);
			if (bZoomIn) {
				m_projection.zoomByFactor(scaleFactor);
				preUpdateView();
				// zoom out
				m_projection.zoomByFactor(1.0 / scaleFactor);
				preUpdateView(); // put it back
				m_projection.zoomByFactor(scaleFactor);
			} else {
				m_projection.zoomByFactor(scaleFactor);
				preUpdateView();
			}
			//compTileDegDimensions();
			m_projection.tagPositionToPixel(m_gc, vc);
			setCenter(m_projection.getViewGeoCenter());
			m_animateEngine.setTiledImageLayers(getActiveLayers());
			m_flyToEngine.getAnimation().cancel();
			m_animateEngine.animateZoomMap(x, y, scaleFactor);
		}
		return (m_bSuspendMapAction == false);
	}

	/**
	 * Non-animated zoom in.
	 */
	public void zoomByFactor(double zoomFactor) {
		/**
		if (!allTilesLoaded()) {
			return;
		}
		 **/
		m_projection.zoomByFactor(zoomFactor);
		updateView();
	}

	public void zoom(double dScale) {
		/**
		if (!allTilesLoaded()) {
			return;
		}
		**/
		m_projection.setScale(dScale);
		updateView();
	}

	public void animateZoom(double scaleFactor) {
		ViewDimension v = m_projection.getViewSize();
		int x = v.getWidth() / 2;
		int y = v.getHeight() / 2;
		zoomOnPixel(x, y, scaleFactor);
	}

	public void setPanelPercentSize(double percent) {
		Element el = m_focusPanel.getElement();
		Style s = el.getStyle();
		s.setWidth(percent, Unit.PC);
		s.setHeight(percent, Unit.PC);
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
		updateView();
	}

	public void setAnimationDurationSeconds(int v) {
		m_animateEngine.setDurationInSecs(v);
	}

	public int getAnimationDurationSeconds() {
		return m_animateEngine.getDurationInSecs();
	}

	public boolean allTilesLoaded() {
		/**
		for (TiledImageLayer layer : m_tiledImageLayers) {
			if (layer.areAllLoaded() == false) {
				return false;
			}
		}
		 **/
		return true;
	}
	
	public ArrayList<TiledImageLayer> getActiveLayers() {
		ArrayList<TiledImageLayer> result = new ArrayList<TiledImageLayer>();
		for (TiledImageLayer layer : m_tiledImageLayers) {
			if (layer.getLayerSet().isActive()) {
				result.add(layer);
			}
		}
		return result;
	}

	public FocusPanel getFocusPanel() {
		return m_focusPanel;
	}
	
	public AbsolutePanel getIconPanel() {
		return m_tileLayersPanel;
	}
	
	@Override
	public IProjection getTempProjection() {
		return m_tempProj;
	}
	
	void drawIcons(double scale, double offsetX, double offsetY) {
		m_iconEngine.drawIcons(scale, offsetX, offsetY);
	}
	
	@Override
	public WidgetPositioner getWidgetPositioner() {
		if (m_widgetPositioner == null) {
			m_widgetPositioner = new WidgetPositioner() {
				@Override
				public void place(Widget widget, int x, int y) {
					if (widget.getParent() == null) {
						getIconPanel().add(widget, x, y);
					} else {
						getIconPanel().setWidgetPosition(widget, x, y);
					}
				}
				@Override
				public void remove(Widget widget) {
					getIconPanel().remove(widget);
				}
			};
		}
		return m_widgetPositioner;
	}
	
}
