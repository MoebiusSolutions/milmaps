/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.user.client.Event;

public abstract class AbstractEditTool implements IShapeTool{
	protected static final boolean PASS_EVENT = true;
	protected static final boolean CAPTURE_EVENT = false;

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
