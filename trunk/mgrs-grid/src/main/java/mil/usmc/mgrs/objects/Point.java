package mil.usmc.mgrs.objects;



public class Point {

    private double m_lat;
    private double m_lng;
    public boolean flag;

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

    /*
     * Get the string representation of this Point
     * in the form: longitude,latitude
     */
    @Override
    public String toString() {
        return m_lng + "," + m_lat;
    }


}
