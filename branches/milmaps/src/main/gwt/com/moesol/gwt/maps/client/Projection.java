package com.moesol.gwt.maps.client;

public class Projection {
	private static final double EarthCirMeters  = 2.0*Math.PI*6378137;
	private static final double MeterPerDeg  = EarthCirMeters/360.0;
	
	public static IProjection.T getType( String srs ){
		IProjection.T type = IProjection.T.Mercator;
		if ( srs.equals("EPSG:4326") || srs.equals("EPSG:2136")){
			type = IProjection.T.CylEquiDist;
		}
		return type;
	}
	
	public static IProjection createProj( IProjection.T type ){
		IProjection proj = null;
		if ( type == IProjection.T.Mercator ){
			proj = new Mercator();
		}else if (type == IProjection.T.CylEquiDist ) {
			proj = new CylEquiDistProj();
		}
		return proj;
	}
	
	public static IProjection getProj( LayerSet ls ){
		IProjection proj = null;
		if ( ls.isAutoRefreshOnTimer() == true )
			return null;
		IProjection.T type = getType(ls.getSrs());
		int size = ls.getPixelWidth();
		double degWidth  = ls.getStartLevelTileWidthInDeg();
		double degHeight = ls.getStartLevelTileHeightInDeg();
		if (size == 0 || degWidth == 0 || degHeight == 0){
			return null;
		}
		// TODO verify mercator scale is set correctly.
		if ( type == IProjection.T.Mercator ){
			proj = new Mercator();//size,degWidth,degHeight);
			double dScale = LayerSetWorker.findScale(ls,proj.getScrnDpi(),0);
			proj.setBaseEquatorialScale(dScale);
			proj.setEquatorialScale(dScale);
			// We want to start with level 1 and not 0.
			//proj.zoomByFactor(2.0);
		}else if (type == IProjection.T.CylEquiDist ) {
			proj = new CylEquiDistProj();//size,degWidth,degHeight);
			double dScale = LayerSetWorker.findScale(ls,proj.getScrnDpi(),0);
			proj.setBaseEquatorialScale(dScale);
			proj.setEquatorialScale(dScale);
		}
		return proj;
	}
}
