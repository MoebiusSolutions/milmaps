package mil.usmc.mgrs.objects;


public class Point {

    public double m_lat;
    public double m_lng;
    public boolean flag = false;

    public Point(double lat, double lon) {
        super();
        m_lat = lat;
        m_lng = lon;
    }

    public Point(double[] coords) throws Exception {
        super();
        if (coords.length != 2) {
            throw new Exception("A point must contain exactly two coordinates.");
        }
        m_lat = coords[0];
        m_lng = coords[1];
    }

    public Point() {
        super();
        m_lat = 0.0;
        m_lng = 0.0;
    }

    public double getLat() {
        return m_lat;
    }

    public void setLat(double lat) {
        m_lat = lat;
    }

    public double getLng() {
        return m_lng;
    }

    public void setLng(double lon) {
        m_lng = lon;
    }

	public void copy( Point p ){
		m_lat = p.m_lat;
		m_lng = p.m_lng;
		flag = p.flag;
	}
	
	public Point clone(){
		Point c = new Point();
		c.copy(this);
		return c;
	}
    /*
     * Get the string representation of this Point
     * in the form: longitude,latitude
     */
    @Override
    public String toString() {
        return m_lng + "," + m_lat;
    }


}
