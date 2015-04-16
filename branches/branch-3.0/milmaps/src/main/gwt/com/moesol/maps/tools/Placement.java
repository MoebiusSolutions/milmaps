/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.maps.tools;

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


import com.google.gwt.user.client.ui.Widget;

/**
 * Record a widget placement.
 * @author hastings
 */
public class Placement {
	public Widget w;
	public int x;
	public int y;
	public int width;
	public int height;
	public int zindex;
	
	@Override
	public String toString() {
		return "Placement [w=" + w + ", x=" + x + ", y=" + y + ", width="
				+ width + ", height=" + height + ", zindex=" + zindex + "]";
	}
}