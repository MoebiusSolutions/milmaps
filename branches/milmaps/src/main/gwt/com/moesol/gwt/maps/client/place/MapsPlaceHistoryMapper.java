/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client.place;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.moesol.gwt.maps.client.tms.TileMapServicePlace;

@WithTokenizers(TileMapServicePlace.Tokenizer.class)
public interface MapsPlaceHistoryMapper extends PlaceHistoryMapper {
}
