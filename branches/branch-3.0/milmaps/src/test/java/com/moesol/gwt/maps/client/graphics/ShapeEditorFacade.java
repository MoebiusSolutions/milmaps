/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Event;

public class ShapeEditorFacade implements IShapeEditor{
	
	

	@Override
	public void setEventFocus(boolean on) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ICanvasTool getCanvasTool() {
		// TODO Auto-generated method stub
		return new ICanvasTool(){

			@Override
			public void setSize(int width, int height) {
			}

			@Override
			public IContext getContext() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getOffsetWidth() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getOffsetHeight() {
				// TODO Auto-generated method stub
				return 0;
			}
			
		};
	}

	@Override
	public void addShape(IShape shape) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeShape(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectAllShapes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deselectAllShapes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShapeTool(IShapeTool tool) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAnchorTool(IAnchorTool tool) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearActiveTool() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IShape> getShapes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShape findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCoordConverter(ICoordConverter converter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ICoordConverter getCoordinateConverter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean needsUpdate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IShapeEditor clearCanvas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShapeEditor clearExistingObjs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShapeEditor renderObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onEventPreview(Event event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void done() {
		// TODO Auto-generated method stub
		
	}

}
