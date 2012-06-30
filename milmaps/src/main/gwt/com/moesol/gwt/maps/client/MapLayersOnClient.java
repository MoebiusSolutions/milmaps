/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;


/**
 * Save one satellite round trip by just keeping the layer
 * set configuration in gwt code on the client.
 */
public class MapLayersOnClient {
	public static LayerSet[] getLayerSets(IProjection p, boolean bUseMapCache ) {
		//p.setBaseScaleForDegWidth(36.0, 512);
		String servletUrl =  getServerUrl() + "/rpf-ww-server";
		LayerSet layerSet0;
		LayerSet layerSet1;
		
		// Note tomcat rejects %2F for security reasons, so data is no longer BMNG/BMNG (Shaded + Bathymetry) Tiled - 5.2004
		// The URL rewrite code does not know which parts to re-encode, so we double encode the + to %2B.
		layerSet0 = new LayerSet();
		if ( bUseMapCache ){
			servletUrl =  getServerUrl() + "/milmapsCache/rs/mapcache";
			layerSet0.setServer(servletUrl);
			layerSet0.setData("I3_Imagery_Prime_World_2D"); 
			layerSet0.setUrlPattern("{server}/{data}/{epsg}/{imgSize}/{level}/{x}/{y}");
	    	layerSet0.setStartLevelTileDimensionsInDeg(180, 180);
	    	layerSet0.withPixelWidth(512).setPixelHeight(512);
	    	layerSet0.setAutoRefreshOnTimer(false);
	    	layerSet0.setZeroTop(true);
	    	layerSet0.setSrs("EPSG:4326");
	    	layerSet0.setStartLevel(0);		
		}
		else {
			layerSet0.setServer(servletUrl);
			layerSet0.setData("BMNG (Shaded %2B Bathymetry) Tiled - 5.2004"); 
			layerSet0.setUrlPattern("{server}/tileset/BMNG/{data}/level/{level}/x/{x}/y/{y}");
			layerSet0.setStartLevelTileDimensionsInDeg(36.0, 36.0);
			layerSet0.withPixelWidth(512).setPixelHeight(512);
			layerSet0.setAutoRefreshOnTimer(false);
			layerSet0.setZeroTop(false);
			layerSet0.setSrs("EPSG:4326");
			layerSet0.setStartLevel(0);
		}
		//layerSet1 = new LayerSet();
		//layerSet1.setServer(servletUrl);
		//layerSet1.setData("esat_world");
		//layerSet1.setSkipLevels(4);
	    //layerSet1.setStartLevelTileDimensionsInDeg(2.25, 2.25);
	    //layerSet1.withPixelWidth(512).setPixelHeight(512);
		//layerSet1.setUrlPattern("{server}/tileset/{data}/level/{level}/x/{x}/y/{y}");
	    //layerSet1.setAutoRefreshOnTimer(false);
	    //layerSet1.setZeroTop(false);
	    //layerSet1.setEpsg(4326);
	    //layerSet1.setStartLevel(0);
		
		return new LayerSet[] { layerSet0 };//, layerSet1 };
	}

	private static String getServerUrl() {
		//return GWT.getModuleBaseURL() + "/../../..";
		// this is for testing.
		//return "http://bv.moesol.com";
		return "http://localhost:8080";
	}
}
