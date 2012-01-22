package com.moesol.gwt.maps.client;


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
