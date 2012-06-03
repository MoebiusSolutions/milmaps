/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.milmaps.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.code.gwt.geolocation.client.Coordinates;
import com.google.code.gwt.geolocation.client.Geolocation;
import com.google.code.gwt.geolocation.client.Position;
import com.google.code.gwt.geolocation.client.PositionCallback;
import com.google.code.gwt.geolocation.client.PositionError;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.LayerSet;
import com.moesol.gwt.maps.client.LayerSetJson;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.MapScale;

public class MobileEntryPoint implements EntryPoint {

    private MapView m_mapView;
    private Label m_msg;
    private FocusPanel m_touchPanel;
    private LayoutPanel m_controlsAndMap;
    private Timer m_posUpdateTimer;

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
        m_controlsAndMap = new LayoutPanel();

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
        Button geoLoc = new Button(" * ", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updatePosition(true);

                if (m_posUpdateTimer == null) {
                    m_posUpdateTimer = new Timer() {

                        @Override
                        public void run() {
                            updatePosition(false);
                        }
                    };
                    m_posUpdateTimer.scheduleRepeating(10000);
                }
            }
        });
        geoLoc.setStylePrimaryName("toolButton");

        m_touchPanel.setWidget(m_mapView);
        m_controlsAndMap.add(m_touchPanel);
        m_controlsAndMap.add(in);
        m_controlsAndMap.add(out);
        m_controlsAndMap.add(m_msg);
        m_controlsAndMap.add(geoLoc);
        m_controlsAndMap.setWidgetLeftWidth(in, 10, Style.Unit.PX, 55, Style.Unit.PX);
        m_controlsAndMap.setWidgetBottomHeight(in, 80, Style.Unit.PX, 55, Style.Unit.PX);
        m_controlsAndMap.setWidgetLeftWidth(out, 10, Style.Unit.PX, 55, Style.Unit.PX);
        m_controlsAndMap.setWidgetBottomHeight(out, 20, Style.Unit.PX, 55, Style.Unit.PX);
        m_controlsAndMap.setWidgetRightWidth(geoLoc, 10, Style.Unit.PX, 55, Style.Unit.PX);
        m_controlsAndMap.setWidgetTopHeight(geoLoc, 20, Style.Unit.PX, 55, Style.Unit.PX);
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

    private void updatePosition(final boolean flyTo) {
        Geolocation.getGeolocation().getCurrentPosition(new PositionCallback() {

            @Override
            public void onSuccess(Position position) {
                Coordinates coords = position.getCoords();
                double lat = coords.getLatitude();
                double lon = coords.getLongitude();
                sendReport(lat, lon);
                GeodeticCoords geo = new GeodeticCoords(lon, lat, AngleUnit.DEGREES, coords.getAltitude());
                if (flyTo) {
                    m_mapView.flyTo(geo, MapScale.parse("1:5K"));
                }
            }

            @Override
            public void onFailure(PositionError error) {
                String message;
                switch (error.getCode()) {
                    case PositionError.UNKNOWN_ERROR:
                        message = "Unknown Error";
                        break;
                    case PositionError.PERMISSION_DENIED:
                        message = "Permission Denied";
                        break;
                    case PositionError.POSITION_UNAVAILABLE:
                        message = "Position Unavailable";
                        break;
                    case PositionError.TIMEOUT:
                        message = "Time-out";
                        break;
                    default:
                        message = "Unknown error code.";
                }
                Window.alert(message);
            }
        });
    }

    private void sendReport(double lat, double lon) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/pli-service/rs/sender/sendReportGeo");
        String report = "<report name=\"Me\" ip=\"127.0.0.1\" port=\"10011\" mcastIface=\"\" lat=\""
                + lat + "\" lon=\"" + lon + "\"/>";
        try {
            builder.sendRequest(report, new RequestCallback() {

                @Override
                public void onResponseReceived(Request request, Response response) {
                    m_mapView.fullUpdateView();
                }

                @Override
                public void onError(Request request, Throwable exception) {
                }
            });
        } catch (RequestException ex) {
            Window.alert("fail");
            Logger.getLogger(MapTouchController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
