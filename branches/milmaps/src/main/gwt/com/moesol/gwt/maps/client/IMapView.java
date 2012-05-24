/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
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
