/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;


public class EditBoxTool extends CommonEditTool{
	private Box m_box = null;

	public EditBoxTool(IShapeEditor se) {
		super(se);
	}
	
	@Override
	public void setShape(IShape shape){
		m_box = (Box)shape; 
		m_abShape = m_box;
	}
}
