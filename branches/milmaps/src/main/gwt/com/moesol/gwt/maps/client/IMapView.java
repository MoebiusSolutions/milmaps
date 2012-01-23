package com.moesol.gwt.maps.client;



public interface IMapView {
	IProjection getProjection();
	IProjection getTempProjection();
	ViewPort getViewport();
	void setSuspendFlag(boolean b);
	void updateView();
	void doUpdateView();
	void setCenter(GeodeticCoords geodeticCoords);
	IconLayer getIconLayer();
	WidgetPositioner getWidgetPositioner();
}
