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
	protected static final boolean PASS_EVENT = true;
	protected static final boolean CAPTURE_EVENT = false;
	protected GeodeticCoords m_geoPos = null;

	public GeodeticCoords getGeoPos() {
		return m_geoPos;
	}

	public void setGeoPos(GeodeticCoords gc) {
		m_geoPos = gc;
	}
	
	@Override
	public boolean handleMouseDblClick(Event event) {
		return PASS_EVENT;
	}

	@Override
	public boolean handleKeyDown(Event event) {
		return PASS_EVENT;
	}

	@Override
	public boolean handleKeyUp(Event event) {
		return PASS_EVENT;
	}
}
