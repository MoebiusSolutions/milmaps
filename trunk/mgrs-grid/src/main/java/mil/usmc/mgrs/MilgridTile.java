package mil.usmc.mgrs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import mil.usmc.mgrs.milGrid.PixBoundingBox;
import mil.usmc.mgrs.milGrid.UtmG;


public class MilgridTile {
	//private static final Logger LOGGER = Logger.getLogger(MgrsXmlResource.class.getName());
	//private static final double TILE_DX = 512;
	//private static final double TILE_DY = 512;
	private IProjection m_proj = null;
	//private MercProj m_mercProj = new MercProj();
	//private CedProj m_cedProj = new CedProj();
	
	private boolean m_bUseMerc = false;
	private int m_width = 512;
	private int m_height = 512;
	private int m_level = -1;
	private int m_tileX = -1;
	private int m_tileY = -1;
	private double m_lng;
	private double m_lat;
	private double m_dlng;
	private double m_dlat;
	private WmsBoundingBox m_bbox;
	private BufferedImage m_img = null;
	private Graphics2D m_g;
	protected Color m_bkgrColor = null;

	//private final AffineTransform m_tx = new AffineTransform();

	/**
	 * Constructor
	 * 
	 * @param zoomLevel
	 *            The zoom level of the requested tile.
	 * @param xTileCoord
	 *            The x coordinate of the requested tile in TMS tile space
	 * @param yTileCoord
	 *            The y coordinate of the requested tile in TMS tile space
	 */
	public MilgridTile( Color c, String srs, int width, int height, int level, int tileX, int tileY ) {
		m_bkgrColor = c;
		m_bUseMerc = isMercator(srs);
		if ( m_bUseMerc ){
			m_proj = new MercProj();
		}
		else{
			m_proj = new CedProj();
		}
		m_proj.init(width, level, tileX, 180);
		m_width = width;
		m_height = height;
		m_level = level;
		m_tileX = tileX;
		m_tileY = tileY;
		createTileImage();
	}
	
	/**
	 * Constructor
	 * 
	 * @param zoomLevel
	 *            The zoom level of the requested tile.
	 * @param xTileCoord
	 *            The x coordinate of the requested tile in TMS tile space
	 * @param yTileCoord
	 *            The y coordinate of the requested tile in TMS tile space
	 */
	public MilgridTile( Color c, String srs, int width, int height, WmsBoundingBox bbox ) {
		m_bkgrColor = c;
		m_bUseMerc = isMercator(srs);
		if ( m_bUseMerc ){
			m_proj = new MercProj();
		}
		else{
			m_proj = new CedProj();
		}
		m_width = width;
		m_height = height;
		m_bbox = bbox;
		m_proj.initUsingBbox(m_width, m_height, bbox);
		createBboxTileImage();
	}
	
	protected boolean isMercator( String srs ){
		boolean bMercProj = true;
		if ( srs.equals("EPSG:4326") || srs.equals("EPSG:2136")){
			bMercProj = false;
		}
		return bMercProj;
	}

	/**
	 * Returns the level for the tile
	 * 
	 * @param zoomLevel
	 */
	public int getLevel() {
		return m_level;
	}

	public void setLevel(int level) {
		m_level = level;
	}


	public BufferedImage getImage() {
		return m_img;
	}

	public int getX() {
		return m_tileX;
	}

	public void setX(int x) {
		m_tileX = x;
	}

	public int getY() {
		return m_tileY;
	}

	public void setY(int y) {
		m_tileY = y;
	}

	public double getLng() {
		return m_lng;
	}

	public double getLat() {
		return m_lat;
	}

	public double getDlng() {
		return m_dlng;
	}

	public double getDlat() {
		return m_dlat;
	}
	

	private void createTileImage() {
		// Create a buffered image that supports transparency
		m_img = new BufferedImage(m_width, m_height, BufferedImage.TYPE_INT_ARGB);
		m_g = m_img.createGraphics();
		
		if( m_bkgrColor.getAlpha()> 0 ){
			m_g.setColor(m_bkgrColor);
			m_g.fillRect(0, 0, m_width, m_height);
		}
		m_g.setColor(Color.YELLOW);
	}
	
	private void createBboxTileImage() {
		// Create a buffered image that supports transparency
		m_img = new BufferedImage(m_width, m_height, BufferedImage.TYPE_INT_ARGB);
		m_g = m_img.createGraphics();
		
		if( m_bkgrColor.getAlpha()> 0 ){
			m_g.setColor(m_bkgrColor);
			m_g.fillRect(0, 0, m_width, m_height);
		}
		m_g.setColor(Color.YELLOW);
	}
	
	void drawBboxGrid() {
		UtmG utm = new UtmG();
		PixBoundingBox box = PixBoundingBox.create( m_proj, m_width, m_height, m_bbox);
		utm.drawGrid(m_g, m_proj,box);
	}


	void drawGrid() {
		UtmG utm = new UtmG();
		PixBoundingBox box = PixBoundingBox.create( m_proj, m_width, m_height, m_tileX, m_tileY );
		utm.drawGrid(m_g, m_proj,box);
	}
}
