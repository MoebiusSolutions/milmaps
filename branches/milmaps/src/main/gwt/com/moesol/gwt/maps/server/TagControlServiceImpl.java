/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.controls.Tag;
import com.moesol.gwt.maps.client.controls.TagControlService;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class TagControlServiceImpl extends RemoteServiceServlet implements TagControlService {

    private static final String TAG_DIR = "/var/tags";
    private static final Logger LOGGER = Logger.getLogger(TagControlServiceImpl.class.getName());

    interface TagDirectoryPathProvider {

        public File get();
    }
    static TagDirectoryPathProvider TAG_DIRECTORY_PATH_PROVIDER = new TagDirectoryPathProvider() {

        @Override
        public File get() {

            return new File(TAG_DIR);
        }
    };

    @Override
    public boolean saveTagToDisk(String name, GeodeticCoords gc, String symbol) {
        try {
            Tag[] existingTags = loadTagsFromDisk();
            if (existingTags != null) {
                for (Tag tag : existingTags) {
                    if (tag.getName().equals(name)) {
                        return false;
                    }
                }
            }

            DocumentBuilder db = makeDocBuilder();
            Document doc = db.newDocument();
            Element tag = doc.createElement("tag");
            tag.setAttribute("name", name);
            tag.setAttribute("lambda", Double.toString(gc.getLambda(AngleUnit.DEGREES)));
            tag.setAttribute("phi", Double.toString(gc.getPhi(AngleUnit.DEGREES)));
            tag.setAttribute("alt", Double.toString(gc.getAltitude()));
            tag.setAttribute("symbol", symbol);
            doc.appendChild(tag);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(bos));
            byte[] tagxmlbytes = bos.toByteArray();

            File tagDir = TAG_DIRECTORY_PATH_PROVIDER.get();
            tagDir.mkdirs();
            File tagFile = new File(tagDir, name + ".xml");

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tagFile);
                fos.write(tagxmlbytes);
            } catch (FileNotFoundException ex) {
                LOGGER.log(Level.SEVERE, "FileNotFound", ex);
            } finally {
                fos.close();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to write xml to disk", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteTagFromDisk(String name) {
        File tagDir = TAG_DIRECTORY_PATH_PROVIDER.get();
        File fileToDelete = new File(tagDir, name + ".xml");
        fileToDelete.delete();
        return true;
    }

    @Override
    public Tag[] loadTagsFromDisk() throws Exception {
        File tagDir = TAG_DIRECTORY_PATH_PROVIDER.get();
        if (!tagDir.exists()) {
            return null;
        }
        File[] tagFiles = tagDir.listFiles();
        if (tagFiles.length == 0) {
            return null;
        }
        Tag[] tags = new Tag[tagFiles.length];
        int i = 0;
        DocumentBuilder db = makeDocBuilder();
        for (File tagFile : tagFiles) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FileInputStream fis = null;
            String name;
            GeodeticCoords geo;
            String symbol;
            try {
                fis = new FileInputStream(tagFile);
                int c;
                while ((c = fis.read()) != -1) {
                    bos.write(c);
                }
                Document doc = db.parse(new ByteArrayInputStream(bos.toByteArray()));
                Element tag = doc.getDocumentElement();
                name = tag.getAttribute("name");
                geo = new GeodeticCoords(Double.parseDouble(tag.getAttribute("lambda")),
                        Double.parseDouble(tag.getAttribute("phi")),
                        AngleUnit.DEGREES,
                        Double.parseDouble(tag.getAttribute("alt")));
                symbol = tag.getAttribute("symbol");
            } finally {
                fis.close();
            }
            tags[i++] = new Tag(name, geo, symbol);
        }
        return tags;
    }

    private DocumentBuilder makeDocBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        return factory.newDocumentBuilder();
    }
}
