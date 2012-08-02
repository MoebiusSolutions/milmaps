/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;


public class EditRectTool extends CommonEditTool{
	
	private Rect m_rect = null;

	public EditRectTool(IShapeEditor se) {
		super(se);
	}
	
	@Override
	public void setShape(IShape shape){
		m_rect = (Rect)shape;
		m_abShape = m_rect;
	}

}
