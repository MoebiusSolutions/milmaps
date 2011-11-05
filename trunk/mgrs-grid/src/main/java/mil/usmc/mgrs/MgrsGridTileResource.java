package mil.usmc.mgrs;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("mgrstile")
public class MgrsGridTileResource {
	private static final Logger LOGGER = Logger
	.getLogger(MgrsGridTileResource.class.getName());

	/**
	* @param R 
	* 		Two char hex value between 0 - 256 for red.
	* @param G 
	* 		Two char hex value between 0 - 256 for green.
	* @param B 
	* 		Two char hex value between 0 - 256 for blue.
	* @param A 
	* 		Two char hex value between 0 - 256 for alpha (opacity)
	* @param zoomLevel
	*       The zoom level of the requested tile.
	* @param xTileCoord
	*       The x coordinate of the requested tile in TMS tile space
	* @param yTileCoord
	*       The y coordinate of the requested tile in TMS tile space
	* @param imageFormat
	*       The format of the returned image tile (for example "png",
	*            "jpeg", etc.)
	* @return A Response object containing the tile image.
	*/
	
	@GET
	@Path("{r: \\w{2}}{g: \\w{2}}{b: \\w{2}}{a: \\w{2}}/{epsg}/{width}/{height}/{zoomLevel}/{xTileCoord}/{yTileCoord}.{imageFormat}")
	@Produces("image/*")
	public Response getTileImage(
			@PathParam("r") String R,
			@PathParam("g") String G,
			@PathParam("b") String B,
			@PathParam("a") String A,
			@PathParam("epsg") int epsg,
			@PathParam("width") int width,
			@PathParam("height") int height,
			@PathParam("zoomLevel") int zoomLevel,
			@PathParam("xTileCoord") int xTileCoord,
			@PathParam("yTileCoord") int yTileCoord,
			@PathParam("imageFormat") String imageFormat ) {
		try {	
			// create the tile given the TMS parameters and the list of unit
			// positions
			int r = Integer.parseInt(R, 16);
			int g = Integer.parseInt(G, 16);
			int b = Integer.parseInt(B, 16);
			int a = Integer.parseInt(A, 16);
			
			Color c = new Color(r,g,b,a);
			Image tileImage = createMgrsTile( c, epsg, width, height, zoomLevel, xTileCoord, yTileCoord );
			return Response.ok(tileImage, new MediaType("image", imageFormat)).build();
		} catch (Exception e) {
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	private Image createMgrsTile( Color color, int epsg, int width, int height,
								  int zoomLevel, int xTileCoord, int yTileCoord ) {
		//MgrsTile tile = new MgrsTile( color, epsg, width, height, 
		//							  zoomLevel, xTileCoord, yTileCoord );
		MilgridTile tile = new MilgridTile( color, epsg, width, height, 
		 						  			zoomLevel, xTileCoord, yTileCoord );
		tile.drawGrid();
		return tile.getImage();
	}

}
