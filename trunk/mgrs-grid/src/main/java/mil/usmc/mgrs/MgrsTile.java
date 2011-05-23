package mil.usmc.mgrs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.util.logging.Level;
import java.util.logging.Logger;


import mil.usmc.mgrs.objects.BoundingBox;
import mil.usmc.mgrs.objects.Grid;
import mil.usmc.mgrs.objects.Line;


public class MgrsTile {
	private static final Logger LOGGER = Logger.getLogger(MgrsXmlResource.class.getName());
	private static final double START_DLNG = 180.0;
	private static final double START_DLAT = 180.0;
	private static final double TILE_DX = 512;
	private static final double TILE_DY = 512;

	private int m_level = -1;
	private int m_x = -1;
	private int m_y = -1;
	private double m_lng;
	private double m_lat;
	private double m_dlng;
	private double m_dlat;
	private BufferedImage m_img = null;
	private Graphics2D m_g;
	private BoundingBox m_bbox = null;
	private boolean useWholeWorldBBox = true;
	private boolean m_bshowLabels = false;
	private String m_gridName = MgrsXmlResource.ZONE_GRID;
	MgrsXmlResource m_mgrs = new MgrsXmlResource();
	Color m_bkgrColor = null;
	Point m_pt = new Point();
	Point m_p = new Point();
	Point m_q = new Point();
	mil.usmc.mgrs.objects.Point m_latStart;
	mil.usmc.mgrs.objects.Point m_latEnd;
	mil.usmc.mgrs.objects.Point m_lngStart;
	mil.usmc.mgrs.objects.Point m_lngEnd;

	private final AffineTransform m_tx = new AffineTransform();

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
	public MgrsTile( Color c, int level, int x, int y) {
		m_bkgrColor = c;
		m_level = level;
		m_x = x;
		m_y = y;
		m_tx.translate(0, TILE_DY);
		m_tx.scale(1, -1);
		updateState();
		createTileImage();
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
		updateState();
	}

	BoundingBox getBoundingBox(){
		return m_bbox;
	}
	public BufferedImage getImage() {
		return m_img;
	}

	public int getX() {
		return m_x;
	}

	public void setX(int x) {
		m_x = x;
		updateState();
	}

	public int getY() {
		return m_y;
	}

	public void setY(int y) {
		m_y = y;
		updateState();
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

	private void positToTile(double lat, double lng, Point p) {

		m_pt.x = (int)((lng - m_lng) * TILE_DX / m_dlng);
		m_pt.y = (int)((lat - m_lat) * TILE_DY / m_dlat);
		m_tx.transform(m_pt,p);
		return;
	}
	
	private double padLat( double lat, double pad){
		return Math.max(-90.0,Math.min(lat+pad,90.0));
	}
	
	private double wrapLng(double lng) {
		if (lng > 180.0) {
			while (lng > 180.0)
				lng -= 360.0;
		} else if (lng < -180.0) {
			while (lng < -180.0)
				lng += 360.0;
		}
		return lng;
	}
	
	private double padLng( double lng, double pad){
		return wrapLng( lng + pad );
	}
	
	
	private void updateBoundingBox(){
		if ( m_level < 2 ){
			m_bshowLabels = false;
			m_bbox = new BoundingBox(-90, -180, 90, 180);
			m_gridName = MgrsXmlResource.ZONE_GRID;
		}
		else if (m_level < 12 ){
			m_bshowLabels = true;
			m_bbox = new BoundingBox(-90, -180, 90, 180);
			m_gridName = MgrsXmlResource.ZONE_GRID;
			
			//m_bbox = new BoundingBox( padLat(m_lat, -10), padLng(m_lng, -10),
			//						  padLat(m_lat + m_dlat,10), padLng(m_lng + m_dlng, 10));
			//m_gridName = MgrsXmlResource.HUNDRED_KM_GRID;
		}
		else {
			m_bshowLabels = true;
			m_bbox = new BoundingBox(-90, -180, 90, 180);
			m_gridName = MgrsXmlResource.ZONE_GRID;
			//m_bbox = new BoundingBox( padLat(m_lat, -10), padLng(m_lng, -10),
			//		  				  padLat(m_lat + m_dlat,10), padLng(m_lng + m_dlng, 10));
			//m_gridName = MgrsXmlResource.TEN_KM_GRID;			
		}
		m_mgrs.setBoundingBox(m_bbox);
	}

	private void updateState() {
		m_dlng = START_DLAT / Math.pow(2.0, m_level);
		m_dlat = START_DLNG / Math.pow(2.0, m_level);
		m_lng = -180.0 + m_x * m_dlng;
		m_lat = -90.0 + m_y * m_dlat;
		updateBoundingBox();
	}

	private void createTileImage() {
		// Create a buffered image that supports transparency
		int width = (int) (TILE_DX);
		int height = (int) (TILE_DY);
		m_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		m_g = m_img.createGraphics();
		if( m_bkgrColor.getAlpha()> 0 ){
			m_g.setColor(m_bkgrColor);
			m_g.fillRect(0, 0, width, height);
		}
		m_g.setColor(Color.YELLOW);
	}
	
	private boolean inThisTile(double lat, double lng) {
		if (lng < m_lng) {
			return false;
		}
		if (lng >= m_lng + m_dlng) {
			return false;
		}
		if (lat < m_lat) {
			return false;
		}
		if (lat >= m_lat + m_dlat) {
			return false;
		}
		return true;
	}
	
	private void drawLine(mil.usmc.mgrs.objects.Point p,
						  mil.usmc.mgrs.objects.Point q){
		positToTile(p.getLat(), p.getLng(),m_p);;
		positToTile(q.getLat(), q.getLng(), m_q);
		m_g.drawLine(m_p.x, m_p.y, m_q.x, m_q.y);
	}
	
	private void drawLabel( mil.usmc.mgrs.objects.Point p, String label ){
		positToTile( p.getLat(), p.getLng(), m_p);
		int w = (int)(TILE_DX);
		int h = (int)(TILE_DY);
		if ( 0 <= m_p.x && m_p.x <= w  ){
			if ( 0 <= m_p.y && m_p.y <= h ){
				m_g.drawString(label, m_p.x, m_p.y);
			}
		}
	}
	
    private void drawGrid(String gridName){

        Grid grid = null;
        grid = m_mgrs.makeMgrsGrid(gridName);

        if (grid == null) {
            LOGGER.log(Level.SEVERE, "{0} is null", gridName);
            return;
        }
        for (Line latLine : grid.getLatLines()) {
            m_latStart = latLine.getStart();
            m_latEnd   = latLine.getEnd();
            drawLine(m_latStart, m_latEnd);
            if ( m_bshowLabels ){
	            if (latLine.getLabelPoint() != null && latLine.getLabelName() != null ) {;
	                drawLabel(latLine.getLabelPoint(),latLine.getLabelName());
	            }
            }
        }
        for (Line lngLine : grid.getLngLines()) {
            if (lngLine == null || m_mgrs.skipNorwayLines(lngLine)) {
                continue;
            }

            m_lngStart = lngLine.getStart();
            m_lngEnd   = lngLine.getEnd();
            drawLine(m_lngStart, m_lngEnd);
            if ( m_bshowLabels ){
	            if (lngLine.getLabelPoint() != null && lngLine.getLabelName() != null ) {;
	                drawLabel(lngLine.getLabelPoint(),lngLine.getLabelName());
	            }
            }
        }
    }

	void drawGrid() {
		drawGrid(m_gridName);
	}
}
