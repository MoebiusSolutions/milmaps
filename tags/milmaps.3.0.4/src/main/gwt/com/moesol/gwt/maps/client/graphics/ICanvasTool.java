/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

public interface ICanvasTool {
	public abstract IContext getContext();
	public abstract int getOffsetWidth();
	public abstract int getOffsetHeight();
	public abstract void setSize(int width, int height);
}
