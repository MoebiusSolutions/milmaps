package mil.usmc.mgrs;

import mil.usmc.resources.TransformResource;
import java.io.*;
import java.util.logging.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

@Path("mgrs")
public class MgrsTransformResource {

    private static final Logger LOGGER = Logger.getLogger(MgrsTransformResource.class.getName());
    private static Templates MGRS_KML;
    private static Templates MGRS_GRID_KML;
    private static MgrsXmlResource res = new MgrsXmlResource();
    private
    @Context
    UriInfo m_uriInfo;

    static {
        loadXslt();
    }

    private static void loadXslt() {
        MGRS_KML = loadXslt("/mgrs.xsl");
        MGRS_GRID_KML = loadXslt("/mgrs-grid.xsl");
    }

    private static Templates loadXslt(String resource) {
        Source xsl = new StreamSource(loadResource(resource));
        try {
            return TransformerFactory.newInstance().newTemplates(xsl);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "failed", e);
            throw new RuntimeException(e);
        }
    }

    private static InputStream loadResource(String res) {
        InputStream r = MgrsTransformResource.class.getResourceAsStream(res);
        if (r == null) {
            LOGGER.log(Level.SEVERE, "resource not found: {0}", res);
        }
        return r;
    }

    @GET
    @Path("grid")
    @Produces("application/vnd.google-earth.kml+xml")
    public TransformResource getMgrsKml() {
        try {
            Transformer t = MGRS_KML.newTransformer();
            t.setParameter("mgrs-url", m_uriInfo.getRequestUri().resolve("kml").toASCIIString());
            DOMSource s = new DOMSource();
            return new TransformResource(t, s);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "failed", e);
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("kml")
    @Produces("application/vnd.google-earth.kml+xml")
    public TransformResource getGridKml() {
        try {
            Transformer t = MGRS_GRID_KML.newTransformer();
            MultivaluedMap<String, String> map = m_uriInfo.getQueryParameters();
            MgrsXmlResource.m_bbox = res.calculateBoundingBox(map);
            double span = MgrsXmlResource.m_bbox.getMinSpan();
            t.setParameter("span", span);
            DOMSource s = new DOMSource(res.makeMgrsXml(span));
            return new TransformResource(t, s);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "failed", e);
            throw new WebApplicationException(e);
        }
    }

    public void setUriInfo(UriInfo uriInfo) {
        m_uriInfo = uriInfo;
    }
}
