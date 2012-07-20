/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;

public class Triangle extends AbstractShape {
	protected AnchorHandle[] m_handleList = new AnchorHandle[3];
	protected AbstractPosTool[] m_vertexList = new AbstractPosTool[3];
	protected AbstractPosTool m_translationTool = null;
	private final AnchorHandle m_translationHandle = new AnchorHandle();
	private int m_X, m_Y;
	public Triangle(){
		m_id = "Triangle";
		m_translationHandle.setStrokeColor(255, 0, 0, 1);
		for(int i = 0; i < 3; i++){
			m_vertexList[i] = null;
		}
	}
	
	protected void checkForExceptions(){
		if (m_convert == null) {
			throw new IllegalStateException("Triangle: m_convert = null");
		}
	}
	
	private void setPosFromPix(int x, int y, AbstractPosTool tool) {
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		GeodeticCoords pos = tool.getGeoPos();
		if (pos == null  || !pos.equals(gc)) {
			tool.setGeoPos(gc);
			m_needsUpdate = true;
		}
	}
	
	private void moveVerticesByOffset(int x, int y){
		for (int i = 0; i < 3; i++){
			m_handleList[i].moveByOffset(x, y);
			int ix = m_handleList[i].getX();
			int iy = m_handleList[i].getY();
			setPosFromPix(ix,iy,m_vertexList[i]);
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
	
	private int addVertex(AbstractPosTool tool){
		for(int i = 0; i < 3; i++){
			if (m_vertexList[i] == null){
				m_vertexList[i] = tool;
				return i;
			}
		}
		return 3;
	}
	
	public void addVertex(int x, int y){
		 AbstractPosTool tool = newVertexTool();
		 int i = addVertex(tool);
		 if (i < 3){
			 setPosFromPix(x,y,tool);
			 AnchorHandle h = new AnchorHandle();
			 h.setCenter(x, y);
			 m_handleList[i] = h;
			 if (i == 0){
				 x -= TRANSLATE_HANDLE_OFFSET_X;
				 m_translationHandle.setCenter(x, y);
			 }
		 }
	}
	
	public IAnchorTool getVertexTool(int i){
		if (-1 < i && i < 3 ){
			return (IAnchorTool)(m_vertexList[i]);
		}
		return null;
	}
	
	public AbstractPosTool getAbstractPosTool(int i){
		if (-1 < i && i < 3 ){
			return m_vertexList[i];
		}
		return null;
	}
	
	public AbstractPosTool getLastPosTool(){
		for(int i = 0; i < 2; i++){
			if(m_vertexList[i] != null && m_vertexList[i+1] == null){
				return m_vertexList[i];
			}
		}
		return m_vertexList[2];
	}

	private ViewCoords getViewPoint(int i){
		ViewCoords vc = null;
		if (0 <= i && i < 3){
			if (m_vertexList[i] != null){
				GeodeticCoords gc = m_vertexList[i].getGeoPos();
				if (gc != null){
					vc = m_convert.geodeticToView(gc);
				}
			}
		}
		return vc;
	}
	
	public int size(){
		for(int i = 0; i < 3; i++){
			if(m_vertexList[i] == null){
				return i;
			}
		}
		return 3;
	}
	
	protected void drawSegments(Context2d context){
		ViewCoords p, q = getViewPoint(0);
		if (q == null){
			return;
		}
		ISplit splitter = m_convert.getISplit();
		// set p to null for first point
		int x = splitter.shift(null, q);
		context.moveTo(x, q.getY());
		int n = size();
		for (int i = 1; i <= n; i++){
			p = q;
			q = getViewPoint(i%3);
			if (q != null){
				x = splitter.shift(p, q);
				context.lineTo(x, q.getY());
			}
		}	
	}

	private void drawBoundary(Context2d context) {
		ISplit splitter = m_convert.getISplit();
		// MUST first initialize
		splitter.initialize(ISplit.NO_ADJUST);
		/////////////////////////////////////////
		drawSegments(context);
		
		if (splitter.isSplit()){
			// Must initialize with new values.
			splitter.initialize(ISplit.ADJUST);
			drawSegments(context);
		}
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
		if (context != null &&  size() > 0) {
			ISplit splitter = m_convert.getISplit();
			for (int i = 0; i < size(); i++){
				GeodeticCoords gc = m_vertexList[i].getGeoPos();
				ViewCoords v = m_convert.geodeticToView(gc);
				m_handleList[i].setCenter(v.getX(),v.getY()).draw(context);
				if(splitter.isSplit()){
					int side = splitter.switchMove(splitter.side(v.getX()));
					int x = v.getX() + splitter.getDistance(side);
					m_handleList[i].setCenter(x, v.getY()).draw(context);
				}
			}
			// translation handle
			int x = m_handleList[0].getX()-TRANSLATE_HANDLE_OFFSET_X;
			int y = m_handleList[0].getY();
			m_translationHandle.setCenter(x, y).draw(context);
			if(splitter.isSplit()){
				int side = splitter.switchMove(splitter.side(x));
				x += splitter.getDistance(side);
				m_translationHandle.setCenter(x, y).draw(context);
			}
		}
		return (IShape)this;
	}
	
	protected int ptClose(int px, int py, double eps){
		/////////////////////////////////////////
		ViewCoords p, q = getViewPoint(0);  
		int n = size();
		ISplit split = m_convert.getISplit();
		for (int i = 1; i <= n; i++) {
			p = q;
			q = getViewPoint(i%3);
			if (q == null){
				return i-1;
			}
			int x = split.adjustFirstX(p.getX(), q.getX());
			double dist = Func.ptLineDist(x, p.getY(), q.getX(), q.getY(), px, py);
			if (dist < eps) {
				return i%3;
			}
			// if the x-value changed, then we know we had to shift it
			// so try shifting one more time and testing. 
			if (Math.abs(x-p.getX()) > m_convert.mapWidth()/2 ){
				x =  split.adjustFirstX(q.getX(), p.getX());
				dist = Func.ptLineDist(p.getX(), p.getY(), x, q.getY(), px, py);
				if (dist < eps) {
					return i;
				}
			}
		}
		return n;
	}

	public boolean ptCloseToEdge(int px, int py, double eps) {
		/////////////////////////////////////////
		int j = ptClose(px, py, eps);
		return (j < size());
	}
	
	public int pointHitSegment(int px, int py){
		return ptClose(px, py, Func.PIX_SELECT_TOLERANCE);
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
		
		AbstractPosTool tool = getTranslationTool();
		setPosFromPix(m_translationHandle.getX(),
					  m_translationHandle.getY(),tool);
		if (tool.isSlected(position)){
			m_X = m_translationHandle.getX();
			m_Y = m_translationHandle.getY();
			return (IAnchorTool)tool;
		}
		return null;
	}

	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
	   	IShapeTool tool = new EditTriangleTool(se);
	   	tool.setShape(this);
	   	return tool;
	}
}
