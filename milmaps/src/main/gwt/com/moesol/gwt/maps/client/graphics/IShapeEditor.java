/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */

package com.moesol.gwt.maps.client.graphics;

import java.util.List;

public interface IShapeEditor extends IHandlerTool{
	public abstract void setEventFocus(boolean on);
	public abstract CanvasTool getCanvasTool();
    public abstract void addShape(IShape shape);
    public abstract void removeShape( String id);
    public abstract void selectAllShapes();
    public abstract void deselectAllShapes();
    public abstract void setShapeTool(IShapeTool tool);
    public abstract void setAnchorTool(IAnchorTool tool);
    public abstract void clearActiveTool();
    public abstract List<IShape> getShapes();
    public abstract IShape findById(String id);
    public abstract ICoordConverter getCoordinateConverter();
    public abstract boolean needsUpdate();
    public abstract void updateCanvas(boolean erase, boolean show);
}
