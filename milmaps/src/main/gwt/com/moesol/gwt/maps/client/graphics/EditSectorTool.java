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

public class EditSectorTool extends CommonEditTool{
	private Sector m_sector = null;
	private boolean m_ctrlKeydown = false;
	private boolean m_shiftKeydown = false;

	public EditSectorTool(IShapeEditor se) {
		super(se);
	}
	
	@Override
	public void setShape(IShape shape){
		m_sector = (Sector)shape; 
		m_abShape = m_sector;
	}
	
	@Override
	public void handleKeyDown(Event event) {
		if (event.getKeyCode() == KeyCodes.KEY_CTRL){
			m_ctrlKeydown = true;
		}
		else if (event.getKeyCode() == KeyCodes.KEY_SHIFT){
			m_shiftKeydown = true;
		}
		m_sector.setKeyboardFlags(m_ctrlKeydown, m_shiftKeydown);
	}

	@Override
	public void handleKeyUp(Event event) {
		if (event.getKeyCode() == KeyCodes.KEY_CTRL){
			m_ctrlKeydown = false;
		}
		else if (event.getKeyCode() == KeyCodes.KEY_SHIFT){
			m_shiftKeydown = false;
		}
		m_sector.setKeyboardFlags(m_ctrlKeydown, m_shiftKeydown);
	}
}
