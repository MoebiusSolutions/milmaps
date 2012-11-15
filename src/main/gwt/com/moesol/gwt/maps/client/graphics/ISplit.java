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
	public static int NO_ADJUST = 0;
	public static int ADJUST = 	1;
	
	public abstract void initialize(int adjust);
	
	public abstract void setAjustFlag(boolean flag);
	
	public abstract ISplit withAjustFlag(boolean flag);
	
	public abstract void setSplit(boolean split);
	
	public abstract ISplit withSplit(boolean split);
	
	public abstract boolean isSplit();
	
	public abstract void setMove(int move);
	
	public abstract ISplit withMove(int move);
	
	public abstract int getMove();
	
	public abstract int shift(ViewCoords p, ViewCoords q);
	
	public abstract int getDistance(int move);
	
	public abstract int side(int x);
	
	public abstract int switchMove(int move);	
	
	public abstract int adjustFirstX(int x, int z);
}
