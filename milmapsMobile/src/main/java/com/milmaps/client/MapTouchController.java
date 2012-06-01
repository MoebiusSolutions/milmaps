/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.milmaps.client;

import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.moesol.gwt.maps.client.DragTracker;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.LayerSet;
import com.moesol.gwt.maps.client.LayerSetJson;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.ViewWorker;
import com.moesol.gwt.maps.client.WorldCoords;
import com.moesol.gwt.maps.client.controls.BubbleControl;
import com.moesol.gwt.maps.client.tms.FeatureReader;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.shared.Feature;

public class MapTouchController implements
        TouchStartHandler, TouchMoveHandler, TouchEndHandler, TouchCancelHandler,
        GestureStartHandler, GestureChangeHandler, GestureEndHandler {

    private final MapView m_mapView;
    private DragTracker m_dragTracker;
    private FeatureReader m_reader;
    private BubbleControl m_bubbleControl;
    private boolean m_firstTap = true;
    private long m_lastTap = 0;
    private static final int THRESHOLD = 700;
    private HasText m_msg = new HasText() {

        @Override
        public void setText(String text) {
        }

        @Override
        public String getText() {
            return null;
        }
    };

    /** Hook out to layerSet json data */
    private final native JsArray<LayerSetJson> getLayerSets() /*-{
     return $wnd.layerSets;
     }-*/;

    public MapTouchController(MapView map) {
        m_mapView = map;
        String server = "";
        JsArray<LayerSetJson> layerSets = getLayerSets();
        if (layerSets != null) {
            for (int i = 0, n = layerSets.length(); i < n; ++i) {
                LayerSetJson l = layerSets.get(i);
                LayerSet ls = l.toLayerSet();
                if (ls.getId().equals("pli")) {
                    server = ls.getServer();
                    break;
                }
            }
        }
        String tileUrlFormatPrefix = "{server}/rs/tiles/";
        FeatureReader.Builder builder = new FeatureReader.Builder(server, tileUrlFormatPrefix
                + "{level}/{lat}/{lng}/features");

        m_reader = builder.build();
        m_bubbleControl = new BubbleControl(m_mapView);
    }

    public MapTouchController withMsg(HasText msg) {
        m_msg = msg;
        return this;
    }

    public void bindHandlers(FocusPanel touchPanel) {
        touchPanel.addTouchStartHandler(this);
        touchPanel.addTouchMoveHandler(this);
        touchPanel.addTouchEndHandler(this);
        touchPanel.addTouchCancelHandler(this);
        touchPanel.addGestureStartHandler(this);
        touchPanel.addGestureChangeHandler(this);
        touchPanel.addGestureEndHandler(this);
    }

    private void maybeDragMap(int x, int y) {
        if (m_dragTracker == null) {
            return; // Not dragging
        }
        WorldCoords newWorldCenter = m_dragTracker.update(x, y);
        if (m_dragTracker.isSameAsLast()) {
            return;
        }

        m_mapView.cancelAnimations();
        m_mapView.setWorldCenter(newWorldCenter);
        m_mapView.partialUpdateView();
    }

    @Override
    public void onTouchStart(TouchStartEvent event) {
        m_msg.setText("ts: " + event.getTargetTouches().length());
        event.preventDefault();

        switch (event.getTargetTouches().length()) {
            case 1: {

                final Touch touch = event.getTargetTouches().get(0);
                m_dragTracker = new DragTracker(
                        touch.getPageX(), touch.getPageY(), m_mapView.getWorldCenter());
                // check for double-tap
                maybeZoomIn(touch);
                
                maybeShowBubble(touch);
                break;
            }
            default:
                m_dragTracker = null;
        }
    }

    private void maybeZoomIn(Touch touch) {
        if (m_firstTap) {
            m_lastTap = System.currentTimeMillis();
            m_firstTap = false;
        } else {
            if (System.currentTimeMillis() - m_lastTap < THRESHOLD) {
                m_mapView.zoomOnPixel(touch.getPageX(), touch.getPageY(), 2);
            }
            m_firstTap = true;
        }
    }

    @Override
    public void onTouchMove(TouchMoveEvent event) {
        m_msg.setText("tm: " + event.getTargetTouches().length());
        event.preventDefault();
        m_firstTap = true; // prevent double-tap-zoom
        switch (event.getTargetTouches().length()) {
            case 1: {
                Touch touch = event.getTargetTouches().get(0);
                maybeDragMap(touch.getPageX(), touch.getPageY());
                break;
            }
            default:
                m_dragTracker = null;
        }
    }

    @Override
    public void onTouchEnd(TouchEndEvent event) {
        m_msg.setText("te: " + event.getTargetTouches().length());
        event.preventDefault();
        if (m_dragTracker != null) {
            m_dragTracker = null;
            m_mapView.dumbUpdateView();
        }
    }

    @Override
    public void onTouchCancel(TouchCancelEvent event) {
        m_msg.setText("tc: " + event.getTargetTouches().length());
        event.preventDefault();
        if (m_dragTracker != null) {
            m_dragTracker = null;
            m_mapView.dumbUpdateView();
        }
    }

    @Override
    public void onGestureStart(GestureStartEvent event) {
        m_msg.setText("gs: " + event.toDebugString());
        event.preventDefault();

        m_mapView.animateZoom(event.getScale());
    }

    @Override
    public void onGestureChange(GestureChangeEvent event) {
        m_msg.setText("gc: " + event.toDebugString());
        event.preventDefault();

        m_mapView.animateZoom(event.getScale());
    }

    @Override
    public void onGestureEnd(GestureEndEvent event) {
        m_msg.setText("ge: " + event.toDebugString());
        event.preventDefault();
    }

    private void maybeShowBubble(final Touch touch) {
        ViewCoords m_vc = new ViewCoords(touch.getPageX(), touch.getPageY());
        ViewWorker viewWorker = m_mapView.getViewport().getVpWorker();
        WorldCoords worldCoords = viewWorker.viewToWorld(m_vc);
        GeodeticCoords gc = m_mapView.getProjection().worldToGeodetic(worldCoords);

        int level = m_mapView.getDivManager().getCurrentLevel();
        double lat = gc.getPhi(AngleUnit.DEGREES);
        double lng = gc.getLambda(AngleUnit.DEGREES);

        m_reader.getFeatures(level, lat, lng,
                new AsyncCallback<List<Feature>>() {

                    @Override
                    public void onSuccess(List<Feature> result) {
                        if (result.size() > 0) {
                            m_bubbleControl.hide();
                            String titles = "";
                            for (Feature feature : result) {
                                titles += feature.getTitle() + "<br/>";
                            }
                            m_bubbleControl.getHtml().setHTML(titles);
                            m_bubbleControl.show(touch.getClientX(), touch.getClientY() - 20);
                        } else {
                            m_bubbleControl.hide();
                        }
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("failure");
                    }
                });
    }
}
