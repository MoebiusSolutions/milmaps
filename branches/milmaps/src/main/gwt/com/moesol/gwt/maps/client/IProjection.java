package com.moesol.gwt.maps.client;

public interface IProjection {
	public enum ZoomFlag {
		OUT, NONE, IN
	}
	
	public enum T {
		CylEquiDist, Mercator
	}
	
	/**
	 * Set the projection type. Example of type is Mercator
	 * @param type
	 */
	public abstract void setType( IProjection.T type );
	
	/**
	 * Gets the projection type. Example of type is Mercator
	 * @return IProjection.T
	 */
	public abstract IProjection.T getType();
	
	/**
	 * initialize the scale using a cell size
	 * @param tileWidth : in pixels
	 * @param degWidth of tile.
	 */
	public abstract void initialize( int tileWidth, double degWidth, double degHeight );
	
	/**
	 * Synchronize the original tile data.
	 * This method does not effect the current scale.
	 * @param tileWidth : in pixels
	 * @param degWidth of tile.
	 */
	//public abstract void synchronize( int tileWidth, double degWidth, double degHeight );
	
	/**
	 * Checks that the projection will support the espg associated with the layerset
	 * @param espg
	 * @return true or false
	 */
	public abstract boolean doesSupport( int espg );
	
	/**
	 * Clone projection
	 */
	public abstract IProjection cloneProj();
	
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
	
	public abstract void setGeoBounds(double minLat, double minLng, 
									  double maxLat, double maxLng );
	public abstract double getMinLat();
	public abstract double getMaxLat();
	public abstract double getMinLng();
	public abstract double getMaxLng();

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
	 * Converts a world coordinate position to a geodetic position
	 * @param w: world coordinate position.
	 * @return double
	 */
	public abstract GeodeticCoords mapCoordsToGeodetic(MapCoords w);


	/**
	 * Converts a geodetic position to a world coordinate position
	 * @param g: geodetic position
	 * @return GeodeticCoords
	 */
	public abstract MapCoords geodeticToMapCoords(GeodeticCoords g);


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
	
	/**
	 * Returns the original scale that is computed when the projection 
	 * is initialized with the layer data.
	 */
	public abstract double getOrigScale();
	
	
	/**
	 * compWidthInPixels : computes the number of pixels between two longitudes.
	 * @param lng1
	 * @param lng2
	 * @return int, the number of pixels
	 */
	public abstract int compWidthInPixels(double lng1, double lng2);
	
	/**
	 * compHeightInPixels: computes the number of pixels between two latitudes
	 * @param lat1
	 * @param lat2
	 * @return int, the number of pixels
	 */
	public abstract int compHeightInPixels(double lat1, double lat2);

}