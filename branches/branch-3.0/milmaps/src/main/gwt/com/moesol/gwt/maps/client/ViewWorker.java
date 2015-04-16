/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

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


import com.google.gwt.event.shared.HandlerRegistration;
import com.moesol.gwt.maps.client.events.ProjectionChangedEvent;
import com.moesol.gwt.maps.client.events.ProjectionChangedHandler;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class ViewWorker implements ProjectionChangedHandler {
	private ViewDimension m_dims = new ViewDimension(400, 600);
	private GeodeticCoords m_geoCenter = new GeodeticCoords(); // view-port center
	private int m_offsetInWcX;
	private int m_offsetInWcY;
	private IProjection m_proj = null;
	private HandlerRegistration m_projectionChangedHandlerRegistration;
	
	
	public void setProjection( IProjection p ) {
		m_proj = p;
	}
	
	public IProjection getProjection() { return m_proj; }
	
	public void intialize( ViewDimension vd, IProjection proj ) {
		 m_proj = proj;
		 m_dims = vd;
		 computeOffsets(m_proj.geodeticToWorld(m_geoCenter));
		 if (m_projectionChangedHandlerRegistration != null) {
			 m_projectionChangedHandlerRegistration.removeHandler();
		 }
		 m_projectionChangedHandlerRegistration = proj.addProjectionChangedHandler(this);
	}
	
	public void setDimension( ViewDimension d ) {
		m_dims = d;
		computeOffsets(getVpCenterInWc());
	}
	
	public ViewDimension getDimension() {
		return m_dims;
	}
	
	public void setGeoCenter( GeodeticCoords gc ) {
		m_geoCenter = gc;
		
		WorldCoords wc = m_proj.geodeticToWorld(m_geoCenter);
		computeOffsets(wc);
	}

	public GeodeticCoords getGeoCenter(){
		return m_geoCenter;
	}
	
	public void updateOffsets(){
		WorldCoords wc = m_proj.geodeticToWorld(m_geoCenter);
		computeOffsets(wc);
	}
	
	public void setCenterInWc(WorldCoords cent) {
		m_geoCenter = m_proj.worldToGeodetic(cent);
		computeOffsets(cent);
	}
	
	private void computeOffsets(WorldCoords wc) {
		m_offsetInWcX = wc.getX()- m_dims.getWidth()/2;
		m_offsetInWcY = wc.getY()+ m_dims.getHeight()/2;
	}
	
	public WorldCoords getVpCenterInWc() {
		return m_proj.geodeticToWorld(m_geoCenter);
	}
	
	public int getOffsetInWcX() {
		return m_offsetInWcX;
	}
	
	public int getOffsetInWcY() {
		return m_offsetInWcY;
	}
	
	public int wcXtoVcX(int wcX ){
		return (wcX - m_offsetInWcX);
	}
	
	public int wcYtoVcY( int wcY ){
		// Normally we would have vY = wc.getY() - m_offsetInWcY.
		// But for the view y axis we want the y values changed to be relative to the 
		// div's top. So we will subtract dY from the div's top. This will also
		// flip the direction of the divs's y axis
		return (m_offsetInWcY - wcY);	// flip y axis	
	}
	
	/**
	 * wcToVC converts World coordinates to view coordinates based on the view sizes
	 * and where the view's center sits in the world coordinate system.`
	 * @param wc
	 * @return ViewCoords
	 */
	public ViewCoords wcToVC( WorldCoords wc ) {
		return new ViewCoords(wcXtoVcX(wc.getX()), wcYtoVcY(wc.getY()));
	}
	
	public int vcXtoWcX( int vcX ){
		return (vcX + m_offsetInWcX);
	}
	
	public int vcYtoWcY( int vcY ){
		return (m_offsetInWcY - vcY);
	}
	
	public double getDegLngDistFromCenter(double lng ){
		double lngDif = lng  - m_geoCenter.getLambda(AngleUnit.DEGREES);
		lngDif = m_proj.wrapLng(lngDif);
		return lngDif;
	}
	
	public ViewCoords geodeticToView(GeodeticCoords gc){
		double degLng = gc.getLambda(AngleUnit.DEGREES);
		double lngDist = getDegLngDistFromCenter(degLng); 
		int deltaX = m_proj.compWidthInPixels(0,lngDist);
		if (lngDist < 0){
			deltaX =  -1*deltaX;
		}
		int viewX =  m_dims.getWidth()/2 + deltaX;
		WorldCoords wc = m_proj.geodeticToWorld(gc);
		int viewY = wcYtoVcY( wc.getY() );
		return new ViewCoords(viewX, viewY);
	}
	
	
	/**
	 * vcToWc converts View Coordinates to World Coordinates based on the view sizes
	 * and where the view's center sits in the world coordinate system.
	 * @param vc
	 * @return
	 */
	public WorldCoords viewToWorld( ViewCoords vc ) {
		return new WorldCoords(vcXtoWcX(vc.getX()), vcYtoWcY(vc.getY()));
	}
	
	public DivCoords viewToDivCoords( DivWorker divWorker, ViewCoords vc ){
		WorldCoords wc = viewToWorld(vc);
		return divWorker.worldToDiv(wc);
	}
	

	/**
	 * This routine need to scale the viewbox correctly so the non-tiled
	 * image size is correct for the div in its natural size.
	 * This routine returns a viewbox with a maximum size of 360 degrees 
	 * in width.
	 */
	public ViewBox  getViewBox(IProjection proj, double f, double padFactor) {
		int padW = (int)(m_dims.getWidth()*padFactor);
		int padH = (int)(m_dims.getHeight()*padFactor);
		int leftWx  = vcXtoWcX(0-padW);
		int topWy   = vcYtoWcY(0-padH);
		int rightWx = vcXtoWcX(m_dims.getWidth()+padW);
		int botWy 	= vcYtoWcY(m_dims.getHeight()+padH);
		int dimWidth  = (int)((m_dims.getWidth()+2*padW)*f);
		
		WorldCoords tl = new WorldCoords(leftWx,topWy);
		WorldCoords br = new WorldCoords(rightWx,botWy);
		GeodeticCoords gtl = proj.worldToGeodetic(tl);
		GeodeticCoords gbr = proj.worldToGeodetic(br);
		
		// Compute height from viewTop and viewBottom;
		int viewTop = lat2vcY(gtl.latitude().degrees());
		int viewBottom = lat2vcY(gbr.latitude().degrees());
		int dimHeight = (int)((viewBottom - viewTop)*f);
		
		ViewBox vb = 
		ViewBox.builder().bottom(gbr.latitude().degrees())
						 .top(gtl.latitude().degrees())
						 .left(gtl.longitude().degrees())
						 .right(gbr.longitude().degrees())
						 .factor(f)
						 .width(dimWidth).height(dimHeight)
						 .degrees().build(); 
		return vb;
	}
	
	private int lat2vcY(double lat) {
		return geodeticToView(GeodeticCoords.builder().latitude(lat).degrees().build()).getY();
	}

	public ViewBox getViewBox(double f){
		f = (f > 0 ? 1/f:1);
		return getViewBox(m_proj, f, 0);
	}

	@Override
	public void onProjectionChanged(ProjectionChangedEvent event) {
		computeOffsets(getVpCenterInWc());
	}
	
	@Override
	public String toString(){
		int width  = m_dims.getWidth();
		int height = m_dims.getHeight();
		double lat = m_geoCenter.getPhi(AngleUnit.DEGREES);
		double lng = m_geoCenter.getLambda(AngleUnit.DEGREES);
		String rtn = "[ m_dims : " + width + " , " + height +" ; " +
	                 "m_geoCenter : " + lat + " , " + lng +" ; " +
	                 "Offset Wc : " + m_offsetInWcX + " , " + m_offsetInWcY +" ]";
		return rtn;
	}
}
