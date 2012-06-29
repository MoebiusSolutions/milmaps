/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.LayerSet;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;

public class FreeForm extends AbstractShape {
	
	List<AbstractVertexTool> m_vertexList = new ArrayList<AbstractVertexTool>();
	
	protected void checkForException(){
		
	}
	
	protected AbstractVertexTool newAnchorTool(){
		AbstractVertexTool tool = new AbstractVertexTool(){

			@Override
			public void handleMouseDown(MouseDownEvent event) {

			}

			@Override
			public void handleMouseMove(MouseMoveEvent event) {

			}

			@Override
			public void handleMouseUp(MouseUpEvent event) {

			}

			@Override
			public void handleMouseOut(MouseOutEvent event) {

			}

			@Override
			public void handleMouseDblClick(Event event) {

			}
			
			@Override
			public boolean isSlected(GeodeticCoords gc) {
				ViewCoords vc = m_convert.geodeticToView(gc);
				ViewCoords pt = m_convert.geodeticToView(m_geoPos);
				return Func.isClose(pt, vc, 4);
			}
			
			@Override
			public void done() {

			}
		};
		return tool;
	}
	
	public IAnchorTool getVertexTool(int i){
		int n = m_vertexList.size();
		if (-1 < i && i < n ){
			return (IAnchorTool)(m_vertexList.get(i));
		}
		return null;
	}
	
	public void addVertex(){
		AbstractVertexTool tool = newAnchorTool();
		int n = m_vertexList.size();
		tool.setI(n);
		m_vertexList.add(tool);
	}
	
	public void insertVertex(int i){
		// add assertion for i here
		AbstractVertexTool tool = newAnchorTool();
		tool.setI(i);
		m_vertexList.add(i,tool);
		int n = m_vertexList.size();
		for (int j = i+1; j < n; j++){
			tool = m_vertexList.get(j);
			tool.setI(j);
		}
	}
	
	public void removeVertex(int i){
		// add assertion for i here
		AbstractVertexTool tool = m_vertexList.get(i);
		m_vertexList.remove(i);
		int n = m_vertexList.size();
		for (int j = i; j < n; j++){
			tool = m_vertexList.get(j);
			tool.setI(j);
		}
	}
	
	@Override
	public IShape erase(Context2d context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShape render(Context2d context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShape drawHandles(Context2d context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean positionTouches(GeodeticCoords position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
		checkForException();
		for (AbstractVertexTool tool : m_vertexList) {
			if (tool.isSlected(position)){
				return tool;
			}
		}
		return null;
	}

}
