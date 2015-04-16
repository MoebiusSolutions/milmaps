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


import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.algorithms.SRngBrg;

public class Util {
	protected ICoordConverter m_conv;
	protected  RangeBearingS m_rb;
	public Util(ICoordConverter conv, RangeBearingS rb){
		m_conv = conv;
		m_rb = rb;
	}
	
	public SRngBrg pixPointsToRngBrg(int px, int py, int qx, int qy){
		GeodeticCoords p = m_conv.viewToGeodetic(new ViewCoords(px,py));
		GeodeticCoords q = m_conv.viewToGeodetic(new ViewCoords(qx,qy));
		return m_rb.gcRngBrgFromTo(p, q);
	}
	
	public GeodeticCoords pixToPos(int px, int py){
		return m_conv.viewToGeodetic(new ViewCoords(px,py));
	}
	
	public ViewCoords posToPix(GeodeticCoords gc){
		return m_conv.geodeticToView(gc);
	}
}
