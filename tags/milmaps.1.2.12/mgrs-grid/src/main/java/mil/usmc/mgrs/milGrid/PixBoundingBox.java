package mil.usmc.mgrs.milGrid;

import mil.usmc.mgrs.IProjection;
import mil.usmc.mgrs.objects.R2;

// Important:
// This bounding box is based on the zero y value at the bottom.
// and is given in world pixel coordinates
//

public class PixBoundingBox {
	public R2 m_tl  = new R2();
	public R2 m_lr = new R2();
	public R2 m_rtnPt = new R2();
	
	public PixBoundingBox(){
		
	}
	
	public PixBoundingBox ( PixBoundingBox b ){
		copy(b);
	}
	
	public PixBoundingBox ( R2 tl, R2 lr ){
		m_tl.copy(tl);
		m_lr.copy(lr);
	}
	
	public PixBoundingBox ( int lx, int ty, int rx, int ly ){
		m_tl.m_x = lx;
		m_tl.m_y = ty;
		m_lr.m_x = rx;
		m_lr.m_y = ly;
	}
	
	public void copy( PixBoundingBox b ){
		m_tl.copy(b.m_tl);
		m_lr.copy(b.m_lr);
	}
	
	public PixBoundingBox clone(){
		return new PixBoundingBox(this);
	}
	
	public int getWidth(){
		return Math.abs(m_tl.m_x - m_lr.m_x);
	}
	
	public int getHeight(){
		return Math.abs(m_tl.m_y - m_lr.m_y);
	}
	
	public int getCenterX(){
		return m_tl.m_x + Math.abs(m_tl.m_x - m_lr.m_x)/2;
	}
	
	public int getCenterY(){
		return m_tl.m_y - Math.abs(m_tl.m_y - m_lr.m_y)/2;
	}
	
	public int getLeftX(){
		return m_tl.m_x;
	}
	
	public int getRightX(){
		return m_lr.m_x;
	}
	
	public int getTopY(){
		return m_tl.m_y;
	}
	
	public int getBottomY(){
		return m_lr.m_y;
	}
	
	public R2 worldPixToBoxPt( R2 pt ){
		// get the map pixel coordinates for the lat, lng
		// compute the xy value relative to the tile's offset
		m_rtnPt.m_x = pt.m_x - m_tl.m_x;
		m_rtnPt.m_y = m_tl.m_y - pt.m_y;
		return m_rtnPt;
	}
	
	public R2 boxPtToWordPix( R2 pt ){
		// get the map pixel coordinates for the lat, lng
		// compute the xy value relative to the tile's offset
		m_rtnPt.m_x = pt.m_x + getLeftX();
		m_rtnPt.m_y = getTopY() - pt.m_y;
		return m_rtnPt;
	}
	
	public void set(IProjection proj, int width, int height, 
									  int tileX, int tileY ){
		R2 tl = proj.tileXYToTopLeftXY( tileX, tileY );
		m_tl.copy(tl);
		m_lr.m_x = tl.m_x + width;
		m_lr.m_y = tl.m_y - height;	
	}
	
	public static PixBoundingBox create( IProjection proj, 
										 int width, int height, 
										 int tileX, int tileY ){
		R2 tl = proj.tileXYToTopLeftXY( tileX, tileY );
		R2 br = new R2( tl.m_x + width, tl.m_y - height);
		
		return new PixBoundingBox(tl,br);
	}
}
