/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.timing.interpolators;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.moesol.gwt.maps.client.timing.interpolators.SplineInterpolator;

public class SplineInterpolatorTest {

	@Test
	public void testInterpolate() {
		SplineInterpolator interpolator = new SplineInterpolator(0.5, 0, 0.5, 1.0);
		assertEquals(0.0, interpolator.interpolate(0.0), 0.0);
		assertEquals(1.0, interpolator.interpolate(1.0), 0.001);
	}

	@Test
	public void testImage() throws IOException {
		SplineInterpolator interpolator = new SplineInterpolator(0.25, 0.25, 0.75, 0.75);
		SplineInterpolator interpolator2 = new SplineInterpolator(1.0, 0.0, 0.0, 1.0);
		SplineInterpolator interpolator3 = new SplineInterpolator(0.0, 1.0, 0.0, 1.0);
		SplineInterpolator interpolator4 = new SplineInterpolator(1.0, 0.0, 1.0, 0.0);
		BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2d = image.createGraphics();
		graphics2d.setColor(Color.BLUE);
		apply(interpolator, graphics2d);
		graphics2d.setColor(Color.RED);
		apply(interpolator2, graphics2d);
		apply(interpolator3, graphics2d);
		apply(interpolator4, graphics2d);
		ImageIO.write(image, "png", new File("target/test-spline-1.png"));
	}

	private void apply(SplineInterpolator interpolator, Graphics2D graphics2d) {
		int priorX = 0;
		int priorY = 512;
		for (int x = 0; x < 512; x++) {
			double progress = x / 512.0;
			double splined = interpolator.interpolate(progress);
			double y = 512.0 - 512.0 * splined;
			
			graphics2d.drawLine(priorX, priorY, (int)x, (int)y);
			
			priorX = (int) x;
			priorY = (int) y;
		}
	}
}
