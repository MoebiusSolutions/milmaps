/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
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
