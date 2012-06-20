/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.place;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.moesol.gwt.maps.client.tms.TileMapServicePlace;

@WithTokenizers(TileMapServicePlace.Tokenizer.class)
public interface MapsPlaceHistoryMapper extends PlaceHistoryMapper {
}
