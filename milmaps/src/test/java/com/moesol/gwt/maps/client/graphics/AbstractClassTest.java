/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.moesol.gwt.maps.client.GeodeticCoords;

public class AbstractClassTest {
	@Test
	public void AbstractDrawingTest(){
		AbstractDrawingTool t = new AbstractDrawingTool(){
			@Override
			public void handleMouseDown(int x, int y) {}

			@Override
			public void handleMouseMove(int x, int y) {}

			@Override
			public void handleMouseUp(int x, int y) {}

			@Override
			public void handleMouseOut(int x, int y) {}

			@Override
			public void handleMouseDblClick(int x, int y) {}

			@Override
			public void handleKeyDown(int keyCode) {}

			@Override
			public void handleKeyUp(int keyCode) {}
		};
		t.setEditor(null);
		t.handleMouseDown(0, 0);
		t.handleMouseMove(0,0);
		t.handleMouseUp(0, 0);
		t.handleMouseOut(0,0);
		t.handleMouseDblClick(0,0);
		t.handleKeyDown(0);
		t.handleKeyUp(0);
	}
	
	@Test
	public void AbstarctEditTest(){
		IShapeEditor se = new ShapeEditorFacade();
		assertEquals(true, se != null);
		AbstractEditTool t = new AbstractEditTool(se){
			@Override
			public void hilite() {}

			@Override
			public void setShape(IShape shape) {}

			@Override
			public void setAnchor(IAnchorTool anchor) {}

			@Override
			public IShape getShape() { return null;}

			@Override
			public void done() {}

			@Override
			public void handleMouseDown(int x, int y) {}

			@Override
			public void handleMouseMove(int x, int y) {}

			@Override
			public void handleMouseUp(int x, int y) {}

			@Override
			public void handleMouseOut(int x, int y) {}
		};
		t.hilite();
		t.setShape(null);
		t.setAnchor(null);
		t.getShape();
		t.done();
		t.handleMouseDown(0, 0);
		t.handleMouseMove(0,0);
		t.handleMouseUp(0, 0);
		t.handleMouseOut(0,0);
	}
	
	@Test
	public void AbstractNewToolTest(){
		AbstractNewTool t = new AbstractNewTool(){

			@Override
			public void setShape(IShape shape) {}

			@Override
			public IShape getShape() { return null;}

			@Override
			public void done() {}

			@Override
			public void handleMouseDown(int x, int y) {}

			@Override
			public void handleMouseMove(int x, int y) {}

			@Override
			public void handleMouseUp(int x, int y) {}
			
		};
		t.setShape(null);
		t.getShape();
		t.done();
		t.handleMouseDown(0, 0);
		t.handleMouseMove(0,0);
		t.handleMouseUp(0, 0);
		t.setAnchor(null);
		t.hilite();
		t.handleMouseOut(0, 0);
		t.handleMouseDblClick(0,0);
		t.handleKeyDown(0);
		t.handleKeyUp(0);
	}
	
	@Test
	public void AbstractPosToolTest(){
		AbstractPosTool t = new AbstractPosTool(){
			@Override
			public boolean isSlected(GeodeticCoords gc) {return false;}

			@Override
			public void handleMouseDown(int x, int y) {}

			@Override
			public void handleMouseMove(int x, int y) {}

			@Override
			public void handleMouseUp(int x, int y) {}

			@Override
			public void handleMouseOut(int x, int y) {}
		};
		t.isSlected(null);
		t.handleMouseDown(0, 0);
		t.handleMouseMove(0,0);
		t.handleMouseUp(0, 0);
		t.handleMouseOut(0, 0);
		t.setGeoPos(null);
		t.getGeoPos();
		t.handleMouseDblClick(0, 0);
		t.handleKeyDown(0);
		t.handleKeyUp(0);
	}
	
	@Test
	public void AbstractShapeTest(){
		AbstractShape s = new AbstractShape(){

			@Override
			public IShape erase(IContext context) {return null;}

			@Override
			public IShape render(IContext context) {return null;}

			@Override
			public IShape drawHandles(IContext context) {return null;}

			@Override
			public IShapeTool createEditTool(IShapeEditor se) {return null;}

			@Override
			public boolean positionTouches(GeodeticCoords position) {
				return false;
			}

			@Override
			public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
				return null;
			}
		};
		s.erase(null);
		s.render(null);
		s.drawHandles(null);
		s.createEditTool(null);
		s.positionTouches(null);
		s.getAnchorByPosition(null);
		s.drawHandles(null);
		s.setCoordConverter(null);
		s.setKeyboardFlags(false,false);
		s.getColor();
		s.setColor(null);
		s.setId("id");
		s.id();
		s.selected(true);
		s.isSelected();
		s.needsUpdate();
	}
	
	@Test
	public void AbstractVertexToolTest(){
		AbstractVertexTool t = new AbstractVertexTool(){

			@Override
			public boolean isSlected(GeodeticCoords gc) {return false;}

			@Override
			public void handleMouseDown(int x, int y) {}

			@Override
			public void handleMouseMove(int x, int y) {}

			@Override
			public void handleMouseUp(int x, int y) {}

			@Override
			public void handleMouseOut(int x, int y) {	}
			
		};
		t.isSlected(null);
		t.handleMouseDown(0, 0);
		t.handleMouseMove(0,0);
		t.handleMouseUp(0, 0);
		t.handleMouseOut(0, 0);
		t.setI(0);
		t.getI();
	}
}
