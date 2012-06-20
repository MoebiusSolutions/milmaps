package mil.usmc.mapCache;

public class Projection {
    private static final double MinLatitude = -90;
    private static final double MaxLatitude = 90;
    private static final double MinLongitude = -180;
    private static final double MaxLongitude = 180;
    
    double m_scale;
    double m_deg;
    double m_pixWidth;
    
	public Projection(double deg, double pixWidth) {
		m_deg = deg;
		m_pixWidth = pixWidth;
		m_scale = pixWidth/deg;
	}  
	
	public double getScale(){ return m_scale; }
	
	public void setScale(double scale){m_scale = scale;}
	
	public int degToPix(double deg) {
		return (int)(deg*m_scale + 0.5);
	}
	
	public double pixToDeg(double pix) {
       return m_scale/pix;
	}

	public int lngToPixX(double lng){
		return degToPix(180+lng);
	}
	
	public int latToPixY(double lng){
		return degToPix(90+lng);
	}
	
	public R2 LatLngToPix(double lat, double lng) {
        int x = (int)lngToPixX(lng);
        int y = (int)latToPixY(lat);
        return new R2(x,y);
	}

	public Point xyPixelToLatLng(int x, int y) {
		double lat = -90.0 + pixToDeg(y);
		double lng = -180 + pixToDeg(x);
		return new Point(lat,lng);
	}
}
