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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ILayerConfigAsync {
	public void getLayerSets(AsyncCallback<LayerSet[]> cb);
	public void log(int level, String logString, AsyncCallback<Void> cb);
}
