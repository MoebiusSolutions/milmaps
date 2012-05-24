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

import java.awt.image.BufferedImage;

import org.easymock.EasyMock;
import org.junit.Ignore;

import com.moesol.gwt.maps.shared.Feature;

@Ignore
class TestFeatureTileRenderer extends
		AbstractFeatureTileRenderer {
	public TestFeatureTileRenderer(IFeatureProvider dataProvider) {
		super(dataProvider);
	}

	private BufferedImage m_icon = EasyMock.createMock(BufferedImage.class);

	@Override
	public BufferedImage getIcon(Feature feature) {
		return m_icon;
	}
}