package com.moesol.gwt.maps.client;


import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class ImageDiv extends AbsolutePanel{
	public Image image;
	
	public ImageDiv(){
		this.getElement().setClassName("imagediv");
		image = new Image();
		image.setWidth("100%");
		image.setHeight("100%");
		add(image);
	}
	
	public void removeImage(){
		remove(image);
		image = null;
	}
	
	public Image getImage(){
		return image;
	}
	
	public void setUrl( String url ){
		if (image != null) {
			image.setUrl(url);
		}
	}
	
	public String getUrl(){
		if (image != null) {
			return image.getUrl();
		}
		return null;
	}
	
	public void setStyleName( String name ){
		if (image != null) {
			image.setStyleName(name);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void addLoadListener(TileImageLoadListener ll){
		if (image != null) {
			image.addLoadListener(ll);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void removeLoadListener(TileImageLoadListener ll){
		if (image != null) {
			image.removeLoadListener(ll);
		}
	}
}
