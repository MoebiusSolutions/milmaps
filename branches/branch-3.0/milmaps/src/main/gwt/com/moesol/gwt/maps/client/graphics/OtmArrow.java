/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


import com.google.gwt.canvas.dom.client.Context2d;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.GeoOps;
import com.moesol.gwt.maps.client.algorithms.Spline;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class OtmArrow {/*extends AbstractShape {
	public static final int INTERPOLATE_GC     = 0;
	public static final int INTERPOLATE_RL     = 1;
	public static final int INTERPOLATE_SPLINE = 2;
	
	protected GeodeticCoords m_geoPoint = new GeodeticCoords();
	protected GeoPolygon m_polygon;
	protected GeoPolygon m_splinedPolygon;
	protected GeoPolygon m_plottedPolygon;
	protected boolean m_closed = false;
	public double m_width = -1;
	protected int m_nav;
	
	//
	public double getWidth() {
		return(m_width);
	}
	//
	public void setWidth(double w) {
		m_width = w;
	}
	//
	// =======================================================================
	// =======================================================================
	//
	public double getCurrentWidth() {
		if (m_width > 0.0) return(m_width);
		//
		GeodeticCoords pnts[] = m_polygon.toArray(new GeodeticCoords[0]);
		//
		if (pnts.length >= 2) {
			double rng = m_rb.gcRangeFromTo(pnts[0],pnts[1]);
			double width = rng/6;
			if (width>0.0)
				return (width);
		}
		//
		return(1.0);
	}
	//
	// =======================================================================
	// =======================================================================
	//
	public OtmArrow() {
		m_polygon = new GeoPolygon();
		m_splinedPolygon = new GeoPolygon();
		m_nav = INTERPOLATE_RL;
	}
	
	public GeodeticCoords getGeoPointByPolygon() {
		GeodeticCoords pnts[] = m_polygon.toArray(new GeodeticCoords[0]);
		if (pnts.length == 0) return (new GeodeticCoords());
		GeodeticCoords p;
		GeodeticCoords q = pnts[0];
		double lat = q.getPhi(AngleUnit.DEGREES);
		double lng = q.getLambda(AngleUnit.DEGREES);
		p = q;
		for (int j = 1; j < pnts.length;  j++) {
			q = pnts[j];
			double bLng = Func.branch(q.getLambda(AngleUnit.DEGREES),
									  p.getLambda(AngleUnit.DEGREES));
			q = new GeodeticCoords(bLng, q.getPhi(AngleUnit.DEGREES),
												  AngleUnit.DEGREES);
			lat += q.getPhi(AngleUnit.DEGREES);
			lng += q.getLambda(AngleUnit.DEGREES);
			p = q;
		}
		lat /= pnts.length;
		lng /= pnts.length;
		return new GeodeticCoords(lat, lng,AngleUnit.DEGREES);
	}
	
	private void resetGeoPoint(){
		m_geoPoint = getGeoPointByPolygon();
	}
	//
	// =======================================================================
	// =======================================================================
	//
	public void rebuildGeometry() {
		//
		resetGeoPoint();
		//
		if (m_polygon.size() < 2) {
			m_splinedPolygon = new GeoPolygon();
			m_plottedPolygon = new GeoPolygon();
			return;
		}
		//
		GeodeticCoords pnts[] = m_polygon.toArray(new GeodeticCoords[0]);
		//
		double width = getWidth();
		if (width <= 0) {
			double rng = m_rb.gcRangeFromTo(pnts[0], pnts[1]);
			width = rng/6;
		}
		if (width <= 0) {
			m_splinedPolygon = new GeoPolygon();
			m_plottedPolygon = new GeoPolygon();
			return;
		}
		//
		if (m_nav == INTERPOLATE_SPLINE) {
			GeodeticCoords spline[] = Spline.SplinePolygon(pnts);
			GeodeticCoords arrow[] = GeoOps.InterpolateArrow(m_rb,spline,width);
			m_plottedPolygon = new GeoPolygon(arrow);
			m_splinedPolygon = new GeoPolygon(spline);
		}
		else if (m_nav == INTERPOLATE_RL) {
			GeodeticCoords spline[] = pnts;
			GeodeticCoords arrow[] = GeoOps.InterpolateArrow(m_rb,spline, width);
			m_plottedPolygon = new GeoPolygon(arrow);
			m_splinedPolygon = new GeoPolygon(spline);
		}
		else if (m_nav == INTERPOLATE_GC) {
			GeodeticCoords spline[] = GeoOps.InterpolatePolygon(m_rb,pnts);
			GeodeticCoords arrow[] = GeoOps.InterpolateArrow(m_rb,spline,width);
			m_plottedPolygon = new GeoPolygon(arrow);
			m_splinedPolygon = new GeoPolygon(spline);
		}
	}
	//
	// =======================================================================
	// =======================================================================
	//
	public GeodeticCoords[] getArrowPoints() {
		if (m_plottedPolygon == null) {
			rebuildGeometry();
		}
		return(m_plottedPolygon.toArray(new GeodeticCoords[0]));
	}
	@Override
	public IShape erase(IContext context) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IShape render(IContext context) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IShape drawHandles(IContext context) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
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
		// TODO Auto-generated method stub
		return null;
	}
	*/
}
