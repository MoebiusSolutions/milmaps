/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */

package com.moesol.gwt.maps.client.graphics;

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


import java.util.List;

import com.google.gwt.user.client.Event;

public interface IShapeEditor {//extends IHandlerTool {
	public abstract void setEventFocus(boolean on);

	public abstract ICanvasTool getCanvasTool();

	public abstract void addShape(IShape shape);
	
	public abstract void removeShape(IShape shape);

	public abstract void removeShape(String id);

	public abstract void selectAllShapes();

	public abstract void deselectAllShapes();

	public abstract void setShapeTool(IShapeTool tool);
	
	public abstract IShapeTool getShapeTool();

	public abstract void setAnchorTool(IAnchorTool tool);

	public abstract void clearActiveTool();

	public abstract List<IShape> getShapes();
	
	public abstract void deleteSelectedShapes();

	public abstract IShape findById(String id);

	public abstract void setCoordConverter(ICoordConverter converter);

	public abstract ICoordConverter getCoordinateConverter();

	public abstract boolean needsUpdate();

	public abstract IShapeEditor clearCanvas();

	public abstract IShapeEditor clearExistingObjs();

	public abstract IShapeEditor renderObjects();
	
	public abstract void onEventPreview(Event event);
	
	public abstract void done();
	
	//public abstract void onHandlersEventPreview(Event event);
	
	public abstract IGraphicChanged addGraphicChangedHandler(IGraphicChanged handler);
	
	public abstract void removeGraphicChangedHandler(IGraphicChanged handler);
}
