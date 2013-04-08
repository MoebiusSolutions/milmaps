/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.maps.server.tms;

import java.util.List;

import com.moesol.gwt.maps.shared.BoundingBox;
import com.moesol.gwt.maps.shared.Feature;
import com.moesol.gwt.maps.shared.Tile;

/**
 * Decorator interface for ITileRenderers that render features and
 * provide extra information about them.
 */
public interface IFeatureTileRenderer extends ITileRenderer {
	public List<Feature> getHitFeatures(Tile tile, double lat, double lng);
	public BoundingBox getBoundingBoxForFeatures(String[] featureIds);
}
