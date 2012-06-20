/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.controls;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTML;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.events.MapViewChangeEvent;

/**
 * Animated bubble to show on map.
 * You need to set CSS styles on the map-BubbleControl
 * or the bubble will show above the map. Below is an
 * example.
 <pre>
 .map-BubbleControl {
 z-index: 10000;
 border: 2px outset;
 padding: 4px;
 background: grey;
 opacity: 0.7;
 filter: alpha(opacity = 70);
 }
 </pre>
 */
public class BubbleControl extends DecoratedPopupPanel {

    private final HTML m_html;
    private final int TOP_OFFSET = -20;
    private final int LEFT_OFFSET = 20;
    private boolean bound = false;
    private int m_duration = 500;
    private int m_initialX;
    private int m_initialY;
    private MapView m_map;
    private HandlerRegistration m_mouseDownHandlerRegistration;
    private HandlerRegistration m_mapViewChangeEventRegistration;
    private HandlerRegistration m_mouseWheelHandlerRegistration;

    public BubbleControl(final MapView map) {
        m_map = map;
        setStylePrimaryName("map-BubbleControl");
        m_html = new HTML("Loading...");
        setWidget(m_html);
        bind();
    }

    public void bind() {
        if (bound) {
            return;
        }

        m_mouseDownHandlerRegistration = m_map.addDomHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.preventDefault();
                hide();
            }
        }, MouseDownEvent.getType());

        m_mouseWheelHandlerRegistration = m_map.addDomHandler(new MouseWheelHandler() {

            @Override
            public void onMouseWheel(final MouseWheelEvent mouseWheelEvent) {
                hide();
            }
        }, MouseWheelEvent.getType());

        m_mapViewChangeEventRegistration = MapViewChangeEvent.register(m_map.getEventBus(), new MapViewChangeEvent.Handler() {

            @Override
            public void onMapViewChangeEvent(final MapViewChangeEvent mapViewValueChangeEvent) {
                hide();
            }
        });

        bound = true;
    }

    public void unbind() {
        if (!bound) {
            return;
        }

        m_mouseDownHandlerRegistration.removeHandler();
        m_mapViewChangeEventRegistration.removeHandler();
        m_mouseWheelHandlerRegistration.removeHandler();

        bound = false;
    }

    /**
     * Show the bubble control over the map using an animation. If you call this
     * method from HoverHandler you can pass clientX and clientY from the event
     * to have the bubble show up near the mouse.
     * 
     * @param x
     *            - clientX
     * @param y
     *            - clientY
     */
    public void animateShow(int x, int y) {
        m_initialX = x;
        m_initialY = y;

        Animation a = new Animation() {

            @Override
            protected void onStart() {
                //getWidget().getElement().getStyle().setProperty("fontSize", 0.0 * 100.0 + "%");
                setPopupPosition(m_initialX, m_initialY);
                show();
            }

            @Override
            protected void onUpdate(double progress) {
                //getWidget().getElement().getStyle().setProperty("fontSize", progress * 100.0 + "%");
                int targetX = Math.min(m_initialX + (int) (LEFT_OFFSET * progress), Window.getClientWidth() - getOffsetWidth());
                int targetY = m_initialY + (int) (TOP_OFFSET * progress) - getOffsetHeight();
                targetY = Math.max(targetY, Document.get().getScrollTop());
                setPopupPosition(targetX, targetY);
            }
        };

        a.run(m_duration);
    }

    /**
     * Show the bubble control over the map without any animation. 
     * 
     * @param x clientX
     * @param y clientY
     */
    public void show(int x, int y) {
        int targetX = Math.min(x + (int) (LEFT_OFFSET), Window.getClientWidth() - getOffsetWidth());
        int targetY = y + (int) (TOP_OFFSET) - getOffsetHeight();
        targetY = Math.max(targetY, Document.get().getScrollTop());
        setPopupPosition(targetX, targetY);
        show();
    }

    /**
     * @return HTML widget. Use this widget to set the bubble HTML contents.
     */
    public HTML getHtml() {
        return m_html;
    }

    /** Get duration of bubble animation.
     * 
     * @return Duration of animation in milliseconds.
     */
    public int getDuration() {
        return m_duration;
    }

    /** Set duration of bubble animation.
     * 
     * @param duration - duration of animation in milliseconds.
     */
    public void setDuration(int duration) {
        m_duration = duration;
    }

    /** Set duration of bubble animation.
     * 
     * @param duration - duration of animation in milliseconds.
     * @return this to allow method chaining.
     */
    public BubbleControl withDuration(int duration) {
        setDuration(duration);
        return this;
    }
}
