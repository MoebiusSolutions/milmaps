package mil.usmc.mapCache;


import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import mil.usmc.mapCache.MapCacheTileResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MapCacheTileResourceTest {
	MapCacheTileResource m_cache = new MapCacheTileResource();
	@Test
	public void testBuildUrl(){
		String server = "http://services.arcgisonline.com/ArcGIS/rest/services";
		String urlPatern = "{server}/{data}/MapServer/tile/{level}/{y}/{x}";
		String data = "I3_Imagery_Prime_World_2D";
		String srs = "EPSG:4326";
		int width = 512;
		int level = 1;
		int xTile = 0;
		int yTile = 1;
		String csUrl = m_cache.buildUrl(server, urlPatern, data, srs, width, level, xTile, yTile );
		
		String ans = "http://services.arcgisonline.com/ArcGIS/rest/services/I3_Imagery_Prime_World_2D/MapServer/tile/1/1/0";
		assertEquals(csUrl,ans );
	}

	@Test
	public void testBuildFilePath(){
		String data = "I3_Imagery_Prime_World_2D";
		String csFormat = "png";
		int size = 512;
		int level = 1;
		int xTile = 0;
		int yTile = 1;

		String dirPath = m_cache.buildDirPath( data, size, level, xTile );
		String filePath = m_cache.buildFilePath(dirPath, yTile, csFormat);
		
		String ans = "e:/milmapsCache/I3_Imagery_Prime_World_2D/512/1/0/1.png";
		assertEquals(filePath,ans );
	}
}
