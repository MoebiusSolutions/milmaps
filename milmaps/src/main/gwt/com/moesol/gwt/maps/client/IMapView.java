package com.moesol.gwt.maps.client;

public interface IMapView {
	IProjection getProjection();
	void setSuspendFlag(boolean b);
	void doUpdateView();
	void setCenter(GeodeticCoords geodeticCoords);
	ViewPort getViewport();
}
