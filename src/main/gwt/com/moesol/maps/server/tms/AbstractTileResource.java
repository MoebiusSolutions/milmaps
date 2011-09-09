package com.moesol.maps.server.tms;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.moesol.gwt.maps.shared.Tile;

/**
 * Extended by REST resource classes capable of fulfilling requests for
 * tile images.
 */
public abstract class AbstractTileResource {
	private static final Logger LOGGER = Logger
			.getLogger(AbstractTileResource.class.getName());
	private static final int DEFAULT_IMAGE_HEIGHT = 512;
	private static final int DEFAULT_IMAGE_WIDTH = 512;

	@Context
	HttpServletRequest request;

	/**
	 * Implements TMS-style tile retrieval. See the
	 * {@link <a href="http://wiki.osgeo.org/wiki/Tile_Map_Service_Specification">TMS Specification</a>}
	 * 
	 * @param zoomLevel
	 *            The zoom level of the requested tile.
	 * @param xTileCoord
	 *            The x coordinate of the requested tile in TMS tile space
	 * @param yTileCoord
	 *            The y coordinate of the requested tile in TMS tile space
	 * @param imageFormat
	 *            The format of the returned image tile (for example "png",
	 *            "jpeg", etc.)
	 * @return A Response object containing the tile image.
	 */
	@GET
	@Path("{zoomLevel}/{xTileCoord}/{yTileCoord}.{imageFormat}")
	@Produces("image/*")
	public Response getTileImage(@PathParam("zoomLevel") int zoomLevel,
			@PathParam("xTileCoord") int xTileCoord,
			@PathParam("yTileCoord") int yTileCoord,
			@PathParam("imageFormat") String imageFormat)
			throws WebApplicationException {
		try {
			Image tileImage = createTileImage(zoomLevel, xTileCoord, yTileCoord);
			return Response.ok(tileImage, new MediaType("image", imageFormat))
					.build();
		} catch (Throwable t) {
			LOGGER.log(
					Level.SEVERE,
					String.format(
							"Unexpected exception caught in getTileImage(%1$s %2$s %3$s %4$s)",
							zoomLevel, xTileCoord, yTileCoord, imageFormat), t);
			throw new WebApplicationException(t, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Subclasses must override to implement the logic to get the requested tile
	 * image.
	 * 
	 * @param zoomLevel
	 *            The zoom level of the requested tile.
	 * @param xTileCoord
	 *            The x coordinate of the requested tile in TMS tile space
	 * @param yTileCoord
	 *            The y coordinate of the requested tile in TMS tile space
	 * @return The format of the returned image tile (for example "png", "jpeg",
	 *         etc.)
	 */
	protected Image createTileImage(int zoomLevel, int xTileCoord,
			int yTileCoord) {
		int imageHeight = DEFAULT_IMAGE_HEIGHT;
		int imageWidth = DEFAULT_IMAGE_WIDTH;
		return createTileImage(zoomLevel, xTileCoord, yTileCoord, imageWidth,
				imageHeight);
	}

	@SuppressWarnings("unchecked")
	protected Image createTileImage(int zoomLevel, int xTileCoord,
			int yTileCoord, int imageWidth, int imageHeight) {
		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_ARGB);

		try {
			Tile tile = createTile(zoomLevel, xTileCoord, yTileCoord,
					imageWidth, imageHeight);

			Graphics2D graphics = image.createGraphics();
			getRenderer().render(tile, graphics, request.getParameterMap());
		} catch (Throwable t) {
			// if all else fails just return a blank image to avoid broken tiles
			LOGGER.log(Level.SEVERE, "Failed rendering image, just returning a blank image...", t);
		}

		return image;
	}

	protected Tile createTile(int zoomLevel, int xTileCoord, int yTileCoord,
			int imageWidth, int imageHeight) {
		Tile.Builder tileBuilder = new Tile.Builder(zoomLevel, xTileCoord,
				yTileCoord);
		Tile tile = tileBuilder.pixelHeight(imageHeight).pixelWidth(imageWidth)
				.build();
		return tile;
	}

	protected Tile createTile(int zoomLevel, int xTileCoord, int yTileCoord) {
		return createTile(zoomLevel, xTileCoord, yTileCoord,
				DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
	}

	protected abstract ITileRenderer getRenderer();
}
