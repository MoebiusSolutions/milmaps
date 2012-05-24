/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.maps.server.tms;

import static org.junit.Assert.*;

import org.junit.Test;

import com.moesol.gwt.maps.client.LayerSet;

public class RemoteTileRendererTest {

	@Test
	public void testGenerateUrl() {
		String urlFormat = "{server}/{data}/{level}/{y}/{x}/{y}.png";
		
		LayerSet layerSet = new LayerSet();
		layerSet.withServer("some-server").withData("some-data");
		layerSet.withUrlPattern(urlFormat);
		
		RemoteTileRenderer remoteTileRenderer = new RemoteTileRenderer("some-server", layerSet);
		assertEquals("some-server/some-data/0/2/1/2.png", remoteTileRenderer.generateUrl(0, 1, 2));
	}
	
	@Test
	public void testGenerateUrl_ZeroTop() {
		String urlFormat = "{server}/{data}/{level}/{y}/{x}/{y}.png";
		
		LayerSet layerSet = new LayerSet();
		layerSet.withServer("some-server").withData("some-data").withZeroTop(true);
		layerSet.withUrlPattern(urlFormat);
		
		RemoteTileRenderer remoteTileRenderer = new RemoteTileRenderer("some-server", layerSet);
		assertEquals("some-server/some-data/2/3/1/3.png", remoteTileRenderer.generateUrl(2, 1, 0));
		
		assertEquals("some-server/some-data/2/0/1/0.png", remoteTileRenderer.generateUrl(2, 1, 3));
		
		assertEquals("some-server/some-data/2/2/1/2.png", remoteTileRenderer.generateUrl(2, 1, 1));
	}
}
