/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.place;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.moesol.gwt.maps.client.gin.MapsGinjector;
import com.moesol.gwt.maps.client.tms.TileMapServiceActivity;
import com.moesol.gwt.maps.client.tms.TileMapServicePlace;

public class MapsActivityMapper implements ActivityMapper {
	
	private MapsGinjector injector;
	
	public MapsActivityMapper(MapsGinjector injector) {
		this.injector = injector;
	}

	@Override
	public Activity getActivity(Place place) {
		if (place instanceof TileMapServicePlace) {
			TileMapServiceActivity activity = injector.getTileMapServiceActivity();
			activity.setPlace((TileMapServicePlace)place);
			return activity;
		} else {
			return null;
		}
	}
}
