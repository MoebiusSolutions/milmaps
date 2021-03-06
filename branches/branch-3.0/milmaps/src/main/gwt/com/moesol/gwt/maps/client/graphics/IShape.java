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


import com.moesol.gwt.maps.client.GeodeticCoords;

public interface IShape {
	//public abstract void setCoordConversion(ICoordConversion cc);
	public abstract void setParentGuid(String guid);
	public abstract String getParentGuid();
	
    public abstract void setGuid(String guid);
    public abstract String getGuid();
    
    public abstract void setType(String type);
    public abstract String getType();
    
    public abstract void setNew(boolean newShape);
    public abstract boolean isNew();
    
	public abstract String getColor();
	public abstract void setColor(String color);

    public abstract IShape selected(boolean selected);
    public abstract IShape erase(IContext context);
    public abstract IShape render(IContext context);
    public abstract IShape drawHandles(IContext context);
    public abstract IShapeTool createEditTool(IShapeEditor se);
    public abstract boolean isSelected();
    public abstract boolean needsUpdate();
    public abstract boolean positionTouches(GeodeticCoords position);
	public abstract IAnchorTool getAnchorByPosition(GeodeticCoords position);
}

