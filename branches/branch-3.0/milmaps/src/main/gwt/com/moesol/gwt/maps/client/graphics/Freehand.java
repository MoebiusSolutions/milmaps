/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

public class Freehand extends FreeForm {
	public Freehand(){
		m_id = "Freehand";
	}
	
	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
	   	IShapeTool tool = new EditFreehandTool(se);
	   	tool.setShape(this);
	   	return tool;
	}
}
