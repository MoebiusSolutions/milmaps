/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.maps.server.tms;

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


import java.awt.Graphics2D;
import java.util.Map;

import com.moesol.gwt.maps.shared.Tile;

/**
 * Implemented by objects that are capable of rendering a tile.
 */
public interface ITileRenderer {
	/**
	 * Render a tile into the given graphics context.
	 * 
	 * @param tile The tile to render.
	 * @param graphics The graphics context to render into.
	 * @param parameters Optional extra parameters.
	 */
	public void render(Tile tile, Graphics2D graphics, Map<String, String[]> parameters);
}
