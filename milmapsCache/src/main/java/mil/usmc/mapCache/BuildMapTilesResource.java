package mil.usmc.mapCache;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mil.usmc.mapCache.Utils.TilePix;
import mil.usmc.mapCache.Utils.TilePosition;

@Path("buildMaps")
public class BuildMapTilesResource {
	private static final Logger LOGGER = Logger
	.getLogger(BuildMapTilesResource.class.getName());
	
	TileInfo m_tileInfo = null;
	String m_srcUrlPat = null;
	String m_server = null;
	String m_data = null;
	String m_servlet = null;
	String m_srs = null;
	double m_factor;
	Projection m_proj = null;
	// next members are used for output
	private String m_outBase = null;//"e:/milmapsCache";
	@POST
	@Path("{http}/{Server}/{servlet}/{dataName}/{dataDeg}/{srs}/{tileSize}/{newLevel}/{outPath}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response buildTiles(	
			@PathParam("http")       String http,
			@PathParam("Server")     String server,
			@PathParam("servlet")    String servlet,
			@PathParam("dataName")   String data,
			@PathParam("dataDeg")    double deg,
			@PathParam("srs")        String srs,
			@PathParam("tileSize")   int size,
			@PathParam("newLevel")   int newLevel,
			@PathParam("outPath")   String outPath) {
		try {	

			m_server = http +"://" + server + "/" + servlet;
			m_servlet = servlet;
			m_srcUrlPat = Utils.getUrlPattern(Utils.WW_TILE_SERVER);
			m_data = data;
			m_outBase = outPath +"/" + servlet +"/" + data;
			m_srs = srs;
			m_proj = new Projection(deg,size);
			String csRtn = buildMapTiles(deg,size,newLevel);
			
			return Response.ok(csRtn,MediaType.TEXT_PLAIN).build();
		} catch (Exception e) {
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	String buildMapTiles(double deg, int size, int newLevel){
		String csStatus = "Process Successfull";
		Boolean bSuccess = true;
		try {
			m_tileInfo = Utils.compTileInfo(deg, size);
			double desDeg = 180.0/(1<<newLevel);
			int numNewXs = (int)(360.0/desDeg);
			int numNewYs = (int)(180.0/desDeg);
			// Compute the scale for the new tiles based on 180 degs for 512 pixels.
			double dScale = Utils.computeScale(96, 180, size, 0);
			// Compute the level of the src tiles for this scale
			int srcLevel = Math.max(0,Utils.computeLevel(96, size, deg, dScale));
			double factor = Utils.computeScaleFactor(deg,desDeg);
			m_proj.setScale(factor*m_proj.getScale());
			for(int i = 0; i < numNewYs; i++){
				for(int j = 0; j < numNewXs; j++){
					bSuccess = buildMapTile(factor,srcLevel,newLevel,i,j,"png");
				}
			}
			if(!bSuccess){
				csStatus = "Process Failed";
			}
		} catch (Exception e) {
			csStatus = "Process Failed";
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
		return csStatus;
	}
	
	protected boolean buildMapTile( 
			 double factor, int srcLevel, int newLevel, 
			 int diRow, int djCol, String imgFormat ) throws FileNotFoundException, MalformedURLException, IOException {

		String dirPath = Utils.buildDirPath(Utils.WW_TILE_SERVER, m_outBase, 
											m_data, 0, newLevel, diRow );
		String filePath = Utils.buildFilePath(Utils.WW_TILE_SERVER, dirPath, 
											  djCol, diRow, imgFormat);
		
		double desDeg = 180.0/(1<<newLevel);
		int orgX = m_proj.lngToPixX(-180.0 + djCol*desDeg);
		int orgY = m_proj.latToPixY(-90.0 + (diRow+1)*desDeg);
		int srcSize = m_tileInfo.size;
		File dir = new File(dirPath);
		boolean bGoodDir = true;
		if (dir.exists() == false){
			bGoodDir = dir.mkdirs();
		}
		boolean bOk = true;
		if ( bGoodDir ){
			// Get Image and write to file
			BufferedImage newImage = newImage(srcSize,BufferedImage.TYPE_INT_ARGB);
			// we use a tile with offset (0,0) because we are laying out
			// the tiles relative to a single tile and will use it to test intersection.
			TilePix destTile = new TilePix(0,0);
			Graphics2D gr = newImage.createGraphics(); 
			int fSrcSize = (int)(srcSize*factor + 0.5);
			for(int i = 0; i < m_tileInfo.numYtiles; i++){
				double botLat = i*m_tileInfo.degHeight + -90.0;
				double topLat = botLat + m_tileInfo.degHeight;
				for(int j = 0; j < m_tileInfo.numXtiles; j++){
					double leftLng = j*m_tileInfo.degWidth + -180.0;
					double rightLng = leftLng + m_tileInfo.degWidth;
					TilePosition tp = Utils.tilePosition(orgX, orgY, topLat, leftLng, 
													  	 botLat, rightLng, m_proj);
					if(Utils.tilesIntersect(tp.tl, fSrcSize, destTile, srcSize)){
						String url = Utils.buildUrl( m_servlet, m_server, 
													 m_srcUrlPat, m_data, 
							 					     m_srs, srcSize, srcLevel, j, i );
						BufferedImage img = Utils.getImageFromURL(url);
						if(img != null){
							gr.drawImage(img, tp.tl.x, tp.tl.y, tp.width, tp.height,null);
						}
						else{
							bOk = false;
						}
					}
				}
			}
			gr.dispose(); 
			if(bOk){// Write file
				File outFile = new File(filePath);
				outFile.createNewFile();
				ImageIO.write(newImage, imgFormat, outFile);
			}
		}
		else{
			bOk = false;
		}
		return bOk;
	}

    
	protected BufferedImage newImage(int size, int type){
		return new BufferedImage(size, size, type);
	}
	
	public static void main(String[] args) {
		// used this main to test my certkey.
		String server = "https://otm.moesol.com";
		String servlet = "/ww-tile-server";
		String atPath = "/tileset";
		String dataName = "/BlueMarbleNG_200412";
		String request = "/level/" +"0" + "/x/" + "0" + "/y/" + "0";
		String url = server + servlet + atPath + dataName + request;
		try{
			BufferedImage img = Utils.getImageFromURL(url);
			String dirPath = "E:/TileServerTest";
			String filePath = dirPath + "/wwImage.png";
			File dir = new File(dirPath);
			boolean bGoodDir = true;
			if (dir.exists() == false){
				bGoodDir = dir.mkdirs();
			}
			if(bGoodDir){
				File outFile = new File(filePath);
				outFile.createNewFile();
				ImageIO.write(img, "png", outFile);
				System.out.println("Wrote image tp " + filePath);
			}
		} catch (Exception e) {
			System.out.println("This sucks");
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
    }
}
