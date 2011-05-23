package com.moesol.maps.server.tms;

import java.util.List;

import com.moesol.gwt.maps.shared.Feature;

/**
 * Implemented by classes capable of providing features.
 */
public interface IFeatureProvider extends ITileDataProvider<List<Feature>> {
	/**
	 * Get a feature by ID.
	 * 
	 * @param featureId The ID of the feature.
	 * @return The requested feature, or null if not found.
	 */
	public Feature getFeature(String featureId);
}
