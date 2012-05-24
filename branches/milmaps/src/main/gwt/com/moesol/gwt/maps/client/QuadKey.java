/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client;

// This code ported from c# to java from the following:
//------------------------------------------------------------------------------
//http://msdn.microsoft.com/en-us/library/bb259689.aspx
//<copyright company="Microsoft">
//  Copyright (c) 2006-2009 Microsoft Corporation.  All rights reserved.
//</copyright>
//------------------------------------------------------------------------------


public class QuadKey {
	/**
	 * This routine computes the quadKey string from the tile's x,y position in
	 * the matrix of tiles and the levelOf detail.
	 * @param tileX 
	 * 		The column of the tile
	 * @param tileY
	 * 		The row of the tile.
	 * @param levelOfDetail
	 * 		The level of the data.
	 * @return
	 */
    static public String tileXYToKey( int tileX, int tileY, int levelOfDetail )
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

	/**
	 * This routine takes a cell defined by the quadKey string and computes the level
	 * and x,y cell of the tile.
	 * @param quadKey
	 * 		string defining the cell.
	 * @return TileXY structure.
	 */
    static public  TileXY keyToTileXY( String quadKey )
    {
        TileXY tile = new TileXY();
        tile.m_levelOfDetail = quadKey.length();
        for (int i = tile.m_levelOfDetail; i > 0; i--)
        {
            int mask = 1 << (i - 1);
            switch (quadKey.charAt(tile.m_levelOfDetail - i))
            {
                case '0':
                    break;

                case '1':
                	tile.m_x |= mask;
                    break;

                case '2':
                	tile.m_y |= mask;
                    break;

                case '3':
                	tile.m_x |= mask;
                	tile.m_y |= mask;
                    break;
            }
        }
        return tile;
    }
}
