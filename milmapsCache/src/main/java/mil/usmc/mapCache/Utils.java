package mil.usmc.mapCache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
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

public class Utils {
	public static String MAP_CACHE = "mapcache";
	public static String WW_TILE_SERVER = "ww-tile_server";
	public static class Tile {
		public int iRow;
		public int jCol;
		@Override
		public String toString() {
			return "[Row = " + iRow + ", col = " + jCol + "]";
		}
	}
	public static class TilePix {
		public int x;
		public int y;
		
		public TilePix(int x, int y){
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString() {
			return "[x = " + x + ", y = " + y + "]";
		}
	}
	
	public static class TilePosition {
		TilePix tl;
		public int width;
		public int height;
		
		@Override
		public String toString() {
			return "[tlX = " + tl.x + ", tlY = " + tl.y + ", width = " + width + ", height = " + height+"]";
		}
	}
	private static final Logger LOGGER = Logger
	.getLogger(MapCacheTileResource.class.getName());
	
	private static final Logger s_logger = Logger.getLogger(Utils.class.getName());
	public static double EarthCirMeters  = 2.0*Math.PI*6378137;
	public static double MeterPerDeg  = EarthCirMeters/360.0;
	
	//private static String intToString(int num, int numDigits){
	//	String format = String.format("%%0%dd", numDigits);
	//	return String.format(format, num);
	//}
	
	public static String buildDirPath( String servlet, String base, String data, int size, int level, int xTile){
		String csDir = null;
		if (servlet.matches(Utils.WW_TILE_SERVER)){
			csDir = base + "/" + level + "/" + xTile;
		}
		else if ( servlet.matches(Utils.MAP_CACHE)){
			csDir = base + "/" + data + "/" + size + "/" + level + "/" + xTile;
		}
		
		return csDir;
	}
	
	public static String buildFilePath( String servlet, String csPath, int xTile, int yTile, String ext ){
		String csFile = null;
		if (servlet.matches(Utils.WW_TILE_SERVER)){
			csFile = csPath + "/" + yTile +"_" + xTile + "." + ext;
		}
		else if ( servlet.matches(Utils.MAP_CACHE)){
			csFile = csPath + "/" + yTile + "." + ext;
		}
		
		return csFile;
	}
	
	public static BufferedImage getImageFromURL(final String url) 
			  throws FileNotFoundException, MalformedURLException, IOException 
	{
		System.out.println("getting: " + url + " " + Thread.currentThread());

		BufferedImage img = null;

		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		InputStream is = conn.getInputStream();
		try {
			// TODO: incorporate this
			int errorCode = conn.getResponseCode();

			if (errorCode == HttpURLConnection.HTTP_OK){
				img = ImageIO.read(is);
			}
		}finally{
			is.close();
		}
		return img;
	}	
	
	public static BufferedImage getAndSaveImageFromURL(
		final String url, 
		final File outFile, 
		final boolean skipRealDownload
	) throws FileNotFoundException, MalformedURLException, IOException 
	{
		if (skipRealDownload ) {
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

	public static void writeTileFile(String url, BufferedImage bi, File outFile) throws IOException {
		try {
			outFile.createNewFile();
			ImageIO.write(bi, "png", outFile);
		} catch (Throwable t) {
			s_logger.log(Level.WARNING, "Could not process url: " + url + ", tile at file: "+outFile.getAbsolutePath(), t);
		} 
	}
	
	public static String getUrlPattern(String servlet){
		if (servlet.matches(Utils.WW_TILE_SERVER)){
			return "{server}/tileset/{data}/level/{level}/x/{x}/y/{y}";
		}
		else if ( servlet.matches(Utils.MAP_CACHE)){
			return "{server}/{data}/MapServer/tile/{level}/{y}/{x}";
		}
		return null;
	}
	
	public static Map<String, String> buildHashMap( String servlet, String server, 
													String urlPatern, String data,
													String srs, int size, int level, 
													int xTile, int yTile ){

		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put("server", server );
		replacements.put("data", data);
		if ( servlet.matches("milmapsCache")){
			replacements.put("srs", srs);
			replacements.put("width", Integer.toString(size));
			replacements.put("height", Integer.toString(size));
			replacements.put("level", Integer.toString(level));
			replacements.put("x", Integer.toString(xTile));
			replacements.put("y", Integer.toString(yTile));
		}
		replacements.put("level", Integer.toString(level));
		replacements.put("x", Integer.toString(xTile));
		replacements.put("y", Integer.toString(yTile));
		return replacements;
	}
	
	public static String buildUrl( String servlet, String server, 
								   String urlPatern, String data,
								   String srs, int size, int level, 
								   int xTile, int yTile ){

		Map<String, String> replacements = null;
		replacements = Utils.buildHashMap(servlet, server, urlPatern, data, 
										   srs, size, level, xTile, yTile);

		String returnStr = urlPatern;
		for (Entry<String, String> e : replacements.entrySet()) {
			returnStr = returnStr.replaceAll("\\{" + e.getKey() + "\\}", e.getValue());
		}		
		return returnStr;
	}
	
	public static boolean isMercator( String srs ){
		boolean bMercProj = true;
		if ( srs.equals("EPSG:4326") || srs.equals("EPSG:2136")){
			bMercProj = false;
		}
		return bMercProj;
	}
	
	public static double computeScaleFactor(double srcDeg, double desDeg){
		// When the src deg is smaller than the desDeg, that means
		// we need to shrink the src tiles to fit so we need to do the
		// following division.
		return srcDeg/desDeg;
	}
	
	public static double computeScale(int dpi, double degW, int size, int level){
		double mpp = 2.54 / (dpi * 100);
		double dx = size;
		double l_mpp = degW* (MeterPerDeg / dx);
		// we want to return ( (mpp*2^n)/(l_mpp) );
		return ((mpp * Math.pow(2, level)) / l_mpp);		
	}
	
	public static int computeLevel(int dpi, int size, double degW, double dScale){
		double mpp = 2.54 / (dpi * 100); // meters per pixel for physical screen
		double m_dx = size;
		double l_mpp = degW* (MeterPerDeg / m_dx);
		double logMess = Math.log(dScale) + Math.log(l_mpp)- Math.log(mpp);
		double dN = logMess / Math.log(2);
		return (int)(Math.rint(dN));
	}
	
	public static TileInfo compTileInfo(double deg, int size){
		int numXtiles = (int)(360.0/deg + 0.5);
		int numYtiles = (int)(180.0/deg + 0.5);
		TileInfo info = new TileInfo(true,numXtiles,numYtiles,size,deg,deg);
		return info;
	}
	
	/**
	 * Translates the coordinate System.
	 * @param srcX
	 * @param srcY
	 * @param orgX
	 * @param orgY
	 * @return
	 */
	public static TilePix translatePt(int srcX,  int srcY, int orgX, int orgY){ 
		// Translate points
		return new TilePix(srcX-orgX,orgY-srcY);
	}
	
	/**
	 * tilePosition computes a tiles top-left pixel and its width and height
	 * relative to an origin in the same coordinate space.
	 * @param origX
	 * @param origY
	 * @param topLat
	 * @param leftLng
	 * @param botLat
	 * @param rightLng
	 * @param proj
	 * @return
	 */
	
	public static TilePosition tilePosition(int origX, int origY,
										double topLat, double leftLng,
										double botLat, double rightLng,
										Projection proj){
		R2 tl = proj.LatLngToPix(topLat, leftLng);
		R2 br = proj.LatLngToPix(botLat, rightLng);
		TilePosition tp = new TilePosition();
		tp.width  = Math.abs(br.m_x - tl.m_x);
		tp.height = Math.abs(tl.m_y - br.m_y);
		tp.tl = Utils.translatePt(tl.m_x, tl.m_y, origX, origY);
		return tp;
	}

	/**
	 * srcTopLeftToTilePix converts the top-left corner of a src tile to
	 * a pixel location relative to the destination tile of a different scale.
	 * This routine assumes tiles start at the bottom of map. Furthermore the 
	 * origin of the destination tile is (0,0) in the top left corner of the tile.
	 * @param iRow: Row of source tile
	 * @param jCol: Col of source tile
	 * @param size of square tiles in pixels
	 * @param diRow: Row of destination tile
	 * @param djCol  Col of destination tile
	 * @param factor: the scale factor between the src and destination tile
	 * @return : TilePix class
	 */
	public static TilePix srcTopLeftToTilePix(int iRow,  int jCol, int size, 
											  int diRow, int djCol, double factor){
		// Find pixel location in new destination coordinate space
		double fSize = size*factor;
		int srcTopPix  = (int)((iRow+1)*fSize+0.5);
		int srcLeftPix = (int)(jCol*fSize+0.5);
		// Find destination tile top-left pixel in destination space
		int desTopPix  = (int)((diRow+1)*size);
		int desLeftPix = (int)(djCol*size);
		// Translate points
		return new TilePix(srcLeftPix-desLeftPix,desTopPix-srcTopPix);
	}
	
	public static boolean tilesIntersect(TilePix p, int pSize, TilePix q, int qSize){
		if((q.x + qSize) <= p.x || (q.x+qSize) <= p.x){
			return false;
		}
		if((p.y+pSize) <= q.y || (q.y+qSize) <= p.y){
			return false;
		}
			return true;
	}	
	
}
