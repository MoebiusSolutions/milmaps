package com.moesol.gwt.maps.client;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class ImageDiv extends AbsolutePanel {
	public final Image image;
	public final AbsolutePanel inner;
	private HandlerRegistration m_loadReg;
	private HandlerRegistration m_errorReg;

	public ImageDiv() {
		image = new Image();
		image.getElement().getStyle().setPosition(Position.ABSOLUTE);
		image.getElement().getStyle().setLeft(0, Unit.PX);
		image.getElement().getStyle().setRight(0, Unit.PX);
		image.getElement().getStyle().setWidth(100, Unit.PCT);
		image.getElement().getStyle().setHeight(100, Unit.PCT);

		// This is what LayoutPanel was adding.
		inner = new AbsolutePanel();
		inner.getElement().getStyle().setPosition(Position.ABSOLUTE);
		inner.getElement().getStyle().setLeft(0, Unit.PX);
		inner.getElement().getStyle().setRight(0, Unit.PX);
		inner.getElement().getStyle().setTop(0, Unit.PX);
		inner.getElement().getStyle().setBottom(0, Unit.PX);
		inner.add(image);
		
		this.getElement().setClassName("imagediv");
		this.getElement().getStyle().setPosition(Position.ABSOLUTE);
		this.add(inner);
	}

	public void removeImage() {
		remove(image);
		// image = null;
	}

	public Image getImage() {
		return image;
	}

	public void setUrl(String url) {
		image.setUrl(url);
	}

	public String getUrl() {
		return image.getUrl();
	}

	public void setStyleName(String name) {
		image.setStyleName(name);
	}

	public void addHandlers(TileImageLoadListener handler) {
		m_loadReg = image.addLoadHandler(handler);
		m_errorReg = image.addErrorHandler(handler);
	}
	public void removeHandlers() {
		m_loadReg.removeHandler();
		m_errorReg.removeHandler();
	}

}
