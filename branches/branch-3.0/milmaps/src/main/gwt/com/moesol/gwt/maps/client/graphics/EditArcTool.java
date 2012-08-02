/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;

public class EditArcTool extends CommonEditTool{
	private Arc m_arc = null;
	
	private boolean m_ctrlKeydown = false;
	private boolean m_shiftKeydown = false;

	public EditArcTool(IShapeEditor se) {
		super(se);
	}
	
	@Override
	public void setShape(IShape shape){
		m_arc = (Arc)shape; 
		m_abShape = m_arc;
	}

	@Override
	public void handleKeyDown(Event event) {
		if (event.getKeyCode() == KeyCodes.KEY_CTRL){
			m_ctrlKeydown = true;
		}
		else if (event.getKeyCode() == KeyCodes.KEY_SHIFT){
			m_shiftKeydown = true;
		}
		m_arc.setKeyboardFlags(m_ctrlKeydown, m_shiftKeydown);
	}

	@Override
	public void handleKeyUp(Event event) {
		if (event.getKeyCode() == KeyCodes.KEY_CTRL){
			m_ctrlKeydown = false;
		}
		else if (event.getKeyCode() == KeyCodes.KEY_SHIFT){
			m_shiftKeydown = false;
		}
		m_arc.setKeyboardFlags(m_ctrlKeydown, m_shiftKeydown);
	}
}
