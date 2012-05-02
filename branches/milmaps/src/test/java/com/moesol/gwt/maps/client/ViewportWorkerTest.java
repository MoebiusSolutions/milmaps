package com.moesol.gwt.maps.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;


public class ViewportWorkerTest {
	private final ViewDimension m_viewDimensions = new ViewDimension(600, 400);
	private final ViewWorker m_vpWorker = new ViewWorker();
	private final IProjection m_proj = new CylEquiDistProj();//512, 180, 180);
	
	@Test
	public void  coordinateConverstionTest(){
		m_vpWorker.intialize(m_viewDimensions, m_proj);
		WorldCoords wc = new WorldCoords(512, 256);
		m_vpWorker.setCenterInWc(wc);
		
		wc = new WorldCoords(200, 500);
		ViewCoords vc = m_vpWorker.wcToVC(wc);
		assertEquals( vc.getX(), -12);
		assertEquals( vc.getY(), -44);
		
		wc = m_vpWorker.viewToWorld(vc);
		assertEquals( wc.getX(), wc.getX());
		assertEquals( wc.getY(), wc.getY());
	}
	
	
	@Test
	public void updateTest() {
		m_vpWorker.intialize(m_viewDimensions, m_proj);
		int offsetWcX = m_vpWorker.getOffsetInWcX();
		int offsetWcY = m_vpWorker.getOffsetInWcY();
		assertEquals( offsetWcX, 212);
		assertEquals( offsetWcY, 456);
		
		GeodeticCoords gc = new GeodeticCoords( 90, 45, AngleUnit.DEGREES);
		m_vpWorker.setGeoCenter(gc);
		offsetWcX = m_vpWorker.getOffsetInWcX();
		offsetWcY = m_vpWorker.getOffsetInWcY();
		assertEquals( offsetWcX, 468);
		assertEquals( offsetWcY, 584);
		
		gc = Degrees.geodetic(-45, -90);
				
		m_vpWorker.setGeoCenter(gc);
		offsetWcX = m_vpWorker.getOffsetInWcX();
		offsetWcY = m_vpWorker.getOffsetInWcY();
		assertEquals( offsetWcX, -44);
		assertEquals( offsetWcY, 328);
	}
	
	private int getIconViewPtX( int i, int j){
	int x[][] = {{ 400, 172, -55, -283, -510, 1310, 1083, 855, 628, 400 },
				 { 628, 400, 173, -55, -282, -510, 1311, 1083, 856, 628 },
				 { 855, 627, 400, 172, -55, -283, -510, 1310, 1083, 855 },
				 { 1083, 855, 628, 400, 173, -55, -282, -510, 1311, 1083 },
				 { 1310, 1082, 855, 627, 400, 172, -55, -283, -510, 1310 },
				 { -510, 1310, 1083, 855, 628, 400, 173, -55, -282, -510 },
				 { -283, -511, 1310 , 1082, 855, 627, 400, 172, -55, -283 },
				 { -55, -283, -511 , 1310, 1082, 855, 627, 400, 172, -55 },
				 { 172, -55, -283, -511 , 1310, 1082, 855, 627, 400, 172 },
				 { 400, 172, -55, -283, -510, 1310, 1083, 855, 628, 400 }};		
		//int k = (10-i + j - 1)%10;
		//if ( k < 0 ){
		//	k += 0;
		//}
		return x[i][j];
	}
	
	@Test
	public void geodeticToViewTest(){
		int x[] = { 400, 172, -55, -283, -510, 1310, 1083, 855, 628, 400 };
		ViewDimension vd = new ViewDimension(800,600);
		m_vpWorker.intialize(vd, m_proj);
		m_proj.setEquatorialScale(2*m_proj.getBaseEquatorialScale());
		WorldDimension wd = m_proj.getWorldDimension();
		assertEquals(2048,wd.getWidth());
		double degInc = 40;
		double iconLng = 0;
		int viewCentX = -400;
		double dPixelInc = 227.55;
		int pixelInc = m_proj.compWidthInPixels(0, degInc);
		assertEquals(228,pixelInc);
		for (int i = 0; i < 10; i++){
			iconLng = -180 + i*degInc;
			GeodeticCoords gc = new GeodeticCoords(iconLng,0, AngleUnit.DEGREES); 
			
			for (int j = 0; j < 10; j++){
				viewCentX = (int)(j*dPixelInc+ 0.5);
				WorldCoords cent = new WorldCoords(viewCentX,512);
				m_vpWorker.setCenterInWc(cent);
				ViewCoords vc = m_vpWorker.geodeticToView(gc);
				int ox = getIconViewPtX(i,j);
				assertEquals(ox,vc.getX(),1);
			}
		}

		m_proj.setEquatorialScale(m_proj.getBaseEquatorialScale()/2);
	}
	
	@Test
	public void getViewBoxTest(){
		m_vpWorker.intialize(m_viewDimensions, m_proj);
		m_vpWorker.setGeoCenter(new GeodeticCoords(0,0,AngleUnit.DEGREES));
		ViewBox vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(70.3124, vb.getTopLat(), 0.001);
		assertEquals(-70.3124, vb.getBotLat(), 0.001);
		assertEquals(-105.46875, vb.getLeftLon(), 0.001);
		assertEquals(105.46875, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
		
		m_vpWorker.setGeoCenter(new GeodeticCoords(-90,0,AngleUnit.DEGREES));
		vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(70.3124, vb.getTopLat(), 0.001);
		assertEquals(-70.3124, vb.getBotLat(), 0.001);
		assertEquals(164.53125, vb.getLeftLon(), 0.001);
		assertEquals(15.46875, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
		
		m_vpWorker.setGeoCenter(new GeodeticCoords(-180,0,AngleUnit.DEGREES));
		vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(70.3124, vb.getTopLat(), 0.001);
		assertEquals(-70.3124, vb.getBotLat(), 0.001);
		assertEquals(74.53125, vb.getLeftLon(), 0.001);
		assertEquals(-74.53125, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
		
		m_vpWorker.setGeoCenter(new GeodeticCoords(90,0,AngleUnit.DEGREES));
		vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(70.3124, vb.getTopLat(), 0.001);
		assertEquals(-70.3124, vb.getBotLat(), 0.001);
		assertEquals(-15.46875, vb.getLeftLon(), 0.001);
		assertEquals(-164.53125, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
		
		/////////////// Change Latitude /////////////////
		m_vpWorker.setGeoCenter(new GeodeticCoords(0,15,AngleUnit.DEGREES));
		vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(85.4296875, vb.getTopLat(), 0.001);
		assertEquals(-55.1953125, vb.getBotLat(), 0.001);
		assertEquals(-105.46875, vb.getLeftLon(), 0.001);
		assertEquals(105.46875, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
		
		m_vpWorker.setGeoCenter(new GeodeticCoords(-90,15,AngleUnit.DEGREES));
		vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(85.4296875, vb.getTopLat(), 0.001);
		assertEquals(-55.1953125, vb.getBotLat(), 0.001);
		assertEquals(164.53125, vb.getLeftLon(), 0.001);
		assertEquals(15.46875, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
		
		m_vpWorker.setGeoCenter(new GeodeticCoords(-180,15,AngleUnit.DEGREES));
		vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(85.4296875, vb.getTopLat(), 0.001);
		assertEquals(-55.1953125, vb.getBotLat(), 0.001);
		assertEquals(74.53125, vb.getLeftLon(), 0.001);
		assertEquals(-74.53125, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
		
		m_vpWorker.setGeoCenter(new GeodeticCoords(90,15,AngleUnit.DEGREES));
		vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(85.4296875, vb.getTopLat(), 0.001);
		assertEquals(-55.1953125, vb.getBotLat(), 0.001);
		assertEquals(-15.46875, vb.getLeftLon(), 0.001);
		assertEquals(-164.53125, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
		//
		m_vpWorker.setGeoCenter(new GeodeticCoords(0,-15,AngleUnit.DEGREES));
		vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(55.1953125, vb.getTopLat(), 0.001);
		assertEquals(-85.4296875, vb.getBotLat(), 0.001);
		assertEquals(-105.46875, vb.getLeftLon(), 0.001);
		assertEquals(105.46875, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
		
		m_vpWorker.setGeoCenter(new GeodeticCoords(-90,-15,AngleUnit.DEGREES));
		vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(55.1953125, vb.getTopLat(), 0.001);
		assertEquals(-85.4296875, vb.getBotLat(), 0.001);
		assertEquals(164.53125, vb.getLeftLon(), 0.001);
		assertEquals(15.46875, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
		
		m_vpWorker.setGeoCenter(new GeodeticCoords(-180,-15,AngleUnit.DEGREES));
		vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(55.1953125, vb.getTopLat(), 0.001);
		assertEquals(-85.4296875, vb.getBotLat(), 0.001);
		assertEquals(74.53125, vb.getLeftLon(), 0.001);
		assertEquals(-74.53125, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
		
		m_vpWorker.setGeoCenter(new GeodeticCoords(90,-15,AngleUnit.DEGREES));
		vb = m_vpWorker.getViewBox(m_proj,0,0);
		assertEquals(55.1953125, vb.getTopLat(), 0.001);
		assertEquals(-85.4296875, vb.getBotLat(), 0.001);
		assertEquals(-15.46875, vb.getLeftLon(), 0.001);
		assertEquals(-164.53125, vb.getRightLon(), 0.001);
		assertEquals(210.9375,vb.getLonSpan(),0.001);
	}
}
