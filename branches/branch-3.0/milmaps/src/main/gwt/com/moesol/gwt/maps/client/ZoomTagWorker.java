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


public class ZoomTagWorker {
	private int m_tagX; // In view coordinates
	private int m_tagY; // In view coordinates
	
	private double m_offsetX = 0;
	private double m_offsetY = 0;
	private double m_vwOffsetWcX; // View's x offset in WCs
	private double m_vwOffsetWcY; // View's y offset in WCs
	
	public ZoomTagWorker(){

	}
	
	public void setViewOffsets( double ox, double oy ){
		m_vwOffsetWcX = ox;
		m_vwOffsetWcY = oy;
	}
	
	public void setTagInVC( int tagX, int tagY ){
		m_tagX = tagX;
		m_tagY = tagY;
	}
	
	public double getOffsetX(){ return m_offsetX; }
	
	public double getOffsetY() { return m_offsetY; }
	
	/**
	 * compViewOffsets: computes the view's new offsets so that 
	 * the tagged points stay in the same spot on the view when
	 * scaled by the factor.
	 * @param factor
	 */
	public void compViewOffsets( double factor ) {
		m_offsetX = factor*(m_vwOffsetWcX + m_tagX)- m_tagX;
		m_offsetY = factor*(m_vwOffsetWcY - m_tagY)+ m_tagY;
	}
}
