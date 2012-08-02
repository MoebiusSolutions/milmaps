/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

public class EditLineTool extends CommonEditTool{
	private Line m_line = null;

	public EditLineTool(IShapeEditor se) {
		super(se);
	}

	@Override
	public void setShape(IShape shape){
		m_line = (Line)shape; 
		m_abShape = m_line;
	}

	@Override
	public IShape getShape() {
		return (IShape)m_line;
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
		m_anchorTool = anchor;
	}

}
