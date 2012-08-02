/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

public class EditCircleTool extends CommonEditTool{	
	private Circle m_circle = null;

	public EditCircleTool(IShapeEditor se) {
		super(se);
	}
	
	@Override
	public void setShape(IShape shape){
		m_circle = (Circle)shape; 
		m_abShape = m_circle;
	}
}
