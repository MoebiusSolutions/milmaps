/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.overlayeditor;

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


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.controls.MapButton;
import com.moesol.gwt.maps.client.graphics.IShapeEditor;
import com.moesol.gwt.maps.client.graphics.ShapeEditor;

// we map want to add widgets to the editor.
public class OverlayEditor extends Composite {

	private final IShapeEditor m_shapeEditor;
	private ShapeSelectionDialog m_shapeSelDlg = null;
	private ShapeDataDialog m_shapeDataDlg = null;
	
    public OverlayEditor(MapView mapView, boolean bHorizontal) {
        super();
        m_shapeEditor = new ShapeEditor(mapView);
        setMapView(bHorizontal);
        
    }

    public void setMapView(boolean bHorizontal) {
        // Data button
        MapButton dataBtn = new MapButton();
        dataBtn.addStyleName("map-OverlayContolOffButton");

        dataBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	int x = event.getClientX();
            	int y = event.getClientY();
            	showShapeDataDlg(x,y);
            }
        });

        // On Button
        MapButton onBtn = new MapButton();
        onBtn.addStyleName("map-OverlayContolOnButton");

        onBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
            	int x = event.getClientX();
            	int y = event.getClientY();
            	showShapeSelectionDlg(x,y);
            }
        });

        if (bHorizontal) {
            HorizontalPanel p = new HorizontalPanel();
            p.add(dataBtn);
            p.add(onBtn);
            initWidget(p);
        } else {
            VerticalPanel p = new VerticalPanel();
            p.add(dataBtn);
            p.add(onBtn);
            initWidget(p);
        }
        addStyleName("map-OverlayEditor");
        setZindex(100000);
    }
    
    
    
    public void setZindex(int zIndex) {
        this.getElement().getStyle().setZIndex(zIndex);
    }
    
    private void showShapeDataDlg(int x, int y){
    	m_shapeDataDlg = new ShapeDataDialog(m_shapeEditor);
    	m_shapeDataDlg.getElement().getStyle().setProperty("zIndex", Integer.toString(9000));
    	m_shapeDataDlg.setPopupPosition(x-150, y-150);
    	m_shapeDataDlg.show();
    }
    
    private void showShapeSelectionDlg(int x, int y){
    	m_shapeSelDlg = new ShapeSelectionDialog(m_shapeEditor);
    	m_shapeSelDlg.getElement().getStyle().setProperty("zIndex", Integer.toString(9000));
    	m_shapeSelDlg.setPopupPosition(x-150, y-150);
    	m_shapeSelDlg.show();
    }
}
