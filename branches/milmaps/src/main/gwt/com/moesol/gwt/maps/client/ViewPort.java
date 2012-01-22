package com.moesol.gwt.maps.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.moesol.gwt.maps.client.WorldCoords.Builder;
import com.moesol.gwt.maps.client.events.ProjectionChangedEvent;
import com.moesol.gwt.maps.client.events.ProjectionChangedHandler;

public class ViewPort implements ProjectionChangedHandler {
	private ViewDimension m_viewDims = new ViewDimension(600, 400);
	private IProjection m_proj = null;
	private final LayerSetWorker m_lsWorker = new LayerSetWorker();
	private final ViewWorker m_vpWorker = new ViewWorker();
	private DivWorker m_divWorker;
	private int m_tilePixWidth  = 512;
	private int m_tilePixHeight = 512;
	private double m_tileDegWidth  = 180;
	private double m_tileDegHeight = 180;
	private int m_level = 0;
  
	private int m_cxTiles;
	private int m_cyTiles;
	private HandlerRegistration m_projectChangedHandlerRegistration;
	
	public ViewPort() {
	}
	
	public IProjection getProjection() {
		return m_proj;
	}
	
	public void setProjection(IProjection proj) {
		if (m_projectChangedHandlerRegistration != null) {
			m_projectChangedHandlerRegistration.removeHandler();
		}
		m_projectChangedHandlerRegistration = proj.addProjectionChangedHandler(this);
		
		m_proj = proj;
		m_lsWorker.setProjection(proj);
		m_vpWorker.intialize(m_viewDims, proj);
		
		GeodeticCoords g = new GeodeticCoords();
		m_vpWorker.setGeoCenter(g);
	}
	
	public ViewWorker getVpWorker(){ return m_vpWorker; }
	
	public void setDivWorker( DivWorker dw ){ 
		m_divWorker = dw; 
	}
	
	public DivWorker getDivWorker(){ return m_divWorker; }
	
	public void setTileDegWidth( double deg ){
		m_tileDegWidth = deg;
	}
	public double getTileDegWidth(){ return m_tileDegWidth; }
	
	public void setTileDegHeight( double deg ){
	  	m_tileDegHeight = deg;
	}
	public double getTileDegHeight(){ return m_tileDegHeight; }
  
    public void setTilePixWidth( int pix ){m_tilePixWidth = pix;}
    public int getTilePixWidth(){ return m_tilePixWidth; }
    public void setTilePixHeight( int pix ){m_tilePixHeight = pix;}
    public int getTilePixHeight(){ return m_tilePixHeight; }
	
	public ViewCoords worldToView(WorldCoords wc, boolean checkWrap) {
		ViewCoords r = m_vpWorker.wcToVC(wc);
		
		// Check for world wrap
		// We may want to remove the wrap check all together.
		int x = r.getX();
		int y = r.getY();
		if (checkWrap == true) { 
			if (x < 0) {
				x = x + m_proj.getWorldDimension().getWidth();
			} else if (x >= m_viewDims.getWidth()) {
				x = x - m_proj.getWorldDimension().getWidth();
			}
		}
		return new ViewCoords(x, y);
	}

    public int getNumberOfRows( double degHeight, int level ){
    	return (int)((180.0 /degHeight)*(1<<level));
    }

	/**
	 * @param vc ViewCoords
	 * @return true if {@code vc} is in the view port
	 */
	public boolean isInViewPort(ViewCoords vc) {
		if (vc.getX() < 0) {
			return false;
		}
		if (vc.getX() >= m_viewDims.getWidth()) {
			return false;
		}
		if (vc.getY() < 0) {
			return false;
		}
		if (vc.getY() >= m_viewDims.getHeight()) {
			return false;
		}
		return true;
	}
	
	/**
	 * @param wc WorldCoords
	 * @return true if {@code wc} is contained in view port
	 */
	public boolean isInViewPort(WorldCoords wc) {
		ViewCoords vc = worldToView(wc, false);
		return isInViewPort(vc);
	}

	/**
	 * @param gc GeodeticCoords
	 * @return true if {@code gc} is contained in view port
	 */
	public boolean isInViewPort(GeodeticCoords gc) {
		WorldCoords wc = m_proj.geodeticToWorld(gc);
		return isInViewPort(wc);
	}
	
	boolean computeInViewPort(TileCoords tc) {
		if (tc.getOffsetX() + tc.getTileHeight() < 0) {
			return false;
		}
		if (tc.getOffsetX() >= m_viewDims.getWidth()) {
			return false;
		}
		if (tc.getOffsetY() + tc.getTileHeight() < 0) {
			return false;
		}
		if (tc.getOffsetY() >= m_viewDims.getHeight()) {
			return false;
		}
		return true;
	}
	
	public int getNumXTiles() {
		return m_proj.getNumXtiles(m_tileDegWidth);
	}
	
	public int getNumYTiles() {
		return m_proj.getNumYtiles(m_tileDegHeight);
	}

	/**
	 * @return width in pixels
	 */
	public int getWidth() {
		return m_viewDims.getWidth();
	}
	
	/**
	 * @return height in pixels
	 */
	public int getHeight() {
		return m_viewDims.getHeight();
	}
	
	public int getCxTiles() {
		return m_cxTiles;
	}
	
	public int getCyTiles() {
		return m_cyTiles;
	}
	
	public void setSize(int w, int h) {
		m_viewDims = new ViewDimension(w, h);
		m_vpWorker.setDimension(m_viewDims);
	}
	
	public int getLevel() { return m_level; }
	
	/**
	 * Keep the view center x on the view and the y within the view port.
	 * 
	 * @param worldCenter
	 * @return worldCenter
	 */
	public WorldCoords constrainAsWorldCenter(WorldCoords worldCenter) {
		WorldDimension worldDimensions = m_proj.getWorldDimension();
		Builder newWc = WorldCoords.builder();
		if (worldCenter.getX() < 0) {
			newWc.setX(worldDimensions.getWidth() + worldCenter.getX());
		} else {
			newWc.setX(worldCenter.getX() % worldDimensions.getWidth());
		}

		int viewCenterY = m_viewDims.getHeight() / 2;
		if (worldDimensions.getHeight() > m_viewDims.getHeight()) {
			if (worldCenter.getY() > worldDimensions.getHeight() - viewCenterY) {
				newWc.setY(worldDimensions.getHeight() - viewCenterY);
			} else if (worldCenter.getY() < 0 + viewCenterY) {
				newWc.setY(viewCenterY);
			} else {
				newWc.setY(worldCenter.getY());
			}
		} else {
			if (worldCenter.getY() < worldDimensions.getHeight() - viewCenterY) {
				newWc.setY(worldDimensions.getHeight() - viewCenterY);
			} else if (worldCenter.getY() > 0 + viewCenterY) {
				newWc.setY(viewCenterY);
			} else {
				newWc.setY(worldCenter.getY());
			}
		}
		
		return newWc.build();
	}

	@Override
	public void onProjectionChanged(ProjectionChangedEvent event) {
		// TODO Auto-generated method stub
		
	}
}
