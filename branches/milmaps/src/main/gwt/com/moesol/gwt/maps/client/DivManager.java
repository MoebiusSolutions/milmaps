package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.moesol.gwt.maps.client.IProjection.ZoomFlag;

public class DivManager {
	private int m_currentLevel = 0;
	private int m_animateLevel = 0;
	private final int m_numDivs = 20;
	private IProjection m_proj= null;
	private final MapView m_map;
	
	private ViewWorker m_vpWorker = null;
	DivPanel[] m_dpArray = new DivPanel[m_numDivs];
	
	public DivManager(MapView map){
		m_map = map;
		for( int i = 0; i < m_numDivs; i++ ){
			m_dpArray[i] = new DivPanel();
			m_dpArray[i].getElement().getStyle().setZIndex(2000);
		}
	}
	
	public boolean isProjectionSet(){
		return ( m_proj != null );
	}
	
	public boolean setProjFromLayerSet( LayerSet ls ){
		int size = ls.getPixelWidth();
		double degWidth  = ls.getStartLevelTileWidthInDeg();
		double degHeight = ls.getStartLevelTileHeightInDeg();
		if ( size != 0 && degWidth != 0 && degHeight != 0 ){
			IProjection.T type = Projection.getType(ls.getEpsg());
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
		initDivDivPanels();
	}
	
	protected void initDivDivPanels(){
		double dScale = m_proj.getOrigEquatorialScale();
		for( int i = 0; i < m_numDivs; i++ ){
			double val = dScale*(1<<i);
			m_dpArray[i].initialize(i, m_map, m_proj.getType(),val);
		}	
	}
	
	public int getNumDivs(){ return m_numDivs; }
	
	public void setViewWorker(ViewWorker vp){
		m_vpWorker = vp;
	}
	
	public void setCenter(GeodeticCoords gc){
		for( int i = 0; i < m_numDivs; i++ ){
			m_dpArray[i].getDivWorker().setGeoCenter(gc);
		}	
	}
	
	private void setCurrentLevelFromMapScale(){
		double dMapEqScale = m_map.getProjection().getEquatorialScale();
		double divBaseEqScale = m_proj.getEquatorialScale();
		double logMess = Math.log(dMapEqScale) - Math.log(divBaseEqScale);
		double dN = logMess / Math.log(2); 
		int level = (int)(dN);//(Math.rint(dN));
		setCurrentLevel(level);
	}
	
	public void setCurrentLevel(int currentLevel) {
		m_animateLevel = m_currentLevel;
		m_currentLevel = currentLevel;
	}

	public int getCurrentLevel() {
		return m_currentLevel;
	}
	
	public int getAnimateLevel(){ return m_animateLevel; }
	
	public DivPanel getCurrentDiv(){
		return getDiv(m_currentLevel);
	}
	
	public DivPanel getAnimateDiv(){
		return getDiv(m_animateLevel);
	}

	public DivPanel getDiv( int level ){
		if ( -1 < level && level < m_numDivs){
			return m_dpArray[level];
		}
		return null;
	}
	
	
	public void attachDivsTo(AbsolutePanel lp){
		for( int i = 0; i < m_numDivs; i++ ){
			lp.add(m_dpArray[i]);
		}
	}
	
	public void setOpacity(double brightness){
		for ( int j = 0; j < m_numDivs; j++ ){
			m_dpArray[j].getElement().getStyle().setOpacity(brightness);
		}	
	}
	
	public void addLayer(LayerSet layerSet) {
		for( int i = 0; i < m_numDivs; i++ ){
			m_dpArray[i].addLayer(layerSet);
		}		
	}
	
	public void removeLayer(LayerSet layerSet) {
		for( int i = 0; i < m_numDivs; i++ ){
			m_dpArray[i].removeLayer(layerSet);
		}
	}
	
	public void clearLayers() {
		for( int i = 0; i < m_numDivs; i++ ){
			m_dpArray[i].clearLayers();
		}
	}
	
	public boolean hasAutoRefreshOnTimerLayers() {
		for( int i = 0; i < m_numDivs; i++ ){
			if ( m_dpArray[i].hasAutoRefreshOnTimerLayers() )
				return true;
		}
		return false;
	}
	
	public boolean allTilesLoaded( int n ) {
		int start = ( m_currentLevel-n < 0 ? 0 : m_currentLevel-n );
		int end = ( m_currentLevel+n > m_numDivs-1 ? m_numDivs-n : m_currentLevel+1 );
		for ( int i = start; i <= end; i++ ){
			if ( m_dpArray[i].allTilesLoaded() == false )
				return false;
		}
		return true;
	}

	public void hideAnimatedTiles() {
		for( int i = 0; i < m_numDivs; i++ ){
			m_dpArray[i].hideAnimatedTiles();
		}
	}
	
	public void doUpdateDivs( int n, double eqScale ){
		//if (zoomFlag == ZoomFlag.NONE && !m_firstSearch) {
			// optimize pans etc.
		//	return;
		//}
		int start = ( m_currentLevel-n < 0 ? 0 : m_currentLevel-n );
		int end = ( m_currentLevel+n > m_numDivs-1 ? m_numDivs-n : m_currentLevel+n );
		GeodeticCoords gc = m_vpWorker.getGeoCenter(); 
		for ( int i = start; i <= end; i++ ){
			if ( m_dpArray[i].upadteViewCenter(gc) ){
				m_dpArray[i].doUpdate(eqScale);
			}
		}
	}
	
	
	public void placeDivPanels( AbsolutePanel panel, ZoomFlag zoomFlag  ){
		setCurrentLevelFromMapScale();
		int start = Math.max(0, m_currentLevel-1);
		int end = Math.min(m_numDivs-1, m_currentLevel+1);
		if ( zoomFlag == ZoomFlag.IN ){
			if ( start != m_currentLevel ){
				m_dpArray[start].getElement().getStyle().setZIndex(2001);
				m_dpArray[start].placeInViewPanel(panel, true);
			}
			if ( end != m_currentLevel ){
				m_dpArray[end].getElement().getStyle().setZIndex(2000);
				m_dpArray[end].placeInViewPanel(panel, false);
			}
			
		}else if ( zoomFlag == ZoomFlag.OUT ){
			if ( start != m_currentLevel ){
				m_dpArray[start].getElement().getStyle().setZIndex(2000);
				m_dpArray[start].placeInViewPanel(panel, false);
			}
			if ( end != m_currentLevel ){
				m_dpArray[end].getElement().getStyle().setZIndex(2001);
				m_dpArray[end].placeInViewPanel(panel, true);
			}
		}
		else{
			m_dpArray[end].getElement().getStyle().setZIndex(2001);
			m_dpArray[end].placeInViewPanel(panel, true);		
		}
		m_dpArray[m_currentLevel].getElement().getStyle().setZIndex(2002);
		m_dpArray[m_currentLevel].placeInViewPanel(panel, true);
	}
	
	public void resizeDivs(int w, int h ){
		for( int i = 0; i < m_numDivs; i++ ){
			m_dpArray[i].resize(w,h);
		}
	}
}

