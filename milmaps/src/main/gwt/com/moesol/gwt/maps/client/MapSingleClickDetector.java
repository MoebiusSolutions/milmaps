/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.moesol.gwt.maps.client.events.MapSingleClickEvent;

import java.util.HashMap;
import java.util.Map;

public class MapSingleClickDetector
{
    private static final Map<EventBus, Timer> eventThrottler = new HashMap<EventBus, Timer>();
    private final EventBus eventBus;
    private final MapView map;
    private final int acceptableErrorMarginYAxis;
    private final int acceptableErrorMarginXAxis;
    private HandlerRegistration mapMouseDownHandlerRegistration;
    private HandlerRegistration mapMouseUpHandlerRegistration;
    private Timer doubleClickTimer;
    private boolean timerExpired;
    private boolean isDoubleClick;
    private boolean bound = false;
    private int clickStartPositionX;
    private int clickStartPositionY;
    private int minTimeBetweenEvents;
    private int doubleClickThresholdTimeMilli;

    public MapSingleClickDetector(final MapView map,
            final EventBus eventBus,
            final int minTimeBetweenEvents,
            final int doubleClickThresholdTimeMilli,
            final int acceptableErrorMarginXAxis,
            final int acceptableErrorMarginYAxis)
    {
        if(doubleClickThresholdTimeMilli >= minTimeBetweenEvents)
        {
            throw new IllegalStateException(
                    "minTimeBetweenEvents may not be less than doubleClickThresholdTimeMilli");
        }

        this.eventBus = eventBus;
        this.map = map;
        this.acceptableErrorMarginXAxis = acceptableErrorMarginXAxis;
        this.acceptableErrorMarginYAxis = acceptableErrorMarginYAxis;
        this.minTimeBetweenEvents = minTimeBetweenEvents;
        this.doubleClickThresholdTimeMilli = doubleClickThresholdTimeMilli;
        this.timerExpired = false;
        this.isDoubleClick = false;
    }

    public void bind()
    {
        if(bound)
        {
            return;
        }

        this.mapMouseDownHandlerRegistration = map.addDomHandler(new MouseDownHandler()
        {
            @Override
            public void onMouseDown(final MouseDownEvent event)
            {
                if(doubleClickTimer != null && !timerExpired)
                {
                    isDoubleClick = true;
                    eventThrottler.get(eventBus).cancel();
                    timerExpired = false;
                    doubleClickTimer.cancel();
                    doubleClickTimer = null;
                    return;
                }
                else if(doubleClickTimer != null)
                {
                    isDoubleClick = false;
                    doubleClickTimer.cancel();
                    doubleClickTimer = null;
                }

                isDoubleClick = false;
                timerExpired = false;
                doubleClickTimer = new Timer()
                {
                    @Override
                    public void run()
                    {
                        timerExpired = true;
                    }
                };
                doubleClickTimer.schedule(doubleClickThresholdTimeMilli);
                clickStartPositionX = event.getX();
                clickStartPositionY = event.getY();
            }
        }, MouseDownEvent.getType());

        this.mapMouseUpHandlerRegistration = map.addDomHandler(new MouseUpHandler()
        {
            @Override
            public void onMouseUp(final MouseUpEvent event)
            {
                if(isWithinAcceptableErrorMargin(event))
                {
                    Timer runningEvent = eventThrottler.get(eventBus);
                    if(runningEvent != null)
                    {
                        runningEvent.cancel();
                    }

                    runningEvent = new Timer()
                    {
                        @Override
                        public void run()
                        {
                            if(!isDoubleClick)
                            {
                                MapSingleClickEvent.fire(eventBus,
                                        clickStartPositionX,
                                        clickStartPositionY);
                            }
                        }
                    };

                    runningEvent.schedule(minTimeBetweenEvents);
                    eventThrottler.put(eventBus, runningEvent);
                }
            }
        }, MouseUpEvent.getType());

        this.bound = true;
    }

    public int getLastClickX()
    {
        return clickStartPositionX;
    }

    public int getLastClickY()
    {
        return clickStartPositionY;
    }

    public void unbind()
    {
        if(!bound)
        {
            return;
        }

        this.mapMouseDownHandlerRegistration.removeHandler();
        this.mapMouseUpHandlerRegistration.removeHandler();
    }

    private boolean isWithinAcceptableErrorMargin(final MouseUpEvent event)
    {
        return clickStartPositionX <= event.getX() + acceptableErrorMarginXAxis
                && clickStartPositionX
                >= event.getX() - acceptableErrorMarginXAxis
                && clickStartPositionY
                <= event.getY() + acceptableErrorMarginYAxis
                && clickStartPositionY
                >= event.getY() - acceptableErrorMarginYAxis;
    }

    public boolean isBound()
    {
        return this.bound;
    }

    public EventBus getEventBus()
    {
        return eventBus;
    }

    public int getMinTimeBetweenEvents()
    {
        return minTimeBetweenEvents;
    }
}
