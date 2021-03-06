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


import com.moesol.gwt.maps.client.units.Radians;

public class ViewBox {
	
	// TODO make this class immutable
	private double m_topLat;
	private double m_leftLon;
	private double m_botLat;
	private double m_rightLon;
	private int m_width;
	private int m_height;
	private double m_factor;
	private boolean m_singleTile = true;
	
	public static class Builder {
		private double left;
		private double bottom;
		private double right;
		private double top;
		private double factor;
		private int width;
		private int height;
		
		public Builder left(double v) {
			left = v;
			return this;
		}
		public Builder bottom(double v) {
			bottom = v;
			return this;
		}
		public Builder right(double v) {
			right = v;
			return this;
		}
		public Builder top(double v) {
			top = v;
			return this;
		}
		public Builder factor(double v) {
			factor = v;
			return this;
		}
		public Builder width(int v){
			width = v;
			return this;
		}
		public Builder height(int v){
			height = v;
			return this;
		}
		public DegreesBuilder degrees() {
			return new DegreesBuilder(this);
		}
		public RadiansBuilder radians() {
			return new RadiansBuilder(this);
		}
	}
	public static class DegreesBuilder extends Builder {
		private final Builder builder;
		
		public DegreesBuilder(Builder b) {
			builder = b;
		}
		public ViewBox build() {
			return new ViewBox(builder.top, builder.left, 
								   builder.bottom, builder.right, 
								   builder.factor,
								   builder.width, builder.height);
		}
	}
	public static class RadiansBuilder extends Builder {
		private final Builder builder;
		
		public RadiansBuilder(Builder b) {
			builder = b;
		}
		public ViewBox build() {
			return new ViewBox(Radians.asDegrees(builder.top), 
							   Radians.asDegrees(builder.left), 
							   Radians.asDegrees(builder.bottom), 
							   Radians.asDegrees(builder.right),
							   builder.factor,
							   builder.width, builder.height);
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}

	private ViewBox(double topLat, double leftLon, 
					double botLat, double rightLon, double factor,
					int width, int height) {
		if (topLat < botLat) {
			throw new IllegalArgumentException(
					"Top lat must be greater than bottom lat: top=" + topLat + " bottom=" + botLat);
		}

		m_topLat   	= topLat;
		m_leftLon  	= leftLon;
		m_botLat   	= botLat;
		m_rightLon  = rightLon;
		m_factor    = factor;
		m_width 	= width;
		m_height 	= height;
	}

	public ViewBox() {
		m_topLat = Double.POSITIVE_INFINITY;
		m_leftLon = Double.NEGATIVE_INFINITY;
		m_botLat = Double.NEGATIVE_INFINITY;
		m_rightLon = Double.POSITIVE_INFINITY;
	}
	
	public double getTopLat() {
		return m_topLat;
	}
	public double top() {
		return getTopLat();
	}

	public double getLeftLon() {
		return m_leftLon;
	}
	public double left() {
		return getLeftLon();
	}

	public double getBotLat() {
		return m_botLat;
	}
	public double bottom() {
		return getBotLat();
	}

	public double getRightLon() {
		return m_rightLon;
	}
	
	public double right() {
		return getRightLon();
	}
	
	public double getFactor() {
		return m_factor;
	}
	
	public double factor() {
		return getFactor();
	}
	
	public int getWidth() {
		return m_width;
	}
	
	public int width() {
		return getWidth();
	}

	public int getHeight() {
		return m_height;
	}
	public int height() {
		return getHeight();
	}

	public double getLatSpan() {
		return (m_topLat - m_botLat);
	}
	
	//This computes the shorter distance
	public double getLonSpan() {
		double dist = m_rightLon - m_leftLon;
		if (dist < 0.0) {
			// contains the 180 mark.
			dist += 360;
		}
		return dist;
	}
	
	public boolean isSingleTile(){
		return m_singleTile;
	}
	
	public String getWmsString(){
		return m_leftLon + "," + m_botLat + "," + m_rightLon + "," + m_topLat;
	}
	
	public boolean isEqual(ViewBox vb){
		if (vb == null) {
			throw new IllegalArgumentException("Can not check again null viewbox");
		}
		if (m_topLat != vb.m_topLat){
			return false;
		}
		if (m_leftLon != vb.m_leftLon){
			return false;
		}
		if (m_botLat != vb.m_botLat){
			return false;
		}
		if (m_rightLon != vb.m_rightLon){
			return false;
		}
		if (m_width != vb.m_width){
			return false;
		}
		if (m_height != vb.m_height){
			return false;
		}
		return true;
	}
	
	public void makeAnyCorrectionsNeeded(IProjection proj){
		WorldDimension wd = proj.getWorldDimension();
		if (wd.getWidth() < m_width) {
			m_leftLon = -180;
			m_rightLon = 180;
			m_width = wd.getWidth();
			m_singleTile = false;
		}
	}

	@Override
	public String toString() {
		return m_topLat + "," + m_leftLon + "," + m_botLat + "," + m_rightLon;
	}
}
