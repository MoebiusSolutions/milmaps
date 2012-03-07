package mil.usmc.mgrs;

import mil.usmc.mgrs.objects.Point;
import mil.usmc.mgrs.objects.R2;

public interface IProjection {
	public static double EARTH_RADIUS_METERS = 6378137; // meters
	
	public abstract void init(int tileSize, int level, int tileX, double degSize);
	
	public abstract double initUsingBbox(int pixWidth, int pixHeight, WmsBoundingBox bbox);
	
	public abstract int mapWidthInPix();
	
	public abstract int mapHeightInPix();
	
	public  abstract double groundResolution(double latitude);
	
	public abstract int lngDegToPixX(double deg);
	
	public abstract double xPixToDegLng(double pix); 
	
	public abstract int latDegToPixY(double deg);
	
	public abstract double yPixToDegLat(double pix);
	
    public abstract R2 latLngToPixelXY(double lat, double lng);
    
	public abstract Point xyPixelToLatLng(int x, int y);
	
	public abstract R2 geoPosToTileXY(double lat, double lng);
	
    public abstract R2 tileXYToTopLeftXY(int tileX, int tileY);
}
