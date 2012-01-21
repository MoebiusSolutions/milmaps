package com.moesol.gwt.maps.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.moesol.gwt.maps.client.IProjection.ZoomFlag;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.shared.BoundingBox;

public class MapView extends Composite implements SourcesChangeEvents {
	private static final double BBOX_ZOOM_BUFFER = 2.0;
	private static final long ONE_YEAR = 365 * 24 * 60 * 60 * 1000;
	//private final AbsolutePanel m_iconsOverTilesPanel = new AbsolutePanel();
	
	private final AbsolutePanel m_viewPanel = new AbsolutePanel();
	private final FocusPanel m_focusPanel = new FocusPanel();
	private final MapController m_mapEventListener = new MapController(this);

	private IProjection m_proj = null;
	private IProjection m_tempProj;
	private final ViewPort m_viewPort = new ViewPort();
	private final DivManager m_divMgr = new DivManager(this);
	private final ViewCoords m_vc = new ViewCoords();
	private final WorldCoords m_wc = new WorldCoords();
	private final WorldCoords m_constrained = new WorldCoords();
	
	
	private final AnimationEngine m_animateEngine = new AnimationEngine(this);
	private final FlyToEngine m_flyToEngine = new FlyToEngine(this);
	private final ChangeListenerCollection m_changeListeners = new ChangeListenerCollection();
	// private final MapControls mapControls = new MapControls(this);

	private final GeodeticCoords m_gc = new GeodeticCoords();

	private final IconLayer m_iconLayer = new IconLayer();

	private int m_dpi = 75;
	private boolean m_bSuspendMapAction = false;
	private double m_mapBrightness = 1.0;
	private boolean m_bProjSet = false;
	private double m_defLat = 0;
	private double m_defLng = 0;
	private double m_previousEqScale;
	private Date m_date = new Date();
	
	public MapView(){
		this(IProjection.T.CylEquiDist);
	}

	public MapView( IProjection.T type ) {
		if ( type == IProjection.T.Mercator ){
			setProjection(new Mercator());
		}else{
			setProjection(new CylEquiDistProj());
		}
		initialize();
	}

	MapsClientBundle clientBundle = GWT.create(MapsClientBundle.class);

	// MapControls getMapControls() {
	// return mapControls;
	// }
	
	public MapView( IProjection.T type, double defLat, double defLng) {
		m_defLat = defLat;
		m_defLng = defLng;
		if ( type == IProjection.T.Mercator ){
			setProjection(new Mercator());//256,360,170.10226));
		}else{
			setProjection(new CylEquiDistProj());
		}
		initialize();
	}

	public  void initialize() {
		ProjectionValues.readCookies(m_proj);
		setCenter(recoverCenter(m_defLng, m_defLat));
		updateSize(m_viewPort.getWidth(), m_viewPort.getHeight());
		m_divMgr.setCenter(m_gc);
		ViewWorker vw = m_viewPort.getVpWorker();
		vw.setVpCenterInWc(m_proj.geodeticToWorld(m_gc));
	
		initDynamicRefreshTimer();

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

	public void setProjection(IProjection proj){
		m_proj = proj;
		m_previousEqScale = proj.getEquatorialScale()*8.0;
		m_viewPort.setProjection(proj);
		m_tempProj = proj.cloneProj();
	}
	
	public boolean setProjFromLayerSet( LayerSet ls ){
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
	
	public void computeGeoCenter(){
		m_viewPort.getVpWorker().computeGeoCenter();
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
		if ( m_bProjSet == false ){
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

	public void setCenter(double latDegrees, double lngDegrees) {
		GeodeticCoords center = new GeodeticCoords();
		center.setPhi(latDegrees, AngleUnit.DEGREES);
		center.setLambda(lngDegrees, AngleUnit.DEGREES);
		setCenter(center);
	}

	/**
	 * Center on a position and scale
	 * 
	 * @param latDegrees
	 * @param lngDegrees
	 * @param scale
	 */
	public void setCenterScale(double latDegrees, double lngDegrees,
			double scale) {
		GeodeticCoords center = new GeodeticCoords();
		center.setPhi(latDegrees, AngleUnit.DEGREES);
		center.setLambda(lngDegrees, AngleUnit.DEGREES);
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
		m_divMgr.setCenter(center);
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
		m_constrained.copyFrom(worldCenter);
		m_viewPort.constrainAsWorldCenter(m_constrained);
		m_viewPort.getVpWorker().setVpCenterInWc(m_constrained);
		m_viewPort.getVpWorker().computeGeoCenter();
		m_divMgr.setCenter(m_viewPort.getVpWorker().getGeoCenter());
	}

	private Timer m_updateTimer = null;
	private Timer m_dynamicTimer = null;
	private int m_dynamicRefreshMillis = 10000;
	private long m_dynamicCounter = new Date().getTime();

	/**
	 * Match the view to the model data.
	 */
	public void updateView() {
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


	private boolean shouldUpdateDivs(){
		int prevLevel = m_proj.getLevelFromScale(m_previousEqScale);
		double scale = m_proj.getEquatorialScale();
		int currentLevel = m_proj.getLevelFromScale(scale);
		m_previousEqScale = m_proj.getEquatorialScale();
		return true;//( prevLevel != currentLevel);
	}
	
	void doUpdateView() {
		if ( shouldUpdateDivs() ){
			m_viewPort.getVpWorker().update(true);
			m_divMgr.doUpdateDivs( 2, m_proj.getEquatorialScale() );
		}
		m_divMgr.placeDivPanels( m_viewPanel, 2 );
		//positionIcons();
		m_changeListeners.fireChange(this);
		recordCenter();
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

	private void initDynamicRefreshTimer() {
		if (m_dynamicTimer != null) {
			return;
		}
		m_dynamicTimer = new Timer() {
			@Override
			public void run() {
				doDynamicRefresh();
			}
		};
		m_dynamicTimer.scheduleRepeating(m_dynamicRefreshMillis);
	}

	private void doDynamicRefresh() {
		m_dynamicCounter++;

		if (isMapActionSuspended()) {
			// An animation is in progress so tiles will be updating anyway
			return;
		}
		if (!hasAutoRefreshOnTimerLayers()) {
			return;
		}
		updateView();
	}

	private boolean hasAutoRefreshOnTimerLayers() {
		return m_divMgr.hasAutoRefreshOnTimerLayers();
	}

	public void drawIcons() {
		List<Icon> icons = getIconLayer().getIcons();
		ZoomFlag flag = m_proj.getZoomFlag();
		double val = (flag == ZoomFlag.IN ? m_proj.getPrevEquatorialScale()
				: m_proj.getEquatorialScale());
		m_tempProj.setEquatorialScale(val);
		for (Icon icon : icons) {
			drawIcon(icon);
		}
	}

	private void drawIcon(Icon icon) {
		m_wc.copyFrom( m_proj.geodeticToWorld(icon.getLocation()) );
		m_vc.copyFrom(m_viewPort.worldToView(m_wc, false));
		Image image = icon.getImage();
		int x = m_vc.getX() + icon.getIconOffset().getX();
		int y = m_vc.getY() + icon.getIconOffset().getY();
		//m_iconsOverTilesPanel.setWidgetPosition(image, x, y);
		//TODO Fix this KBT
		//m_tileLayersPanel.setWidgetPosition(image, x, y);
		//Label label = icon.getLabel();
		//if ( label != null )
			//m_tileLayersPanel.setWidgetPosition(label, x+18, y);
	}

	private void positionIcons() {
		List<Icon> icons = getIconLayer().getIcons();

		for (Icon icon : icons) {
			positionOneIcon(icon);
		}
	}

	//TODO Fix this KBT m_tileLayersPanel.setWidgetPosition "see below".
	private void positionOneIcon(Icon icon) {
		m_wc.copyFrom( m_proj.geodeticToWorld(icon.getLocation()) );
		m_vc.copyFrom(m_viewPort.worldToView(m_wc, true));
		Image image = icon.getImage();
		int x = m_vc.getX() + icon.getIconOffset().getX();
		int y = m_vc.getY() + icon.getIconOffset().getY();
		if (image.getParent() == null) {
			//m_iconsOverTilesPanel.add(image, x, y);
			//m_tileLayersPanel.add(image, x, y);
		} else {
			//m_iconsOverTilesPanel.setWidgetPosition(image, x, y);
			//m_tileLayersPanel.setWidgetPosition(image, x, y);
		}
		Label label = icon.getLabel();
		if ( label != null ){
			x += icon.getImagePixWidth() + 4;
			if (label.getParent() == null) {
				//m_tileLayersPanel.add(label, x, y);
			} else {
				//m_tileLayersPanel.setWidgetPosition(label, x, y);
			}
		}
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
	
	private void resizeDivs( int w, int h){
		m_divMgr.resizeDivs(w,h);
		m_focusPanel.setPixelSize(w, h);
		ViewDimension vd = m_viewPort.getVpWorker().getDimension();
		vd.setWidth(w);
		vd.setHeight(h);
		m_viewPanel.setPixelSize(w, h);
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
		resizeDivs( w, h );
		setPixelSize(w, h);
		this.getViewport().getVpWorker().update(true);
		//updateDivPanel();
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
		if (m_bSuspendMapAction == false) {
			ViewWorker vpWorker = this.getViewport().getVpWorker();
			m_wc.copyFrom(vpWorker.getVpCenterInWc());
			m_wc.setX(m_wc.getX() + dx);
			m_wc.setY(m_wc.getY() + dy);
			setWorldCenter(m_wc);
			//updateDivPanel();
			updateView();
		}
		return (m_bSuspendMapAction == false);
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
		vp.setVpCenterInWc(m_proj.geodeticToWorld(vp.getGeoCenter()));
		updateView();
	}
	
	//TODO
	public void ZoomAndMove( double factor, int offsetX, int offsetY ){
		ViewWorker vpWorker = this.getViewport().getVpWorker();
		m_proj.zoomByFactor(factor);
		ViewDimension vd = vpWorker.getDimension();
		m_wc.setX(offsetX + vd.getWidth()/2);
		m_wc.setY(offsetY - vd.getHeight()/2);
		vpWorker.setVpCenterInWc(m_wc);
		m_viewPort.getVpWorker().computeGeoCenter();
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
	 * Clients will call this routine to fly to a center lat, lng.
	 * 
	 * @param lat
	 * @param lng
	 * @param scale
	 */
	public void flyTo(double lat, double lng, double zoomX) {
		m_flyToEngine.flyTo(lat, lng, zoomX);
	}

	/**
	 * Clients will call this routine to fly to a center lat, lng and scale
	 * based on a bounding box.
	 * 
	 * @param box
	 */
	public void flyTo(BoundingBox box) {
		double minLon = box.getMinLon();
		double maxLon = box.getMaxLon();
		double minLat = box.getMinLat();
		double maxLat = box.getMaxLat();
		double pixWidth = m_proj.compWidthInPixels( minLon, maxLon);
		double pixHeight = m_proj.compHeightInPixels( minLat, maxLat);
		double zoomX = 1.0;
		double zH = ((double)m_viewPort.getHeight()) / (pixHeight + BBOX_ZOOM_BUFFER);
		double zW = ((double)m_viewPort.getWidth()) / (pixWidth + BBOX_ZOOM_BUFFER);
		zoomX = Math.min(zH, zW);
		m_flyToEngine.flyTo(box.getCenterLat(), box.getCenterLng(), zoomX);
	}

	/**
	 * This routine centers on a position with a given scale.
	 * 
	 * @param lat
	 * @param lng
	 * @param scale
	 */
	public void centerOn(double lat, double lng, double scale) {
		setCenter(lat, lng);
		updateView();
	}

	public int getDynamicRefreshMillis() {
		return m_dynamicRefreshMillis;
	}

	public void setDynamicRefreshMillis(int dynamicRefreshMillis) {
		m_dynamicRefreshMillis = dynamicRefreshMillis;
	}

	public long getDynamicCounter() {
		return m_dynamicCounter;
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

	public void timerZoom(final boolean zoomIn, final double scaleFactor) {
		final int INTERVAL_MILSECS = 100;
		Timer timer = new Timer() {
			int iCount = 0;
			final int TIMER_COUNT = 10;

			@Override
			public void run() {
				iCount++;
				if (iCount > TIMER_COUNT) {
					zoomByFactor(scaleFactor);
					cancel();
				}
			}
		};
		timer.schedule(INTERVAL_MILSECS);
	}
	
	public FocusPanel getFocusPanel() {
		return m_focusPanel;
	}
}
