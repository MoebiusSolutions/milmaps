package mil.usmc.mgrs.milGrid;

public class UtmLocSize {
	public double dLowerLat  = 0;
	public double dLeftLng   = 0;
	public double dOriginLng = 0;
	public int    iWidth  = 0;
	public int    iHeight = 0;

	public void copy( UtmLocSize  ls ){
		dLowerLat = ls.dLowerLat;
	    dLeftLng  = ls.dLeftLng;
	    dOriginLng  = ls.dOriginLng;
	    iWidth    = ls.iWidth;
	    iHeight   = ls.iHeight;
	}
}
