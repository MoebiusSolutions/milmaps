/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

public abstract class AbstractEditTool implements IShapeTool{
	protected ICanvasTool m_canvas = null;
	protected boolean m_mouseDown = false;
	protected IAnchorTool m_anchorTool = null;
	protected ICoordConverter m_convert;
	protected IShapeEditor m_editor;
	
	public AbstractEditTool(IShapeEditor se) {
		m_editor  = se;
		m_canvas  = se.getCanvasTool();
		m_convert = se.getCoordinateConverter();
	}
	
	public IAnchorTool getAnchorTool(){ return m_anchorTool; }

	@Override
	public void handleMouseDblClick(int x, int y) {
	}

	@Override
	public void handleKeyDown(int keyCode) {
	}

	@Override
	public void handleKeyUp(int keyCode) {
	}
}
