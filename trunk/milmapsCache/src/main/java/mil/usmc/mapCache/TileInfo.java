package mil.usmc.mapCache;

public class TileInfo {
	boolean zeroTop;
	public int numXtiles;
	public int numYtiles;
	public int size;
	public double degWidth;
	public double degHeight;
	
	public TileInfo(boolean zeroTop, int numX, int numY, int size,
									 double degWidth, double degHeight){
		this.zeroTop   = zeroTop;
		this.numXtiles = numX;
		this.numYtiles = numY;
		this.size      = size;
		this.degWidth  = degWidth;
		this.degHeight = degHeight;	
	}
}
