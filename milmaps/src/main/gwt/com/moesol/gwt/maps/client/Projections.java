package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.units.Degrees;
import com.moesol.gwt.maps.shared.BoundingBox;

/**
 * Common computations needed for projections
 * @author hastings
 */
public class Projections {
	
	/**
	 * Search for the tightest scale where view port contains box. If box is currently visible in the view port
	 * then search in until box is not visible and return the last scale where box was still visible. If box
	 * is currently not visible in the view port, then search out until it is visible. Searching supports
	 * projections such as Mercator where the scale changes based on location.
	 * 
	 * @param viewPort ViewPort
	 * @param box BoundingBox to scale to
	 * @return projection scale that makes box visible in the viewport
	 */
	public static double findScaleFor(ViewPort viewPort, BoundingBox box) {
		IProjection oldProjection = viewPort.getProjection();
		IProjection tmp = oldProjection.cloneProj();
		tmp.setViewGeoCenter(Degrees.geodetic(box.getCenterLat(), box.getCenterLng() ));
		viewPort.setProjection(tmp); // Updates view port state
		try {
			GeodeticCoords upperLeft = Degrees.geodetic(box.getMaxLat(), box.getMinLon());
			GeodeticCoords lowerRight = Degrees.geodetic(box.getMinLat(), box.getMaxLon());
			// If both already fit then search by zooming in until they do not 
			boolean searchIn = viewPort.isInViewPort(upperLeft) && viewPort.isInViewPort(lowerRight);
			double searchMultiplier = searchIn ? 2.0 : 0.5;
			double lastScale = tmp.getScale();
			// Max search 64 iterations
			for (int i = 0; i < 64; i++) {
				tmp.setScale(tmp.getScale() * searchMultiplier);
				viewPort.setProjection(tmp);
				boolean nowInView = viewPort.isInViewPort(upperLeft) && viewPort.isInViewPort(lowerRight);
				if (searchIn != nowInView) {
					if (searchIn) {
						return lastScale; // Last scale was the last to still show the coordinates
					}
					return tmp.getScale(); // This scale is the first to show the coordinates
				}
				lastScale = tmp.getScale();
			}
			return oldProjection.getScale(); // Could not find what we were looking for return current scale.
		} finally {
			// We just mucked around with the view port state, restore it.
			viewPort.setProjection(oldProjection);
		}
	}
}
