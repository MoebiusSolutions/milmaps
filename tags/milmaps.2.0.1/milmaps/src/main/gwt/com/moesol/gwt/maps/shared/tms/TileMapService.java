/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
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
