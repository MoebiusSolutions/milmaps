/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.shared.tms;

/**
 * Interface for getting TMS metadata, the implementation doesn't necessarily need to
 * be an RPC service.
 *
 */
public interface TileMapService {
	public TileMapMetadata getMapMetadata(String serviceUrl, String mapId);

	public TileMapServiceMetadata getServiceMetadata(String serviceUrl);
}
