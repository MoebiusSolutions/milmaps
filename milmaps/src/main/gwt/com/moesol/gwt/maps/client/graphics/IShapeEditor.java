/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */

package com.moesol.gwt.maps.client.graphics;

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
}
