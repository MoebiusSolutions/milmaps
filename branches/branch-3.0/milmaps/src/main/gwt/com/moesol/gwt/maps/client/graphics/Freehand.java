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
		Freehand ff = new Freehand();
		ff.setCoordConverter(conv);
		for (int i = 0; i < pos.length; i++) {
			ff.addVertex(pos[i]);
		}
		return (IShape) ff;
	}

	public static IShapeTool create(IShapeEditor editor, GeodeticCoords[] pos) {
		ICoordConverter conv = editor.getCoordinateConverter();
		IShape shape = create(conv, pos);
		editor.addShape(shape);
		return shape.createEditTool(editor);
	}
	
	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
	   	IShapeTool tool = new EditFreehandTool(se);
	   	tool.setShape(this);
	   	return tool;
	}
}
