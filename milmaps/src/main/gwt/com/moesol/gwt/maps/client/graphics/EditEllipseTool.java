/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;


public class EditEllipseTool extends CommonEditTool{
	private Ellipse m_ellipse = null;

	public EditEllipseTool(IShapeEditor se) {
		super(se);
	}
	
	@Override
	public void setShape(IShape shape){
		m_ellipse = (Ellipse)shape; 
		m_abShape = m_ellipse;
	}

	@Override
	public IShape getShape() {
		return (IShape)m_ellipse;
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
		m_anchorTool = anchor;
	}
}
