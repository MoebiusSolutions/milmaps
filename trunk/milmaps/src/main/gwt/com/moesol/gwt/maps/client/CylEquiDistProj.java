package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.units.AngleUnit;


/**
 * Handles the conversion of geodetic coordinates to world coordinates. The
 * world is an image of the earth with pixel 0,0 at -180,-90. If the zoom factor
 * goes negative fileTile will return a tile whose width and height have been
 * scaled.
 * 
 * @author Moebius Solutions, Inc.
 */
public class CylEquiDistProj extends AbstractProjection {

	// 111120 meters per degree on the equator.
	public CylEquiDistProj() {
		super();
	}

	public CylEquiDistProj(int dpi) {
		super(dpi);
	}

	/**
	 * Copy state from <code>orig</code>
	 * 
	 * @param orig
	 */
	public CylEquiDistProj(IProjection orig) {
		copyFrom(orig);
	}
	
	@Override
	public double pixToLngDegMag(int pix){
		return ((pix * m_scrnMpp / m_scale) / 111120.0);
	}

	@Override
	public int lngDegToPixMag(double deg){
		return (int)( (m_scale * (deg * 111120.0) / m_scrnMpp) + 0.5 );
	}
	
	
	@Override
	public double pixToLatDegMag(int centY, int pix){
		return ((pix * m_scrnMpp / m_scale) / 111120.0);
	}
	

	@Override
	public int latDegToPixMag(double centLat, double deg){
		return (int)( (m_scale * (deg * 111120.0) / m_scrnMpp) + 0.5 );
	}


	@Override
	public WorldDimension getWorldDimension() {
		int wdX = (int) (m_scale * (360 * 111120.0) / m_scrnMpp + 0.5);
		int wdY = (int) (m_scale * (180 * 111120.0) / m_scrnMpp + 0.5);

		m_wdSize.setWidth(wdX);
		m_wdSize.setHeight(wdY);
		return m_wdSize;
	}
	
	@Override
	public void setViewSize(ViewDimension vp) {
		m_vpSize.copyFrom(vp);
		m_wholeWorldScale = Math.max( computeScale(2*m_maxLng, m_vpSize.getWidth()),
				  					  computeScale(2*m_maxLat, m_vpSize.getHeight()) );
	}
	
	@Override
	public GeodeticCoords worldToGeodetic(WorldCoords w) {
		// we have to mod by world size in case we had
		// to extend the whole world
		double lng = pixToLngDegMag(w.getX() % m_wdSize.getWidth()) - 180.0;
		if (w.getX() > 10 && lng == m_minLng)
			lng = m_maxLng;
		double lat = pixToLatDegMag(0,w.getY()) + m_minLat;
		m_returnedGeodeticCoords.set(lng, lat, AngleUnit.DEGREES);
		return m_returnedGeodeticCoords;
	}
	

	@Override
	public WorldCoords geodeticToWorld(GeodeticCoords g) {
		m_returnedWorldCoords
				.setX(lngDegToPixMag(g.getLambda(AngleUnit.DEGREES) + m_maxLng));
		m_returnedWorldCoords
				.setY(latDegToPixMag(0,g.getPhi(AngleUnit.DEGREES) + m_maxLat));
		return m_returnedWorldCoords;
	}

	protected double computeScale(double deg, int pix) {
		double mpp = deg*(111120.0 / pix);
		return (m_scrnMpp / mpp);
	}
}

