/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.user.client.Event;

public abstract class AbstractNewTool implements IShapeTool {
	
	@Override
	public void setAnchor(IAnchorTool anchor) {
	}

	@Override
	public void hilite() {	
	}
	
	@Override
	public void handleMouseOut(Event event) {
	}

	@Override
	public void  handleMouseDblClick(Event event) {
	}

	@Override
	public void handleKeyDown(Event event) {
	}

	@Override
	public void handleKeyUp(Event event) {
	}
}
