/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.ViewWorker;
import com.moesol.gwt.maps.client.WorldCoords;

public abstract class ConverterBase implements  ICoordConverter{
	private static final int DONT_MOVE  = 0;
	private static final int MOVE_LEFT  = 1;
	private static final int MOVE_RIGHT = 2;
	protected MapView m_map;
	
	public ConverterBase(){
	}
	
	@Override
	public void setMap(MapView map) {
		m_map = map;
	}
	
	// Coordinate conversion
	@Override
	public ViewCoords geodeticToView(GeodeticCoords gc) {
		if (m_map == null) {
			throw new IllegalStateException("ConverterBase: m_map = null");
		}
		ViewWorker vw = m_map.getViewport().getVpWorker();
		return vw.geodeticToView(gc);
	}

	@Override
	public GeodeticCoords viewToGeodetic(ViewCoords vc) {
		if (m_map == null) {
			throw new IllegalStateException("ConverterBase: m_map = null");
		}
		ViewWorker vw = m_map.getViewport().getVpWorker();
		WorldCoords wc = vw.viewToWorld(vc);
		IProjection proj = m_map.getProjection();
		return proj.worldToGeodetic(wc);
	}

	@Override
	public WorldCoords geodeticToWorld(GeodeticCoords gc) {
		if (m_map == null) {
			throw new IllegalStateException("ConverterBase: m_map = null");
		}
		IProjection proj = m_map.getProjection();
		return proj.geodeticToWorld(gc);
	}

	@Override
	public GeodeticCoords worldToGeodetic(WorldCoords wc) {
		if (m_map == null) {
			throw new IllegalStateException("ConverterBase: m_map = null");
		}
		IProjection proj = m_map.getProjection();
		return proj.worldToGeodetic(wc);
	}
	
	@Override
	public int mapWidth(){
		if (m_map == null) {
			throw new IllegalStateException("ConverterBase: m_map = null");
		}
		IProjection proj = m_map.getProjection();
		return proj.iMapWidth();
	}
	
	@Override
	public ViewCoords worldToView(WorldCoords wc){
		if (m_map == null) {
			throw new IllegalStateException("ConverterBase: m_map = null");
		}
		ViewWorker vw = m_map.getViewport().getVpWorker();
		return vw.wcToVC(wc);
	}
	
	@Override
	public  WorldCoords viewToWorld(ViewCoords vc){
		if (m_map == null) {
			throw new IllegalStateException("ConverterBase: m_map = null");
		}
		ViewWorker vw = m_map.getViewport().getVpWorker();
		return vw.viewToWorld(vc);
	}

	public boolean makeOneSided( ViewCoords[] vwPts, ViewCoords[] outPts){

		int  dX, dY;
		int  d2X, d2Y;
		int  width = mapWidth()/2;
		boolean bSplit = false;
		boolean bAdjust   = false;
		
		int numPts = vwPts.length;
		int mapWidth = mapWidth();
		//boolean bClosed = vwPts[0].equals(vwPts[numPts-1]);
		long tCount = numPts;//( bClosed != false? numPts : numPts-1 );
			  
		int move = DONT_MOVE;
		ViewCoords center = geodeticToView(m_map.getCenter());
		
		
		dX = vwPts[0].getX();
		dY = vwPts[0].getY();
		outPts[0] = new ViewCoords(dX,dY);
		for ( int i = 0; i < tCount; i++ ){
			int j = (i+1)%numPts;
			dX = vwPts[i].getX();
			dY = vwPts[i].getY();
			d2X = vwPts[j].getX();
			d2Y = vwPts[j].getY();
			if ( Math.abs(d2X-dX) > width ){
				bSplit = true;
			    bAdjust = !bAdjust;
			    if ( DONT_MOVE == move ) {  
					if ( dX < center.getX() ){
						move = MOVE_LEFT;
					}
			        else{
			          move = MOVE_RIGHT;
			        }
			    }
			}
			if ( bAdjust ){
				d2X += (MOVE_LEFT == move ? -1*mapWidth : mapWidth); 
			}
			outPts[j] = new ViewCoords(d2X,d2Y);
		}
		return bSplit;
	}
	
	public ViewCoords[] duplicateOnOtherSide(ViewCoords[] pts){
		int dX, dY;
		int width = mapWidth()/2;
		int numPts = pts.length;
		int mapWidth = mapWidth();
		int move = (pts[0].getX() < width ? MOVE_RIGHT : MOVE_LEFT);
		ViewCoords[] outPts = new ViewCoords[numPts];
	    for ( int i = 0; i < numPts; i++ ){
	    	dX = pts[i].getX();
	    	dY = pts[i].getY();
	    	dX +=  (MOVE_LEFT == move ? -1*mapWidth : mapWidth);
	    	outPts[i] = new ViewCoords(dX,dY);
		}
		return outPts;
	}
}
