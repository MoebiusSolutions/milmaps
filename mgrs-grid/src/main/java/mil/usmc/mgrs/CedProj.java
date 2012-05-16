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
public class CedProj extends AbstractProjection {
    private static final double MinLatitude = -90;
    private static final double MaxLatitude = 90;
    private static final double MinLongitude = -180;
    private static final double MaxLongitude = 180;
    
	public CedProj() {
		m_orgTilePixWidth = 512;
		m_orgTilePixHeight = 512;
		m_orgTileDegWidth = 180.0;
		m_orgTileDegHeight = 180.0;
	}  
	
    public int mapWidthSize(int levelOfDetail)
    {
        return (int) m_orgTilePixWidth << (levelOfDetail + 1);
    }
    
    @Override
    public  double groundResolution(double latitude)
    {
        return (2 * Math.PI * IProjection.EARTH_RADIUS_METERS) / mapWidth();
    } 
	
    @Override
    public R2 tileXYToTopLeftXY( int tileX, int tileY  ){
    	m_pixel.m_x = tileX*m_orgTilePixWidth;
    	m_pixel.m_y = (tileY+1)*m_orgTilePixHeight;

    	return m_pixel;
    }
    
    @Override
    public int mapHeightInPix() {
    	double width = mapWidth()/2.0;
    	return (int)(width+0.5);
    }
	
	@Override
	public int lngDegToPixX(double deg) {
		deg = clip(deg, MinLongitude, MaxLongitude);
		deg = translateLng(deg - m_centLng);
		double x = (deg)/180;
		return (int)((x * mapWidthInPix()/2)+0.5);
	}
	
	@Override
	public double xPixToDegLng(double pix) {
        double mapSize = mapWidth()/2;
        double x = (pix / mapSize);   
        return wrapLng(m_centLng + (x*180));
	}

	@Override
	public int latDegToPixY(double deg) {
		deg = clip(deg, MinLatitude, MaxLatitude);
		double y = (deg + 90)/180;
		return (int)((y * mapHeightInPix())+0.5);
	}

	@Override
	public double yPixToDegLat(double pix) {
		double mapSize =  mapWidth()/2.0;
        double x = (pix / mapSize) - 0.5;   
        return (x*180); 
	}

	@Override
	public R2 latLngToPixelXY(double lat, double lng) {
        m_pixel.m_x = (int) lngDegToPixX(lng);
        m_pixel.m_y = (int) latDegToPixY(lat);
        return m_pixel;
	}

	@Override
	public Point xyPixelToLatLng(int x, int y) {
		m_geoPt.m_lat = yPixToDegLat(y);
		m_geoPt.m_lng = xPixToDegLng(x);
		return m_geoPt;
	}

	@Override
	public R2 geoPosToTileXY(double lat, double lng) {
		// TODO Auto-generated method stub
		return latLngToPixelXY(lat, lng);
	}	
}


