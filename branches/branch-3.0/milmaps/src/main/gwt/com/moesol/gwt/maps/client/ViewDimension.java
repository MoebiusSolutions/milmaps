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



public class ViewDimension {
	private final int m_width;
	private final int m_height;
	
	public static class Builder {
		private int m_width;
		private int m_height;
		
		public ViewDimension build() {
			return new ViewDimension(m_width, m_height);
		}
		public Builder setWidth(int w) { m_width = w; return this; }
		public Builder setHeight(int h) { m_height = h; return this; }
		public int getWidth() { return m_width; }
		public int getHeight() { return m_height; }
	}
	public static Builder builder() {
		return new Builder();
	}
	
	public ViewDimension() {
		m_width = m_height = 0;
	}
	public ViewDimension(int width, int height) {
		m_width = width;
		m_height = height;
	}
	
	public int getHeight() {
		return m_height;
	}
	public int getWidth() {
		return m_width;
	}

    @Override
    public boolean equals(final Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }

        final ViewDimension that = (ViewDimension) o;

        if(m_height != that.m_height)
        {
            return false;
        }
        if(m_width != that.m_width)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = m_width;
        result = 31 * result + m_height;
        return result;
    }

    @Override
	public String toString() {
		return "[w=" + m_width + ",h=" + m_height + "]";
	}
	
}
