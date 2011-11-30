package com.moesol.gwt.maps.client;

public class ViewDimension {
	private int m_width;
	private int m_height;
	
	public ViewDimension() {
		m_width = m_height = 0;
	}
	
	public ViewDimension(int width, int height) {
		m_width = width;
		m_height = height;
	}
	
	public void copyFrom( ViewDimension vd ){
		m_width  = vd.getWidth();
		m_height = vd.getHeight();
	}
	
	public int getHeight() {
		return m_height;
	}
	public void setHeight(int height) {
		m_height = height;
	}
	public int getWidth() {
		return m_width;
	}
	public void setWidth(int width) {
		m_width = width;
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
