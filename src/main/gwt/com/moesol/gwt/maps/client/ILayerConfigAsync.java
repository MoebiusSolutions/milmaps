/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ILayerConfigAsync {
	public void getLayerSets(AsyncCallback<LayerSet[]> cb);
	public void log(int level, String logString, AsyncCallback<Void> cb);
}
