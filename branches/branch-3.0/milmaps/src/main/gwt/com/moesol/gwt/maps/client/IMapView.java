/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


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
