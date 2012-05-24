/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.WorldCoords.Builder;

public class ViewPort {
	private ViewDimension m_viewDims = new ViewDimension(600, 400);
	private IProjection m_proj = null;
	private final ViewWorker m_vpWorker = new ViewWorker();
	private DivWorker m_divWorker;
  
	public ViewPort() {
	}
	
	public IProjection getProjection() {
		return m_proj;
	}
	
	public void setProjection(IProjection proj) {
		m_proj = proj;
		m_vpWorker.intialize(m_viewDims, proj);
		
		GeodeticCoords g = new GeodeticCoords();
		m_vpWorker.setGeoCenter(g);
	}
	
	public ViewWorker getVpWorker() {
		return m_vpWorker;
	}
	
	public void setDivWorker(DivWorker dw) {
		m_divWorker = dw; 
	}
	public DivWorker getDivWorker() {
		return m_divWorker;
	}
	
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
	
	public void setSize(int w, int h) {
		m_viewDims = new ViewDimension(w, h);
		m_vpWorker.setDimension(m_viewDims);
	}
	
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

}
