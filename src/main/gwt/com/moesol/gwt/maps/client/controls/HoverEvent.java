package com.moesol.gwt.maps.client.controls;

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
