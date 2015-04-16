/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

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


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.moesol.maps.tools.Placement;

public class RecordingIconPlacer implements WidgetPositioner {

	List<Placement> images = new ArrayList<Placement>();
	List<Placement> labels = new ArrayList<Placement>();

	public void place(Widget widget, int x, int y, int zindex) {
		Placement p = new Placement();
		p.w = widget;
		p.x = x;
		p.y = y;
		p.zindex = zindex;

		if (widget instanceof Image) {
			images.add(p);
		} else {
			labels.add(p);
		}
	}

	@Override
	public void remove(Widget widget) {
	}

}
