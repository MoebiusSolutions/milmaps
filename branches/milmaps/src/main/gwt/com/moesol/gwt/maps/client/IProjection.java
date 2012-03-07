package com.moesol.gwt.maps.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.moesol.gwt.maps.client.events.ProjectionChangedHandler;
import com.moesol.gwt.maps.shared.BoundingBox;

public interface IProjection {
	public static double EARTH_RADIUS_MEERS = 6378137; // meters
	
	public enum T {
		CylEquiDist, Mercator
	}
	
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
	 * Checks that the projection will support the espg associated with the layerset
	 * @param espg
	 * @return true or false
	 */
	public abstract boolean doesSupport( String srs );
	
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

	BoundingBox getDegreeBoundingBox();

	/**
	 * sets the decimal equatorial scale of the projection
	 * @param dScale
	 */
	public abstract void setEquatorialScale(double dScale);

	/**
	 * gets the decimal equatorial scale of the projection
	 * @return
	 */
	public abstract double getEquatorialScale();

	/**
	 * This routine is used to zoom the map by a factor of the current scale.
	 * @param scaleFactor
	 */
	public abstract void zoomByFactor(double scaleFactor);
	
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
	 * Returns the original scale that is computed when the projection 
	 * is initialized with the layer data.
	 */
	public abstract double getBaseEquatorialScale();
	
	
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
	
	/**
	 * getLevelFromScale: computes the level using the original scale 
	 * as a reference value.
	 * @param eqScale
	 * @param roundValue
	 * @return int, level corresponding to the scale, may be negative.
	 */
	int getLevelFromScale(double eqScale, double roundValue );
	
	/**
	 * getScaleFromLevel
	 * @param level
	 * @return double, the scale for the given level.
	 */
	double getScaleFromLevel(int level);
	
	/**
	 * whole world map width in pixels
	 * @return
	 */
	public abstract int iMapWidth();
	
	/**
	 * wrapLng keeps the longitude between -180 and 180.
	 * @param lng
	 * @return
	 */
	public abstract double wrapLng(double lng);

	/**
	 * Register a projection changed handler
	 * @param handler
	 * @return HandlerRegistration
	 */
	HandlerRegistration addProjectionChangedHandler(ProjectionChangedHandler handler);
}