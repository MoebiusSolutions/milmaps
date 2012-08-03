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

public class Converter implements  ICoordConverter{
	
	protected MapView m_map;
	protected ISplit m_splitter = null;
	
	public Converter(){
	}
	
	@Override
	public void setMap(MapView map) {
		m_map = map;
		m_splitter = new Splitter(map.getViewport());
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

	@Override
	public ISplit getISplit() {
		return m_splitter;
	}

	@Override
	public void setISplit(ISplit split) {
		m_splitter = split;
		
	}
}
