package com.moesol.gwt.maps.client;

public class Projection {
	
	public static IProjection.T getType( int espg ){
		IProjection.T type = IProjection.T.Mercator;
		switch ( espg ){
			case 2163:
			case 4326:
				type = IProjection.T.CylEquiDist;
			break;
		}
		return type;
	}
	
	public static IProjection getProj( LayerSet ls ){
		IProjection proj = null;
		if ( ls.isAutoRefreshOnTimer() == true )
			return null;
		IProjection.T type = getType(ls.getEpsg());
		int size = ls.getPixelWidth();
		double degWidth  = ls.getStartLevelTileWidthInDeg();
		double degHeight = ls.getStartLevelTileHeightInDeg();
		if ( size == 0 || degWidth == 0 || degHeight == 0 )
			return null;
		if ( type == IProjection.T.Mercator ){
			proj = new Mercator();//size,degWidth,degHeight);
			// We want to start with level 1 and not 0.
			//proj.zoomByFactor(2.0);
		}else if (type == IProjection.T.CylEquiDist ) {
			proj = new CylEquiDistProj();//size,degWidth,degHeight);
		}
		return proj;
	}
}
