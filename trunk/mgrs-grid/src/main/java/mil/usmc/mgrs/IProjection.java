package mil.usmc.mgrs;

import mil.usmc.mgrs.objects.Point;
import mil.usmc.mgrs.objects.R2;

public interface IProjection {
	
	public abstract void initialize( int tileSize );

	public abstract int lngDegToPixX( int level, double deg );
	
	public abstract double xPixToDegLng( int level, double pix ); 
	
	public abstract int latDegToPixY( int level, double deg );
	
	public abstract double yPixToDegLat( int level, double pix );
	
    public abstract R2 latLngToPixelXY( int levelOfDetail, double lat, double lng );
    
	public abstract Point xyPixelToLatLng( int levelOfDetail, int x, int y );

	public abstract R2 geoPosToTileXY( int level, double lat, double lng );
	
    public abstract R2 tileXYToTopLeftXY( int tileX, int tileY );
    
    public abstract int mapSize( int level );
}
