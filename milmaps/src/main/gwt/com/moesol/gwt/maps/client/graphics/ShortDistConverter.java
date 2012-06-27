/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.ViewCoords;

public class ShortDistConverter extends ConverterBase {

	@Override
	public ViewCoords[][] massagePts(ViewCoords[] pts) {
		if (m_map == null) {
			throw new IllegalStateException("ConverterBase: m_map = null");
		}
		int maxDiff = mapWidth()/2;
		int length = pts.length;
		boolean bSplit = false;
		for (int i = 0; i < length - 1; i++) {
			if (i > 0){
				int diff = pts[i].getX() - pts[i-1].getX();
				if (Math.abs(diff) > maxDiff){
					bSplit = true;
					break;
				}
			}
		}
		if (bSplit){
			for (int i = 0; i < length - 1; i++) {
				if (i > 0){
					int diff = pts[i].getX() - pts[i-1].getX();
					if (Math.abs(diff) > maxDiff){
						bSplit = true;
						break;
					}
				}
			}			
		}
		return null;
	}

}
