package com.moesol.gwt.maps.client;

/**
 * DivWorker class is used to help handle all tile placements. It works with just one projection
 * scale and original dimension. It should be used in conjunction with tile builder.
 * @author User
 *
 */
public class DivWorker {
	private GeodeticCoords m_geoCenter = new GeodeticCoords();
	private final DivCoords m_returnedDivCoords = new DivCoords();
	private final WorldCoords m_wc = new WorldCoords();
	private final WorldCoords m_divCenterWc = new WorldCoords(); // viewport center in wc.
	private final MapCoords m_divCenterMc = new MapCoords(); // viewport center in wc.
	private final DivDimensions m_baseDims = new DivDimensions();
	private final PixelXY m_topLeft = new PixelXY();
	private double m_eqScale;
	private double m_offsetInMcX;
	private double m_offsetInMcY;
	IProjection m_proj = null;
	
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
	
	public void copyFrom( DivWorker dw ){
		setDivBaseDimensions(dw.getDivBaseDimensions());
		setGeoCenter(dw.getGeoCenter());
		setDivCentInWc(dw.getDivCenterInWc(),true);
	}
	
	public void setDivBaseDimensions( DivDimensions dd ){ m_baseDims.copyFrom(dd); }
	public DivDimensions getDivBaseDimensions(){ return m_baseDims; }
	
	public void setDivBasePixelSize( int width, int height){
		m_baseDims.setWidth(width);
		m_baseDims.setHeight(height);
	}
	
	public void setProjection( IProjection p ){ 
		m_proj = p; 
	}
	
	public IProjection getProjection(){ return m_proj; }
	
	public void setMapLevel(int mapLevel) {
		this.m_mapLevel = mapLevel;
	}
	
	public int getMapLevel() {
		return m_mapLevel;
	}
	
	public void setEqScale(double scale){ 
		m_eqScale = scale; 
	}
	
	public double getEqScale(){ return m_eqScale; }
	
	public void setGeoCenter( GeodeticCoords gc ){
		m_geoCenter.copyFrom(gc);
	}
	
	public GeodeticCoords getGeoCenter(){
		return m_geoCenter;
	}
	
	public void setOffsetInWcX(int offsetInWcX) {
		this.m_offsetInMcX = offsetInWcX;
	}

	public int getOffsetInWcX() {
		return (int) Math.rint(m_offsetInMcX);
	}
	
	public void setOffsetInWcY(int offsetInWcY) {
		this.m_offsetInMcY = offsetInWcY;
	}

	public int getOffsetInWcY() {
		return (int) Math.rint(m_offsetInMcY);
	}
	
	public void update(boolean bUseGeoCenter){
		if ( bUseGeoCenter ){
			m_divCenterMc.copyFrom(m_proj.geodeticToMapCoords(m_geoCenter));
			m_divCenterWc.copyFrom(m_divCenterMc);
		}
		m_offsetInMcX = m_divCenterMc.getX()- m_baseDims.getWidth()/2;
		m_offsetInMcY = m_divCenterMc.getY()+ m_baseDims.getHeight()/2;
	}
	
	public void setDivCentInWc( WorldCoords cent, boolean bCompOffsets) {
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
		m_offsetInMcX = m_divCenterMc.getX()- m_baseDims.getWidth()/2;
		m_offsetInMcY = m_divCenterMc.getY()+ m_baseDims.getHeight()/2;
	}
	
	public WorldCoords getDivCenterInWc() {
		return m_divCenterWc;
	}

	public int wcXtoDcX(int wcX ){
		return (int) Math.rint((wcX - m_offsetInMcX));
	}
	
	public int wcYtoDcY( int wcY ){
		// Normally we would have vY = wc.getY() - m_offsetInWcY.
		// But for the view y axis we want the y values changed to be relative to the 
		// div's top. So we will subtract dY from the div's top. This will also
		// flip the direction of the divs's y axis
		return (int) Math.rint((m_offsetInMcY - wcY));	// flip y axis	
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
	
	public DivCoords wcToDC( int wcX, int wcY ) {
		m_returnedDivCoords.setX(wcXtoDcX(wcX));
		m_returnedDivCoords.setY(wcYtoDcY(wcY)); 
		return m_returnedDivCoords;
	}
	
	public int dcXtoWcX( int dcX ){
		return (int) Math.rint((dcX + m_offsetInMcX));
	}
	
	public int dcYtoWcY( int dcY ){
		return (int) Math.rint((m_offsetInMcY - dcY));
	}
	
	/**
	 * dcXToPercent converts Div x pixel to percent position based on the div's size
	 * @param vc
	 * @return
	 */
	public double dcXToPercent( int dcX ) {
		double x = dcX;
		return x/m_baseDims.getWidth();
	}
	
	/**
	 * dcXToPercent converts Div x pixel to percent position based on the div's size
	 * @param vc
	 * @return
	 */
	public double dcYToPercent( int dcY ) {
		double y = dcY;
		return y/m_baseDims.getHeight();
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
		m_boxBounds.bot   = dcYToPercent(y+height+1)*100;
		m_boxBounds.right = dcXToPercent(x+width+1)*100;
		return m_boxBounds;
	}
	
	public PixelXY computeDivLayoutInView( 
		IProjection mapProj, ViewWorker vw, 
		DivDimensions dim,  boolean show )
	{
		int viewOx = vw.getOffsetInWcX();
		int viewOy = vw.getOffsetInWcY();
		int left;
		int top;
		if ( show ){
			double scale = mapProj.getEquatorialScale();
			double factor = scale/m_proj.getEquatorialScale();
			dim.setWidth((int)(m_baseDims.getWidth()*factor + 0.5));
			dim.setHeight((int)(m_baseDims.getHeight()*factor + 0.5));
			m_wc.copyFrom(mapProj.geodeticToWorld(m_geoCenter));
			left = (m_wc.getX()- dim.getWidth()/2) - viewOx;
			top  = viewOy - (m_wc.getY()+ dim.getHeight()/2);
		}
		else{
			ViewDimension vd = vw.getDimension();
			left = viewOx + vd.getWidth();
			top  = viewOy + vd.getHeight();
		}
		m_topLeft.m_x = left;
		m_topLeft.m_y = top;
		return m_topLeft;
	}
	
	@Override
	public String toString() {
		int oWcX = (int)Math.rint(m_offsetInMcX);
		int oWcY = (int)Math.rint(m_offsetInMcY);
		return "[" +" ox: "+ oWcX + ", oy: "+ oWcY + "]";
	}
	
}
