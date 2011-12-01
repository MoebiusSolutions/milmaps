package com.moesol.gwt.maps.client;

public class ViewWorker {
	private final ViewDimension m_dims = new ViewDimension(1000, 800);
	private final ViewCoords m_returnedViewCoords = new ViewCoords();
	private final WorldCoords m_returnedWc = new WorldCoords();
	private final WorldCoords m_wc = new WorldCoords();
	private final GeodeticCoords m_geoCenter = new GeodeticCoords(); // view-port center
	private final WorldCoords m_vpCenterWc = new WorldCoords(); // viewport center in wc.
	private int m_offsetInWcX;
	private int m_offsetInWcY;
	
	IProjection m_proj = null;
	
	public IProjection getProjection(){ return m_proj; }
	
	public void intialize( ViewDimension vd, IProjection proj ){
		 m_proj = proj;
		 m_dims.copyFrom(vd);
	}
	
	public void setDimension( ViewDimension d ){
		m_dims.copyFrom(d);
	}
	
	public ViewDimension getDimension(){
		return m_dims;
	}
	
	public void setGeoCenter( GeodeticCoords gc ){
		m_geoCenter.copyFrom(gc);
	}
	
	public GeodeticCoords getGeoCenter(){
		return m_geoCenter;
	}
	
	public void update(boolean bUseGeoCenter){
		if ( bUseGeoCenter ){
			m_vpCenterWc.copyFrom(m_proj.geodeticToMapCoords(m_geoCenter));
		}
		m_offsetInWcX = m_vpCenterWc.getX()- m_dims.getWidth()/2;
		m_offsetInWcY = m_vpCenterWc.getY()+ m_dims.getHeight()/2;
	}
	
	/**
	 * computeGeoCenter: computes the view's geoCenter
	 * from the view's stored center in Wcs
	 * @return
	 */
	public GeodeticCoords computeGeoCenter(){
		m_geoCenter.copyFrom(m_proj.worldToGeodetic(m_vpCenterWc));
		return m_geoCenter;
	}
	
	public void setVpCenterInWc( WorldCoords cent) {
		m_vpCenterWc.copyFrom(cent);
		m_offsetInWcX = cent.getX()- m_dims.getWidth()/2;
		m_offsetInWcY = cent.getY()+ m_dims.getHeight()/2;
	}
	
	public WorldCoords getVpCenterInWc() {
		return m_vpCenterWc;
	}
	
	public void setOffsetInWcX(int offsetInWcX) {
		m_offsetInWcX = offsetInWcX;
	}

	public int getOffsetInWcX() {
		return m_offsetInWcX;
	}
	
	public void setOffsetInWcY(int offsetInWcY) {
		m_offsetInWcY = offsetInWcY;
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
	 * @param checkWrap
	 * @return ViewCoords
	 */
	public ViewCoords wcToVC( WorldCoords wc ) {
		m_returnedViewCoords.setX(wcXtoVcX(wc.getX()));
		m_returnedViewCoords.setY(wcYtoVcY(wc.getY()));
		return m_returnedViewCoords;
	}
	/*
	public ViewCoords wcToVC( WorldCoords wc ) {
		ViewCoords r = m_returnedViewCoords;
		int vW2 = m_dims.getWidth()/2;// view width/2
		int vH2 = m_dims.getHeight()/2;// view height/2
		//m_wc.copyFrom( m_proj.getViewCenterInWC() );// view center in wc.
		int vX = wc.getX() - m_vpCenterWc.getX() + vW2;
		r.setX(vX);
		// Normally we would have vY = (wc.getY() - m_viewCentInWc.getY() + vH2).
		// But for the view y axis we want the y values changed to be relative to the 
		// view's top. So we will subtract vY from the view's top. This will also
		// flip the direction of the view's y axis
		int vY = m_dims.getHeight()- wc.getY() + m_vpCenterWc.getY() - vH2;
		r.setY(vY); // flip y axis
		
		return r;
	}
	*/
	
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
	public WorldCoords vcToWC( ViewCoords vc ) {
		m_returnedWc.setX(vcXtoWcX(vc.getX()));
		m_returnedWc.setY(vcYtoWcY(vc.getY()));
		return m_returnedWc;
	}
	
	public DivCoords viewCtoDivC( DivWorker divWorker, ViewCoords vc ){
		WorldCoords wc = vcToWC(vc);
		return divWorker.wcToDC(wc);
	}
}
