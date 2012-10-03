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

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.GeoOps;
import com.moesol.gwt.maps.client.algorithms.Spline;
import com.moesol.gwt.maps.client.units.Distance;
import com.moesol.gwt.maps.client.units.DistanceUnit;

public class Arrow extends AbstractShape {
	protected GeoPolygon m_plottedPolygon = null;
	protected List<AnchorHandle> m_handleList = new ArrayList<AnchorHandle>();
	protected List<AbstractPosTool> m_vertexList = new ArrayList<AbstractPosTool>();
	protected AbstractPosTool m_translationTool = null;
	protected final AnchorHandle m_translationHandle = new AnchorHandle();
	private int m_X, m_Y;
	double m_width = 0.0;
	
	public static IShape create(ICoordConverter conv, Distance width,
								GeodeticCoords[] pos) {
		Arrow arrow = new Arrow();
		arrow.setWidth(width.getDistance(DistanceUnit.KILOMETERS));
		arrow.setCoordConverter(conv);
		for (int i = 0; i < pos.length; i++){
			arrow.addVertex(pos[i]);
		}
		return (IShape) arrow;
	}
	
	public static IShapeTool create(IShapeEditor editor, Distance width,
									GeodeticCoords[] pos) {
		ICoordConverter conv = editor.getCoordinateConverter();
		IShape shape = create(conv,width,pos);
		return shape.createEditTool(editor);
	}
	
	public Arrow(){
		m_id = "Arrow";
	}
	
	protected void checkForExceptions(){
		if (m_convert == null) {
			throw new IllegalStateException("Arrow: m_convert = null");
		}
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
			public void handleMouseDown(int x, int y) {
			}

			@Override
			public void handleMouseMove(int x, int y) {
				setPosFromPix(x, y, this);
			}

			@Override
			public void handleMouseUp(int x, int y) {
				setPosFromPix(x, y, this);
			}

			@Override
			public void handleMouseOut(int x, int y) {
			}

			@Override
			public void handleMouseDblClick(int x, int y) {
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
				public void handleMouseDown(int x, int y) {
				}
	
				@Override
				public void handleMouseMove(int x, int y) {
					moveVerticesByOffset(x - m_X, y - m_Y);
					m_X = x;
					m_Y = y;
				}
	
				@Override
				public void handleMouseUp(int x, int y) {
					moveVerticesByOffset(x - m_X, y - m_Y);
					m_X = x;
					m_Y = y;
				}
	
				@Override
				public void handleMouseOut(int x, int y) {
				}
	
				@Override
				public void handleMouseDblClick(int x, int y) {
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
	
	public void addVertex(GeodeticCoords gc){
		 AbstractPosTool tool = newVertexTool();
		 tool.setGeoPos(gc);
		 m_vertexList.add(tool);
		 AnchorHandle h = new AnchorHandle();
		 ViewCoords v = m_convert.geodeticToView(gc);
		 if (m_vertexList.size() == 1){
			 int x = v.getX() - TRANSLATE_HANDLE_OFFSET_X;
			 m_translationHandle.setCenter(x,v.getY());
			 tool = getTranslationTool();
			 setPosFromPix(x,v.getY(),tool);		 
		 }
		 h.setCenter(v.getX(),v.getY()); 
		 m_handleList.add(h);
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
	/*
	public void removeVertex(AbstractPosTool vertex){
		m_vertexList.remove(vertex);
	}
	
	public void removeVertex(int i){
		// add assertion for i here
		m_vertexList.remove(i);
	}
	*/
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
	
	public double getWidth() {
		return(m_width);
	}
	//
	public void setWidth(double w) {
		m_width = w;
	}
	
	public double getCurrentWidth() {
		if (m_width > 0.0){
			return m_width;
		}
		//
		if (m_vertexList.size() >= 2) {
			GeodeticCoords p = m_vertexList.get(0).getGeoPos();
			GeodeticCoords q = m_vertexList.get(1).getGeoPos();
			double rng = m_rb.gcRangeFromTo(p,q);
			double width = rng/6;
			if (width>0.0)
				return (width);
		}
		//
		return(1.0);
	}
	
	protected boolean buildSpline(){
		int size = m_vertexList.size();
		if (size < 1 ){
			return false;
		}
		GeodeticCoords[] backbone = new GeodeticCoords[size];
		for( int i = 0; i < size; i++){
			backbone[i] = m_vertexList.get(i).getGeoPos();
		}
		if (size > 2){
			double width = getCurrentWidth();
			GeodeticCoords[] spline = Spline.SplinePolygon(backbone);
			GeodeticCoords[] arrow = GeoOps.InterpolateArrow(m_rb,spline,width);
			m_plottedPolygon = new GeoPolygon(arrow);
			return true;
		}
		
		return false;
	}
	
	protected void drawSegments(IContext context){
		if (!buildSpline()){
			return;
		}
		
		ViewCoords p, q = m_convert.geodeticToView(m_plottedPolygon.get(0));
		
		if (q == null){
			return;
		}
		
		ISplit splitter = m_convert.getISplit();
		int x = splitter.shift(null, q);
		context.moveTo(x, q.getY());
		int n = m_plottedPolygon.size();
		for (int i = 1; i < n; i++){
			p = q;
			q = m_convert.geodeticToView(m_plottedPolygon.get(i));
			if (q != null){
				x = splitter.shift(p, q);
				context.lineTo(x, q.getY());
			}
		}
	}

	private void drawBoundary(IContext context) {
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

	private void draw(IContext context) {
		context.beginPath();
		context.setStrokeStyle(m_color);
		context.setLineWidth(2);
		drawBoundary(context);
		context.stroke();
	}
	
	@Override
	public IShape erase(IContext context) {
		return null;
	}

	@Override
	public IShape render(IContext context) {
		draw(context);
		return (IShape)this;
	}

	@Override
	public IShape drawHandles(IContext context) {
		if (context != null &&  m_vertexList.size() > 0) {
			ISplit splitter = m_convert.getISplit();
			for (int i = 0; i < m_vertexList.size(); i++){
				GeodeticCoords gc = m_vertexList.get(i).getGeoPos();
				ViewCoords v = m_convert.geodeticToView(gc);
				m_handleList.get(i).setStrokeColor(255, 255, 255, 1);
				m_handleList.get(i).setCenter(v.getX(),v.getY()).draw(context);
				if(splitter.isSplit()){
					int side = splitter.switchMove(splitter.side(v.getX()));
					int x = v.getX() + splitter.getDistance(side);
					m_handleList.get(i).setCenter(x, v.getY()).draw(context);
				}
			}
			// translation handle
			GeodeticCoords gc  = m_translationTool.getGeoPos();
			ViewCoords v = m_convert.geodeticToView(gc);
			m_translationHandle.setStrokeColor(255, 0, 0, 1);
			m_translationHandle.setCenter(v.getX(),v.getY()).draw(context);
			if(splitter.isSplit()){
				int side = splitter.switchMove(splitter.side(v.getX()));
				int x = v.getX() + splitter.getDistance(side);
				m_translationHandle.setCenter(x, v.getY()).draw(context);
			}
		}
		return (IShape)this;
	}
	
	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
	   	IShapeTool tool = new CommonEditTool(se);
	   	tool.setShape(this);
	   	return tool;
	}
	
	protected int ptCloseToSpine(int px, int py, double eps){
		/////////////////////////////////////////
		ViewCoords p, q = getViewPoint(0);  
		int n = m_vertexList.size();
		ISplit split = m_convert.getISplit();
		for (int i = 1; i < n; i++) {
			p = q;
			q = getViewPoint(i);
			int x = split.adjustFirstX(p.getX(), q.getX());
			double dist = Func.ptLineDist(x, p.getY(), q.getX(), q.getY(), px, py);
			if (dist < eps) {
				return i;
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
	
	protected boolean ptClose(int px, int py, double eps){
		///////////////////////////////////////// 
		ViewCoords p, q = m_convert.geodeticToView(m_plottedPolygon.get(0));
		if (q == null){
			return false;
		}
		ISplit split = m_convert.getISplit();
		int n = m_plottedPolygon.size();
		for (int i = 1; i < n; i++){
			p = q;
			q = m_convert.geodeticToView(m_plottedPolygon.get(i));
			if (q != null){
				int x = split.adjustFirstX(p.getX(), q.getX());
				double dist = Func.ptLineDist(x, p.getY(), q.getX(), q.getY(), px, py);
				if (dist < eps) {
					return true;
				}
				// if the x-value changed, then we know we had to shift it
				// so try shifting one more time and testing. 
				if (Math.abs(x-p.getX()) > m_convert.mapWidth()/2 ){
					x =  split.adjustFirstX(q.getX(), p.getX());
					dist = Func.ptLineDist(p.getX(), p.getY(), x, q.getY(), px, py);
					if (dist < eps) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean ptCloseToEdge(int px, int py, double eps) {
		/////////////////////////////////////////
		int j = ptCloseToSpine(px, py, eps);
		if (j < m_vertexList.size()){
			return true;
		}
		return ptClose(px,py,eps);
	}
	
	public int pointHitSegment(int px, int py){
		return ptCloseToSpine(px, py, Func.PIX_SELECT_TOLERANCE);
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
