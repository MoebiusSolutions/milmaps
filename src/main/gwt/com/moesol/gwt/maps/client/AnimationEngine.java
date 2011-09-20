package com.moesol.gwt.maps.client;

import java.util.ArrayList;

import com.google.gwt.animation.client.Animation;

public class AnimationEngine extends Animation {
	private final MapView m_mapView;
	private ArrayList<TiledImageLayer> m_tiledImageLayers;
	private double m_x;
	private double m_y;
	private double m_xDiff;
	private double m_yDiff;
	private double m_factor;
	private boolean m_directionIn;
	private int m_durationInSecs = 750;
	  
	public AnimationEngine( MapView mv ) {
	   m_mapView = mv;
	}
	 
	public void setTiledImageLayers(ArrayList<TiledImageLayer>  layers) {
		this.m_tiledImageLayers = layers;
	}
	
	public ArrayList<TiledImageLayer> getTiledImageLayers() {
	    return m_tiledImageLayers;
	}
	  
	protected void zoom( double val, double offsetX, double offsetY ){
		for( TiledImageLayer layer : m_tiledImageLayers) {
			layer.drawTileImages( val, offsetX, offsetY );
		} 
		m_mapView.drawIcons(val, offsetX, offsetY);
	}
	
	public void animateZoomMap( final double x, final double y, final double scaleFactor ) {
		m_x = x;
		m_y = y;
		m_directionIn = ( scaleFactor >= 1.0 ? true : false);
		m_factor = ( m_directionIn ? scaleFactor : 1.0/scaleFactor);
		IProjection p = m_mapView.getProjection(); 
		m_xDiff =  x - p.getViewSize().getWidth()/2.0;
		m_yDiff =  y - p.getViewSize().getHeight()/2.0;

		m_mapView.setSuspendFlag(true);
		run(m_durationInSecs);
	}
	
	

	@Override
	protected void onUpdate(double progress) {
		double offsetX , offsetY;
		double scale = Math.min(1.0,progress);
		if( m_directionIn ){
			scale = 1.0 + ((m_factor-1)*Math.min(1.0,progress));
			offsetX = (scale - 1.0)*(m_x);
			offsetY = (scale - 1.0)*(m_y);
		}
		else {
			scale = m_factor - (m_factor-1)*Math.min(1.0, progress);
			offsetX = (scale - 1.0)*m_x - (scale/m_factor)*m_xDiff;
			offsetY = (scale - 1.0)*m_y - (scale/m_factor)*m_yDiff;
		}
		zoom( scale, offsetX, offsetY);
	}

	@Override
	protected void onComplete(){
		super.onComplete();
		m_mapView.setSuspendFlag(false);
		m_mapView.hideAnimatedTiles();
		m_mapView.doUpdateView();
	}
	
	@Override
	protected void onCancel(){
		super.onCancel();
		m_mapView.setSuspendFlag(false);
		m_mapView.hideAnimatedTiles();
		m_mapView.doUpdateView();
	}

	public int getDurationInSecs() {
		return m_durationInSecs;
	}

	public void setDurationInSecs(int durationInSecs) {
		m_durationInSecs = durationInSecs;
	}
}