/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.maps.server.tms;

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


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.moesol.gwt.maps.shared.BoundingBox;
import com.moesol.gwt.maps.shared.Feature;
import com.moesol.gwt.maps.shared.Tile;

/**
 * Abstract base class for ITileRenderers that are also capable of 
 * being queries for feature information.
 */
public abstract class AbstractFeatureTileRenderer implements
		IFeatureTileRenderer {

	/*
	 * (non-Javadoc)
	 * @see com.moesol.maps.server.tms.IFeatureTileRenderer#getBoundingBoxForFeatures(java.lang.String[])
	 */
	@Override
	public BoundingBox getBoundingBoxForFeatures(String[] featureIds) {

		ArrayList<Feature> features = new ArrayList<Feature>();
		for (String featureId : featureIds) {
			Feature feature = m_featureProvider.getFeature(featureId);
			if (feature != null) {
				features.add(feature);
			}
		}

		BoundingBox bbox1 = getBoundingBoxForFeatures(features,
				m_ascendingLngComparator);

		BoundingBox bbox2 = getBoundingBoxForFeatures(features,
				m_descendingLngComparator);

		BoundingBox bestFit;
		if (bbox1.getLonSpan() < bbox2.getLonSpan()) {
			bestFit = bbox1;
		} else {
			bestFit = bbox2;
		}

		return bestFit;
	}

	BoundingBox getBoundingBoxForFeatures(ArrayList<Feature> features,
			Comparator<Feature> comparator) {
		Collections.sort(features, comparator);

		BoundingBox bbox1 = new BoundingBox();

		for (Feature feature : features) {
			bbox1.union(feature.getLat(), feature.getLng());
		}
		return bbox1;
	}

	private IFeatureProvider m_featureProvider;
	private Comparator<Feature> m_descendingLngComparator = new Comparator<Feature>() {
		@Override
		public int compare(Feature o1, Feature o2) {
			return Double.compare(o2.getLng(), o1.getLng());
		}
	};
	private Comparator<Feature> m_ascendingLngComparator = new Comparator<Feature>() {
		@Override
		public int compare(Feature o1, Feature o2) {
			return Double.compare(o1.getLng(), o2.getLng());
		}
	};

	public AbstractFeatureTileRenderer(IFeatureProvider dataProvider) {
		m_featureProvider = dataProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.moesol.maps.server.tms.ITileRenderer#render(com.moesol.gwt.maps.shared.Tile, java.awt.Graphics2D, java.util.Map)
	 */
	@Override
	public void render(Tile tile, Graphics2D graphics,
			Map<String, String[]> parameters) {
		for (Feature feature : m_featureProvider.getData()) {
			renderFeature(tile, graphics, feature);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.moesol.maps.server.tms.IFeatureTileRenderer#getHitFeatures(com.moesol.gwt.maps.shared.Tile, double, double)
	 */
	@Override
	public List<Feature> getHitFeatures(Tile tile, double lat, double lng) {
		List<Feature> features = new ArrayList<Feature>();
		for (Feature feature : m_featureProvider.getData()) {
			if (isPointInsideFeature(tile, lat, lng, feature)) {
				features.add(feature);
			}
		}
		return features;
	}

	private void renderFeature(Tile tile, Graphics2D graphics, Feature feature) {
		double unitLat = feature.getLat();
		double unitLng = feature.getLng();

		BufferedImage icon = getIcon(feature);

		double halfIconLngSpan = tile
				.pixelSpanToLongitudeSpan(icon.getWidth() / 2);
		double halfIconLatSpan = tile
				.pixelSpanToLatitudeSpan(icon.getHeight() / 2);

		double minLng = unitLng - halfIconLngSpan;
		double maxLng = unitLng + halfIconLatSpan;

		double newUnitLng = wrapLng(tile.getCenterLng(), unitLng, minLng,
				maxLng);
		if (newUnitLng != unitLng) {
			minLng = newUnitLng - halfIconLngSpan;
		}

		double minLat = unitLat + halfIconLatSpan;

		double x = tile.longitudeToPixelSpace(minLng);
		double y = tile.latitudeToPixelSpace(minLat);
		graphics.drawImage(icon, (int) x, (int) y, null);
	}

	// TODO: there are probably other wrap lng functions in ghetto-maps
	// need to refactor to use common code
	double wrapLng(double tileCenterLng, double unitLng, double minLng,
			double maxLng) {
		if (tileCenterLng > 0.0 && minLng <= -180.0) {
			return unitLng + 360.0;
		} else if (tileCenterLng < 0.0 && maxLng >= 180.0) {
			return unitLng - 360.0;
		} else
			return unitLng;
	}

	protected abstract BufferedImage getIcon(Feature feature);

	protected boolean isPointInsideFeature(Tile tile, double lat, double lng,
			Feature feature) {
		BufferedImage icon = getIcon(feature);

		final int iconWidth = icon.getWidth();
		final int iconHeight = icon.getHeight();

		return isPointInsideFeature(tile, lat, lng, feature, iconWidth,
				iconHeight);
	}

	boolean isPointInsideFeature(Tile tile, double lat, double lng,
			Feature feature, final int iconWidth, final int iconHeight) {
		double halfIconLngSpan = tile.pixelSpanToLongitudeSpan(iconWidth / 2);
		double halfIconLatSpan = tile.pixelSpanToLatitudeSpan(iconHeight / 2);

		double featureLat = feature.getLat();
		double featureLng = feature.getLng();
		BoundingBox bbox = BoundingBox.builder()
				.left(featureLng - halfIconLngSpan)
				.bottom(featureLat - halfIconLatSpan)
				.right(featureLng + halfIconLngSpan)
				.top(featureLat + halfIconLatSpan)
				.degrees()
				.build();

		return bbox.contains(lat, lng);
	}
}
