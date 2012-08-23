/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.GeodeticCoords;

public class Freehand extends FreeForm {
	
	public Freehand(){
		m_id = "Freehand";
	}
	
	public static IShape create(ICoordConverter conv, GeodeticCoords[] pos) {
		Freehand fh = new Freehand();
		fh.setCoordConverter(conv);
		for (int i = 0; i < pos.length; i++) {
			fh.addVertex(pos[i]);
		}
		return (IShape) fh;
	}
	
	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
	   	IShapeTool tool = new FreeXEditTool(se);
	   	tool.setShape(this);
	   	return tool;
	}
}
