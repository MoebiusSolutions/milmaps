/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client.events;

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
