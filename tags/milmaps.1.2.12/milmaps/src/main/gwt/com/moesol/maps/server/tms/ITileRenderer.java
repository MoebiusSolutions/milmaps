package com.moesol.maps.server.tms;

import java.awt.Graphics2D;
import java.util.Map;

import com.moesol.gwt.maps.shared.Tile;

/**
 * Implemented by objects that are capable of rendering a tile.
 */
public interface ITileRenderer {
	/**
	 * Render a tile into the given graphics context.
	 * 
	 * @param tile The tile to render.
	 * @param graphics The graphics context to render into.
	 * @param parameters Optional extra parameters.
	 */
	public void render(Tile tile, Graphics2D graphics, Map<String, String[]> parameters);
}
