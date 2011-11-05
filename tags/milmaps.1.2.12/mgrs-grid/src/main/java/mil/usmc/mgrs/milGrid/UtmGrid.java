package mil.usmc.mgrs.milGrid;


import java.awt.FontMetrics;
import java.awt.Graphics2D;

import mil.usmc.mgrs.IProjection;
import mil.usmc.mgrs.objects.Point;
import mil.usmc.mgrs.objects.R2;

public class UtmGrid {
	protected double INVALID_VALUE = 99999999.9;
	protected double INAVLID_ROWCOL = 1000;
	protected static double RadToDeg = 57.29577951;
	protected static double DegToRad = 0.017453293;

	protected int NUM_GRID_PTS = 20;
	protected int NUM_CLIP_PTS = 30;
	
	protected R2 m_r2Pt = new R2();
	
	public enum ECellCorner{
		eTopLeft ,
		eTopRight,
		eBotRight,
		eBotLeft
	};

	public enum ESide{
		eHorz,
		eVert
	};
	
	protected IProjection m_proj;
	//protected boolean m_bDrawToImage;
	// Grid info.
	protected int m_levelOfDetail;
	protected boolean  m_bLabelOn;
	// constant
	
	public UtmGrid( IProjection proj, int level )
	{
	  m_bLabelOn      = true;
	  m_proj      	  = proj;
	  m_levelOfDetail = level;
	} 
	
	public boolean getCornerCell( ECellCorner eCorner, PixBoundingBox box, UtmCell cell )
	{
	  Point p = null;

	  // Get TopLeft Lat/Lng
	  switch ( eCorner ){
	    case eTopLeft :
	      p = m_proj.xyPixelToLatLng(m_levelOfDetail, box.getLeftX(), box.getTopY());
	    break;
	    case eTopRight :
	      p = m_proj.xyPixelToLatLng(m_levelOfDetail, box.getRightX(), box.getTopY());
	    break;
	    case eBotRight :
	      p = m_proj.xyPixelToLatLng(m_levelOfDetail, box.getRightX(), box.getBottomY());
	    break;
	    case eBotLeft :
	      p = m_proj.xyPixelToLatLng(m_levelOfDetail, box.getLeftX(), box.getBottomY());
	    break;
	  }
	  if ( p != null  ){
	    cell.setLatLng(p.m_lat, p.m_lng);
	    return true;
	  }
	  return false;
	}
	
	protected R2 posToBoxPt( PixBoundingBox box, double lat, double lng ){
		// get the map pixel coordinates for the lat, lng
		m_r2Pt.copy(m_proj.latLngToPixelXY(m_levelOfDetail, lat, lng));
		// compute the xy value relative to the tile's offset
		m_r2Pt.m_x = m_r2Pt.m_x - box.getLeftX();
		m_r2Pt.m_y = box.getTopY() - m_r2Pt.m_y;
		return m_r2Pt;
	}
	
	// the points are relative to the bounding box
	public int latLngToBoxPt( 
		PixBoundingBox box,
		double lat, 
		double lng, 
		int nDex, 
		R2[]pt
	)
	{
		R2 p = posToBoxPt( box,  lat,  lng );
		if ( p != null ){
			pt[nDex] = new R2(p);
			nDex += 1;
		}
		return nDex;
	}
	
    public int getHorizontalPts(
    	PixBoundingBox box,
	    double lat1, double lng1,
		double lat2, double lng2,
		int    numPts,
		int    nDex,
		R2[]   pt
	)
	{
    	double  dLng;
    	if ( numPts > 0 ){ 
    		double degInc = (lng2-lng1)/numPts;
    		for( int i = 1; i < numPts; i++ ){
    			dLng = lng1 + i*degInc; 
    			nDex = latLngToBoxPt(box,lat1,dLng,nDex,pt);
		    }
    	}
    	return nDex;
	}
    
    public int getVerticalPts(
    	PixBoundingBox box,
    	double lat1, double lng1,
    	double lat2, double lng2,
    	int    numPts,
    	int    nDex,
    	R2[]   pt
    )
    {
    	double  dLat;
    	if ( numPts > 0 ){
    	    double degInc = (lat2-lat1)/numPts;
    	    for( int i = 1; i < numPts; i++ ){
    	      dLat = lat1 + i*degInc; 
    	      nDex = latLngToBoxPt(box,dLat,lng1,nDex,pt);
    	    }
    	 }
    	return nDex;
   	}
    
    public void drawLabel( 
    	Graphics2D g,
    	PixBoundingBox box,
    	UtmCell cell
    )
	{
    	if ( m_bLabelOn ) {
    		if ( 19 == cell.m_iRow ){
    			if ( cell.m_iZone == 32 || cell.m_iZone == 34 || cell.m_iZone == 36 )
    				return;
    		}
    		R2 pt = null;

    		pt =  posToBoxPt( box, cell.m_dLat, cell.m_dLng );

    		FontMetrics fm = g.getFontMetrics();
    		String label = cell.getCellLabel();
    		int h = fm.getHeight();
    		g.drawString(label, pt.m_x + 8, pt.m_y-(h+5));
    	}
    	return;
	}
    
    public void drawSidesOfCell( 
    	Graphics2D g,
    	PixBoundingBox box,
    	UtmCell    cell,
    	int        numSides,
    	int        numPts, 
    	R2[]       pImPt 
    )
    {
    	// Get the top lat and right lng of the cell.
    	double  dBotLat    = cell.m_dLat;
    	double  dLeftLng   = cell.m_dLng;
    	double  dTopLat    = cell.getTopLat();
    	double  dRightLng  = cell.getRightLng();

    	int nDex = 0;
    	// TopLeft point
    	nDex = latLngToBoxPt( box, dTopLat, dLeftLng, nDex, pImPt);
    	nDex = getHorizontalPts( box, dTopLat, dLeftLng, dTopLat, 
    							 	  dRightLng, numPts, nDex, pImPt );
    	// TopRight point
    	nDex = latLngToBoxPt( box, dTopLat, dRightLng, nDex, pImPt );
    	nDex = getVerticalPts( box, dTopLat, dRightLng, dBotLat, 
    								dRightLng, numPts, nDex, pImPt );
    	// BottomRight point
    	nDex = latLngToBoxPt( box, dBotLat, dRightLng, nDex, pImPt );
    	if ( numSides == 3 ){
    	    nDex = getHorizontalPts( box, dBotLat, dRightLng, dBotLat, 
    	    							  dLeftLng, numPts, nDex, pImPt );
    		nDex = latLngToBoxPt( box, dBotLat, dLeftLng, nDex, pImPt);
    	 }
    	 // Draw sides
    	 Utmhelp.drawPolyline(g,pImPt,nDex); // TRUE );
    	 ///////////////////////////////////////////////////////////////////////
    	 // Draw labels
    	 drawLabel( g, box, cell );
    }
    
    protected int comptuteNumSidePts( int cellJump )
    {
      return ( (cellJump-1)*5 );
    }
    
    public void drawGrid( Graphics2D g, PixBoundingBox box, int cellJump )
    {
      int numZones = 60; 
      int minZone  = 1; 
      int numRows  = 20;
      int minRow   = 0;

      int numSidePts = comptuteNumSidePts(cellJump);
      int nPts = numSidePts*3 + 6;
      R2[]  pImPt = new R2[nPts];

      UtmCell tCell = new UtmCell(cellJump);
      // Loop through and draw the cells.
      //CPolyDraw pd(m_pViewProj,nPts,FALSE);
      int inc = cellJump;
      int i = minRow; 
      for ( int m = 0; m < numRows; m += inc ){
        int j = minZone;
        for ( int n = 0; n < numZones; n += inc ){
          tCell.setRowZone(i,j);
          int numSides = ( i == minRow ? 3 : 2);
          drawSidesOfCell( g, box, tCell, numSides, numSidePts, pImPt );
          j += inc;
          if ( j > 60 )
            j = j - 60;
        }
        i += inc;
        i = Math.min(19,i);
      }
    }
}
