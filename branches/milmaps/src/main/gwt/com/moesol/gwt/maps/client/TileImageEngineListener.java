/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client;

public interface TileImageEngineListener {
	Object createImage(ViewBox vb, TileCoords tileCoords);
	void useImage(ViewBox vb, TileCoords tileCoords, Object image);
	void hideImage(Object image);
	void destroyImage(Object image);
}
