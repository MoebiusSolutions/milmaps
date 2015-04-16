/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

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


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.moesol.gwt.maps.client.stats.Stats;

public class IconLayer {
	private final List<Icon> m_icons = new ArrayList<Icon>();
//        private final List<GeodeticCoords> m_prevIconLocations = new ArrayList<GeodeticCoords>();
	private long m_version = 0L;
	
	public IconLayer() {
	}
	
	public void addIcon(Icon icon) {
		m_icons.add(icon);
//                m_prevIconLocations.add(icon.getLocation());
		m_version++;
	}
	
	public void removeIcon(Icon icon) {
//                int index = m_icons.indexOf(icon);
//                if (m_prevIconLocations.remove(index) == null) {
//                        return;
//                }
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
//
//        public boolean iconsHaveMoved() {
//                if (m_icons.size() != m_prevIconLocations.size()) {
//                    return false;
//                }
//                for (int i = 0; i < m_icons.size(); i++) {
//                    if (!m_icons.get(i).getLocation().equals(m_prevIconLocations.get(i))) {
//                        return true;
//                    }
//                }
//                return false;
//        }
}	
