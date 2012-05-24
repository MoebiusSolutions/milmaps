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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.moesol.gwt.maps.client.LayerSet;
import com.moesol.gwt.maps.shared.Tile;

/**
 * Renders tiles retrieve by URL. 
 */
public class RemoteTileRenderer implements ITileRenderer {

	public interface IBaseUrlProvider {
		public String getBaseUrl();
	}

	private static final Logger LOGGER = Logger
			.getLogger(RemoteTileRenderer.class.getName());
	private LayerSet m_layerSet;
	private String m_urlFormat;
	private IBaseUrlProvider m_baseUrlProvider;
	private String m_layerId;
	private ITileImageCache m_tileCache;

	/**
	 * @param layerId The ID for the layer.
	 * @param layerSet The configuration object for the remote layer
	 * set.
	 */
	public RemoteTileRenderer(String layerId, LayerSet layerSet) {
		m_layerId = layerId;
		m_layerSet = layerSet;
		m_urlFormat = generateUrlFormat(layerSet);
	}

	public RemoteTileRenderer withBaseUrlProvider(
			IBaseUrlProvider baseUrlProvider) {
		m_baseUrlProvider = baseUrlProvider;
		return this;
	}

	public RemoteTileRenderer withTileCache(ITileImageCache tileCache) {
		m_tileCache = tileCache;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.moesol.maps.server.tms.ITileRenderer#render(com.moesol.gwt.maps.shared.Tile, java.awt.Graphics2D, java.util.Map)
	 */
	@Override
	public void render(Tile tile, Graphics2D graphics,
			Map<String, String[]> parameters) {
		int tileWidth = tile.getPixelWidth();
		int tileHeight = tile.getPixelHeight();
		int level = tile.getLevel();
		int x = tile.getX();
		int y = tile.getY();

		BufferedImage image = getImage(level, x, y);

		if (image != null) {
			graphics.drawImage(image, 0, 0, tileWidth, tileHeight, null);
		}
	}

	BufferedImage getImage(int level, int x, int y) {
		BufferedImage image = null;
		if (m_tileCache != null) {
			image = m_tileCache.getImage(m_layerId, level, x, y);
		}

		if (image == null) {
			String url = generateUrl(level, x, y);
			try {
				// don't need an observer here since it's a buffered image
				image = ImageIO.read(new URL(url));

				if (image != null) {
					m_tileCache.addImage(m_layerId, level, x, y, image);
				}
			} catch (MalformedURLException e) {
				LOGGER.log(Level.SEVERE, "URL was malformed: " + url, e);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Failed reading image from URL: "
						+ url, e);
			}
		}
		return image;
	}

	String generateUrlFormat(LayerSet layerSet) {
		// TODO: need to integrate the URL generation code with
		// client-side equivalent
		String format = layerSet.getUrlPattern();
		format = format.replaceAll("\\{server\\}", layerSet.getServer());
		format = format.replaceAll("\\{data\\}", layerSet.getData());
		return format;
	}

	String generateUrl(int level, int x, int y) {
		// TODO: need to integrate the URL generation code with
		// client-side equivalent
		String url = m_urlFormat.replaceAll("\\{x\\}", String.valueOf(x));
		int layerY = y;
		if (m_layerSet.isZeroTop()) {
			int numYTiles = (int) Math.pow(2.0, level);
			layerY = (numYTiles - y) - 1;
		}
		url = url.replaceAll("\\{y\\}", String.valueOf(layerY));
		url = url.replaceAll("\\{level\\}", String.valueOf(level));
		if (m_baseUrlProvider != null) {
			url = m_baseUrlProvider.getBaseUrl() + url;
		}
		return url;
	}
}
