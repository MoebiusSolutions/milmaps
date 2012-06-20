/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.controls;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.moesol.gwt.maps.client.MapView;

public class MapDimmerControl extends Composite{
    private MapView m_mapView;

    private double m_opacity = 0.5;

    private HTML m_dimmerDimButton;

    private HTML m_dimmerBrightButton;

    private HTML m_dimmerControlPanel;

    public MapDimmerControl() {

    }

    public MapDimmerControl(MapView mapView, boolean bHorizontal ) {
        super();
        setMapView(mapView,bHorizontal );
    }

    public void setMapView(MapView mapView, boolean bHorizontal ) {

        m_mapView = mapView;

        FlowPanel dimmerPanel = new FlowPanel();
        dimmerPanel.setStyleName("map-DimmerControl2");
        m_dimmerControlPanel = new HTML();
        m_dimmerControlPanel.setStyleName("map-DimmerControl2Panel");
        m_dimmerControlPanel.addStyleName("map-ControlPanel2");
//                m_dimmerControlPanel.addStyleName("map-ControlTransparent");
        m_dimmerDimButton = new HTML();
        m_dimmerDimButton.setStyleName("map-ControlButton2");
        m_dimmerDimButton.addStyleName("map-DimmerControl2Dim");
        m_dimmerDimButton.addStyleName("map-DimmerControl2DimButton");
//                m_dimmerDimButton.addStyleName("map-ControlTransparent");
        m_dimmerBrightButton = new HTML();
        m_dimmerBrightButton.setStyleName("map-ControlButton2");
        m_dimmerBrightButton.addStyleName("map-DimmerControl2Brighten");
        m_dimmerBrightButton.addStyleName("map-DimmerControl2BrightenButton");
//                m_dimmerBrightButton.addStyleName("map-ControlTransparent");
        dimmerPanel.add(m_dimmerControlPanel);
        dimmerPanel.add(m_dimmerDimButton);
        dimmerPanel.add(m_dimmerBrightButton);
        initWidget(dimmerPanel);

        m_dimmerDimButton.addMouseDownHandler(new MouseDownHandler() {
        @Override
            public void onMouseDown(MouseDownEvent e) {
                e.preventDefault();
                resetStyleNamesTo(m_dimmerDimButton, true, "map-DimmerControl2DimButton");
                dimMap();
            }
        });

        m_dimmerDimButton.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent e) {
                e.preventDefault();
                resetStyleNamesTo(m_dimmerDimButton, true, "map-DimmerControl2DimButtonOver");
            }
        });

        m_dimmerDimButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent e) {
                e.preventDefault();
                resetStyleNamesTo(m_dimmerDimButton, true, "map-DimmerControl2DimButtonOver");
            }
        });

        m_dimmerDimButton.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent e) {
                e.preventDefault();
                resetStyleNamesTo(m_dimmerDimButton, true, "map-DimmerControl2DimButton");
            }
        });

        m_dimmerBrightButton.addMouseDownHandler(new MouseDownHandler() {
        @Override
            public void onMouseDown(MouseDownEvent e) {
                e.preventDefault();
                resetStyleNamesTo(m_dimmerBrightButton, false, "map-DimmerControl2BrightenButton");
                brightenMap();
            }
        });

        m_dimmerBrightButton.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent e) {
                e.preventDefault();
                resetStyleNamesTo(m_dimmerBrightButton, false, "map-DimmerControl2BrightenButtonOver");
            }
        });

        m_dimmerBrightButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent e) {
                e.preventDefault();
                resetStyleNamesTo(m_dimmerBrightButton, false, "map-DimmerControl2BrightenButtonOver");
            }
        });

        m_dimmerBrightButton.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent e) {
                e.preventDefault();
                resetStyleNamesTo(m_dimmerBrightButton, false, "map-DimmerControl2BrightenButton");
            }
        });
        setZindex(100000);
    }
        
        /**
     * Catch-all method for making sure only one button type is set on the element at any time.
     * This is the easiest necessary step to take cases such as the user hold-clicking on a button,
     * dragging onto the other button, and releasing their click. 
     * 
     * @param button
     * @param isLockButton
     * @param newName 
     */
    private void resetStyleNamesTo(HTML button, boolean isDimButton, String newName) {
        if (isDimButton) {
            button.removeStyleName("map-DimmerControl2DimButton");
            button.removeStyleName("map-DimmerControl2DimButtonOver");
        }
        else {
            button.removeStyleName("map-DimmerControl2BrightenButton");
            button.removeStyleName("map-DimmerControl2BrightenButtonOver");
        }
        button.addStyleName(newName);
    }
	
    private void dimMap(){
        m_mapView.incrementMapBrightness(-0.1);
    }

    private void brightenMap(){
        m_mapView.incrementMapBrightness(0.1);
    }

    public void setZindex( int zIndex ){
        this.getElement().getStyle().setZIndex(zIndex);
    }

    public void setOpacity(double val) {
//		m_opacity =  Math.min(1.0,Math.max(0.0,val));
    }

    public double getOpacity() {
        return m_opacity;
    }
}
