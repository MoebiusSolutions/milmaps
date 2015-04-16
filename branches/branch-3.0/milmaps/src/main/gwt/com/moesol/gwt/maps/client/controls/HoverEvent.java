/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.controls;

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


import com.google.gwt.event.shared.GwtEvent;

public class HoverEvent extends GwtEvent<HoverHandler> {
	private static final Type<HoverHandler> TYPE = new Type<HoverHandler>();
	private int m_x;
	private int m_y;
	private int m_clientX;
	private int m_clientY;

	@Override
	public Type<HoverHandler> getAssociatedType() {
		return TYPE;
	}
	
	public static Type<HoverHandler> getType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HoverHandler handler) {
		handler.onHover(this);
	}

	public int getX() {
		return m_x;
	}

	public void setX(int x) {
		m_x = x;
	}
	public HoverEvent withX(int x) {
		setX(x); return this;
	}

	public int getY() {
		return m_y;
	}

	public void setY(int y) {
		m_y = y;
	}
	public HoverEvent withY(int y) {
		setY(y); return this;
	}

	public int getClientX() {
		return m_clientX;
	}
	public void setClientX(int clientX) {
		m_clientX = clientX;
	}
	public HoverEvent withClientX(int x) {
		setClientX(x); return this;
	}

	public int getClientY() {
		return m_clientY;
	}

	public void setClientY(int clientY) {
		m_clientY = clientY;
	}
	public HoverEvent withClientY(int y) {
		setClientY(y); return this;
	}

}
