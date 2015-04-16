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


public interface IContext {

	public abstract void beginPath();
	
	public abstract void clearRect(double x, double y, double w, double h);
	public abstract void closePath();
	
	public abstract void lineTo(double x, double y);
	public abstract void moveTo(double x, double y);
	
	public abstract void setLineWidth(double width);
	public abstract void setStrokeStyle(String style);
	
	public abstract void stroke();
	public abstract void strokeRect(double x, double y, double w, double h);
}
