/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


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
