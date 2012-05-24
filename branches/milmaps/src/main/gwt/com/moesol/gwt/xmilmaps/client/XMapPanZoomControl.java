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

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import com.moesol.gwt.maps.client.controls.MapPanZoomControl;

@ExportPackage("milmaps")
public class XMapPanZoomControl extends MapPanZoomControl implements Exportable {
	
	@Export("initPan")
	public void initPan( int maxPanPixels, int millisBetweenPans ){
		initPanVals( maxPanPixels, millisBetweenPans );
	}
}
