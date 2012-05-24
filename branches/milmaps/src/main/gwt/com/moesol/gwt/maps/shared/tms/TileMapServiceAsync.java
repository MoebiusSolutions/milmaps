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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Interface for getting TMS metadata asynchronously, the implementation doesn't necessarily need to
 * be an RPC service.
 *
 */
public interface TileMapServiceAsync {
	public void getMapMetadata(String serviceUrl, String mapId,
			AsyncCallback<TileMapMetadata> callback);

	public void getServiceMetadata(String serviceUrl,
			AsyncCallback<TileMapServiceMetadata> callback);
}
