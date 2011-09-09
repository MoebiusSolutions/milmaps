package com.moesol.gwt.maps.client.tms;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class TileMapRemoveEvent extends GwtEvent<TileMapRemoveEvent.Handler> {
	private TileMapMetadata tileMapMetadata;
	
	public static final GwtEvent.Type<Handler> TYPE = new Type<TileMapRemoveEvent.Handler>();
	
	public interface Handler extends EventHandler {
		void onTileMapRemove(TileMapRemoveEvent event);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onTileMapRemove(this);
	}

	public TileMapRemoveEvent(TileMapMetadata tileMapMetadata) {
		super();
		this.tileMapMetadata = tileMapMetadata;
	}

	public TileMapMetadata getTileMapMetadata() {
		return tileMapMetadata;
	}
}
