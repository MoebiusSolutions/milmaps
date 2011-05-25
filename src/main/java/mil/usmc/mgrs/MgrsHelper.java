package mil.usmc.mgrs;

import java.util.logging.*;
import mil.geotransj.*;

public class MgrsHelper extends Mgrs {

    private static final Logger LOGGER = Logger.getLogger(MgrsHelper.class.getName());
    private int m_gridZone;
    private char m_gridRow;
    private char m_gridSquareColumn;
    private char m_gridSquareRow;
    private int m_easting;
    private int m_northing;

    public int getGridZone() {
        return m_gridZone;
    }

    public void setGridZone(int gridZone) {
        m_gridZone = gridZone;
    }

    public char getGridRow() {
        return m_gridRow;
    }

    public void setGridRow(char gridRow) {
        m_gridRow = gridRow;
    }

    public char getGridSquareColumn() {
        return m_gridSquareColumn;
    }

    public void setGridSquareColumn(char gridSquareColumn) {
        m_gridSquareColumn = gridSquareColumn;
    }

    public char getGridSquareRow() {
        return m_gridSquareRow;
    }

    public void setGridSquareRow(char gridSquareRow) {
        m_gridSquareRow = gridSquareRow;
    }

    public int getEasting() {
        return m_easting;
    }

    public void setEasting(int easting) {
        m_easting = easting;
    }

    public int getNorthing() {
        return m_northing;
    }

    public void setNorthing(int northing) {
        m_northing = northing;
    }

    public void setMgrs(String mgrs) {
        try {
            String cs = mgrs.substring(0, 2);
            m_gridZone = Integer.parseInt(cs);
            m_gridRow = mgrs.charAt(2);
            m_gridSquareColumn = mgrs.charAt(3);
            m_gridSquareRow = mgrs.charAt(4);
            cs = mgrs.substring(5, 10);
            m_easting = Integer.parseInt(cs);
            cs = mgrs.substring(10, 15);
            m_northing = Integer.parseInt(cs);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "failed", e);
        }
    }

    /**
     * Check if an MGRS string is valid by attempting to convert it to Geodetic.
     * If there is no resulting Geodetic, then the MGRS string is invalid.
     * @param mgrs The MGRS string to check for validity.
     * @return True if the MGRS string is valid, false if it is invalid.
     */
    public boolean isValid(String mgrs) {
        Geodetic geo;
        try {
            geo = this.MgrsToGeodetic(mgrs);
            if (geo != null) {
                return true;
            } else {
                return false;
            }
        } catch (GeoTransException e) {
            return false;
        }
    }

    /**
     * Wrapper around geotransj.MgrsToGeodetic that returns null if there was
     * a conversion error.
     * @param mgrs The MGRS string to convert
     * @return The new Geodetic object, or null.
     */
    public Geodetic mgrsToGeo(String mgrs) {
        try {
            return this.MgrsToGeodetic(mgrs);
        } catch (GeoTransException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Wrapper around geotransj.geodeticToMgrs that returns null if there was
     * a conversion error.
     * @param lat
     * @param lng
     * @param precision
     * @return The MGRS String, or null.
     */
    public String geoToMgrs(double lat, double lng, int precision) {
        try {
            return this.geodeticToMgrs(lat, lng, precision);
        } catch (GeoTransException ex) {
            LOGGER.log(Level.SEVERE, "failed", ex);
            return null;
        }
    }

    /**
     * Wrapper around geotransj.Get_Grid_Values which returns the letter range
     * used for the 2nd letter in the MGRS coordinate string, based on the set
     * number of the utm zone.
     * @param zone The UTM zone in which the letter range will be determined.
     * @return An integer array containing the ASCII values of the low and high
     * rows.
     */
    public int[] getMgrsLetterRange(int zone) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int[] ltr2_low_value = new int[1];
        int[] ltr2_high_value = new int[1];
        double[] false_northing = new double[1];
        this.Get_Grid_Values(zone, ltr2_low_value, ltr2_high_value, false_northing);

        int low = ltr2_low_value[0];
        int high = ltr2_high_value[0];
        low = alphabet.charAt(low);
        high = alphabet.charAt(high);
        int[] range = new int[]{low, high};
        return range;
    }

    public char getNextGridSquareColumn() {
        int charValue = m_gridSquareColumn;

        ++charValue;  // normal case, get next letter
        if (charValue == 'I' || charValue == 'O') { // No 'I' or 'O' allowed in GridSquareColumn
            ++charValue;
        }
        return (char) charValue;
    }

    public char getNextGridSquareRow() {
        int charValue = m_gridSquareRow;

        if (charValue == 'V') { // 'V' is the last letter used in GridSquareRow
            charValue = 'A';
        } else {
            ++charValue;  // normal case, get next letter
            if (charValue == 'I' || charValue == 'O') { // No 'I' or 'O' allowed in GridSquareRow
                ++charValue;
            }
        }
        return (char) charValue;
    }

    public char getPreviousGridRow() {
        int charValue = m_gridRow;
        if (m_gridRow == 'J' || m_gridRow == 'P') {
            charValue -= 2;
        } else {
            charValue -= 1;
        }
        return (char) charValue;
    }

    public char getPreviousGridSquareColumn() {
        int charValue = m_gridSquareColumn;
        if (charValue == 'Z') {
            charValue = 'A';
        } else if (charValue == 'J' || charValue == 'P') {
            charValue -= 2;
        } else {
            charValue -= 1;
        }
        return (char) charValue;
    }

    public char getPreviousGridSquareRow() {
        int charValue = m_gridSquareRow;
        if (charValue == 'A') {
            charValue = 'V';
        } else if (charValue == 'J' || charValue == 'P') {
            charValue -= 2;
        } else {
            charValue -= 1;
        }
        return (char) charValue;
    }

    @Override
    public String toString() {
        return String.format("%02d%c%c%c%05d%05d",
                getGridZone(), getGridRow(), getGridSquareColumn(), getGridSquareRow(),
                getEasting(), getNorthing());
    }
}
