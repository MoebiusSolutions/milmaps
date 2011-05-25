package com.moesol.gwt.maps.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;

public class IconLayer {
	private final List<Icon> m_icons = new ArrayList<Icon>();
	
	public void addIcon(Icon i) {
		m_icons.add(i);
	}
	
	public void removeIcon(Icon i) {
		if (m_icons.remove(i)) {
			Image image = i.getImage();
			image.removeFromParent();
		}
	}
	
	public List<Icon> getIcons() {
		return m_icons;
	}
}	
