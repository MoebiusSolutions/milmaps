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

public class Freehand extends Polygon {
	
	public Freehand(){
		m_type = "Freehand";
	}
	
	public static IShape create(ICoordConverter conv, GeodeticCoords[] pos) {
		Freehand fh = new Freehand();
		fh.setCoordConverter(conv);
		for (int i = 0; i < pos.length; i++) {
			fh.addVertex(pos[i]);
		}
		return (IShape) fh;
	}
	
	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
	   	IShapeTool tool = new PolygonEditTool(se);
	   	tool.setShape(this);
	   	return tool;
	}
}
