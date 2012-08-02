/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;


public class EditTriangleTool extends CommonEditTool{
	private Triangle m_triangle = null;

	public EditTriangleTool(IShapeEditor se) {
		super(se);
	}

	@Override
	public void setShape(IShape shape){
		m_triangle = (Triangle)shape; 
		m_abShape = m_triangle;
	}
}
