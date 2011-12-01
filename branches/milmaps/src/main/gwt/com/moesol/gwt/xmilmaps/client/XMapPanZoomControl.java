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
