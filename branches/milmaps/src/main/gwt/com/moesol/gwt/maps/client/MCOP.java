/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.moesol.gwt.maps.client.touch.TouchPanel;

public class MCOP implements EntryPoint {

	private MapView m_map;
	private Label m_msg;

	public MCOP() {
		super();
	}

	@Override
	public void onModuleLoad() {
		RootPanel mapPanel = RootPanel.get("mapPanel");
		if (mapPanel != null) {
			doMapPanel(mapPanel);
			return;
		}
	}

	private void doMapPanel(RootPanel mapPanel) {
		DOM.setInnerHTML(mapPanel.getElement(), "");
		TouchPanel touchPanel = makeTouchPanel();
		AbsolutePanel controlsAndMap = new AbsolutePanel();
		
		m_msg = new Label("msg...");
		m_map = new MapView();
		loadLayerConfigsFromClient();
		
		int w = Window.getClientWidth() - 20;
		int h = Window.getClientHeight() - 20;
		
		controlsAndMap.setPixelSize(w, h);
		m_map.setPixelSize(w, h);
		m_map.updateView();
		bindListeners(touchPanel, m_map);
		
		Button in = new Button(" + ", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				goIn();
			}
		});
		Button out = new Button(" - ", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				goOut();
			}
		});

		HorizontalPanel bar = new HorizontalPanel();
		bar.add(in);
		bar.add(out);
		bar.add(m_msg);
		
		touchPanel.setWidget(m_map);
		controlsAndMap.add(touchPanel);
		controlsAndMap.add(bar);
		controlsAndMap.setWidgetPosition(bar, 10, 10);
		mapPanel.add(controlsAndMap);
		
		// Layer a tranparent dialog...
//		DialogBox db = new DialogBox();
//		VerticalPanel dbPanel = new VerticalPanel();
//		db.setHTML("<a href='' onclick='false'>x</a>");
//		db.setPopupPosition(100, 10);
//		db.setPixelSize(100, 100);
//		db.show();
	}

	private void bindListeners(TouchPanel touchPanel, MapView map) {
		MapController controller = map.getController();
		touchPanel.addTouchStartHandler(controller);
		touchPanel.addTouchMoveHandler(controller);
		touchPanel.addTouchEndHandler(controller);
		touchPanel.addTouchCancelHandler(controller);
		controller.withMsg(m_msg);
	}

	private TouchPanel makeTouchPanel() {
		TouchPanel touchPanel = new TouchPanel();
		touchPanel.setStyleName("touchPanel");
//		touchPanel.addTouchStartHandler(new TouchStartHandler() {
//			@Override
//			public void onTouchStart(TouchStartEvent event) {
//				event.preventDefault();
//				Window.alert("got touchstart event: x: " 
//						+ event.touches().get(0).pageX()
//						+ "y: "
//						+ event.touches().get(0).pageY()
//						);
//			}
//		});
//		touchPanel.addTouchEndHandler(new TouchEndHandler() {
//			@Override
//			public void onTouchEnd(TouchEndEvent event) {
//				event.preventDefault();
//				Window.alert("got touchend event: " + event.touches().length());
//			}
//		});
//		touchPanel.addTouchMoveHandler(new TouchMoveHandler() {
//			@Override
//			public void onTouchMove(TouchMoveEvent event) {
//				event.preventDefault();
//				Window.alert("got touchmove event: " + event.touches().length());
//			}
//		});
//		touchPanel.addTouchCancelHandler(new TouchCancelHandler() {
//			@Override
//			public void onTouchCancel(TouchCancelEvent event) {
//				event.preventDefault();
//				Window.alert("got touchcancel event: " + event.touches().length());
//			}
//		});
		return touchPanel;
	}

	private void loadLayerConfigsFromClient() {
		// TODO the projection will auto adjust to layerset values but
		// for now we are going to use a layerset with top-level
		// tiles that are 180.0. The default (Worldwind) uses 36.0
		// degree tiles.
		IProjection p = m_map.getProjection();
		//p.LEVEL0_DLAMBDA = Degrees.asRadians(180.0);
		//p.LEVEL0_DPHI = Degrees.asRadians(180.0);
		//p.setdLamda(Degrees.asRadians(180.0));
		//p.setdPhi(Degrees.asRadians(180.0));
		// re-read cookies with new start.
		//ProjectionValues.readCookies(p);
		
		m_map.addLayer(new LayerSetRegistry().ARCGIS_I3);
			
		LayerSet layerSet = new LayerSet();
		layerSet.setServer("http://mcop-server/rpf-ww-server/tiles");
		layerSet.setData("tracks");
		m_map.addLayer(layerSet);
	}

	protected void goIn() {
		m_map.animateZoom(2);
		//m_map.updateView();
	}
	
	protected void goOut() {
		m_map.animateZoom(1.0/2.0);
		//m_map.updateView();
	}

}
