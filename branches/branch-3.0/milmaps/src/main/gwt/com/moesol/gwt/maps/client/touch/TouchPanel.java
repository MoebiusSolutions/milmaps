/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.touch;

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


import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;

public class TouchPanel extends SimplePanel {
	
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return addHandler(handler, TouchStartEvent.getType());
	}
	
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return addHandler(handler, TouchEndEvent.getType());
	}

	public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
		return addHandler(handler, TouchMoveEvent.getType());
	}
	
	public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
		return addHandler(handler, TouchCancelEvent.getType());
	}
	
	@Override
	protected void onAttach() {
		super.onAttach();
		sinkTouchEvents(getElement());
	}

	private native void sinkTouchEvents(Element elem) /*-{
		elem.addEventListener('touchstart', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, false);
		elem.addEventListener('touchend', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, false);
		elem.addEventListener('touchmove', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, false);
		elem.addEventListener('touchcancel', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, false);
	}-*/;
	
}
