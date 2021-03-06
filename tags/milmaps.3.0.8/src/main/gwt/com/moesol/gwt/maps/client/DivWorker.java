/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.events.ProjectionChangedEvent;
import com.moesol.gwt.maps.client.events.ProjectionChangedHandler;
import com.moesol.gwt.maps.client.units.AngleUnit;

/**
 * DivWorker class is used to help handle all tile placements. It works with just one projection
 * scale and original dimension. It should be used in conjunction with tile builder.
 * @author <a href="www.moesol.com">Moebius Solutions, Inc.</a>
 */
public class DivWorker implements ProjectionChangedHandler {
	private final int UPDATE_PAD = 1;
	private GeodeticCoords m_geoCenter = new GeodeticCoords();
	private MapCoords m_divCenterMc = null; // viewport center in wc.
	private final DivDimensions m_baseDims = new DivDimensions();
	private double m_eqScale;
	private double m_offsetInMcX;
	private double m_offsetInMcY;
	private IProjection m_divProj = null;
	
	private int m_mapLevel;
	
	public class BoxBounds {
		public double top;
		public double left;
		public double bottom;
		public double right;
		
		@Override
		public String toString() {
			return "[" +" top: "+ top + ", left: "+ left +" bot: "+ bottom + ", right: "+ right + "]";
		}
	}
	
	public static class ImageBounds {
		public int top;
		public int left;
		public int bottom;
		public int right;
		
		@Override
		public String toString() {
			return "[" +" top: "+ top + ", left: "+ left +" bot: "+ bottom + ", right: "+ right + "]";
		}
	}
	
	public static ImageBounds newImageBounds(){
		return  new ImageBounds();
	}
	
	
	public BoxBounds m_boxBounds = new BoxBounds();
	
	public DivWorker(){
		
	}
	
	public void copyFrom( DivWorker dw ){
		setDivBaseDimensions(dw.getDivBaseDimensions());
		setGeoCenter(dw.getGeoCenter());
	}
	
	public void setDivBaseDimensions( DivDimensions dd ){ 
		m_baseDims.copyFrom(dd); 
	}
	
	public DivDimensions getDivBaseDimensions(){ return m_baseDims; }
	
	public void setDivBaseDimensions( int width, int height){
		m_baseDims.setWidth(width);
		m_baseDims.setHeight(height);
	}
	
	public void setProjection( IProjection p ){ 
		m_divProj = p; 
	}
	
	public IProjection getProjection(){ return m_divProj; }
	
	public void setMapLevel(int mapLevel) {
		this.m_mapLevel = mapLevel;
	}
	
	public int getMapLevel() {
		return m_mapLevel;
	}
	
	public void setEqScale(double scale){ 
		m_eqScale = scale; 
	}
	
	public double getEqScale(){ return m_eqScale; }
	
	public void setGeoCenter( GeodeticCoords gc ){
		m_geoCenter = gc;
	}
	
	public GeodeticCoords getGeoCenter(){
		return m_geoCenter;
	}
	
	public void setOffsetInWcX(int offsetInWcX) {
		this.m_offsetInMcX = offsetInWcX;
	}

	public int getOffsetInWcX() {
		return (int) Math.rint(m_offsetInMcX);
	}
	
	public void setOffsetInWcY(int offsetInWcY) {
		this.m_offsetInMcY = offsetInWcY;
	}

	public int getOffsetInWcY() {
		return (int) Math.rint(m_offsetInMcY);
	}
	
	public void setDiv( GeodeticCoords gc ){
		m_geoCenter = gc;
		m_divCenterMc = m_divProj.geodeticToMapCoords(m_geoCenter);
		computeOffsets();
	}
	
	public void updateDivWithCurrentGeoCenter(){
		m_divCenterMc = m_divProj.geodeticToMapCoords(m_geoCenter);
		computeOffsets();		
	}
	
	public void computeOffsets(){
		m_offsetInMcX = m_divCenterMc.getX()- m_baseDims.getWidth()/2;
		m_offsetInMcY = m_divCenterMc.getY()+ m_baseDims.getHeight()/2;
	}
	
	public int worldToDivX(int wcX){
		int tDx = wcX;
		if (m_offsetInMcX+m_baseDims.getWidth() < wcX){
			WorldDimension wd = m_divProj.getWorldDimension();
			tDx -= wd.getWidth();
			if ( m_offsetInMcX < tDx){
				return (int) Math.rint((tDx - m_offsetInMcX));
			}
		}
		tDx = wcX;
		if (tDx < m_offsetInMcX){
			WorldDimension wd = m_divProj.getWorldDimension();
			tDx += wd.getWidth();
			if (tDx <= m_offsetInMcX+m_baseDims.getWidth()){
				return (int) Math.rint((wcX - m_offsetInMcX));
			}
		}
		return (int) Math.rint((wcX - m_offsetInMcX));
	}
	
	public int worldToDivY( int wcY ){
		// Normally we would have vY = wc.getY() - m_offsetInWcY.
		// But for the view y axis we want the y values changed to be relative to the 
		// div's top. So we will subtract dY from the div's top. This will also
		// flip the direction of the divs's y axis
		return (int) Math.rint((m_offsetInMcY - wcY));	// flip y axis	
	}
	
	/**
	 * wcToVC converts World coordinates to div coordinates based on the div's size
	 * and where the div's center sits in the world coordinate system.`
	 * @param wc
	 * @param checkWrap
	 * @return ViewCoords
	 */
	public DivCoords worldToDiv( WorldCoords wc ) {
		int dx = worldToDivX(wc.getX());
		int dy = worldToDivY(wc.getY());
		
		return new DivCoords(dx,dy);
	}
	
	public DivCoords worldToDiv( int wcX, int wcY) {
		return new DivCoords(worldToDivX(wcX), worldToDivY(wcY));
	}
	
	public int divToWorldX( int dcX ){
		return (int) Math.rint((dcX + m_offsetInMcX));
	}
	
	public int divToWorldY( int dcY ){
		return (int) Math.rint((m_offsetInMcY - dcY));
	}
	
	/**
	 * dcXToPercent converts Div x pixel to percent position based on the div's size
	 * @param vc
	 * @return
	 */
	public double divXToPercent( double dcX ) {
		return dcX/m_baseDims.getWidth();
	}
	
	/**
	 * dcXToPercent converts Div x pixel to percent position based on the div's size
	 * @param vc
	 * @return
	 */
	public double divYToPercent( double dcY ) {
		return dcY/m_baseDims.getHeight();
	}
	
	/**
	 * computeTileBounds computes a given tiles percentage "top, left, bottom, right 
	 * position in a divPanel at its current state. 
	 * @param tileX
	 * @param tileY
	 * @param width
	 * @param height
	 * @return
	 */
	public BoxBounds computePercentBounds(int x, int y, int width, int height) {
		m_boxBounds.top   = divYToPercent(y)*100;
		m_boxBounds.left  = divXToPercent(x)*100;
		m_boxBounds.bottom   = divYToPercent(y+height)*100;
		m_boxBounds.right = divXToPercent(x+width)*100;
		return m_boxBounds;
	}
	
	public double getScaleFactor(IProjection mapProj) {
		double mapScale = mapProj.getEquatorialScale();
		double divOrigScale = m_divProj.getEquatorialScale();
		return (mapScale/divOrigScale);
	}
	
	public DivDimensions getScaledDimensions(IProjection mapProj){
		double f = getScaleFactor(mapProj);
		int width = (int)(m_baseDims.getWidth()*f + 0.5);
		int height = (int)(m_baseDims.getHeight()*f + 0.5);
		return new DivDimensions(width,height);
	}
	
	public void resize(int w, int h) {
		int dW = m_baseDims.getWidth();
		int dH = m_baseDims.getHeight();
		if ( dW < w ){
			int f = (w/dW) + 1;
			dW *= f; 
		}
		if ( dH < h ){
			int f = (h/dH) + 1;
			dH *= f;
		}
		setDivBaseDimensions( dW, dH);
		updateDivWithCurrentGeoCenter();
	}
	
	public int wcYtoVcY( int wcY ){
		// Normally we would have vY = wc.getY() - m_offsetInWcY.
		// But for the view y axis we want the y values changed to be relative to the 
		// div's top. So we will subtract dY from the div's top. This will also
		// flip the direction of the divs's y axis
		return (int)(m_offsetInMcY - wcY);	// flip y axis	
	}
	
	public double getDegLngDistFromCenter(double lng ){
		double lngDif = lng  - m_geoCenter.getLambda(AngleUnit.DEGREES);
		lngDif = m_divProj.wrapLng(lngDif);
		return lngDif;
	}
	
	/**
	 * geodeticToWc attempts to keep the wc point on the div
	 * while finding the correct point. Mostly useful with 
	 * wrap situations.
	 * @param  GeodeticCoords gc
	 * @return WorldCoords
	 */
	public WorldCoords geodeticToWc(GeodeticCoords gc){
		return m_divProj.geodeticToWorld(gc);
	}
	
	/**
	 * This scales the divPanel to match "map world scale" before placing it in the view
	 * @param mapProj
	 * @param vw
	 * @param dim
	 * @return The offset in view coordinates.
	 */
	public ViewCoords computeDivLayoutInView(IProjection mapProj, ViewWorker vw, DivDimensions dim) {
		int viewOy = vw.getOffsetInWcY();
		double factor = getScaleFactor(mapProj);
		dim.setWidth((int)(m_baseDims.getWidth()*factor + 0.5));
		dim.setHeight((int)(m_baseDims.getHeight()*factor + 0.5));
		WorldCoords centerWc = mapProj.geodeticToWorld(m_geoCenter);
		int left = computeDivLeft(centerWc, dim, vw, factor);
		int top  = viewOy - (centerWc.getY()+ dim.getHeight()/2);
		return new ViewCoords(left, top);
	}

	private int computeIntersect(int left1, int width1, int left2, int width2) {
		int maxLeft = Math.max(left1, left2);
		int minRight = Math.min(left1 + width1, left2 + width2);
		return minRight - maxLeft;
	}
	
	private int computeDivLeft(WorldCoords wc, DivDimensions div, ViewWorker vw, double factor) {
		int worldWidth = (int)(factor * m_divProj.getWorldDimension().getWidth() + 0.5);
		int viewOx = vw.getOffsetInWcX();
		ViewDimension view = vw.getDimension();
		
		int left = (wc.getX()- div.getWidth()/2) - viewOx;
		if (left > 0) {
			// Part of view empty, rotate?
			//    0 ------------- vw
			//       left----dw
			//       left--------------dw
			int oldIntersection = computeIntersect(0, view.getWidth(), left, div.getWidth());
			int newLeft = left - worldWidth;
			int newIntersection = computeIntersect(0, view.getWidth(), newLeft, div.getWidth());
			if (newIntersection > oldIntersection) {
				left = newLeft;
			}
		} else if (left + div.getWidth() < view.getWidth()) {
			// Part of view empty, rotate?
			//       0 ------------------ vw
			//  left----------dw
			//          left-------dw
			int oldIntersection = computeIntersect(0, view.getWidth(), left, div.getWidth());
			int newLeft = left + worldWidth;
			int newIntersection = computeIntersect(0, view.getWidth(), newLeft, div.getWidth());
			if (newIntersection > oldIntersection) {
				left = newLeft;
			}
		}
		return left;
	}
	
	private int leftBound(int newLeft, int oldLeft, int divWidth ){
		if ( newLeft < divWidth ){
			return Math.min(newLeft, oldLeft);
		}
		return oldLeft;
	}
	
	private int rightBound(int newRight, int oldRight ){
		if ( newRight > 0 ){
			return Math.max(newRight, oldRight);
		}
		return oldRight;
	}
	
	private int topBound(int newTop, int oldTop, int divHeight ){
		if ( newTop < divHeight ){
			return Math.min(newTop, oldTop);
		}
		return oldTop;
	}
	
	private int bottomBound(int newBot, int oldBot){
		if ( newBot > 0 ){
			return Math.max(newBot, oldBot);
		}
		return oldBot;
	}
	
	public void computeImageBounds(TileCoords tileCoords, 
								   DivDimensions divBaseDim, ImageBounds b ){
		if (tileCoords == null) {
			return;
		}
		// Offset should be in div coordinates
		int x = tileCoords.getOffsetX();
		int y = tileCoords.getOffsetY();
		int width = tileCoords.getTileWidth();
		int height = tileCoords.getTileHeight();
		
		b.left   = leftBound(x, b.left, divBaseDim.getWidth());
		b.right  = rightBound(x+width, b.right);
		b.top    = topBound(y,b.top,divBaseDim.getHeight());
		b.bottom = bottomBound(y+height, b.bottom);
	}
	
	
	public boolean hasDivMovedTooFar(IProjection mapProj, 
									 ViewWorker vwWorker, 
									 DivCoordSpan ds) {
		double factor = getScaleFactor(mapProj);
		if (ds.isBad()) {
			return true;
		}
		int imgTop    = (int)(factor*ds.getTop());
		int imgLeft   = (int)(factor*ds.getLeft());
		int imgBottom = (int)(factor*ds.getBottom());
		int imgRight  = (int)(factor*ds.getRight());
		
		DivDimensions dim = new DivDimensions();
		ViewCoords tl = computeDivLayoutInView(mapProj, vwWorker, dim);
		ViewCoords br = new ViewCoords(tl.getX()+ imgRight, tl.getY()+ imgBottom);
		ViewDimension view = vwWorker.getDimension();
		WorldDimension map = mapProj.getWorldDimension();
		if (map.getHeight() < view.getHeight()){
			if (-5 < (tl.getX()+imgLeft) || br.getX() < view.getWidth()+5){
				return true;
			}	
			return false;
		}
		
		if (-UPDATE_PAD < tl.getX()+imgLeft){
			return true;
		}
		if (br.getX() < view.getWidth()+UPDATE_PAD){
			return true;
		}
		if (-UPDATE_PAD < tl.getY()+imgTop){
			return true; 
		}
		if (br.getY() < view.getHeight()+UPDATE_PAD) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		int oWcX = (int)Math.rint(m_offsetInMcX);
		int oWcY = (int)Math.rint(m_offsetInMcY);
		return "[" +" ox: "+ oWcX + ", oy: "+ oWcY + "]";
	}

	@Override
	public void onProjectionChanged(ProjectionChangedEvent event) {
		computeOffsets();
	}
	
}
