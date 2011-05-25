package mil.usmc.mgrs.objects;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;

/**
 *
 * @author Alex
 */
public class GridCell {

    public Point m_sw;
    public Point m_se;
    public Point m_ne;
    public Point m_nw;
    private ArrayList<Line> m_latLines;
    private ArrayList<Line> m_lngLines;

    public GridCell(Point sw, Point se, Point ne, Point nw) {
        Line south = new Line(sw, se);
        Line east = new Line(se, ne);
        Line north = new Line(ne, nw);
        Line west = new Line(nw, sw);

        m_latLines = new ArrayList<Line>();
        m_lngLines = new ArrayList<Line>();
        m_sw = sw;
        m_se = se;
        m_ne = ne;
        m_nw = nw;
        m_latLines.add(south);
        m_latLines.add(north);
        m_lngLines.add(west);
        m_lngLines.add(east);
    }

    public ArrayList<Line> getLatLines() {
        return m_latLines;
    }

    public void setLatLines(ArrayList<Line> latLines) {
        m_latLines = latLines;
    }

    public ArrayList<Line> getLngLines() {
        return m_lngLines;
    }

    public void setLngLines(ArrayList<Line> lngLines) {
        m_lngLines = lngLines;
    }

    public void setLabel(Point point, String name) {
        m_latLines.get(0).setLabel(point, name);
    }

    public String getLabelName() {
        return m_latLines.get(0).getLabelName();
    }

    public int getZone() {
        return Integer.parseInt(getLabelName().substring(0, 2));
    }

    /**
     * Return true if cell is the full size of one MGRS zone.
     */
    public boolean isFullSized() {
        int latSpan = (int) (m_nw.getLat() - m_sw.getLat());
        int lngSpan = (int) (m_ne.getLng() - m_nw.getLng());
        boolean vertical = (latSpan == 8);
        boolean horizontal = (lngSpan == 6);
        
        return vertical && horizontal;
    }
}
