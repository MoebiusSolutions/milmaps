package mil.usmc.mgrs.objects;



import java.util.ArrayList;

/**
 *
 * @author Alex Pastel
 */
public class Grid {

    private ArrayList<Line> m_latLines;
    private ArrayList<Line> m_lngLines;
    private ArrayList<GridCell> m_gridCells;

    public Grid(ArrayList<Line> latLines, ArrayList<Line> lonLines) {
        super();
        m_latLines = latLines;
        m_lngLines = lonLines;
    }

    public Grid(ArrayList<GridCell> gridCells) {
        super();
        m_latLines = new ArrayList<Line>();
        m_lngLines = new ArrayList<Line>();
        m_gridCells = gridCells;

        for (GridCell gridCell : gridCells) {
            for (Line latLine : gridCell.getLatLines()) {
                m_latLines.add(latLine);
            }
            for (Line lngLine : gridCell.getLngLines()) {
                m_lngLines.add(lngLine);
            }
        }
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

    public void setLngLines(ArrayList<Line> lonLines) {
        m_lngLines = lonLines;
    }

    public int getNumTotalLines() {
        return m_latLines.size() + m_lngLines.size();
    }

    public Line getLatLineAt(int index) {
        return m_latLines.get(index);
    }

    public Line getLngLineAt(int index) {
        return m_lngLines.get(index);
    }

    public ArrayList<GridCell> getGridCells() {
        return m_gridCells;
    }
}
