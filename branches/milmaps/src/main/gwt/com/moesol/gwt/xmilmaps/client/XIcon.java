/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.xmilmaps.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import com.moesol.gwt.maps.client.Icon;
import com.moesol.gwt.maps.client.units.Degrees;


@ExportPackage("milmaps")
public class XIcon extends Icon implements Exportable{
	
	@Export("setLatLng")
	public void setLatLng( double lat, double lng ){
		setLocation(Degrees.geodetic(lat, lng));
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
