/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.events;

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


import com.google.gwt.event.shared.GwtEvent;
import com.moesol.gwt.maps.client.IProjection;

public class ProjectionChangedEvent extends GwtEvent<ProjectionChangedHandler> {
	public static final Type<ProjectionChangedHandler> TYPE = new Type<ProjectionChangedHandler>();
	private final IProjection m_projection;

	public ProjectionChangedEvent(IProjection p) {
    	m_projection = p;
	}

	@Override
	public Type<ProjectionChangedHandler> getAssociatedType() {
		return TYPE;
	}

    public static Type<ProjectionChangedHandler> getType() {
        return TYPE;
    }

	@Override
	protected void dispatch(ProjectionChangedHandler handler) {
		handler.onProjectionChanged(this);
	}
	
    public IProjection getProjection() {
		return m_projection;
	}

}
