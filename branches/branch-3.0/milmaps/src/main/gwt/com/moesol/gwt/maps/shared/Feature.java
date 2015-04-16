/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.shared;

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


public class Feature {
	public static class Builder {
		private final String m_title;
		private String m_iconUrl;
		private final double m_lat;
		private final double m_lng;
		
		public Builder(String title, double lat, double lng) {
			m_title = title;
			m_lat = lat;
			m_lng = lng;
		}
		
		public Builder iconUrl(String iconUrl) {
			m_iconUrl = iconUrl;
			return this;
		}
		
		public Feature build() {
			return new Feature(m_title, m_lat, m_lng, m_iconUrl);
		}
	}
	
	private final String m_title;
	private final String m_iconUrl;
	private final double m_lat;
	private final double m_lng;
	
	public Feature(String title, double lat, double lng, String iconUrl) {
		m_title = title;
		m_lat = lat;
		m_lng = lng;
		m_iconUrl = iconUrl;
	}
	
	public String getTitle() {
		return m_title;
	}
	
	public double getLat() {
		return m_lat;
	}

	public double getLng() {
		return m_lng;
	}

	public String getIcon() {
		return m_iconUrl;
	}
}
