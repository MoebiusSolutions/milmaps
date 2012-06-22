package com.moesol.gwt.maps.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.moesol.gwt.maps.client.stats.Stats;

public class IconLayer {
	private final List<Icon> m_icons = new ArrayList<Icon>();
	private long m_version = 0L;
	
	public IconLayer() {
	}
	
	public void addIcon(Icon icon) {
		m_icons.add(icon);
		m_version++;
	}
	
	public void removeIcon(Icon icon) {
		if (!m_icons.remove(icon)) {
			return;
		}
		
		Image image = icon.getImage();
		if (image != null) {
			image.removeFromParent();
		}
		
		Label label = icon.getLabel();
		if (label != null) {
			label.removeFromParent();
		}
		
		Image leader = icon.getLabelLeaderImage();
		if (leader != null) {
			leader.removeFromParent();
			Stats.decrementLabelLeaderImageOutstanding();
		}
		
		m_version++;
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
