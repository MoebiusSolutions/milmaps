package mil.usmc.mapCache;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProjectionTest {
	Projection m_proj = new Projection(36.0,512);
	@Test
	public void testDegToPixel(){
		int j = 0;
		for ( double lng = -180; lng < 360; ){
			int xPix = m_proj.lngToPixX(lng);
			assertEquals(j*512,xPix);
			j++;
			lng += 36.0;
		}
		j = 0;
		for ( double lat = -90; lat < 180; ){
			int yPix = m_proj.latToPixY(lat);
			assertEquals(j*512,yPix);
			j++;
			lat += 36.0;
		}
	}
	
	@Test
	public void testScaledDegToPixel(){
		m_proj.setScale(0.2*m_proj.getScale());
		int j = 0;
		for ( double lng = -180; lng < 360; ){
			double xPix = m_proj.lngToPixX(lng);
			double expPix = (0.2*j*512);
			assertEquals(expPix,xPix, 1);
			j++;
			lng += 36.0;
		}
		j = 0;
		for ( double lat = -90; lat < 180; ){
			int yPix = m_proj.latToPixY(lat);
			double expPix = (0.2*j*512);
			assertEquals(expPix,yPix, 1);
			j++;
			lat += 36.0;
		}
		// Testing right boundary value
		double xPix = m_proj.lngToPixX(0);
		assertEquals(512,xPix, 1);
		
	}
}
