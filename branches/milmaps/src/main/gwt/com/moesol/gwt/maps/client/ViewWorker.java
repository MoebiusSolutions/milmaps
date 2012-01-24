package com.moesol.gwt.maps.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.moesol.gwt.maps.client.events.ProjectionChangedEvent;
import com.moesol.gwt.maps.client.events.ProjectionChangedHandler;

public class ViewWorker implements ProjectionChangedHandler {
	private ViewDimension m_dims = new ViewDimension(400, 600);
	private GeodeticCoords m_geoCenter = new GeodeticCoords(); // view-port center
	private int m_offsetInWcX;
	private int m_offsetInWcY;
	private IProjection m_proj = null;
	private HandlerRegistration m_projectionChangedHandlerRegistration;
	
	
	public void setProjection( IProjection p ) {
		m_proj = p;
	}
	
	public IProjection getProjection() { return m_proj; }
	
	public void intialize( ViewDimension vd, IProjection proj ) {
		 m_proj = proj;
		 m_dims = vd;
		 computeOffsets(m_proj.geodeticToWorld(m_geoCenter));
		 if (m_projectionChangedHandlerRegistration != null) {
			 m_projectionChangedHandlerRegistration.removeHandler();
		 }
		 m_projectionChangedHandlerRegistration = proj.addProjectionChangedHandler(this);
	}
	
	public void setDimension( ViewDimension d ) {
		m_dims = d;
		computeOffsets(getVpCenterInWc());
	}
	
	public ViewDimension getDimension() {
		return m_dims;
	}
	
	// TODO remove me, use word  coords only
	public void setGeoCenter( GeodeticCoords gc ) {
		m_geoCenter = gc;
		
		WorldCoords wc = m_proj.geodeticToWorld(m_geoCenter);
		computeOffsets(wc);
	}

	// TODO remove me, use word  coords only
	public GeodeticCoords getGeoCenter(){
		return m_geoCenter;
	}
	
	public void setCenterInWc(WorldCoords cent) {
		m_geoCenter = m_proj.worldToGeodetic(cent);
		computeOffsets(cent);
	}
	
	private void computeOffsets(WorldCoords wc) {
		m_offsetInWcX = wc.getX()- m_dims.getWidth()/2;
		m_offsetInWcY = wc.getY()+ m_dims.getHeight()/2;
	}
	
	public WorldCoords getVpCenterInWc() {
		return m_proj.geodeticToWorld(m_geoCenter);
	}
	
	public int getOffsetInWcX() {
		return m_offsetInWcX;
	}
	
	public int getOffsetInWcY() {
		return m_offsetInWcY;
	}
	
	public int wcXtoVcX(int wcX ){
		return (wcX - m_offsetInWcX);
	}
	
	public int wcYtoVcY( int wcY ){
		// Normally we would have vY = wc.getY() - m_offsetInWcY.
		// But for the view y axis we want the y values changed to be relative to the 
		// div's top. So we will subtract dY from the div's top. This will also
		// flip the direction of the divs's y axis
		return (m_offsetInWcY - wcY);	// flip y axis	
	}
	
	/**
	 * wcToVC converts World coordinates to view coordinates based on the view sizes
	 * and where the view's center sits in the world coordinate system.`
	 * @param wc
	 * @return ViewCoords
	 */
	public ViewCoords wcToVC( WorldCoords wc ) {
		return new ViewCoords(wcXtoVcX(wc.getX()), wcYtoVcY(wc.getY()));
	}
	
	public int vcXtoWcX( int vcX ){
		return (vcX + m_offsetInWcX);
	}
	
	public int vcYtoWcY( int vcY ){
		return (m_offsetInWcY - vcY);
	}
	
	
	/**
	 * vcToWc converts View Coordinates to World Coordinates based on the view sizes
	 * and where the view's center sits in the world coordinate system.
	 * @param vc
	 * @return
	 */
	public WorldCoords viewToWorld( ViewCoords vc ) {
		return new WorldCoords(vcXtoWcX(vc.getX()), vcYtoWcY(vc.getY()));
	}
	
	public DivCoords viewToDivCoords( DivWorker divWorker, ViewCoords vc ){
		WorldCoords wc = viewToWorld(vc);
		return divWorker.worldToDiv(wc);
	}

	@Override
	public void onProjectionChanged(ProjectionChangedEvent event) {
		computeOffsets(getVpCenterInWc());
	}
}
