package com.moesol.gwt.maps.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.AbsolutePanel;

public class DivManager {
	private static int LEVEL_RANGE = 1;
	public static final int NUMDIVS = 20;
	private int m_currentLevel = 0;
	private int m_oldLevel = 0;
	private IProjection m_proj= null;
	private final IMapView m_map;
	private double m_opacity = 1.0;
	private double m_oldOpacity = 1.0;
	//private Browser.Type m_browser;
	
	private ViewWorker m_vpWorker = null;
	DivPanel[] m_dpArray = new DivPanel[NUMDIVS];
	private List<LayerSet> m_layerSets = new ArrayList<LayerSet>();
	private DivDimensions m_lastResize = new DivDimensions();
	
	public DivManager(IMapView map){
		m_map = map;
	}
	
	public void attachDivsTo(AbsolutePanel lp){
		for( int i = 0; i < NUMDIVS; i++ ){
			m_dpArray[i] = new DivPanel(i);
			lp.add(m_dpArray[i]);
		}
		
		// we will initialize the projection, but the projection
		// may change when the layer sets are loaded.
		initProjections( 512, 512,180,180,IProjection.T.CylEquiDist);
	}
	private void ensureDivPanel(int level) {
		if (divPanelExists(level)) {
			return;
		}
		m_dpArray[level] = new DivPanel(level);
		getMapView().getViewPanel().add(m_dpArray[level]);
		copyLayerSets(m_dpArray[level]);
		initOneDivPanel(level);
	}
	private void detachDivPanel(int level) {
		if (!divPanelExists(level)) {
			return;
		}
		
		m_dpArray[level].close();
		getMapView().getViewPanel().remove(m_dpArray[level]);
		m_dpArray[level] = null;
	}
	private boolean divPanelExists(int level) {
		return m_dpArray[level] != null;
	}
	private void copyLayerSets(DivPanel divPanel) {
		for (LayerSet ls : m_layerSets) {
			divPanel.addLayer(ls);
		}
	}

	public MapView getMapView() {
		MapView mapView = (MapView) m_map; // TODO
		return mapView;
	}
	
	public boolean isProjectionSet(){
		return ( m_proj != null );
	}
	
	public boolean setProjFromLayerSet( LayerSet ls ){
		int size = ls.getPixelWidth();
		double degWidth  = ls.getStartLevelTileWidthInDeg();
		double degHeight = ls.getStartLevelTileHeightInDeg();
		if ( size != 0 && degWidth != 0 && degHeight != 0 ){
			IProjection.T type = Projection.getType(ls.getSrs());
			initProjections( size, size, degWidth, degHeight, type );
			return true;
		}
		return false;
	}
	
	public void initProjections( int tilePixWidth, int tilePixHeight,
								  double tileDegWidth, double tileDegHeight, 
								  IProjection.T projType ){
		m_proj = Projection.createProj(projType);
		m_proj.initialize(tilePixWidth, tileDegWidth, tileDegHeight);
		
		initDivPanels();
	}
	
	protected void initDivPanels() {
		for (int i = 0; i < NUMDIVS; i++) {
			if (divPanelExists(i)) {
				initOneDivPanel(i);
			}
		}
	}

	private void initOneDivPanel(int level) {
		double eqScale = m_proj.getBaseEquatorialScale();
		double scale = eqScale*(1 << level);
		m_dpArray[level].initialize(level, m_map, m_proj.getType(),scale);
		m_dpArray[level].resize(m_lastResize.getWidth(), m_lastResize.getHeight());
	}
	
	public int getNumDivs(){ return NUMDIVS; }
	
	public void setViewWorker(ViewWorker vp){
		m_vpWorker = vp;
	}
	
	public void setCenter(GeodeticCoords gc) {
		for (int i = 0; i < NUMDIVS; i++) {
			if (divPanelExists(i)) {
				m_dpArray[i].getDivWorker().setGeoCenter(gc);
			}
		}
	}
	
	private void setCurrentLevelFromMapScale() {
		double dMapEqScale = m_map.getProjection().getEquatorialScale();
		double divBaseEqScale = m_proj.getEquatorialScale();
		double logMess = Math.log( dMapEqScale ) - Math.log( divBaseEqScale );
		double dN = logMess / Math.log(2); 
		int level = Math.max(0,(int)dN);
		setCurrentLevel( level );
	}
	
	public void setCurrentLevel(int currentLevel) {
		m_oldLevel = m_currentLevel;
		m_currentLevel = currentLevel;
	}

	public int getCurrentLevel() {
		return m_currentLevel;
	}
	
	public DivPanel getCurrentDiv() {
		return getDiv(m_currentLevel);
	}
	
	private DivPanel getDiv(int i) {
		if (-1 < i && i < NUMDIVS) {
			return m_dpArray[i];
		}
		return null;
	}
	
	private boolean noChangedInOpacityOrLevel(){
		if (m_opacity == m_oldOpacity && m_currentLevel == m_oldLevel) {
			return true;
		}
		m_oldOpacity = m_opacity;
		m_oldLevel = m_currentLevel;
		return false;
	}
	
	public void adjustOpacity(){
		if (noChangedInOpacityOrLevel()) {
			return;
		}
		if (m_currentLevel == 0) {
			m_dpArray[0].setOpacity(false, m_opacity);	
		}
		else {
			int n = Math.max(0, m_currentLevel - LEVEL_RANGE);
			if (m_opacity == 1.0) {
				for(int j = m_currentLevel; j >= n; j--){
					if (divPanelExists(j)) {
						m_dpArray[j].setOpacity(true, m_opacity);			
					}
				}				
			}
			else {
				//double inc = ( n == 0 ? m_opacity/(m_currentLevel+1) : m_opacity/LEVEL_RANGE);
				m_dpArray[m_currentLevel].setOpacity(false, m_opacity);
				for (int j = m_currentLevel-1; j >= n; j--) {
					//double opacity = Math.max(0, m_opacity - (m_currentLevel-j)*inc);
					if (divPanelExists(j)) {
						m_dpArray[j].setOpacity(true, 0);			
					}
				}
			}
		}
	}
	
	public void setOpacity(double opacity){
		m_oldOpacity = m_opacity;
		m_opacity = opacity;
		adjustOpacity();
	}
	
	public void addLayer(LayerSet layerSet) {
		m_layerSets.add(layerSet);
		
		for (int i = 0; i < NUMDIVS; i++) {
			if (divPanelExists(i)) {
				m_dpArray[i].addLayer(layerSet);
			}
		}
	}
	
	public void removeLayer(LayerSet layerSet) {
		m_layerSets.remove(layerSet);
		
		for (int i = 0; i < NUMDIVS; i++) {
			if (divPanelExists(i)) {
				m_dpArray[i].removeLayer(layerSet);
			}
		}
	}
	
	public void clearLayers() {
		m_layerSets.clear();
		
		for (int i = 0; i < NUMDIVS; i++) {
			if (divPanelExists(i)) {
				m_dpArray[i].clearLayers();
			}
		}
	}
	
	public boolean hasAutoRefreshOnTimerLayers() {
		for (LayerSet layerSet : m_layerSets) {
			if (!layerSet.isActive()) {
				continue;
			}
			if (layerSet.isAutoRefreshOnTimer()) {
				return true;
			}
		}
		return false;
	}
	
	public double getClosestLevelScale(double eqScale) {
		IProjection proj = m_map.getProjection();
		int level = proj.getLevelFromScale( eqScale, 0.5);
		return proj.getScaleFromLevel(level);	
	}

	public void doUpdateDivsCenterScale( double eqScale ){
		int n = Math.max(0, m_currentLevel - LEVEL_RANGE);
		GeodeticCoords gc = m_vpWorker.getGeoCenter(); 
		for( int i = n; i <= m_currentLevel; i++ ){
			ensureDivPanel(i);
			m_dpArray[i].updateViewCenter(gc);
			m_dpArray[i].doUpdate(eqScale);
		}
	}
	
	public void doUpdateDivsVisibility(AbsolutePanel panel) {
		setCurrentLevelFromMapScale();
		int n = Math.max(0, m_currentLevel - LEVEL_RANGE);
		for (int i = 0; i < n; i++) {
			makePanelHidden(i, panel);
		}
		for (int i = n; i <= m_currentLevel; i++) {
			ensureDivPanel(i);
			m_dpArray[i].setVisible(true);
		}
		for (int i = m_currentLevel+1; i < NUMDIVS; i++) {
			makePanelHidden(i, panel);
		}	
		adjustOpacity();
	}

	public void makePanelHidden(int level, AbsolutePanel panel) {
		if (!divPanelExists(level)) {
			return;
		}
		m_dpArray[level].removeAllTiles();
		m_dpArray[level].makePanelSmall(panel,0,0);
		detachDivPanel(level);
	}
	
	public void placeDivsInViewPanel( AbsolutePanel panel ) {
		setCurrentLevelFromMapScale();
		int n = Math.max( 0, m_currentLevel - LEVEL_RANGE );
		for ( int i = n; i <= m_currentLevel; i++ ) {
			ensureDivPanel(i);
			m_dpArray[i].placeInViewPanel(panel);
		}
	}
	
	public void resizeDivs(int w, int h) {
		m_lastResize.setWidth(w);
		m_lastResize.setHeight(h);
		for (int i = 0; i < NUMDIVS; i++) {
			if (divPanelExists(i)) {
				m_dpArray[i].resize(w, h);
			}
		}
	}
	
	public boolean hasDivMovedToFar() {
		DivPanel dp = getCurrentDiv();
		DivWorker dw = dp.getDivWorker();
		ViewWorker vw = m_map.getViewport().getVpWorker();
		DivDimensions dim = dp.getScaledDims();
		IProjection mapProj = m_map.getProjection();
		DivCoordSpan ds = dp.getUsedDivSpan();
		return dw.hasDivMovedTooFar(mapProj, vw, dim, ds);
	}

	public boolean hasIconsMoved() {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void positionIcons() {
		getCurrentDiv().positionIcons();
	}

	public WidgetPositioner getWidgetPositioner() {
		return getCurrentDiv().getWidgetPositioner();
	}
}

