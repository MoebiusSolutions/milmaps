package com.moesol.gwt.maps.client.controls;

import java.util.ArrayList;

import com.moesol.gwt.maps.client.MapScale;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.units.Degrees;

/**
 * Handles search events to implement fly-to functionality. Encapsulating
 * the fly-to functionality allowed it to be decoupled from the SearchControl
 * (which could be used for other types of searches).
 */
public class FlyToController implements SearchHandler {
	private MapView m_map;

	private ArrayList<IFlyToHandler> m_handlers = new ArrayList<IFlyToHandler>();
	
	public FlyToController() {
	}

	public FlyToController( MapView map ) {
		m_map = map;
	}
	
	public void setMapView(MapView map ) {
		
		m_map = map;
	}
	
	/**
	 * Add a handler for fly to queries. This allows applications
	 * to provide their own fly-to logic, while allowing the controller
	 * to be general. Handlers should be added in order of precedence in
	 * cases where multiple handlers are capable of handling the same
	 * query.
	 * 
	 * @param handler The handler to add.
	 */
	public void addFlyToHandler(IFlyToHandler handler) {
		m_handlers.add(handler);
	}

	/**
	 * Implemented by classes that can handle fly-to queries.
	 */
	public interface IFlyToHandler {
		/**
		 * Determines whether the handler is capable of handling the
		 * query. For example, a handler might only handle queries
		 * that conform to a certain regular expression.
		 * 
		 * @param input The query string.
		 * @return
		 */
		boolean canHandle(String input);

		/**
		 * Attempt Handle the input fly-to query.
		 * 
		 * @param input The query string.
		 */
		void handleFlyTo(String input);
	}

	/*
	 * (non-Javadoc)
	 * @see com.moesol.gwt.maps.client.controls.SearchHandler#onSearch(com.moesol.gwt.maps.client.controls.SearchEvent)
	 */
	@Override
	public void onSearch(SearchEvent searchEvent) {
		String str = searchEvent.getSearchString();
		str.trim();

		// allow registered handlers to attempt to
		// handle the query first, in sequential order
		for (IFlyToHandler handler : m_handlers) {
			if (handler.canHandle(str)) {
				handler.handleFlyTo(str);
				return;
			}
		}

		double lat;
		double lng;
		MapScale scale;
		double zoomX;
		String[] s = str.split(",");

		if (s.length > 1) {
			// TODO better parsing code for lat/lng.
			lat = Double.parseDouble(s[0]);
			lng = Double.parseDouble(s[1]);
			if (s.length == 3) {
				scale = MapScale.parse(s[2]);
			} else {
				scale = MapScale.parse("1:1M");
			}
			m_map.flyTo(Degrees.geodetic(lng, lat), scale);
		} else if (s.length == 1) {
			zoomX = Double.parseDouble(s[0]);
			m_map.animateZoom(zoomX);
		}
	}
}
