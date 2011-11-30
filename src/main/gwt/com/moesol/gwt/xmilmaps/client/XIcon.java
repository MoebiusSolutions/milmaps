package com.moesol.gwt.xmilmaps.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.Icon;
import com.moesol.gwt.maps.client.units.AngleUnit;


@ExportPackage("milmaps")
public class XIcon extends Icon implements Exportable{
	
	@Export("setLatLng")
	public void setLatLng( double lat, double lng ){
		GeodeticCoords location = new GeodeticCoords();
		location.set(lng, lat, AngleUnit.DEGREES);
		setLocation(location);
	}
	
	@Export("setUrl")
	public void setUrl( String url ){
		setIconUrl(url);
	}
	
	@Export("setSize")
	public void setSize( int w, int h ){
		getImage().setPixelSize(w, h);
	}
}
