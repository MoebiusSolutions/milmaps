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

import com.google.gwt.user.client.rpc.RemoteService;

public interface ILayerConfig extends RemoteService {
	public static final int S_LOG_SEVERE = 1000;
	public static final int S_LOG_WARNING = 900;
	public static final int S_LOG_INFO = 800;
	public static final int S_LOG_CONFIG = 700;
	public static final int S_LOG_FINE = 500;
	public static final int S_LOG_FINER = 400;
	public static final int S_LOG_FINEST = 300;
	public static final int S_LOG_ALL = Integer.MIN_VALUE;
	
	public LayerSet[] getLayerSets();
	public void log(int level, String logString);
}
