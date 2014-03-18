/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.graphics.ICanvasTool;
import com.moesol.gwt.maps.client.graphics.IShapeEditor;



public interface IMapView {
	IProjection getProjection();
	IProjection getTempProjection();
	ViewPort getViewport();
	IconEngine getIconEngine();
	void setSuspendFlag(boolean b);
	void updateView();
	void dumbUpdateView();
	void partialUpdateView();
	void setCenter(GeodeticCoords geodeticCoords);
    void setZoom(double equatorFactor);
    double getZoom();
	DivManager getDivManager();
	long getDynamicCounter();
	double getMapBrightness();
	boolean isMapActionSuspended();
	IconLayer getIconLayer();
	WidgetPositioner getWidgetPositioner();
	void setShapeEditor(IShapeEditor shapeEditor);
	ICanvasTool getICanvasTool();
	void attachCanvas();
	MapController getController();
	void setEditorFocus(boolean focus);
}
