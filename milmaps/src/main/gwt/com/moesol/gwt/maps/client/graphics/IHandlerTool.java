/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */

package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.user.client.Event;

public interface IHandlerTool {
	public abstract void handleMouseDown(Event event);
	public abstract void handleMouseMove(Event event);
	public abstract void handleMouseUp(Event event);
	public abstract void handleMouseOut(Event event);
	public abstract void handleMouseDblClick(Event event);
	public abstract void handleKeyDown(Event event);
	public abstract void handleKeyUp(Event event);
}