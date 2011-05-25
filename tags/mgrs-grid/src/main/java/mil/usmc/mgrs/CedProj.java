package mil.usmc.mgrs;

import mil.usmc.mgrs.objects.Point;
import mil.usmc.mgrs.objects.R2;


/**
 * Handles the conversion of geodetic coordinates to world coordinates. The
 * world is an image of the earth with pixel 0,0 at -180,-90. If the zoom factor
 * goes negative fileTile will return a tile whose width and height have been
 * scaled.
 * 
 * @author Moebius Solutions, Inc.
 */
// Cylindrical Equal Distance projection (CedProj)
public class CedProj {
    private static final double MinLatitude = -90;
    private static final double MaxLatitude = 90;
    private static final double MinLongitude = -180;
    private static final double MaxLongitude = 180;
    
	public static double RadToDeg = 57.29577951;
	public static double DegToRad = 0.017453293;
    
	protected int m_orgTilePixSize = 512;
    
    private Point m_geoPt = new Point();
    private R2 m_pixel = new R2();
    private R2 m_tile = new R2();
    
	public CedProj() {
	}
	
	public void initialize( int tileSize ){	
		m_orgTilePixSize = tileSize;
	}   
	
    private double clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }
	
    public int mapWidthSize(int levelOfDetail)
    {
        return (int) m_orgTilePixSize << levelOfDetail + 1;
    }
    
    public int mapHeightSize(int levelOfDetail)
    {
        return mapWidthSize(levelOfDetail)/2;
    }

	public int lngDegToPixX( int level, double deg ){
		deg = clip(deg, MinLongitude, MaxLongitude);
		double x = (deg + 180)/360;
		return (int)((x * mapWidthSize(level))+0.5);
	}
	

	public double xPixToDegLng( int level, double pix  )
	{
        double mapSize = mapWidthSize(level);
        double x = (pix / mapSize) - 0.5;   
        return (x*360);             
	} 
	

	public int latDegToPixY( int level, double deg ){
		deg = clip(deg, MinLatitude, MaxLatitude);
		double y = (deg + 90)/180;
		return (int)((y * mapHeightSize(level))+0.5);
	}
	

	public double yPixToDegLat( int level, double pix  )
	{
        double mapSize = mapHeightSize(level);
        double x = (pix / mapSize) - 0.5;   
        return (x*180);             
	} 
	
	

    public R2 latLngToPixelXY( int levelOfDetail, double lat, double lng )
    {
        m_pixel.m_x = (int) lngDegToPixX(levelOfDetail,lng);
        m_pixel.m_y = (int) latDegToPixY(levelOfDetail,lat);
        return m_pixel;
    }
    
	public Point xyPixelToLatLng( int levelOfDetail, int x, int y) {
		// we have to mod by world size in case we had
		// to extend the whole world
		m_geoPt.m_lat = yPixToDegLat(levelOfDetail,y);
		m_geoPt.m_lng = xPixToDegLng(levelOfDetail,x);
		return m_geoPt;
	}



    
	public R2 geoPosToTileXY( int level, double lat, double lng ){

        double x = (lng + 180) / 360; 
        double y = (lat + 90 ) / 180;

        int mapWidthSize = mapWidthSize(level);
        int mapHeightSize = mapHeightSize(level);
        double pixX = clip(x * mapWidthSize + 0.5, 0, mapWidthSize);
        double pixY = clip(y * mapHeightSize + 0.5, 0, mapHeightSize);
		m_tile.m_x =  (int)Math.floor(pixX/ m_orgTilePixSize);
		m_tile.m_y =  (int)Math.floor(pixY/m_orgTilePixSize);
		return m_tile;
	}
	
    public R2 tileXYToTopLeftXY( int tileX, int tileY  ){
    	m_pixel.m_x = tileX*m_orgTilePixSize;
    	m_pixel.m_y = (tileY+1)*m_orgTilePixSize;

    	return m_pixel;
    }	
}


