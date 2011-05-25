package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LoadListener;
import com.google.gwt.user.client.ui.Widget;

public class Icon {
	private final GeodeticCoords m_location = new GeodeticCoords();
	private final ViewCoords m_iconOffset = new ViewCoords();
	private static String MISSING_IMAGE_URL = "missing.gif";
	private String m_iconUrl;
	private String m_clickUrl;
	private Image m_image = new Image();
	private final LoadListener m_loadListener = new LoadListener() {
		@Override
		public void onError(Widget w) {
			Image image = (Image) w;
			image.removeLoadListener(this);
			image.setUrl(MISSING_IMAGE_URL);
		}
		@Override
		public void onLoad(Widget w) {
			Image image = (Image) w;
			image.removeLoadListener(this);
		}
	};
	
	public Icon() {
	}
	
	public String getTitle() {
		return m_image.getTitle();
	}
	
	public void setTitle(String title) {
		m_image.setTitle(title);
	}

	public String getIconUrl() {
		return m_iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		m_iconUrl = iconUrl;
		m_image.addLoadListener(m_loadListener);
		m_image.setUrl(iconUrl);
	}

	public String getClickUrl() {
		return m_clickUrl;
	}

	public void setClickUrl(String clickUrl) {
		m_clickUrl = clickUrl;
	}

	public GeodeticCoords getLocation() {
		return m_location;
	}

	public void setLocation(GeodeticCoords location) {
		m_location.copyFrom(location);
	}
	
	public Image getImage() {
		return m_image;
	}

	public ViewCoords getIconOffset() {
		return m_iconOffset;
	}
	
	/**
	 * The default icon offset is 0, 0, the upper left hand corner of
	 * the icon. The offset is added to the icon position when placed on the map.
	 * To center an icon set the x offset to -width/2 and the y offset to -height/2.
	 * 
	 * @param offset
	 */
	public void setIconOffset(ViewCoords offset) {
		m_iconOffset.setX(offset.getX());
		m_iconOffset.setY(offset.getY());
	}
	
}
