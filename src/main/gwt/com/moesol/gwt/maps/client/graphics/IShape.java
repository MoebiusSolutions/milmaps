/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */

package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.GeodeticCoords;

public interface IShape {
	//public abstract void setCoordConversion(ICoordConversion cc);
	public abstract void setParentGuid(String guid);
	public abstract String getParentGuid();
	
    public abstract void setGuid(String guid);
    public abstract String getGuid();
    
    public abstract void setType(String type);
    public abstract String getType();
    
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

