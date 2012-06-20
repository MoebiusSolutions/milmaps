/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.maps.server.tms;

import java.awt.image.BufferedImage;

/**
 * Implemented by classes capable of caching tile images.
 */
public interface ITileImageCache {
	public BufferedImage getImage(String layerId, int level, int x, int y);

	public void addImage(String layerId, int level, int x, int y, BufferedImage image);
}
