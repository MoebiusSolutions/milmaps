/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;


public abstract  class AbstractDrawingTool implements IHandlerTool {
	IShapeEditor m_shapeEditor;
	
	public void setEditor(IShapeEditor listener){
		m_shapeEditor = listener;
	}
}
