package mil.usmc.mgrs;

import mil.usmc.mgrs.objects.Point;
import mil.usmc.mgrs.objects.R2;

public abstract class AbstractProjection implements IProjection {
	public static double RadToDeg = 57.29577951;
	public static double DegToRad = 0.017453293;
	public static int DOTS_PER_INCH = 96;
	
	protected double EarthCirMeters  = 2.0*Math.PI*IProjection.EARTH_RADIUS_METERS;
	protected double MeterPerDeg  = EarthCirMeters/360.0;
	
	protected int m_scrnDpi = DOTS_PER_INCH;   // screen dot per inch
	protected double m_scrnMpp = 2.54 / (m_scrnDpi * 100.0);
	protected int m_boxWidthInPix ;
	protected int m_boxHeightInPix;
	protected double m_eqScale;
	protected double m_baseEqScale;
	protected double m_centLng;
	
	protected int m_orgTilePixWidth;
	protected int m_orgTilePixHeight;
	protected double m_orgTileDegWidth;
	protected double m_orgTileDegHeight;
	
    protected Point m_geoPt = new Point();
    protected R2 m_pixel = new R2();
    protected R2 m_tile = new R2();
	
    protected double clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }
    
	// this is used for levels "multiple tiles"
	private void computebaseScale(){
		double earth_mpp = (m_orgTileDegWidth * MeterPerDeg)/m_orgTilePixWidth;
		// meters per pixel for physical screen
		m_scrnMpp = 2.54 / (m_scrnDpi * 100.0); 
		m_eqScale = (m_scrnMpp / earth_mpp);
		m_baseEqScale = m_eqScale;
	}
	
	public double wrapLng(double lng) {
		int k = (int)Math.abs((lng/360));
		if (lng > 180.0) {
			lng -= k*360;
			if (lng > 180.0)
				lng -= 360.0;
		} else if (lng < -180.0) {
			lng += k*360;
			if (lng < -180.0)
				lng += 360.0;
		}
		return lng;
	}
	
	// Map size in pixels
	protected double mapWidth() {
		return (EarthCirMeters*m_eqScale/m_scrnMpp);
	}
	
	@Override
	public void init(int tileSize, int level, int tileX, double degSize) {
		m_orgTilePixWidth = tileSize;
		m_orgTilePixHeight = tileSize;
		double degWidth = degSize/(1<<level);
		m_centLng = wrapLng(tileX*degWidth + degWidth/2.0);
		computebaseScale();
		m_eqScale = m_baseEqScale*(1<<level);
	}
	
	@Override
	public double initUsingBbox(int boxWidthInPix, int boxHeightInPix, WmsBoundingBox bbox) {
    	m_boxWidthInPix = boxWidthInPix;
		m_boxHeightInPix = boxHeightInPix;
		double degWidth = bbox.getLngDegSpan();
		m_centLng = wrapLng(bbox.left()+degWidth/2);
		double earth_mpp = (degWidth * MeterPerDeg)/boxWidthInPix;
		// meters per pixel for physical screen 
		m_eqScale = (m_scrnMpp / earth_mpp);
		m_baseEqScale = m_eqScale;
		return m_eqScale;
	}
	
    @Override
    public int mapWidthInPix() {
    	double width = mapWidth();
    	return (int)(width+0.5);
    }
	
	public int findLevel(double dpi, double projScale) {
		double mpp = 2.54 / (dpi * 100); // meters per pixel for physical screen
		double m_dx = m_orgTilePixWidth;
		double l_mpp = 180 * (111120.0 / m_dx);
		// compute the best level.
		if ( projScale == 0.0 ){
			projScale = (mpp / l_mpp);
		}
		double logMess = Math.log(projScale) + Math.log(l_mpp)
				- Math.log(mpp);
		double dN = logMess / Math.log(2);
		return (int)(Math.rint(dN)) + 1;
	}
}
