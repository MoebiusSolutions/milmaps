package com.moesol.gwt.maps.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class IconLayer {
	private final List<Icon> m_icons = new ArrayList<Icon>();
	private long m_version = 0L;
	
	public void addIcon(Icon i) {
		m_icons.add(i);
		m_version++;
	}
	
	public void removeIcon(Icon i) {
		if (m_icons.remove(i)) {
			Image image = i.getImage();
			image.removeFromParent();
			Label label = i.getLabel();
			if ( label != null ){
				label.removeFromParent();
			}
			m_version++;
		}
	}
	
	public List<Icon> getIcons() {
		return m_icons;
	}
	
	/**
	 * @return a different number each time the list is changed.
	 */
	public long getVersion() {
		return m_version;
	}
}	
