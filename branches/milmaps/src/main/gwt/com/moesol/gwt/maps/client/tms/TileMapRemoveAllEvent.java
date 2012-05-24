/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.tms;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class TileMapRemoveAllEvent extends GwtEvent<TileMapRemoveAllEvent.Handler> {
	
	public static final GwtEvent.Type<Handler> TYPE = new Type<TileMapRemoveAllEvent.Handler>();
	
	public interface Handler extends EventHandler {
		void onTileMapRemoveAll(TileMapRemoveAllEvent event);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onTileMapRemoveAll(this);
	}
}
