package com.moesol.gwt.maps.client.tms;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class TileMapAddEvent extends GwtEvent<TileMapAddEvent.Handler> {
	private TileMapMetadata tileMapMetadata;
	
	public static final GwtEvent.Type<Handler> TYPE = new Type<TileMapAddEvent.Handler>();
	
	public interface Handler extends EventHandler {
		void onTileMapAdd(TileMapAddEvent event);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onTileMapAdd(this);
	}

	public TileMapAddEvent(TileMapMetadata tileMapMetadata) {
		super();
		this.tileMapMetadata = tileMapMetadata;
	}

	public TileMapMetadata getTileMapMetadata() {
		return tileMapMetadata;
	}
}
