/*
 * $Id$
 *
 * (c) Copyright, Moebius Solutions, Inc., 2012
 * 
 * LICENSE: GPLv3
 */
package com.milmaps.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.moesol.gwt.maps.client.LayerSet;
import com.moesol.gwt.maps.client.LayerSetJson;
import com.moesol.gwt.maps.client.MapView;

public class MobileEntryPoint implements EntryPoint {

    private MapView m_mapView;
    private Label m_msg;
    private FocusPanel m_touchPanel;
    private AbsolutePanel m_controlsAndMap;

    public MobileEntryPoint() {
        super();
    }

    @Override
    public void onModuleLoad() {
        final RootPanel mapPanel = RootPanel.get("mapPanel");
        if (mapPanel != null) {
            doMapPanel(mapPanel);
            Window.addResizeHandler(new ResizeHandler() {

                @Override
                public void onResize(ResizeEvent event) {
                    setView();
                }
            });
        }
    }

    /** Hook out to layerSet json data */
    private final native JsArray<LayerSetJson> getLayerSets() /*-{
     return $wnd.layerSets;
     }-*/;

    private void doMapPanel(RootPanel mapPanel) {
        DOM.setInnerHTML(mapPanel.getElement(), "");
        m_touchPanel = makeTouchPanel();
        m_controlsAndMap = new AbsolutePanel();

        m_msg = new Label("msg...");
        m_mapView = new MapView();
        loadLayerConfigsFromClient();
        setView();
        bindListeners(m_touchPanel, m_mapView);

        Button in = new Button(" + ", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                goIn();
            }
        });
        in.setStylePrimaryName("toolButton");
        Button out = new Button(" - ", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                goOut();
            }
        });
        out.setStylePrimaryName("toolButton");

        VerticalPanel bar = new VerticalPanel();
        bar.add(in);
        bar.add(out);
        bar.add(m_msg);
        bar.getElement().getStyle().setZIndex(5000);
        bar.getElement().setId("buttonBar");

        m_touchPanel.setWidget(m_mapView);
        m_controlsAndMap.add(m_touchPanel);
        m_controlsAndMap.add(bar);
        m_controlsAndMap.setWidgetPosition(bar, 0, 20);
        m_controlsAndMap.setWidgetPosition(m_touchPanel, 0, 0);
        mapPanel.add(m_controlsAndMap);
    }

    private void setView() {
        int w = Window.getClientWidth();
        int h = Window.getClientHeight();

        m_controlsAndMap.setPixelSize(w, h);
        m_touchPanel.setPixelSize(w, h);
        m_mapView.resizeMap(w, h);
        m_mapView.updateView();
    }

    private void bindListeners(FocusPanel touchPanel, MapView map) {
        MapTouchController controller = new MapTouchController(map);
        controller.bindHandlers(touchPanel);
        controller.withMsg(m_msg);
    }

    private FocusPanel makeTouchPanel() {
        FocusPanel touchPanel = new FocusPanel();
        touchPanel.setStyleName("touchPanel");

        return touchPanel;
    }

    private void loadLayerConfigsFromClient() {
        JsArray<LayerSetJson> layerSets = getLayerSets();
        if (layerSets == null) {
            return;
        }
        for (int i = 0, n = layerSets.length(); i < n; ++i) {
            LayerSetJson l = layerSets.get(i);
            LayerSet ls = l.toLayerSet();
            m_mapView.addLayer(ls);
        }
    }

    protected void goIn() {
        m_mapView.animateZoom(2);
        //m_map.updateView();
    }

    protected void goOut() {
        m_mapView.animateZoom(1.0 / 2.0);
        //m_map.updateView();
    }
}
