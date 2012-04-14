package com.moesol.gwt.maps.client;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class ImageDiv extends AbsolutePanel {
	public Image image;
	public final AbsolutePanel inner;

	public ImageDiv() {
		image = new Image();
		image.setWidth("100%");
		image.setHeight("100%");

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
		image = null;
	}

	public Image getImage() {
		return image;
	}

	public void setUrl(String url) {
		if (image != null) {
			image.setUrl(url);
		}
	}

	public String getUrl() {
		if (image != null) {
			return image.getUrl();
		}
		return null;
	}

	public void setStyleName(String name) {
		if (image != null) {
			image.setStyleName(name);
		}
	}

	@SuppressWarnings("deprecation")
	public void addLoadListener(TileImageLoadListener ll) {
		if (image != null) {
			image.addLoadListener(ll);
		}
	}

	@SuppressWarnings("deprecation")
	public void removeLoadListener(TileImageLoadListener ll) {
		if (image != null) {
			image.removeLoadListener(ll);
		}
	}
}
