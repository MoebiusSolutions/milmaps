/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.CssColor;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;

public abstract class AbstractShape implements IShape{
	protected static int TRANSLATE_HANDLE_OFFSET_X = 20;
	protected String m_id;
	protected String m_color = "rgb(255, 255, 255)";
	protected CssColor m_cssColor = null;
	protected boolean m_bSeletected = false;
	protected boolean m_needsUpdate = false;
	
	protected boolean m_ctrlKeydown = false;
	protected boolean m_shiftKeydown = false;
	
	protected static final RangeBearingS m_rb = new RangeBearingS();
	
	protected ICoordConverter m_convert;
	
	// Shape interface implementation
	public void setCoordConverter(ICoordConverter cc) {
		m_convert = cc;
	}
	
	public void setKeyboardFlags(boolean altKey, boolean shiftKey) {
		m_ctrlKeydown = altKey;
		m_shiftKeydown = shiftKey;
	}
	
	protected void syncColor(){
		if (m_cssColor == null){
			m_cssColor = CssColor.make(m_color);
		}
	}
	
	@Override
	public String getColor() {
		return m_color;
	}

	@Override
	public void setColor(String color) {
		if (!m_color.equalsIgnoreCase(color)){
			m_cssColor = null;
		}
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
