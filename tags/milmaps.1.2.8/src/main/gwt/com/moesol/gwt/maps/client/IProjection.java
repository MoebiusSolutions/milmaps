package com.moesol.gwt.maps.client;

public interface IProjection {
	public enum ZoomFlag {
		OUT, NONE, IN
	}
	
	/**
	 * initialize the scale using a cell size
	 * @param tileWidth : in pixels
	 * @param degWidth of tile.
	 */
	public abstract void initialize( int tileWidth, double degWidth, double degHeight );
	
	/**
	 * Copies information from one projection to the next.
	 * @param orig
	 */
	public abstract void copyFrom(IProjection orig);
	
	/**
	 * returns the screen's dots (or pixels) per inch.
	 * @return
	 */
	public abstract int getScrnDpi();

	/**
	 * sets the decimal scale of the projection
	 * @param dScale
	 */
	public abstract void setScale(double dScale);

	/**
	 * sets the decimal scale of the projection
	 * @return
	 */
	public abstract double getScale();

	/**
	 * returns the previous scale when changed by a zoom or set scale
	 * @return
	 */
	public abstract double getPrevScale();

	/**
	 * sets the zoom in, out or none flag
	 * @param flag
	 */
	public abstract void setZoomFlag(ZoomFlag flag);

	/**
	 * returns the zoom flag.
	 * @return
	 */
	public abstract ZoomFlag getZoomFlag();

	/**
	 * Sets the viewport's center in geodectic coordinates
	 * @param c
	 */
	public abstract void setViewGeoCenter(GeodeticCoords c);

	/**
	 * sets the center of the view to geodetic values based on view pixels.
	 * This routine is used to re-center the view to a location from view pixels.
	 * @param vc
	 */
	public abstract void setCenterFromViewPixel(ViewCoords vc);

	/**
	 * This routine is used when zooming and you want to keep a particular
	 * geo-position on top of a particular pixel.
	 * @param gc
	 * @param vc
	 */
	public abstract void tagPositionToPixel(GeodeticCoords gc, ViewCoords vc);

	/**
	 * returns the viewport's geo-center.
	 * @return
	 */
	public abstract GeodeticCoords getVpGeoCenter();

	/**
	 * Returns the center of the viewPort in World Coordinates
	 * @return
	 */
	public abstract WorldCoords getWcCenter();

	/**
	 * This routine is used to zoom the map to a particular scale.
	 * @param scale
	 */
	public abstract void zoom(double scale);

	/**
	 * This routine is used to zoom the map by a factor of the current scale.
	 * @param scaleFactor
	 */
	public abstract void zoomByFactor(double scaleFactor);
	
	
	public abstract int lngDegToPixX( double deg );
	
	public abstract double xPixToDegLng( double pix );
	
	public abstract int latDegToPixY( double deg );
	
	public abstract double yPixToDegLat( double pix );

	/**
	 * This routine is used to handle the -180 and 180 boundary.
	 * For example 182 degrees is would be converted to -178 degrees
	 * @param lng
	 * @return
	 */
	public abstract double wrapLng(double lng);

	/**
	 * Converts a world coordinate position to a geodetic position
	 * @param w: world coordinate position.
	 * @return double
	 */
	public abstract GeodeticCoords worldToGeodetic(WorldCoords w);

	/**
	 * Converts a geodetic position to a world coordinate position
	 * @param g: geodetic position
	 * @return GeodeticCoords
	 */
	public abstract WorldCoords geodeticToWorld(GeodeticCoords g);

	/**
	 * Converts a position in view coordinate to a geodetic position
	 * @param v : view coordinate position.
	 * @return WorldCoords
	 */
	public abstract GeodeticCoords viewToGeodetic(ViewCoords v);

	/**
	 * Converts a position in geodetic coordinates to a position in view coordinates
	 * @param g : geodetic position.
	 * @return GeodeticCoords
	 */
	//public abstract ViewCoords geodeticToView(GeodeticCoords g);

	/**
	 * This routine returns the size (dimensions) of the whole earth map
	 * @return WorldDimension
	 */
	public abstract WorldDimension getWorldDimension();

	/**
	 * This routine is used to set the size of the view port in pixels.
	 * @param vp: ViewDimentsions
	 */
	public abstract void setViewSize(ViewDimension vp);
	
	/**
	 * This routine returns the size (dimensions) of the view port
	 * @return ViewDimension
	 */
	public abstract ViewDimension getViewSize();
	
	
	/**
	 * Returns the number of horizontal tiles for whole world
	 * 
	 * * @param tileDegWidth
	 *   The width of the tile in degrees. Should be 180/(2^n)
	 *   
	 * @return 
	 * 		integer number of tiles
	 */
	public abstract int getNumXtiles( double tileDegWidth );
	
	/**
	 * Returns the number of vertical tiles for whole world
	 * 
	 * * @param tileDegWidth
	 *   The width of the tile in degrees. Should be 180/(2^n)
	 *   
	 * @return
	 * 		integer number of tiles
	 */
	public abstract int getNumYtiles( double tileDegWidth );
	
	/**
	 * geoPosToTileXY() converts world coordinates to tile coordinates
	 * 
	 * * @param levelOfDetail
	 * 		map level
	 * @param g
	 * 		GeodeticCoords structure.
	 * @return
	 * 		TileXY based on the Microsoft way of doing things.
	 *  see: http://msdn.microsoft.com/en-us/library/bb259689.aspx
	 */
	public abstract TileXY geoPosToTileXY( int level, GeodeticCoords g );
	
	
	public abstract WorldCoords tileXYToTopLeftXY(  int level, TileXY tile   );
	
	public abstract int adjustSize( int level, int size );
	
	public abstract int compWidthInPixels(double lng1, double lng2);
	
	public abstract int compHeightInPixels(double lat1, double lat2);

}