package mil.usmc.mapCache;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class SplitTest {
	String m_csBase = "/milmapsCache";
	Split m_split = new Split();
	Split.ImgInfo m_info = m_split.createInfObj();
	
	public SplitTest(){
		m_split.setBase(m_csBase);
	}
	@Test
	public void testCreateImageInfo(){	
		String path = "/milmapsCache/I3_Imagery_Prime_World_2D/512/1/0/1.png";
		File file = new File(path);
		Split.ImgInfo inf = m_split.getImageInfo(file);
		assertEquals(1,inf.level );
		assertEquals(0,inf.x );
		assertEquals(1,inf.y );
	}
	
	@Test
	public void testBuildDirPath(){
		//String data = "I3_Imagery_Prime_World_2D";
		//String csFormat = "png";
		int size = 512;
		int level = 1;
		int xTile = 0;
		String ans = "/milmapsCache/bmng/512/1/0";
		String dirPath = m_split.buildDirPath("bmng", size, level, xTile);
		assertEquals(ans, dirPath);
	}
	
	@Test
	public void testBuildDirPaths(){
		String path = "/milmapsCache/I3_Imagery_Prime_World_2D/512/1/0/1.png";
		File file = new File(path);
		Split.ImgInfo inf = m_split.getImageInfo(file);
		String[] dirPaths = m_split.buildDirPaths(inf);
		
		String ans[] = { "/milmapsCache/bmng/256/1/0", "/milmapsCache/bmng/256/1/1"} ;
		assertEquals(ans[0], dirPaths[0]);
		assertEquals(ans[1], dirPaths[1]);
	}
	
	@Test
	public void testBuildFilePath(){
		int size = 512;
		int level = 1;
		int xTile = 0;
		int yTile = 1;
		String ans = "/milmapsCache/bmng/512/1/0/1.png";
		String dirPath = m_split.buildDirPath("bmng", size, level, xTile);
		String filePath = m_split.buildFilePath(dirPath, yTile, "png");
		assertEquals(ans, filePath);
	}
}
