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
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;

public class FreeForm extends AbstractShape {
	protected static int TRANSLATE_HANDLE_OFFSET_X = 20;
	protected List<AnchorHandle> m_handleList = new ArrayList<AnchorHandle>();
	protected List<AbstractPosTool> m_vertexList = new ArrayList<AbstractPosTool>();
	protected AbstractPosTool m_translationTool = null;
	private final AnchorHandle m_translationHandle = new AnchorHandle();
	private int m_X, m_Y;
	public FreeForm(){
		m_id = "Free Form";
		m_translationHandle.setStrokeColor(255, 0, 0, 1);
	}
	
	protected void checkForExceptions(){
	}
	
	private void setPosFromPix(int x, int y, AbstractPosTool tool) {
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		GeodeticCoords pos = tool.getGeoPos();
		if (pos == null  || !pos.equals(gc)) {
			tool.setGeoPos(gc);
			m_needsUpdate = true;
			// Update translation handle
			if (m_vertexList.size() > 0 && tool == m_vertexList.get(0)){
				x -= TRANSLATE_HANDLE_OFFSET_X;
				gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
				getTranslationTool().setGeoPos(gc);
			}
		}
	}
	
	private void moveVerticesByOffset(int x, int y){
		for (int i = 0; i < m_vertexList.size(); i++){
			m_handleList.get(i).moveByOffset(x, y);
			int ix = m_handleList.get(i).getX();
			int iy = m_handleList.get(i).getY();
			setPosFromPix(ix,iy,m_vertexList.get(i));
		}		
		
	}
	
	protected AbstractPosTool newVertexTool(){
		AbstractPosTool tool = new AbstractPosTool(){

			@Override
			public void handleMouseDown(Event event) {
			}

			@Override
			public void handleMouseMove(Event event) {
				int x = event.getClientX();
				int y = event.getClientY();
				setPosFromPix(x, y, this);
			}

			@Override
			public void handleMouseUp(Event event) {
				int x = event.getClientX();
				int y = event.getClientY();
				setPosFromPix(x, y, this);
			}

			@Override
			public void handleMouseOut(Event event) {
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
		};
		return tool;
	}
	
	protected AbstractPosTool getTranslationTool(){
		if (m_translationTool == null){
			m_translationTool = new AbstractPosTool(){
				@Override
				public void handleMouseDown(Event event) {
				}
	
				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX()- m_X;
					int y = event.getClientY()- m_Y;
					moveVerticesByOffset(x,y);
					m_X = event.getClientX();
					m_Y = event.getClientY();
				}
	
				@Override
				public void handleMouseUp(Event event) {
					int x = event.getClientX()- m_X;
					int y = event.getClientY()- m_Y;
					moveVerticesByOffset(x,y);
					m_X = event.getClientX();
					m_Y = event.getClientY();
				}
	
				@Override
				public void handleMouseOut(Event event) {
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
			};
		}
		return m_translationTool;
	}
	
	public void addVertex(int x, int y){
		 AbstractPosTool tool = newVertexTool();
		 m_vertexList.add(tool);
		 setPosFromPix(x,y,tool);
		 AnchorHandle h = new AnchorHandle();
		 h.setCenter(x, y);
		 m_handleList.add(h);
		 if (m_vertexList.size() == 1){
			 x -= TRANSLATE_HANDLE_OFFSET_X;
			 m_translationHandle.setCenter(x, y);
			 setPosFromPix(x,y,m_translationTool);
		 }
	}
	
	public void insertVertex(int i, int x, int y){
		AbstractPosTool tool = newVertexTool();
		 setPosFromPix(x,y,tool);
		m_vertexList.add(i,tool);
		AnchorHandle h = new AnchorHandle();
		h.setCenter(x,y);
		m_handleList.add(i,h);
	}
	
	public IAnchorTool getVertexTool(int i){
		int n = m_vertexList.size();
		if (-1 < i && i < n ){
			return (IAnchorTool)(m_vertexList.get(i));
		}
		return null;
	}
	
	public AbstractPosTool getAbstractPosTool(int i){
		int n = m_vertexList.size();
		if (-1 < i && i < n ){
			return m_vertexList.get(i);
		}
		return null;
	}
	
	public AbstractPosTool getLastPosTool(){
		int n = m_vertexList.size();
		if (n > 0){
			return m_vertexList.get(n-1);
		}
		return null;
	}
	
	public IAnchorTool getLastVertexTool(){
		int n = m_vertexList.size();
		if (n > 0){
			return m_vertexList.get(n-1);
		}
		return null;
	}
	
	public void removeVertex(AbstractPosTool vertex){
		m_vertexList.remove(vertex);
	}
	
	public void removeVertex(int i){
		// add assertion for i here
		m_vertexList.remove(i);
	}
	
	private ViewCoords getViewPoint(int i){
		ViewCoords vc = null;
		if (0 <= i && i < m_vertexList.size()){
			GeodeticCoords gc = m_vertexList.get(i).getGeoPos();
			if (gc != null){
				vc = m_convert.geodeticToView(gc);
			}
		}
		return vc;
	}
	
	protected void drawSegments(Context2d context){
		ViewCoords p = getViewPoint(0);
		if (p != null){
			context.moveTo(p.getX(), p.getY());
		}
		int n = m_vertexList.size();
		for (int i = 1; i < n; i++){
			p = getViewPoint(i);
			context.lineTo(p.getX(), p.getY());	
		}	
	}

	private void drawBoundary(Context2d context) {
		checkForExceptions();
		drawSegments(context);
	}

	private void draw(Context2d context) {
		context.beginPath();
		context.setStrokeStyle(m_color);
		context.setLineWidth(2);
		drawBoundary(context);
		context.stroke();
	}
	
	@Override
	public IShape erase(Context2d context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShape render(Context2d context) {
		draw(context);
		return (IShape)this;
	}

	@Override
	public IShape drawHandles(Context2d context) {
		if (context != null &&  m_vertexList.size() > 0) {
			for (int i = 0; i < m_vertexList.size(); i++){
				GeodeticCoords gc = m_vertexList.get(i).getGeoPos();
				ViewCoords v = m_convert.geodeticToView(gc);
				m_handleList.get(i).setCenter(v.getX(),v.getY()).draw(context);
			}
			// translation handle
			GeodeticCoords gc  = m_translationTool.getGeoPos();
			ViewCoords v = m_convert.geodeticToView(gc);
			m_translationHandle.setCenter(v.getX(),v.getY()).draw(context);
		}
		return (IShape)this;
	}
	
	protected int ptClose(int px, int py, double eps){
		/////////////////////////////////////////
		ViewCoords p, q = getViewPoint(0);  
		int n = m_vertexList.size();
		for (int i = 1; i < n; i++) {
			p = q;
			q = getViewPoint(i);
			double dist = Func.ptLineDist(p, q, px, py);
			if (dist < eps) {
				return i;
			}
		}
		return n;
	}

	public boolean ptCloseToEdge(int px, int py, double eps) {
		/////////////////////////////////////////
		int j = ptClose(px, py, eps);
		return (j < m_vertexList.size());
	}
	
	public int pointHitSegment(int px, int py){
		return ptClose(px, py, Func.PIX_SELECT_TOLERANCE);
	}
	
	public int size(){
		return m_vertexList.size();
	}
	
	@Override
	public boolean positionTouches(GeodeticCoords position) {
		checkForExceptions();
		ViewCoords vc = m_convert.geodeticToView(position);
		return ptCloseToEdge( vc.getX(), vc.getY(), Func.PIX_SELECT_TOLERANCE);
	}
	

	@Override
	public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
		checkForExceptions();
		for (AbstractPosTool tool : m_vertexList) {
			if (tool.isSlected(position)){
				return (IAnchorTool)tool;
			}
		}
		if (m_translationTool.isSlected(position)){
			m_X = m_translationHandle.getX();
			m_Y = m_translationHandle.getY();
			return m_translationTool;
		}
		return null;
	}

}
