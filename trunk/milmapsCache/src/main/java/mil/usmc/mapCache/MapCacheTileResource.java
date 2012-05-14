package mil.usmc.mapCache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("mapcache")
public class MapCacheTileResource {
	
	private String m_csBase = "e:/milmapsCache";
	private String m_data = "bmng";
	
	private boolean m_skipRealDownload = Boolean.getBoolean("skip.download");
	private String m_urlPatern =  Utils.getUrlPattern(Utils.MAP_CACHE);
	private String m_server = "http://services.arcgisonline.com/ArcGIS/rest/services";
	private String m_imageFormat = "png";
	private Split m_split = new Split();
	private Split.ImgInfo m_inf = m_split.createInfObj();
	
	public MapCacheTileResource(){
		m_split.setBase(m_csBase);
	}
	
	/**
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
	@Path("{data}/{srs}/{imgSize}/{level}/{xTile}/{yTile}")
	@Produces("image/*")
	public Response getTileImage(
			@PathParam("data") String data,
			@PathParam("srs") String srs,
			@PathParam("imgSize") int size,
			@PathParam("level") int level,
			@PathParam("xTile") int xTile,
			@PathParam("yTile") int yTile) {
		try {	
			// create the tile given the TMS parameters and the list of unit
			// positions
			
			BufferedImage tileImage = buildMapTile( m_server,m_urlPatern, data, srs, 
												    size, level, xTile, yTile, m_imageFormat );

			return Response.ok(tileImage, new MediaType("image", m_imageFormat))
					.build();
		} catch (Exception e) {
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void splitFile(int level, int x, int y, BufferedImage img ) throws IOException{
		m_inf.level = level;
		m_inf.x = x;
		m_inf.y = y;
		m_split.splitImage(m_inf, img);		
	}

	BufferedImage buildMapTile( String server, String urlPatern, String data,
								String srs, int size, int level, int xTile, int yTile,
								String imageFormat ) throws FileNotFoundException, MalformedURLException, IOException {
		
		BufferedImage img = null;
		String dirPath = Utils.buildDirPath(Utils.MAP_CACHE,m_csBase, 
										    m_data, size, level, xTile );
		String filePath = Utils.buildFilePath(Utils.MAP_CACHE,dirPath,0, yTile, imageFormat);
		File file = new File(filePath);
		if ( file.exists() == true ){
			img = (BufferedImage)ImageIO.read(file);
			if ( size == 512 ){
				splitFile(level, xTile, yTile, img );
			}
		}
		else if ( size == 512 ){
			File dir = new File(dirPath);
			boolean bGoodDir = true;
			if ( dir.exists() == false ){
				bGoodDir = dir.mkdirs();
			}
			if ( bGoodDir ){
				// Get Image and write to file
				String url = Utils.buildUrl( "mapcache", server, urlPatern, data, 
											 srs, size, level, xTile, yTile );
				img = Utils.getAndSaveImageFromURL(url,file,m_skipRealDownload);
				splitFile(level, xTile, yTile, img );
			}
		}
		/* we will add this when I can lock the files.
		else{ // 256
			// we need to convert the 256 info to 512 info
			int x = xTile/2;
			int y = yTile/2;
			int s = 512;
			String url = uTILS.buildUrl( "mapcache", server, urlPatern, data, epsg, s, level, x, y );
			img = getImageFromURL(url,file);
			if ( img != null ){
				BufferedImage img2 = null;
				splitFile(level, x, y, img );
				if ( file.exists() == true ){
					img2 = (BufferedImage)ImageIO.read(file);
				}
				return img2;
			}
		}
		*/
		return img;
	}
	
	/*
	private void writeSuccessFile(String url, InputStream is, File outFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(outFile);
		try {
			int c = 0;
			while ((c = is.read()) != -1) {
				fos.write(c);
			}
			fos.flush();
		} catch (Throwable t) {
			s_logger.log(Level.WARNING, "Could not process url: "+url+", tile at file: "+outFile.getAbsolutePath(), t);
		} finally {
			fos.close();
		}
	}

	private void writeFailureFile(String url, int errorCode, File errorFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(errorFile);
		String errorCodeStr = Integer.toString(errorCode);
		
		try {
			fos.write(errorCodeStr.getBytes());
			fos.flush();
			s_logger.log(Level.WARNING, "Could not process url: "+url+", tile at file: "+errorFile.getAbsolutePath());
		} catch (Throwable t) {
			s_logger.log(Level.WARNING, "Could not process url: "+url+", tile at file: "+errorFile.getAbsolutePath(), t);
		} finally {
			fos.close();
		}
	}
	
	private void deleteErrorFile(File errorFile) {
		if (errorFile.exists()) {
			errorFile.delete();
		}
	}
	*/
}

