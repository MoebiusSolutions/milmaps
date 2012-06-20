/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */

package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.moesol.gwt.maps.client.GeodeticCoords;

public interface IShape {
	//public abstract void setCoordConversion(ICoordConversion cc);
    public abstract void setId(String Id);
    public abstract String id();
    // public abstract whichHandle(); ????
    public abstract boolean touchesCoordinates();
    public abstract void selected(boolean selected);
    public abstract boolean isSelected();
    public abstract void render(Context2d context);
    public abstract void erase(Context2d context);
    public abstract boolean needsUpdate();
    public abstract boolean positionTouches(GeodeticCoords position);
	IAnchorTool getAnchorByPosition(GeodeticCoords position);
}

