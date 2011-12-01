package com.moesol.gwt.maps.client;


import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class ImageDiv extends AbsolutePanel{
	public final Image image;
	
	public ImageDiv(){
		this.getElement().setClassName("imagediv");
		image = new Image();
		image.setWidth("100%");
		image.setHeight("100%");
		add(image);
	}
	
	public Image getImage(){
		return image;
	}
	
	public void setUrl( String url ){
		image.setUrl(url);
	}
	
	public String getUrl(){
		return image.getUrl();
	}
	
	public void setStyleName( String name ){
		image.setStyleName(name);
	}
	
	@SuppressWarnings("deprecation")
	public void addLoadListener(TileImageLoadListener ll){
		image.addLoadListener(ll);
	}
	
	@SuppressWarnings("deprecation")
	public void removeLoadListener(TileImageLoadListener ll){
		image.removeLoadListener(ll);
	}
}
