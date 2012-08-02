/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;


public class EditEArcTool extends CommonEditTool{
	private EArc m_arc = null;

	public EditEArcTool(IShapeEditor se) {
		super(se);
	}
	
	@Override
	public void setShape(IShape shape){
		m_arc = (EArc)shape; 
		m_abShape = m_arc;
	}

	@Override
	public IShape getShape() {
		return (IShape)m_arc;
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
		m_anchorTool = anchor;
	}

}
