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

import org.junit.Before;

import com.moesol.gwt.maps.client.CylEquiDistProj;
import com.moesol.gwt.maps.client.DivManager;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.IMapView;
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.IconEngine;
import com.moesol.gwt.maps.client.IconLayer;
import com.moesol.gwt.maps.client.MapController;
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.client.WidgetPositioner;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;

public class MapViewMock implements IMapView {
	public static RangeBearingS m_rb = new RangeBearingS();
	protected Arrow m_arrow;
	private ViewPort m_viewPort = new ViewPort();
	private IProjection proj;
	public Converter m_conv;
	public Util m_util;
	private ICanvasTool m_canvas = new CanvasToolMock();
	public boolean m_editorEventFocus = false;
	

	public MapViewMock(){
		proj = new CylEquiDistProj(512, 180, 180);
		m_viewPort.setProjection(proj);
		m_conv = new Converter(m_viewPort);
		m_util = new Util(m_conv,m_rb);
		m_arrow = new Arrow();
		m_arrow.setCoordConverter(m_conv);
		
	}
	
	@Override
	public IProjection getProjection() {
		return null;
	}

	@Override
	public IProjection getTempProjection() {
		return null;
	}

	@Override
	public ViewPort getViewport() {
		return m_viewPort;
	}

	@Override
	public IconEngine getIconEngine() {
		return null;
	}

	@Override
	public void setSuspendFlag(boolean b) {
	}

	@Override
	public void updateView() {
	}

	@Override
	public void dumbUpdateView() {
	}

	@Override
	public void partialUpdateView() {
	}

	@Override
	public void setCenter(GeodeticCoords geodeticCoords) {
	}

	@Override
	public DivManager getDivManager() {
		return null;
	}

	@Override
	public long getDynamicCounter() {
		return 0;
	}

	@Override
	public double getMapBrightness() {
		return 0;
	}

	@Override
	public boolean isMapActionSuspended() {
		return false;
	}

	@Override
	public IconLayer getIconLayer() {
		return null;
	}

	@Override
	public WidgetPositioner getWidgetPositioner() {
		return null;
	}

	@Override
	public void setShapeEditor(IShapeEditor shapeEditor){
	}

	@Override
	public ICanvasTool getICanvasTool() {
		return m_canvas;
	}

	@Override
	public void attachCanvas() {
	}

	@Override
	public MapController getController() {
		return null;
	}

	@Override
	public void setEditorFocus(boolean focus){
		m_editorEventFocus = focus;
	}
}
