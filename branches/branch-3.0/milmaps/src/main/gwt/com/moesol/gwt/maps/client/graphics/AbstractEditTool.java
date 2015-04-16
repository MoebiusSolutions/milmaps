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


public abstract class AbstractEditTool implements IShapeTool{
	protected ICanvasTool m_canvas = null;
	protected boolean m_mouseDown = false;
	protected IAnchorTool m_anchorTool = null;
	protected ICoordConverter m_convert;
	protected IShapeEditor m_editor;
	
	public AbstractEditTool(IShapeEditor se) {
		m_editor  = se;
		m_canvas  = se.getCanvasTool();
		m_convert = se.getCoordinateConverter();
	}
	
	public IAnchorTool getAnchorTool(){ return m_anchorTool; }

	@Override
	public void handleMouseDblClick(int x, int y) {
	}

	@Override
	public void handleKeyDown(int keyCode) {
	}

	@Override
	public void handleKeyUp(int keyCode) {
	}
}
