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


import com.google.gwt.user.client.rpc.RemoteService;

public interface ILayerConfig extends RemoteService {
	public static final int S_LOG_SEVERE = 1000;
	public static final int S_LOG_WARNING = 900;
	public static final int S_LOG_INFO = 800;
	public static final int S_LOG_CONFIG = 700;
	public static final int S_LOG_FINE = 500;
	public static final int S_LOG_FINER = 400;
	public static final int S_LOG_FINEST = 300;
	public static final int S_LOG_ALL = Integer.MIN_VALUE;
	
	public LayerSet[] getLayerSets();
	public void log(int level, String logString);
}
