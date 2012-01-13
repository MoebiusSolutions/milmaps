package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.ui.AbsolutePanel;


public interface IMapView {
	IProjection getProjection();
	IProjection getTempProjection();
	ViewPort getViewport();
	void setSuspendFlag(boolean b);
	void doUpdateView();
	void setCenter(GeodeticCoords geodeticCoords);
	IconLayer getIconLayer();
	
	AbsolutePanel getIconPanel();
}
