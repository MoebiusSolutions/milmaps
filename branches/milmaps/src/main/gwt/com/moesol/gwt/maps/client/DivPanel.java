package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.LayoutPanel;

public class DivPanel extends AbsolutePanel {
	private final LayoutPanel m_tileLayersPanel = new LayoutPanel();
	private final DivWorker m_divWorker = new DivWorker();
	
	
	public DivPanel(){
		m_tileLayersPanel.setStylePrimaryName("tileLayerPanel");
		m_tileLayersPanel.setWidth("100%");
		m_tileLayersPanel.setHeight("100%");
		m_tileLayersPanel.getElement().setClassName("tileLayerPanel"); 
		DivDimensions dd = m_divWorker.getDivDimension();
		super.setPixelSize(dd.getWidth(), dd.getHeight());
		this.add(m_tileLayersPanel);
		this.getElement().setClassName("DivPanelContainer");
	}
	
	@Override
	public void setPixelSize( int width, int height){
		super.setPixelSize(width, height);
		m_divWorker.setPixelSize(width, height);
	}
	
	public void setDimensions(DivDimensions d){
		super.setPixelSize(d.getWidth(), d.getHeight());
		m_divWorker.setPixelSize(d.getWidth(), d.getHeight());
	}
	
	public void zoomByFactor( double factor ){
		m_divWorker.zoomByFactor(factor);
		DivDimensions dd = m_divWorker.getDivDimension();
		super.setPixelSize(dd.getWidth(), dd.getHeight());
		return;
	}
	
	public DivWorker getDivWorker(){ return m_divWorker; }
	
	public LayoutPanel getTileLayerPanel(){ return m_tileLayersPanel; }
	
}
