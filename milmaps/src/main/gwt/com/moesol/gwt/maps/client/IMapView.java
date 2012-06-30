/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;



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
	DivManager getDivManager();
	long getDynamicCounter();
	double getMapBrightness();
	boolean isMapActionSuspended();
	IconLayer getIconLayer();
	WidgetPositioner getWidgetPositioner();
}
