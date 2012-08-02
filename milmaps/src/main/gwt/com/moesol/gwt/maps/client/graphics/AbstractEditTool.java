/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public abstract class AbstractEditTool implements IShapeTool{
	protected Canvas m_canvas = null;
	protected boolean m_mouseDown = false;
	protected IAnchorTool m_anchorTool = null;
	protected ICoordConverter m_convert;
	protected IShapeEditor m_editor;
	protected boolean m_ctrlKeydown = false;
	protected boolean m_shiftKeydown = false;
	
	public AbstractEditTool(IShapeEditor se) {
		m_editor  = se;
		m_canvas  = se.getCanvasTool().canvas();
		m_convert = se.getCoordinateConverter();
	}

	@Override
	public void handleMouseDblClick(Event event) {
	}

	@Override
	public void handleKeyDown(Event event) {
	}

	@Override
	public void handleKeyUp(Event event) {
	}
}
