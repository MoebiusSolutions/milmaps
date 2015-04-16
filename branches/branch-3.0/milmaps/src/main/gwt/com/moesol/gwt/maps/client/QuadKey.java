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
