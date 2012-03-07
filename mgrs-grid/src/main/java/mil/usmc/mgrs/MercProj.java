package mil.usmc.mgrs;

import mil.usmc.mgrs.objects.Point;
import mil.usmc.mgrs.objects.R2;

//------------------------------------------------------------------------------
// http://msdn.microsoft.com/en-us/library/bb259689.aspx
// <copyright company="Microsoft">
//     Copyright (c) 2006-2009 Microsoft Corporation.  All rights reserved.
// </copyright>
//------------------------------------------------------------------------------


public class MercProj extends AbstractProjection
{	
    private static final double MinLatitude = -85.05112878;
    private static final double MaxLatitude = 85.05112878;
    private static final double MinLongitude = -180;
    private static final double MaxLongitude = 180;

    public MercProj(){
    	m_orgTilePixWidth = 256;
    	m_orgTilePixHeight = 256;
    	m_orgTileDegWidth = 180.0;
    	m_orgTileDegHeight = 180.0;
    }

	
    /// <summary>
    /// Determines the ground resolution (in meters per pixel) at a specified
    /// latitude and level of detail.
    /// </summary>
    /// <param name="latitude">Latitude (in degrees) at which to measure the
    /// ground resolution.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <returns>The ground resolution, in meters per pixel.</returns>
    @Override
    public  double groundResolution(double latitude)
    {
        latitude = clip(latitude, MinLatitude, MaxLatitude);
        return Math.cos(latitude * Math.PI / 180) * 2 * Math.PI * IProjection.EARTH_RADIUS_METERS / mapWidth();
    } 
    
    @Override
    public int mapHeightInPix() {
    	return mapWidthInPix();
    }
	
	/**
	 * This routine finds the whole world pixel value, based on zero pixel at 
	 * the top of the map
	 * Converts lat in degrees to the world coordinate pixel y value in doubles
	 * @param dPhi : Geodetic Coordinate
	 * @return a double (the y value).
	 */

	protected double lat2PixY(double dLat){
		dLat = clip(dLat, MinLatitude, MaxLatitude);
        double sinLat = Math.sin(DegToRad*dLat);
        double y = 0.5 - Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI);
        return (y * mapWidth());
	}
      
	
	private double translateLng(double lng){
		if (lng < -180) {
			lng += 360;
		}
		else if (lng > 180){
			lng -= 360;
		}
		return lng;
	}
	
	/**
	 * Converts Longitude in degs to the pixel coordinate X value
	 * @param dLng : Geodetic longitude Coordinate
	 * @return a double (the x pixel value).
	 */

	protected double lng2PixX(double deg)
	{
		deg = clip(deg, MinLongitude, MaxLongitude);
		deg = translateLng(deg - m_centLng);
		double x = (deg)/180;
		return (int)((x * mapWidthInPix()/2)+0.5);
	}
	
	/**
	 * Convert the x value of a world coordinate into longitude
	 * @param dX : World pixel coordinate x value
	 * @return a double (longitude)
	 */

	protected double pixX2Lng(double dX){
        double mapWidth = mapWidth()/2;
        double x = (dX / mapWidth) - 0.5;   
        return wrapLng(m_centLng + (x*180));
	}

	/**
	 * This routine is based on pixel zero at the top of the map.
	 * Convert the y pixel component of a world coordinate into a latitude in degs
	 * @param dY : word coordinate y value
	 * @return a double, the y value. If an error occurs it returns DEG_ERROR
	 */
 
	protected double  pixY2Lat(double dY ){
        double mapWidth = mapWidth();
        double y = 0.5 - (clip(dY, 0, mapWidth -1 ) / mapWidth);
        double lat = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        return lat;
	}

    @Override
	public int lngDegToPixX( double deg ){
		return (int)(lng2PixX(deg)+0.5);
	}
	
    @Override
	public double xPixToDegLng(double pix){
		return pixX2Lng(pix);
	}
	
    @Override
	public int latDegToPixY(double deg){
		// First we find the whole world pixel value based on the 
		// top of the map having y value equal to zero.
		double pixY = lat2PixY(deg);
		// Next we have to translate it for a map with zero y-value
		// at the bottom of the map.
		pixY =  mapWidth() - pixY;
		return (int)( pixY+0.5);
	}
	
	/**
	 * This routine converts pixY to a latitude with whole world pixel zero
	 * at the bottom of the map (not the top)
	 */
    @Override
	public double yPixToDegLat(double pix){
		// First we need to translate the pixel since the y direction is reversed
		double mapWidth = mapWidth();
		pix = mapWidth - pix;
		return pixY2Lat(pix);
	}


    @Override
    public R2 latLngToPixelXY(double lat, double lng )
    {
        m_pixel.m_x = (int) lngDegToPixX(lng);
        m_pixel.m_y = (int) latDegToPixY(lat);
        return m_pixel;
    }
    
    @Override
	public Point xyPixelToLatLng(int x, int y) {
		// we have to mod by world size in case we had
		// to extend the whole world
		m_geoPt.m_lat = yPixToDegLat(y);
		m_geoPt.m_lng = xPixToDegLng(x);
		return m_geoPt;
	}

    
    @Override
	public R2 geoPosToTileXY(double lat, double lng){

        double x = (lng + 180) / 360; 
        double sinLat = Math.sin(lat * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI);

        double mapWidth = mapWidth();
        double pixX = clip(x * mapWidth + 0.5, 0, mapWidth);
        double pixY = clip(y * mapWidth + 0.5, 0, mapWidth);
		m_tile.m_x =  (int)Math.floor(pixX/ m_orgTilePixWidth);
		m_tile.m_y =  (int)Math.floor(pixY/m_orgTilePixHeight);
		// translate so it is based on row zero at the bottom of the map
		//int j = (1<<level) - 1;
		// We can use the width since the tiles are square.
		int j = ((int)(mapWidth()+0.5))/m_orgTilePixWidth;
		m_tile.m_y = j - m_tile.m_y;
		return m_tile;
	}
	
    @Override
    public R2 tileXYToTopLeftXY(int tileX, int tileY ){
    	m_pixel.m_x = tileX*m_orgTilePixWidth;
    	m_pixel.m_y = (tileY+1)*m_orgTilePixHeight;

    	return m_pixel;
    }	
    
    public String tileXYToQuadKey( int tileX, int tileY, int levelOfDetail )
    {
    	StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--)
        {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0)
            {
                digit++;
            }
            if ((tileY & mask) != 0)
            {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }


    /// <summary>
    /// Converts a QuadKey into tile XY coordinates.
    /// </summary>
    /// <param name="quadKey">QuadKey of the tile.</param>
    /// <param name="tileX">Output parameter receiving the tile X coordinate.</param>
    /// <param name="tileY">Output parameter receiving the tile Y coordinate.</param>
    /// <param name="levelOfDetail">Output parameter receiving the level of detail.</param>
    public  R2 quadKeyToTileXY( String quadKey )
    {
        m_tile.m_x = m_tile.m_y = 0;
        m_tile.m_levelOfDetail = quadKey.length();
        for (int i = m_tile.m_levelOfDetail; i > 0; i--)
        {
            int mask = 1 << (i - 1);
            switch (quadKey.charAt(m_tile.m_levelOfDetail - i))
            {
                case '0':
                    break;

                case '1':
                	m_tile.m_x |= mask;
                    break;

                case '2':
                	m_tile.m_y |= mask;
                    break;

                case '3':
                	m_tile.m_x |= mask;
                	m_tile.m_y |= mask;
                    break;

                //default:
                //    throw new ArgumentException("Invalid QuadKey digit sequence.");
            }
        }
        return m_tile;
    }
}
