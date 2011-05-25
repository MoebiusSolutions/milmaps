package mil.usmc.mgrs;

import mil.usmc.mgrs.objects.Point;
import mil.usmc.mgrs.objects.R2;

//------------------------------------------------------------------------------
// http://msdn.microsoft.com/en-us/library/bb259689.aspx
// <copyright company="Microsoft">
//     Copyright (c) 2006-2009 Microsoft Corporation.  All rights reserved.
// </copyright>
//------------------------------------------------------------------------------


public class MercProj
{	
    private static final double MinLatitude = -85.05112878;
    private static final double MaxLatitude = 85.05112878;
    private static final double MinLongitude = -180;
    private static final double MaxLongitude = 180;
    
	public static double RadToDeg = 57.29577951;
	public static double DegToRad = 0.017453293;
    
	protected int m_orgTilePixSize = 0;
    
    private Point m_geoPt = new Point();
    private R2 m_pixel = new R2();
    private R2 m_tile = new R2();


    private double clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }
    
	public void initialize( int tileSize ){	
		m_orgTilePixSize = tileSize;
	}   


    public int mapSize(int levelOfDetail)
    {
        return (int) m_orgTilePixSize << levelOfDetail;
    }


	/**
	 * This routine finds the whole world pixel value, based on zero pixel at 
	 * the top of the map
	 * Converts lat in degrees to the world coordinate pixel y value in doubles
	 * @param dPhi : Geodetic Coordinate
	 * @return a double (the y value).
	 */
	protected double lat2PixY( int level, double dLat ){
		dLat = clip(dLat, MinLatitude, MaxLatitude);
        double sinLat = Math.sin(DegToRad*dLat);
        double y = 0.5 - Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI);
        return (y * mapSize(level));
	}
        
	/**
	 * Converts Longitude in degs to the pixel coordinate X value
	 * @param dLng : Geodetic longitude Coordinate
	 * @return a double (the x pixel value).
	 */
	protected double lng2PixX( int level, double dLng )
	{
		dLng = clip(dLng, MinLongitude, MaxLongitude);
		double x = (dLng + 180)/360;
		return (x * mapSize(level));
	}
	
	/**
	 * Convert the x value of a world coordinate into longitude
	 * @param dX : World pixel coordinate x value
	 * @return a double (longitude)
	 */
	protected double pixX2Lng( int level, double dX )
	{
        double mapSize = mapSize(level);
        double x = (dX / mapSize) - 0.5;   
        return (x*360);
	}

	/**
	 * This routine is based on pixel zero at the top of the map.
	 * Convert the y pixel component of a world coordinate into a latitude in degs
	 * @param dY : word coordinate y value
	 * @return a double, the y value. If an error occurs it returns DEG_ERROR
	 */
	protected double  pixY2Lat( int level, double dY )
	{
        double mapSize = mapSize(level);
        double y = 0.5 - (clip(dY, 0, mapSize -1 ) / mapSize);
        double lat = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        return lat;
	}


	public int lngDegToPixX( int level, double deg ){
		return (int)(lng2PixX(level,deg)+0.5);
	}
	

	public double xPixToDegLng( int level, double pix ){
		return pixX2Lng(level,pix);
	}
	

	public int latDegToPixY( int level, double deg ){
		// First we find the whole world pixel value based on the 
		// top of the map having y value equal to zero.
		double pixY = lat2PixY(level,deg);
		// Next we have to translate it for a map with zero y-value
		// at the bottom of the map.
		pixY =  mapSize(level) - pixY;
		return (int)( pixY+0.5);
	}
	
	/**
	 * This routine converts pixY to a latitude with whole world pixel zero
	 * at the bottom of the map (not the top)
	 */

	public double yPixToDegLat( int level, double pix ){
		// First we need to translate the pixel since the y direction is reversed
		double mapSize = mapSize(level);
		pix = mapSize - pix;
		return pixY2Lat(level,pix);
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
        double sinLat = Math.sin(lat * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI);

        int mapSize = (int) ( m_orgTilePixSize << level );
        double pixX = clip(x * mapSize + 0.5, 0, mapSize);
        double pixY = clip(y * mapSize + 0.5, 0, mapSize);
		m_tile.m_x =  (int)Math.floor(pixX/ m_orgTilePixSize);
		m_tile.m_y =  (int)Math.floor(pixY/m_orgTilePixSize);
		// translate so it is based on row zero at the bottom of the map
		int j = (1<<level) - 1;
		m_tile.m_y = j - m_tile.m_y;
		return m_tile;
	}
	
    public R2 tileXYToTopLeftXY( int tileX, int tileY  ){
    	m_pixel.m_x = tileX*m_orgTilePixSize;
    	m_pixel.m_y = (tileY+1)*m_orgTilePixSize;

    	return m_pixel;
    }
    


    /// <summary>
    /// Converts tile XY coordinates into a QuadKey at a specified level of detail.
    /// </summary>
    /// <param name="tileX">Tile X coordinate.</param>
    /// <param name="tileY">Tile Y coordinate.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <returns>A string containing the QuadKey.</returns>
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
    

	public int findLevel(double dpi, double projScale) {
		double mpp = 2.54 / (dpi * 100); // meters per pixel for physical screen
		double m_dx = m_orgTilePixSize;
		double l_mpp = 180 * (111120.0 / m_dx);
		// compute the best level.
		if ( projScale == 0.0 ){
			projScale = (mpp / l_mpp);
		}
		double logMess = Math.log(projScale) + Math.log(l_mpp)
				- Math.log(mpp);
		double dN = logMess / Math.log(2);
		return (int)(Math.rint(dN)) + 1;
	}
}
