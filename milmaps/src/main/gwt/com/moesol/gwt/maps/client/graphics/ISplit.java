/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.ViewCoords;

public interface ISplit {
	public abstract void setAjustFlag(boolean flag);
	
	public abstract boolean isSplit();
	
	public abstract void setMove(int move);
	
	public abstract int getMove();
	
	public abstract int shift(ViewCoords p, ViewCoords q);
	
	public abstract int getDistance(int move);
}
