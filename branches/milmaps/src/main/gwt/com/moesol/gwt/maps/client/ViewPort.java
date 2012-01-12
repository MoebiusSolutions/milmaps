package com.moesol.gwt.maps.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;




public class ViewPort {
	private final ViewDimension m_viewDims = new ViewDimension(600, 400);
	private IProjection m_proj = null;
	private final ViewCoords  m_returnedViewCoords = new ViewCoords();
	private final LayerSetWorker m_lsWorker = new LayerSetWorker();
	private final ViewWorker m_vpWorker = new ViewWorker();
	private DivWorker m_divWorker;
	private int m_tilePixWidth  = 512;
	private int m_tilePixHeight = 512;
	private double m_tileDegWidth  = 180;
	private double m_tileDegHeight = 180;
	private int m_level = 0;
  
	private int m_cxTiles;
	private int m_cyTiles;
	
	public ViewPort(){
	}
	
	public void setProjection( IProjection proj ){
		m_proj = proj;
		m_lsWorker.setProjection(proj);
		m_vpWorker.intialize(m_viewDims, proj);
		
		GeodeticCoords g = new GeodeticCoords();
		m_vpWorker.setGeoCenter(g);
	}
	
	public ViewWorker getVpWorker(){ return m_vpWorker; }
	
	public void setDivWorker( DivWorker dw ){ 
		m_divWorker = dw; 
	}
	
	public DivWorker getDivWorker(){ return m_divWorker; }
	
	public DialogBox alertWidget(final String header, String content) {
        final DialogBox box = new DialogBox();
        final VerticalPanel panel = new VerticalPanel();
        final String strContent = ".  " + content + "  .";
        box.setText(header);
        panel.add(new Label( strContent ));
        final Button btnClose = new Button("Close");
        ClickHandler handler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                box.hide();
            }
        };
        btnClose.addClickHandler(handler);
        
        // few empty labels to make widget larger
        final Label emptyLabel = new Label("");
        emptyLabel.setSize("auto","25px");
        panel.add(emptyLabel);
        panel.add(emptyLabel);
        btnClose.setWidth("90px");
        panel.add(btnClose);
        panel.setCellHorizontalAlignment(btnClose, HasAlignment.ALIGN_RIGHT);
        box.add(panel);
        box.getElement().getStyle().setProperty("zIndex", Integer.toString(9000) );
        return box;
    }
	
	public void setTileDegWidth( double deg ){
		m_tileDegWidth = deg;
	}
	public double getTileDegWidth(){ return m_tileDegWidth; }
	
	public void setTileDegHeight( double deg ){
	  	m_tileDegHeight = deg;
	}
	public double getTileDegHeight(){ return m_tileDegHeight; }
  
    public void setTilePixWidth( int pix ){m_tilePixWidth = pix;}
    public int getTilePixWidth(){ return m_tilePixWidth; }
    public void setTilePixHeight( int pix ){m_tilePixHeight = pix;}
    public int getTilePixHeight(){ return m_tilePixHeight; }
	
	public ViewCoords worldToView(WorldCoords wc, boolean checkWrap) {
		ViewCoords r = m_returnedViewCoords;
		r.copyFrom(m_vpWorker.wcToVC(wc));
		
		// Check for world wrap
		// We may want to remove the wrap check all together.
		if ( checkWrap == true ){ 
			if (r.getX() < 0) {
				int checkX = r.getX() + m_proj.getWorldDimension().getWidth();
				r.setX(checkX);
			} else if (r.getX() >= m_viewDims.getWidth()) {
				int checkX = r.getX() - m_proj.getWorldDimension().getWidth();
				r.setX(checkX);
			}
		}
		return r;
	}
	
    public int getNumberOfRows( double degHeight, int level ){
    	return (int)((180.0 /degHeight)*(1<<level));
    }

	boolean computeInViewPort(TileCoords tc) {
		if (tc.getOffsetX() + tc.getTileHeight() < 0) {
			return false;
		}
		if (tc.getOffsetX() >= m_viewDims.getWidth()) {
			return false;
		}
		if (tc.getOffsetY() + tc.getTileHeight() < 0) {
			return false;
		}
		if (tc.getOffsetY() >= m_viewDims.getHeight()) {
			return false;
		}
		return true;
	}
	
	public int getNumXTiles() {
		return m_proj.getNumXtiles(m_tileDegWidth);
	}
	
	public int getNumYTiles() {
		return m_proj.getNumYtiles(m_tileDegHeight);
	}

	private int getCenterY() {
		return m_viewDims.getHeight() / 2;
	}
	
	public int getWidth() {
		return m_viewDims.getWidth();
	}
	
	public int getHeight() {
		return m_viewDims.getHeight();
	}
	
	public int getCxTiles() {
		return m_cxTiles;
	}
	
	public int getCyTiles() {
		return m_cyTiles;
	}
	
	public void setSize(int w, int h) {
		m_viewDims.setWidth(w);
		m_viewDims.setHeight(h);
		m_vpWorker.setDimension(m_viewDims);
	}
	
	public int getLevel() { return m_level; }
	
		/**
		 * Keep the view center x on the view and the y within the view port.
		 * 
		 * @param viewCenter
		 * @return viewCenter
		 */
	public void constrainAsWorldCenter(WorldCoords centerToUpdate) {
		WorldDimension dim = m_proj.getWorldDimension();
		if (centerToUpdate.getX() < 0) {
			centerToUpdate.setX(dim.getWidth() + centerToUpdate.getX());
		} else {
			centerToUpdate.setX(centerToUpdate.getX() % dim.getWidth());
		}
		
		int hmid = getCenterY();
		if (centerToUpdate.getY() < hmid) {
			centerToUpdate.setY(hmid);
		} if (centerToUpdate.getY() > dim.getHeight() - hmid) {
			centerToUpdate.setY(dim.getHeight() - hmid);
		}
	}
}
