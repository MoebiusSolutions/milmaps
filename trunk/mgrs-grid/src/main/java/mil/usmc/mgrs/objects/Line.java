package mil.usmc.mgrs.objects;

import java.util.logging.Logger;
import mil.geotransj.Geodetic;
import mil.usmc.mgrs.MgrsHelper;
import mil.usmc.mgrs.MgrsXmlResource;

/**
 *
 * @author Alex Pastel
 */
public class Line {

    private Point m_start;
    private Point m_end;
    private Point m_lblPnt;
    private String m_lblName;
    private static MgrsHelper mgrsHelper = new MgrsHelper();
    private static final Logger LOGGER = Logger.getLogger(MgrsXmlResource.class.getName());

    public Line(Point start, Point end) {
        super();
        m_start = start;
        m_end = end;
        m_lblPnt = null;
        m_lblName = null;
    }

    public Line(Point start, Point end, Point label, String name) {
        super();
        m_start = start;
        m_end = end;
        m_lblPnt = label;
        m_lblName = name;
    }

    public Line(String mgrsStart, String mgrsEnd) {
        Geodetic startGeo = mgrsHelper.mgrsToGeo(mgrsStart);
        Geodetic endGeo = mgrsHelper.mgrsToGeo(mgrsEnd);

        if (startGeo == null || endGeo == null) { // Create blank line
            m_start = new Point();
            m_end = new Point();
        } else {
            m_start = new Point(startGeo.getLatitude(), startGeo.getLongitude());
            m_end = new Point(endGeo.getLatitude(), endGeo.getLongitude());
        }
        m_lblPnt = null;
        m_lblName = null;
    }

    public Line(String mgrsStart, String mgrsEnd, String mgrsLabel, String name) {
        Geodetic startGeo = mgrsHelper.mgrsToGeo(mgrsStart);
        Geodetic endGeo = mgrsHelper.mgrsToGeo(mgrsEnd);

        if (startGeo == null || endGeo == null) { // Create blank line
            m_start = new Point();
            m_end = new Point();
        } else {
            m_start = new Point(startGeo.getLatitude(), startGeo.getLongitude());
            m_end = new Point(endGeo.getLatitude(), endGeo.getLongitude());
        }
        Geodetic labelGeo = mgrsHelper.mgrsToGeo(mgrsLabel);
        m_lblPnt = new Point(labelGeo.getLatitude(), labelGeo.getLongitude());
        m_lblName = name;
    }

    public Line() {
        super();
        m_start = new Point();
        m_end = new Point();
        m_lblPnt = new Point();
        m_lblName = "";
    }

    public Point getStart() {
        return m_start;
    }

    public void setStart(Point start) {
        m_start = start;
    }

    public Point getEnd() {
        return m_end;
    }

    public void setEnd(Point end) {
        m_end = end;
    }

    public void setLabel(Point lblPoint, String lblName) {
        if (lblPoint.getLng() > 180) {
            lblPoint.setLng(lblPoint.getLng() - 360);
        }
        m_lblPnt = lblPoint;
        m_lblName = lblName;
    }

    public Point getLabelPoint() {
        return m_lblPnt;
    }

    public String getLabelName() {
        return m_lblName;
    }

    public Point calculateLabelPoint() {
        double lat = (m_end.getLat() + m_start.getLat()) / 2;
        double lng = (m_end.getLng() + m_start.getLng()) / 2;
        lat += 0.4929765363200005;
        return new Point(lat, lng);
    }

    @Override
    public String toString() {
        return "start = " + m_start.toString() + "\nend = " + m_end.toString();
    }
}
