/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;

public abstract class AbstractPosTool implements IAnchorTool {
	protected GeodeticCoords m_geoPos = null;

	public GeodeticCoords getGeoPos() {
		return m_geoPos;
	}

	public void setGeoPos(GeodeticCoords gc) {
		m_geoPos = gc;
	}
	
	@Override
	public void handleMouseDblClick(Event event) {
	}

	@Override
	public void handleKeyDown(Event event) {
	}

	@Override
	public void handleKeyUp(Event event) {
	}
}
