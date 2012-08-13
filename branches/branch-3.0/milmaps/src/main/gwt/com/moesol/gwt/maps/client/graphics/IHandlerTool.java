/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */

package com.moesol.gwt.maps.client.graphics;

public interface IHandlerTool {
	public abstract void handleMouseDown(int x, int y);
	public abstract void handleMouseMove(int x, int y);
	public abstract void handleMouseUp(int x, int y);
	public abstract void handleMouseOut(int x, int y);
	public abstract void handleMouseDblClick(int x, int y);
	public abstract void handleKeyDown(int keyCode);
	public abstract void handleKeyUp(int keyCode );
}