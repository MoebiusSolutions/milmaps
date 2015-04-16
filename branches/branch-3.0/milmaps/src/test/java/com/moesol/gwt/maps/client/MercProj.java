/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


import com.moesol.gwt.maps.client.units.Degrees;


public class MercProj
{
    private static final double EarthRadius = 6378137;
    private static final double MinLatitude = -85.05112878;
    private static final double MaxLatitude = 85.05112878;
    private static final double MinLongitude = -180;
    private static final double MaxLongitude = 180;
    
    private GeodeticCoords m_geoPt = new GeodeticCoords();
    private TileXY m_tile = new TileXY();

    /// <summary>
    /// Clips a number to the specified minimum and maximum values.
    /// </summary>
    /// <param name="n">The number to clip.</param>
    /// <param name="minValue">Minimum allowable value.</param>
    /// <param name="maxValue">Maximum allowable value.</param>
    /// <returns>The clipped value.</returns>
    private double clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }
    
    

    /// <summary>
    /// Determines the map width and height (in pixels) at a specified level
    /// of detail.
    /// </summary>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <returns>The map width and height in pixels.</returns>
    public int mapSize(int levelOfDetail)
    {
        return (int) 256 << levelOfDetail;
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
    public  double groundResolution(double latitude, int levelOfDetail)
    {
        latitude = clip(latitude, MinLatitude, MaxLatitude);
        return Math.cos(latitude * Math.PI / 180) * 2 * Math.PI * EarthRadius / mapSize(levelOfDetail);
    }



    /// <summary>
    /// Determines the map scale at a specified latitude, level of detail,
    /// and screen resolution.
    /// </summary>
    /// <param name="latitude">Latitude (in degrees) at which to measure the
    /// map scale.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <param name="screenDpi">Resolution of the screen, in dots per inch.</param>
    /// <returns>The map scale, expressed as the denominator N of the ratio 1 : N.</returns>
    public double mapScale(double latitude, int levelOfDetail, int screenDpi)
    {
        return groundResolution(latitude, levelOfDetail) * screenDpi / 0.0254;
    }



    /// <summary>
    /// Converts a point from latitude/longitude WGS-84 coordinates (in degrees)
    /// into pixel XY coordinates at a specified level of detail.
    /// </summary>
    /// <param name="latitude">Latitude of the point, in degrees.</param>
    /// <param name="longitude">Longitude of the point, in degrees.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <param name="pixelX">Output parameter receiving the X coordinate in pixels.</param>
    /// <param name="pixelY">Output parameter receiving the Y coordinate in pixels.</param>
    public WorldCoords latLngToPixelXY( int levelOfDetail, double latitude, double longitude )
    {
        latitude = clip(latitude, MinLatitude, MaxLatitude);
        longitude = clip(longitude, MinLongitude, MaxLongitude);

        double x = (longitude + 180) / 360; 
        double sinLatitude = Math.sin(latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);
        int mapSize = mapSize(levelOfDetail);
        int wx = (int) clip(x * mapSize + 0.5, 0, mapSize - 1);
        int wy = mapSize - (int) clip(y * mapSize + 0.5, 0, mapSize - 1);
        return new WorldCoords(wx, wy);
    }



    /// <summary>
    /// Converts a pixel from pixel XY coordinates at a specified level of detail
    /// into latitude/longitude WGS-84 coordinates (in degrees).
    /// </summary>
    /// <param name="pixelX">X coordinate of the point, in pixels.</param>
    /// <param name="pixelY">Y coordinates of the point, in pixels.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <param name="latitude">Output parameter receiving the latitude in degrees.</param>
    /// <param name="longitude">Output parameter receiving the longitude in degrees.</param>
    public GeodeticCoords pixelXYToLatLng( int levelOfDetail , int pixelX, int pixelY )
    {
        int mapSize = mapSize(levelOfDetail);
        pixelY = mapSize - pixelY;
        double x = (clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        //pixelY = mapSize - pixelY;
        double y = 0.5 - (clip(pixelY, 0, mapSize - 1) / mapSize);

        double latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        double longitude = 360 * x;
        m_geoPt = Degrees.geodetic(latitude, longitude);
        return m_geoPt;
    }



    /// <summary>
    /// Converts pixel XY coordinates into tile XY coordinates of the tile containing
    /// the specified pixel.
    /// </summary>
    /// <param name="pixelX">Pixel X coordinate.</param>
    /// <param name="pixelY">Pixel Y coordinate.</param>
    /// <param name="tileX">Output parameter receiving the tile X coordinate.</param>
    /// <param name="tileY">Output parameter receiving the tile Y coordinate.</param>
    public TileXY pixelXYToTileXY( int level, int pixelX, int pixelY )
    {
        m_tile.m_x = pixelX / 256;
        m_tile.m_y = (mapSize(level)-pixelY) / 256;
		int j = (1<<level) - 1;
		m_tile.m_y = j - m_tile.m_y;
        return m_tile;
    }

    /// <summary>
    /// Converts tile XY coordinates into pixel XY coordinates of the upper-left pixel
    /// of the specified tile.
    /// </summary>
    /// <param name="tileX">Tile X coordinate.</param>
    /// <param name="tileY">Tile Y coordinate.</param>
    /// <param name="pixelX">Output parameter receiving the pixel X coordinate.</param>
    /// <param name="pixelY">Output parameter receiving the pixel Y coordinate.</param>
    public WorldCoords tileXYToPixelXY( int tileX, int tileY )
    {
    	return new WorldCoords(tileX * 256, tileY * 256);
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
    public  TileXY quadKeyToTileXY( String quadKey )
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
		double m_dx = 256;
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
