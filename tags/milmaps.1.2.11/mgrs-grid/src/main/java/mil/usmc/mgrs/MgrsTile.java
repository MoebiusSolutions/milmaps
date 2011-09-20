package mil.usmc.mgrs;

import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

import mil.usmc.mgrs.milGrid.PixBoundingBox;
import mil.usmc.mgrs.milGrid.UtmG;


public class MgrsTile {
	private IProjection m_proj = null;
	private int m_width = 512;
	private int m_height = 512;
	private int m_level = -1;
	private int m_tileX = -1;
	private int m_tileY = -1;
	private BufferedImage m_img = null;
	private Graphics2D m_g;
	//private boolean useWholeWorldBBox = true;
	protected Color m_bkgrColor = null;
	PixBoundingBox  m_box = new PixBoundingBox();

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
	public MgrsTile( Color c, int epsg, int width, int height, int level, int tileX, int tileY ) {
		m_bkgrColor = c;
		if ( isMercator(epsg) ){
			m_proj = new MercProj();
			m_proj.initialize(width);
		}
		else{
			m_proj = new CedProj();
			m_proj.initialize(width);
		}
		m_width = width;
		m_height = height;
		m_level = level;
		m_tileX = tileX;
		m_tileY = tileY;
		m_box.set(m_proj, width, height, tileX, tileY);
		createTileImage();
	}
	// Mercator 3857
	protected boolean isMercator( int espg ){
		boolean bMercProj = true;
		switch ( espg ){
			case 2163:
			case 4326:
				bMercProj = false;
			break;
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

	PixBoundingBox getBoundingBox(){
		return m_box;
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
	
	


	void drawGrid() {
		UtmG utm = new UtmG();
		utm.drawGrid( m_g, m_proj, m_level, m_box);
	}
}
