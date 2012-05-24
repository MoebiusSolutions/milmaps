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

/** These are the well known public, free map tile servers */
public class LayerSetRegistry {
	public final LayerSet ARCGIS_I3 = new LayerSet()
		.withServer("http://localhost:8080/geowebcache/service/tms/1.0.0")
		.withData("I3_Imagery_Prime_World_2D")
		.withUrlPattern("{server}/BMNG@EPSG:4326@png/{level}/{y}/{x}.png")
		//.withServer("http://services.arcgisonline.com/ArcGIS/rest/services")
		//.withData("I3_Imagery_Prime_World_2D")
		//.withUrlPattern("{server}/{data}/MapServer/tile/{level}/{y}/{x}")
		.withZeroTop(true)
		.build();
	public final LayerSet BLUE_MARBLE_NEXTGEN = new LayerSet()
		.withServer("http://worldwind25.arc.nasa.gov/tile/tile.aspx")
		.withData("bmng.topo.bathy.200405")
		.build();
	public final LayerSet ESAT_WORLD = new LayerSet()
		.withServer("http://worldwind25.arc.nasa.gov/lstile/lstile.aspx")
		.withData("esat_world") // esat_worlddds give dds files.
		.withSkipLevels(4)
		.build();
}
