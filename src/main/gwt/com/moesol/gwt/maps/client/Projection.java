package com.moesol.gwt.maps.client;

public class Projection {
	enum T {
		CylEquiDist, Mercator
	}
	
	public static Projection.T getType( int espg ){
		Projection.T type = T.Mercator;
		switch ( espg ){
			case 2163:
			case 4326:
				type = T.CylEquiDist;
			break;
		}
		return type;
	}
	
	public static IProjection getProj( LayerSet ls ){
		IProjection proj = null;
		if ( ls.isAutoRefreshOnTimer() == true )
			return null;
		Projection.T type = getType(ls.getEpsg());
		int size = ls.getPixelWidth();
		double degWidth  = ls.getStartLevelTileWidthInDeg();
		double degHeight = ls.getStartLevelTileHeightInDeg();
		if ( size == 0 || degWidth == 0 || degHeight == 0 )
			return null;
		if ( type == Projection.T.Mercator ){
			proj = new Mercator(size,degWidth,degHeight);
		}else if (type == Projection.T.CylEquiDist ) {
			proj = new CylEquiDistProj(size,degWidth,degHeight);
		}
		return proj;
	}
}
