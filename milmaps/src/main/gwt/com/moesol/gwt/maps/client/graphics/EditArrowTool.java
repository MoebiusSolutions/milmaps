/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;


public class EditArrowTool extends CommonEditTool{
	private Arrow m_arrow = null;

	public EditArrowTool(IShapeEditor se) {
		super(se);
	}
	
	@Override
	public void setShape(IShape shape){
		m_arrow = (Arrow)shape; 
		m_abShape = m_arrow;
	}
}
