/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.server;

import java.io.File;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.controls.Tag;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;

public class TagControlServiceImplTest {

    private static final String SHIP = "ship";
    private static final String TANK = "tank";
    private static final String UNIT = "units and some other things too";

    public TagControlServiceImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        TagControlServiceImpl.TAG_DIRECTORY_PATH_PROVIDER = new TagControlServiceImpl.TagDirectoryPathProvider() {

            @Override
            public File get() {
                return new File("target/tags");
            }
        };
    }

    @After
    public void tearDown() {
        File tagDir = TagControlServiceImpl.TAG_DIRECTORY_PATH_PROVIDER.get();
        File[] files = tagDir.listFiles();
        for (File file : files) {
            file.delete();
        }
        if (tagDir.exists()) {
            tagDir.delete();
        }
    }

    /**
     * Test of saveTagToDisk method, of class TagControlServiceImpl.
     */
    @Test
    public void testSaveTagToDisk() {
        System.out.println("saveTagToDisk");
        GeodeticCoords gc = new GeodeticCoords(45, -120, AngleUnit.DEGREES, 432);
        String symbol = "sfap-----------";
        TagControlServiceImpl instance = new TagControlServiceImpl();
        boolean expResult = true;
        boolean result = instance.saveTagToDisk(TANK, gc, symbol);
        assertEquals(expResult, result);

        gc = Degrees.geodetic(1.001, -34.34);
        result = instance.saveTagToDisk(SHIP, gc, symbol);
        assertEquals(expResult, result);
        File tagDir = TagControlServiceImpl.TAG_DIRECTORY_PATH_PROVIDER.get();
        assertTrue(tagDir.exists());
        File file1 = new File(tagDir, TANK + ".xml");
        assertTrue(file1.exists());
        File file2 = new File(tagDir, SHIP + ".xml");
        assertTrue(file2.exists());
        File file3 = new File(tagDir, UNIT + ".xml");
        assertFalse(file3.exists());
        instance.saveTagToDisk(UNIT, gc, symbol);
        assertTrue(file3.exists());
        assertFalse(instance.saveTagToDisk(UNIT, gc, symbol));
    }

    /**
     * Test of deleteTagFromDisk method, of class TagControlServiceImpl.
     */
    @Test
    public void testDeleteTagFromDisk() {
        System.out.println("deleteTagFromDisk");
        testSaveTagToDisk();
        TagControlServiceImpl instance = new TagControlServiceImpl();
        instance.deleteTagFromDisk(TANK);
        File tagDir = TagControlServiceImpl.TAG_DIRECTORY_PATH_PROVIDER.get();
        assertTrue(tagDir.exists());
        File file1 = new File(tagDir, TANK + ".xml");
        assertFalse(file1.exists());
        File file2 = new File(tagDir, SHIP + ".xml");
        assertTrue(file2.exists());
        instance.deleteTagFromDisk(SHIP);
        assertFalse(file2.exists());
    }

    /**
     * Test of loadTagsFromDisk method, of class TagControlServiceImpl.
     */
    @Test
    public void testLoadTagsFromDisk() throws Exception {
        System.out.println("loadTagsFromDisk");
        testSaveTagToDisk();
        TagControlServiceImpl instance = new TagControlServiceImpl();
        Tag[] tags = instance.loadTagsFromDisk();
        assertEquals(3, tags.length);
        for (Tag tag : tags) {
            System.out.println(tag.toString());
        }
    }
}
