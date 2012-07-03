/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.CssColor;

public abstract class AbstractShape implements IShape{
	protected String m_id;
	protected CssColor m_color = CssColor.make(255, 255, 255);
	protected boolean m_bSeletected = false;
	protected boolean m_needsUpdate = false;
	
	protected ICoordConverter m_convert;
	
	// Shape interface implementation
	public void setCoordConverter(ICoordConverter cc) {
		m_convert = cc;
	}
	
	@Override
	public CssColor getColor() {
		return m_color;
	}

	@Override
	public void setColor(CssColor color) {
		m_color = color;
	}
	
	@Override
	public void setId(String id) {
		m_id = id;
	}
	
	@Override
	public String id() {
		return m_id;
	}
	
	@Override
	public IShape selected(boolean selected) {
		m_bSeletected = selected;
		return (IShape)this;
	}
	
	@Override
	public boolean isSelected() {
		return m_bSeletected;
	}
	
	@Override
	public boolean needsUpdate() {
		if (m_needsUpdate) {
			m_needsUpdate = false;
			return true;
		}
		return false;
	}
}
