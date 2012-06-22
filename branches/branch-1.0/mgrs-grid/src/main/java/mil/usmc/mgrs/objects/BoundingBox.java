package mil.usmc.mgrs.objects;



public class BoundingBox {

    private static final String QUERY_PARAM_FORMAT = "%1$f,%2$f,%3$f,%4$f";
    private double m_minLon;
    private double m_minLat;
    private double m_maxLon;
    private double m_maxLat;

    public BoundingBox() {

        // corresponds to the null set
        m_minLon = Double.POSITIVE_INFINITY;
        m_minLat = Double.POSITIVE_INFINITY;
        m_maxLon = Double.NEGATIVE_INFINITY;
        m_maxLat = Double.NEGATIVE_INFINITY;
    }

    public BoundingBox(double minLat, double minLon, double maxLat, double maxLon) {

        m_minLon = minLon;
        m_minLat = minLat;
        m_maxLon = maxLon;
        m_maxLat = maxLat;
    }

    public static BoundingBox valueOf(String boundingBoxString) throws Exception {

        String[] bboxTokens = boundingBoxString.split(",");

        if (bboxTokens.length < 4) {

            throw new Exception("Bounding box contained too few arguments");
        } else {

            try {
                double minLon = Double.parseDouble(bboxTokens[0]);
                double minLat = Double.parseDouble(bboxTokens[1]);
                double maxLon = Double.parseDouble(bboxTokens[2]);
                double maxLat = Double.parseDouble(bboxTokens[3]);

                return new BoundingBox(minLat, minLon, maxLat, maxLon);
            } catch (NumberFormatException e) {

                throw new Exception("not all bbox arguments were numeric", e);
            }
        }
    }

    public static BoundingBox union(BoundingBox first, BoundingBox second) {

        double minLon = Math.min(first.getMinLon(), second.getMinLon());
        double minLat = Math.min(first.getMinLat(), second.getMinLat());
        double maxLon = Math.max(first.getMaxLon(), second.getMaxLon());
        double maxLat = Math.max(first.getMaxLat(), second.getMaxLat());

        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    public static BoundingBox intersection(BoundingBox first, BoundingBox second) {

        double minLon = Math.max(first.getMinLon(), second.getMinLon());
        double minLat = Math.max(first.getMinLat(), second.getMinLat());
        double maxLon = Math.min(first.getMaxLon(), second.getMaxLon());
        double maxLat = Math.min(first.getMaxLat(), second.getMaxLat());

        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    public boolean within(double lat, double lon) {

        return lat >= m_minLat && lat < m_maxLat && lon >= m_minLon && lon < m_maxLon;
    }

    public double getMaxLat() {
        return m_maxLat;
    }

    public double getMaxLon() {
        return m_maxLon;
    }

    public double getMinLat() {
        return m_minLat;
    }

    public double getMinLon() {
        return m_minLon;
    }

    public double getLatSpan() {

        return getMaxLat() - getMinLat();
    }

    public double getLonSpan() {

        return getMaxLon() - getMinLon();
    }

    public double getMinSpan() {
        return Math.min(getLonSpan(), getLatSpan());
    }

    @Override
    public String toString() {

        return String.format(QUERY_PARAM_FORMAT, m_minLon, m_minLat, m_maxLon, m_maxLat);
    }
}
