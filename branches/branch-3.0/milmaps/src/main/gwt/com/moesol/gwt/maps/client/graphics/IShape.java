/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */

package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.moesol.gwt.maps.client.GeodeticCoords;

public interface IShape {
	//public abstract void setCoordConversion(ICoordConversion cc);
    public abstract void setId(String Id);
    public abstract String id();
	public abstract CssColor getColor();
	public abstract void setColor(CssColor color);

    public abstract IShape selected(boolean selected);
    public abstract IShape erase(Context2d context);
    public abstract IShape render(Context2d context);
    public abstract IShape drawHandles(Context2d context);
    public abstract boolean isSelected();
    public abstract boolean needsUpdate();
    public abstract boolean positionTouches(GeodeticCoords position);
	IAnchorTool getAnchorByPosition(GeodeticCoords position);
}

