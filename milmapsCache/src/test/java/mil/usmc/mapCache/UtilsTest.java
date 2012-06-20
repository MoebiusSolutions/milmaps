package mil.usmc.mapCache;

import static org.junit.Assert.assertEquals;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import mil.usmc.mapCache.Utils.TilePix;
import mil.usmc.mapCache.Utils.TilePosition;

import org.junit.Test;

public class UtilsTest {
	private void assertTilePix(TilePix tp, int expX, int expY){
		assertEquals(expX,tp.x);
		assertEquals(expY,tp.y);
	}
	 
	@Test
	public void testTilesIntersect(){
		int srcSize = 512;
		double f = Utils.computeScaleFactor(36,180);
		TilePix destTile = new TilePix(0,0);
		int fSrcSize = (int)(srcSize*f + 0.5);
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 10; j++){
				TilePix tp =  Utils.srcTopLeftToTilePix(i, j, srcSize, 0, 0, f);
				boolean value = Utils.tilesIntersect(tp, fSrcSize, destTile, srcSize);
				boolean expValue = (j < 5 ? true : false);
				assertEquals(expValue,value);
			}
		}	
	}
	
	private void testTilePositionFor(double desDeg){
		Projection proj = new Projection(36.0,512);
		TileInfo ti = Utils.compTileInfo(36, 512);
		double factor = Utils.computeScaleFactor(36.0,desDeg);
		proj.setScale(factor*proj.getScale());
		int orgY = proj.latToPixY(-90.0 + desDeg);
		int orgX = proj.lngToPixX(-180.0 + desDeg);
			
		for(int i = 0; i < ti.numYtiles; i++){
			double botLat = i*ti.degHeight + -90.0;
			double topLat = botLat + ti.degHeight;
			for(int j = 0; j < ti.numXtiles; j++){
				double leftLng = j*ti.degWidth + -180.0;
				double rightLng = leftLng + ti.degWidth;
				TilePosition tp = Utils.tilePosition(orgX, orgY, topLat, leftLng, 
												 	 botLat, rightLng, proj);
				R2 p = proj.LatLngToPix(topLat, leftLng);
				assertTilePix(tp.tl, p.m_x-orgX, orgY-p.m_y);
			}
		}
	}
	
	@Test
	public void testTilePosition(){
		testTilePositionFor(180);
		testTilePositionFor(90);
	}
	
	@Test
	public void testSrcTopLeftToTilePix(){
		int size = 512;
		double f = Utils.computeScaleFactor(36,180);
		double fSize = f*size;
		TilePix tp;
		//srcTopLeftToTilePix(iRow,jCol,size,diRow,djCol,f)
		// Testing layout along the x-axis for 1st 180x180 tile
		for(int jCol = 0; jCol < 5; jCol++){
			tp = Utils.srcTopLeftToTilePix(4, jCol, size, 0, 0, f);
			int expX = (int)(jCol*fSize + 0.5);
			assertTilePix(tp,expX,0);		
		}
		// Testing layout along the x-axis for 2nd 180x180 tile
		for(int jCol = 5; jCol < 10; jCol++){
			tp = Utils.srcTopLeftToTilePix(4, jCol, size, 0, 1, f);
			int expX = (int)((jCol-5)*fSize + 0.5);
			assertTilePix(tp,expX,0);		
		}
		// Testing layout along the y-axis for 1st 180x180 tile
		for(int iRow = 0; iRow < 5; iRow ++){
			tp = Utils.srcTopLeftToTilePix(iRow, 0, size, 0, 0, f);
			int expY = size - (int)((iRow+1)*fSize + 0.5);
			assertTilePix(tp,0,expY);		
		}
		// Testing combinations for first tile
		for(int jCol = 0; jCol < 5; jCol++){
			int expX = (int)(jCol*fSize + 0.5);
			for(int iRow = 0; iRow < 5; iRow ++){
				int expY = size - (int)((iRow+1)*fSize + 0.5);
				tp = Utils.srcTopLeftToTilePix(iRow, jCol, size, 0, 0, f);
				assertTilePix(tp,expX,expY);
			}		
		}
		// Testing combinations for 2nd tile
		for(int jCol = 5; jCol < 10; jCol++){
			int expX = (int)((jCol-5)*fSize + 0.5);
			for(int iRow = 0; iRow < 5; iRow ++){
				int expY = size - (int)((iRow+1)*fSize + 0.5);
				tp = Utils.srcTopLeftToTilePix(iRow, jCol, size, 0, 1, f);
				assertTilePix(tp,expX,expY);
			}		
		}
	}
}
