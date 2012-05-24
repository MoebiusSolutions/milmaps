/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.xmilmaps.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class MilmapsExp implements EntryPoint {

	public void onModuleLoad() {

		GWT.create(XMap.class);
		GWT.create(XFlyToControl.class);
		GWT.create(XMapDimmerControl.class);
		GWT.create(XMapPanZoomControl.class);
		GWT.create(XPositionControl.class);
		GWT.create(XIcon.class);
		loadImpl();
	}
	private native void loadImpl() /*-{    
		if ($wnd.mapsOnLoad && typeof $wnd.mapsOnLoad == 'function') $wnd.mapsOnLoad();  
	}-*/;
}
