package com.moesol.maps.server.tms;

import java.util.List;

import org.junit.Ignore;

import com.moesol.gwt.maps.shared.Feature;

@Ignore
class TestFeatureProvider implements IFeatureProvider {
	private List<Feature> m_data;

	@Override
	public List<Feature> getData() {
		return m_data;
	}

	public void setData(List<Feature> data) {
		m_data = data;
	}

	@Override
	public Feature getFeature(String featureId) {
		for (Feature feature : m_data) {
			if (feature.getTitle().equals(featureId)) {
				return feature;
			}
		}
		return null;
	}
}