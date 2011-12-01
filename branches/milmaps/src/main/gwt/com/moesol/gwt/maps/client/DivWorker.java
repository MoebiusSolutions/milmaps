package com.moesol.gwt.maps.client;

public class DivWorker {
	private GeodeticCoords m_geoCenter = new GeodeticCoords();
	private final DivCoords m_returnedDivCoords = new DivCoords();
	private final WorldCoords m_returnedWc = new WorldCoords();
	private final WorldCoords m_wc = new WorldCoords();
	private final WorldCoords m_divCenterWc = new WorldCoords(); // viewport center in wc.
	private final MapCoords m_divCenterMc = new MapCoords(); // viewport center in wc.
	private final DivDimensions m_dims = new DivDimensions(600,400);
	private double m_scale;
	private int m_offsetInWcX;
	private int m_offsetInWcY;
	//private ViewWorker m_vpWorker = null;
	
	private int m_mapLevel;
	
	public class BoxBounds {
		public double top;
		public double left;
		public double bot;
		public double right;
		
		@Override
		public String toString() {
			return "[" +" top: "+ top + ", left: "+ left +" bot: "+ bot + ", right: "+ right + "]";
		}
	}
	public BoxBounds m_boxBounds = new BoxBounds();
	
	public DivWorker(){
		
	}
	
	IProjection m_proj = null;
	
	public void setDivDimensions( DivDimensions dd ){ m_dims.copyFrom(dd); }
	public DivDimensions getDivDimension(){ return m_dims; }
	public void setPixelSize( int width, int height){
		m_dims.setWidth(width);
		m_dims.setHeight(height);
	}
	
	public void setProjection( IProjection p ){ m_proj = p; }
	public IProjection getProjection(){ return m_proj; }
	
	public void setMapLevel(int mapLevel) {
		this.m_mapLevel = mapLevel;
	}
	
	public int getMapLevel() {
		return m_mapLevel;
	}
	
	public void setScale(double scale){ m_scale = scale; }
	
	public double getScale(){ return m_scale; }
	
	public void setGeoCenter( GeodeticCoords gc ){
		m_geoCenter.copyFrom(gc);
	}
	
	public GeodeticCoords getGeoCenter(){
		return m_geoCenter;
	}
	
	public void setOffsetInWcX(int offsetInWcX) {
		this.m_offsetInWcX = offsetInWcX;
	}

	public int getOffsetInWcX() {
		return m_offsetInWcX;
	}
	
	public void setOffsetInWcY(int offsetInWcY) {
		this.m_offsetInWcY = offsetInWcY;
	}

	public int getOffsetInWcY() {
		return m_offsetInWcY;
	}
	
	public void update(boolean bUseGeoCenter){
		if ( bUseGeoCenter ){
			m_divCenterMc.copyFrom(m_proj.geodeticToMapCoords(m_geoCenter));
			m_divCenterWc.copyFrom(m_divCenterMc);
		}
		m_offsetInWcX = m_divCenterWc.getX()- m_dims.getWidth()/2;
		m_offsetInWcY = m_divCenterWc.getY()+ m_dims.getHeight()/2;
	}
	
	public void setDivCenterInWc( WorldCoords cent, boolean bCompOffsets) {
		m_divCenterMc.copyFrom(cent);
		m_divCenterWc.copyFrom(cent);
		if ( bCompOffsets ){
			computeOffsets();
		}
	}
	
	public void setDiv( GeodeticCoords gc ){
		m_geoCenter.copyFrom(gc);
		m_divCenterMc.copyFrom(m_proj.geodeticToMapCoords(m_geoCenter));
		m_divCenterWc.copyFrom(m_divCenterMc);
		computeOffsets();
	}
	
	public void computeOffsets(){
		m_offsetInWcX = m_divCenterWc.getX()- m_dims.getWidth()/2;
		m_offsetInWcY = m_divCenterWc.getY()+ m_dims.getHeight()/2;
	}
	
	public WorldCoords getDivCenterInWc() {
		return m_divCenterWc;
	}
	
	public void zoomByFactor( double factor ){
		m_divCenterMc.setX((m_divCenterWc.getX()*factor));
		m_divCenterMc.setY((m_divCenterWc.getY()*factor));
		m_divCenterWc.copyFrom(m_divCenterMc);
		m_dims.setWidth((int)(m_dims.getWidth()*factor + 0.5));
		m_dims.setHeight((int)(m_dims.getHeight()*factor + 0.5));
		m_offsetInWcX = m_divCenterWc.getX()- m_dims.getWidth()/2;
		m_offsetInWcY = m_divCenterWc.getY()+ m_dims.getHeight()/2;
	}
	
	public void szooomByScale( double scale ){
		double currentScale = m_proj.getScale();
		zoomByFactor(  scale/currentScale );
		
	}
	
	public int wcXtoDcX(int wcX ){
		return (wcX - m_offsetInWcX);
	}
	
	public int wcYtoDcY( int wcY ){
		// Normally we would have vY = wc.getY() - m_offsetInWcY.
		// But for the view y axis we want the y values changed to be relative to the 
		// div's top. So we will subtract dY from the div's top. This will also
		// flip the direction of the divs's y axis
		return (m_offsetInWcY - wcY);	// flip y axis	
	}
	
	/**
	 * wcToVC converts World coordinates to div coordinates based on the div's size
	 * and where the div's center sits in the world coordinate system.`
	 * @param wc
	 * @param checkWrap
	 * @return ViewCoords
	 */
	public DivCoords wcToDC( WorldCoords wc ) {
		m_returnedDivCoords.setX(wcXtoDcX(wc.getX()));
		m_returnedDivCoords.setY(wcYtoDcY(wc.getY())); 
		return m_returnedDivCoords;
	}
	/*
	public DivCoords wcToDC( WorldCoords wc ) {
		DivCoords r = m_returnedDivCoords;
		int dW2 = m_dims.getWidth()/2;// div width/2
		int dH2 = m_dims.getHeight()/2;// div height/2
		//m_wc.copyFrom( m_proj.getViewCenterInWC() );// view center in wc.
		int dX = wc.getX() - m_divCenterWc.getX() + dW2;
		r.setX(dX);
		// Normally we would have dY = (wc.getY() - m_divCentInWc.getY() + vH2).
		// But for the view y axis we want the y values changed to be relative to the 
		// div's top. So we will subtract vY from the view's top. This will also
		// flip the direction of the div's y axis
		int dY = m_dims.getHeight()- wc.getY() + m_divCenterWc.getY() - dH2;
		r.setY(dY); // flip y axis
		
		return r;
	}
	*/
	
	public int dcXtoWcX( int dcX ){
		return (dcX + m_offsetInWcX);
	}
	
	public int dcYtoWcY( int dcY ){
		return (m_offsetInWcY - dcY);
	}
	
	/**
	 * vcToWc converts div Coordinates to World Coordinates based on the div's size
	 * and where the div's center sits in the world coordinate system.
	 * @param vc
	 * @return
	 */
	public WorldCoords dcToWC( DivCoords dc ) {
		m_returnedWc.setX(dcXtoWcX(dc.getX()));
		m_returnedWc.setY(dcYtoWcY(dc.getY()));
		return m_returnedWc;
	}
	
	public ViewCoords divCtoViewC( ViewWorker vpWorker, DivCoords dc ){
		WorldCoords wc = dcToWC(dc);
		return vpWorker.wcToVC(wc);
	}
	
	/**
	 * dcXToPercent converts Div x pixel to percent position based on the div's size
	 * @param vc
	 * @return
	 */
	public double dcXToPercent( int dcX ) {
		double x = dcX;
		return x/m_dims.getWidth();
	}
	
	/**
	 * dcXToPercent converts Div x pixel to percent position based on the div's size
	 * @param vc
	 * @return
	 */
	public double dcYToPercent( int dcY ) {
		double y = dcY;
		return y/m_dims.getHeight();
	}
	
	/**
	 * computeTileBounds computes a given tiles percentage "top, left, bottom, right 
	 * position in a divPanel at its current state. 
	 * @param tileX
	 * @param tileY
	 * @param width
	 * @param height
	 * @return
	 */
	public BoxBounds computePerccentBounds ( int x, int y, int width, int height ){
		
		m_boxBounds.top   = dcYToPercent(y)*100;
		m_boxBounds.left  = dcXToPercent(x)*100;
		m_boxBounds.bot   = dcYToPercent(y+height)*100;
		m_boxBounds.right = dcXToPercent(x+width)*100;
		return m_boxBounds;
	}
	
	@Override
	public String toString() {
		return "[" +" ox: "+ m_offsetInWcX + ", oy: "+ m_offsetInWcY + "]";
	}

}
