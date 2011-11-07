package mil.usmc.mapCache;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static final Logger LOGGER = Logger
	.getLogger(MapCacheTileResource.class.getName());
	
	private String m_csBase = "e:/milmapsCache";
	private boolean m_skipRealDownload = Boolean.getBoolean("skip.download");
	
	private static final Logger s_logger = Logger.getLogger(MapCacheTileResource.class.getName());
	///////////////////////////////////////////
	// This is just for now, we will create a config file later
	private String m_data = "bmng";
	private String m_urlPatern =  "{server}/{data}/MapServer/tile/{level}/{y}/{x}";
	private String m_server = "http://services.arcgisonline.com/ArcGIS/rest/services";
	private String m_imageFormat = "png";
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
	@Path("{data}/{epsg}/{imgSize}/{level}/{xTile}/{yTile}")
	@Produces("image/*")
	public Response getTileImage(
			@PathParam("data") String data,
			@PathParam("epsg") int epsg,
			@PathParam("imgSize") int size,
			@PathParam("level") int level,
			@PathParam("xTile") int xTile,
			@PathParam("yTile") int yTile) {
		try {	
			// create the tile given the TMS parameters and the list of unit
			// positions
			
			BufferedImage tileImage = buildMapTile( m_server,m_urlPatern, data, epsg, 
												    size, level, xTile, yTile, m_imageFormat );
			return Response.ok(tileImage, new MediaType("image", m_imageFormat))
					.build();
		} catch (Exception e) {
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	String buildDirPath( String data, int size, int level, int xTile ){
		String csDir = m_csBase + "/" + data + "/" + size + "/" + level + "/" + xTile;
		return csDir;
	}
	
	String buildFilePath( String csPath, int yTile, String ext ){
		String csFile = csPath + "/" + yTile + "." + ext;
		return csFile;
	}

	String buildUrl( String server, String urlPatern, String data,
					 int epsg, int size, int level, int xTile, int yTile ){
		
		Map<String, String> replacements = new HashMap<String, String>();

		replacements.put("server", server );
		replacements.put("data", data);
		replacements.put("level", Integer.toString(level));
		replacements.put("epsg", Integer.toString(epsg));
		replacements.put("width", Integer.toString(size));
		replacements.put("height", Integer.toString(size));
		replacements.put("x", Integer.toString(xTile));
		replacements.put("y", Integer.toString(yTile));
		//replacements.put("bbox", computeBbox(layerSet, levelInUrl));
		//replacements.put("quadkey", computeQuadKey(layerSet, levelInUrl));
		
		String returnStr = urlPatern;
		for (Entry<String, String> e : replacements.entrySet()) {
			returnStr = returnStr.replaceAll("\\{" + e.getKey() + "\\}", e.getValue());
		}		
		return returnStr;
	}
	
	BufferedImage buildMapTile( String server, String urlPatern, String data,
								int epsg, int size, int level, int xTile, int yTile,
								String imageFormat ) throws FileNotFoundException, MalformedURLException, IOException {
		
		BufferedImage img = null;
		String dirPath = buildDirPath(m_data, size, level, xTile );
		String filePath = buildFilePath(dirPath, yTile, imageFormat);
		File file = new File(filePath);
		if ( file.exists() == true ){
			img = (BufferedImage)ImageIO.read(file);
		}
		else{
			String url = buildUrl( server, urlPatern, data, epsg, size, level, xTile, yTile );
			img = getImageFromURL(url,file);
		}
		
		return img;
	}
	
/*
	BufferedImage buildMapTile( String server, String urlPatern, String data,
								int epsg, int size, int level, int xTile, int yTile,
								String imageFormat ) throws FileNotFoundException, MalformedURLException, IOException {
		
		BufferedImage img = null;
		String dirPath = buildDirPath(m_data, size, level, xTile );
		String filePath = buildFilePath(dirPath, yTile, imageFormat);
		File file = new File(filePath);
		if ( file.exists() == true ){
			img = (BufferedImage)ImageIO.read(file);
		}
		else{
			File dir = new File(dirPath);
			if ( dir.exists() == false ){
				if ( dir.mkdirs() == true ){
					// Get Image and write to file
					String url = buildUrl( server, urlPatern, data, epsg, size, level, xTile, yTile );
					img = getImageFromURL(url,file);
				}
			}
			else{
				String url = buildUrl( server, urlPatern, data, epsg, size, level, xTile, yTile );
				img = getImageFromURL(url,file);
			}
		}
		
		return img;
	}
 */
	
	
	BufferedImage getImageFromURL(final String url, final File outFile) 
								 throws FileNotFoundException, MalformedURLException, IOException 
	{
		if (m_skipRealDownload ) {
			System.out.println("skipping: " + url + " " + Thread.currentThread());
			return null;
		}
		System.out.println("getting: " + url + " " + Thread.currentThread());
		
		BufferedImage img = null;
		
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		InputStream is = conn.getInputStream();
		try {
			// TODO: incorporate this
			int errorCode = conn.getResponseCode();
			
			if (errorCode == HttpURLConnection.HTTP_OK) {
				img = ImageIO.read(is);
				writeTileFile(url, img, outFile);
				//if ( errorFile != null )
				//	deleteErrorFile(errorFile);
			}// else if ( errorFile != null ){
			//	writeFailureFile(url, errorCode, errorFile);
			//}
		} finally {
			is.close();
		}
		return img;
	}
	
	private void writeTileFile(String url, BufferedImage bi, File outFile) throws IOException {
		try {
			outFile.createNewFile();
			ImageIO.write(bi, "png", outFile);
		} catch (Throwable t) {
			s_logger.log(Level.WARNING, "Could not process url: "+url+", tile at file: "+outFile.getAbsolutePath(), t);
		} 
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

