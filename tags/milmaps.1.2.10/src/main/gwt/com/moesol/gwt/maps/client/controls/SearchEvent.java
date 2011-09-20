package com.moesol.gwt.maps.client.controls;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a search event for query string.
 */
public class SearchEvent extends GwtEvent<SearchHandler> {
	public static final Type<SearchHandler> TYPE = new Type<SearchHandler>();
	
	private final String m_searchString;
	
	public SearchEvent(String searchString) {
		m_searchString = searchString;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SearchHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchHandler handler) {
		handler.onSearch(this);
	}

	public String getSearchString() {
		return m_searchString;
	}
}
