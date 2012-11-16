/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;


public interface IShapeTool extends IHandlerTool {
	public abstract void hilite();
	public abstract void setShape(IShape shape);
	public abstract void setAnchor(IAnchorTool anchor);
	public abstract IShape getShape();
	public abstract void done();
}
