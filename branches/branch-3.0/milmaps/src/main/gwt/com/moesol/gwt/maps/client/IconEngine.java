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


import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.moesol.gwt.maps.client.stats.Sample;

public class IconEngine {
	private final IMapView m_mapView;
	private final ViewWorker m_viewWorker;
	//private final DivManager m_divMgr;

	
	public IconEngine(IMapView mv) {
		m_mapView = mv;
		m_viewWorker = m_mapView.getViewport().getVpWorker();
		//m_divMgr = m_mapView.getDivManager();
	}

	public void positionIcons(WidgetPositioner widgetPositioner, DivWorker divWorker) {
		Sample.DIV_POSITION_ICONS.beginSample();
		
		List<Icon> icons = m_mapView.getIconLayer().getIcons();
		for (Icon icon : icons) {
			positionOneIcon(icon, widgetPositioner, divWorker);
		}
		
		Sample.DIV_POSITION_ICONS.endSample();
	}
	
	public DivCoords getIconDivCoords( DivWorker dw, GeodeticCoords gc){
		WorldCoords wc = dw.geodeticToWc(gc);
		return dw.worldToDiv(wc);
	}

	public void positionOneIcon(Icon icon, WidgetPositioner widgetPositioner, 
														    DivWorker divWorker) {
		DivCoords dc = getIconDivCoords(divWorker, icon.getLocation());
		
		Image image = icon.getImage();
		if (image != null) {
			widgetPositioner.place(image, 
					dc.getX() + icon.getIconOffset().getX(), 
					dc.getY() + icon.getIconOffset().getY(), 
					icon.getZIndex());
		}
		
		Label label = icon.getLabel();
		if (label != null) {
			// We may want to move this block of code
			//int width = 0;
			//int height = 0;
			//if (label.getOffsetWidth() > 0) {
			//	width = label.getOffsetWidth();
			//	height = label.getOffsetHeight();
			//}
			//else if (label.getText().length() > 0) {
			//	width = (int)(label.getText().length()*CharWidthInPixels +0.5);
			//	height = CharHeightInPixels;
			//}
			//// End of block
			widgetPositioner.place(label, 
					dc.getX() + icon.getDeclutterOffset().getX(), 
					dc.getY() + icon.getDeclutterOffset().getY(),
					icon.getZIndex() );
		}
		
		Image leader = icon.getLabelLeaderImage();
		if (leader != null) {
			widgetPositioner.place(leader, 
					dc.getX() - DeclutterEngine.LEADER_IMAGE_WIDTH / 2, 
					dc.getY() - DeclutterEngine.LEADER_IMAGE_HEIGHT / 2,
					icon.getZIndex());
		}
	}

}
