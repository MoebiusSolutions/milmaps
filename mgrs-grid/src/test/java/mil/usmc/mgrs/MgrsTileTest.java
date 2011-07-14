/**
 * 
 */
package mil.usmc.mgrs;

import static org.junit.Assert.*;

import java.awt.Color;

import mil.usmc.mgrs.milGrid.PixBoundingBox;
import mil.usmc.mgrs.objects.BoundingBox;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author User
 *
 */
public class MgrsTileTest {
	private double bLat;
	private double lLng;
	private double tLat;
	private double rLng;
	private double deg;
	
	@Test
	public void testGetBoundingBox(){
		Color c = new Color(0,0,0,0);
		MgrsTile mgrs = new MgrsTile(c,4326,512,512,0,0,0);
		// Once we get all grid levels working, we should
		// remove the next 5 lines and comment back in the 
		// rest of the test.
		//BoundingBox box = mgrs.getBoundingBox();
		//assertEquals(box.getMinLat(), -90.0, 0.000001);
		//assertEquals(box.getMinLon(), -180.0, 0.000001);
		//assertEquals(box.getMaxLat(), 90.0, 0.000001);
		//assertEquals(box.getMaxLon(), 180.0, 0.000001);
		/*
		for ( int level = 0; level < 3; level++ ){
			mgrs.setLevel(level);
			int xnum = (int)Math.pow(2, level+1);
			int ynum = (int)Math.pow(2, level);
			double latdeg = 180.0/ynum;
			double lngdeg = 180.0/ynum;
			for ( int x = 0; x < xnum; x++ ){
				lLng = -180.0 + x*lngdeg;
				rLng = lLng + lngdeg;
				mgrs.setX(x);
				for ( int y = 0; y < ynum; y++ ){
					bLat = -90.0 + y*latdeg;
					tLat = bLat + latdeg;
					mgrs.setY(y);
					BoundingBox box = mgrs.getBoundingBox();
					assertEquals(box.getMinLat(), bLat, 0.000001);
					assertEquals(box.getMinLon(), lLng, 0.000001);
					assertEquals(box.getMaxLat(), tLat, 0.000001);
					assertEquals(box.getMaxLon(), rLng, 0.000001);
				}
			}
		}
		*/
	}
}
