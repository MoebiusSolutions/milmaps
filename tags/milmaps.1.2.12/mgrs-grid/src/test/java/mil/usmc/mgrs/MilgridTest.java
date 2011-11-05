package mil.usmc.mgrs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import mil.usmc.mgrs.milGrid.PixBoundingBox;
import mil.usmc.mgrs.milGrid.UtmG;
import mil.usmc.mgrs.objects.Point;

import org.junit.Test;


public class MilgridTest {
	private BufferedImage m_img = null;
	private Graphics2D m_g;
	
	private void createTileImage() {
		// Create a buffered image that supports transparency
		m_img = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		m_g = m_img.createGraphics();
		
		m_g.setColor(Color.YELLOW);
	}
	
	@Test
	public void testMilGrid(){
		IProjection proj = new CedProj();
		proj.initialize(512);
		PixBoundingBox box = new PixBoundingBox();
		Point bl = new Point();
		Point tr = new Point();
		
		// level: 8, x :259, y : 139
		int level = 8;
		int tX = 259;
		int tY = 139;
		//box.set( proj, 512, 512, tileX, tileY );
		//assertEquals(tileX*512,box.getLeftX());
		//assertEquals((tileY+1)*512,box.getTopY());
		//assertEquals((tileX+1)*512,box.getRightX());
		//assertEquals(tileY*512,box.getBottomY());
		int x = box.getLeftX();
		int y = box.getBottomY();
		bl.copy(proj.xyPixelToLatLng(level, x, y));
		x = box.getRightX();
		y = box.getTopY();
		tr.copy(proj.xyPixelToLatLng(level, x, y));
		/////////////////////////////
		createTileImage();
		UtmG utm = new UtmG();
		
		int k = (int)(Math.pow(2,level));
		//for ( int tileX = 0; tileX < 2*k; tileX++ ){
		//  tX = tileX;
		//	for ( int tileY = 0; tileY < k; tileY++ ){
			//  ty = tileY;
				box.set( proj, 512, 512, tX, tY );
				utm.drawGrid( m_g, proj, level, box);
			//}
		//}
		//y = 0;
	}
	
}
