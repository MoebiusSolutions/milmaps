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
	public abstract boolean handleMouseDown(Event event);
	public abstract boolean handleMouseMove(Event event);
	public abstract boolean handleMouseUp(Event event);
	public abstract boolean handleMouseOut(Event event);
	public abstract boolean handleMouseDblClick(Event event);
	public abstract boolean handleKeyDown(Event event);
	public abstract boolean handleKeyUp(Event event);
}