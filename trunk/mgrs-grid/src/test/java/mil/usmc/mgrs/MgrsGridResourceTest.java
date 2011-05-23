package mil.usmc.mgrs;

import java.util.logging.*;
import javax.ws.rs.core.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;

import org.apache.cxf.jaxrs.impl.MetadataMap;

/**
 *
 * @author Alex
 */
public class MgrsGridResourceTest {

    private static MgrsTransformResource res = new MgrsTransformResource();
    private static final Logger LOGGER = Logger.getLogger(MgrsGridResourceTest.class.getName());
    private static UriInfo uriInfo;
    private static MultivaluedMap<String, String> map;
    private String BBOX_STRING;

    public MgrsGridResourceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        uriInfo = mock(UriInfo.class);
        map = new MetadataMap<String, String>();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testUnitedStates() {
        LOGGER.log(Level.INFO, "testUnitedStates()");
        BBOX_STRING = "-124.599199,30.260742,-76.006955,48.314632";
        runTransform();
    }

    @Test
    public void testHundredKm() {
        LOGGER.log(Level.INFO, "testHundredKm()");
        BBOX_STRING = "-122.660067,28.804658,-76.169483,46.418426";
        runTransform();
    }

    @Test
    public void testNorwaySpecialCase() {
        LOGGER.log(Level.INFO, "testNorwaySpecialCase()");
        BBOX_STRING = "-30.96330300815977,48.90608449133239,40.18756423830658,67.69632644819427";
        runTransform();
    }

    @Test
    public void testDisappearingLines() {
        LOGGER.log(Level.INFO, "testDisappearingLines()");
        BBOX_STRING = "3.359017,5.845258,353.879973,75.430046";
        runTransform();
    }

    @Test
    public void test100KmLongitudeLines() {
        LOGGER.log(Level.INFO, "test100KmLongitudeLines()");
        BBOX_STRING = "-81.842667,18.678922,-33.221757,38.454316";
        runTransform();
    }

    @Test
    public void testMergingGridLines() {
        LOGGER.log(Level.INFO, "testMergingGridLines()");
        BBOX_STRING = "-52.089551,16.218235,-13.861099,35.454234";
        runTransform();
    }

    @Test
    public void test100KmSouthernHemisphere() {
        LOGGER.log(Level.INFO, "test100KmSouthernHemisphere()");
        BBOX_STRING = "-55.481975,-18.739429,-14.338434,1.775099";
        runTransform();
    }

    @Test
    public void test100KmRowH() {
        LOGGER.log(Level.INFO, "test100KmRowH()");
        BBOX_STRING = "-87.074453,-42.882977,-51.718504,-28.674088";
        runTransform();
    }

    @Test
    public void test100KmRowJ() {
        LOGGER.log(Level.INFO, "test100KmRowJ()");
        BBOX_STRING = "133.149741,-35.082814,167.076949,-20.623165";
        runTransform();
    }

    @Test
    public void test100KmRowF() {
        LOGGER.log(Level.INFO, "test100KmRowF()");
        BBOX_STRING = "-59.412484,-59.256934,-3.810432,-42.723053";
        runTransform();
    }

    @Test
    public void test100KmRowC() {
        LOGGER.log(Level.INFO, "test100KmRowC()");
        BBOX_STRING = "-97.926636,-81.256492,-11.157767,-70.308697";
        runTransform();
    }

    @Test
    public void test100KmRowX() {
        LOGGER.log(Level.INFO, "test100KmRowX()");
        BBOX_STRING = "-105.410152,70.507829,20.794090,84.143197";
        runTransform();
    }

    @Test
    public void testNew100KmGrid() {
        LOGGER.log(Level.INFO, "testNew100KmGrid()");
        BBOX_STRING = "-59.344038,10.596479,-19.469937,28.888508";
        runTransform();
    }

    @Test
    public void test10KmGrid() {
        LOGGER.log(Level.INFO, "test10KmGrid()");
        BBOX_STRING = "-53.304047,11.599774,-51.163903,12.752860";
        runTransform();
    }

    @Test
    public void test10KmGridQ() {
        LOGGER.log(Level.INFO, "test10KmGridQ()");
        BBOX_STRING = "-47.025475,20.624114,-43.288730,22.604471";
        runTransform();
    }

    @Test
    public void test10KmGridG() {
        LOGGER.log(Level.INFO, "test10KmGridG()");
        BBOX_STRING = "-28.682743,-45.245593,-26.748664,-44.533060";
        runTransform();
    }

    @Test
    public void test10KmGridJ() {
        LOGGER.log(Level.INFO, "test10KmGridJ()");
        BBOX_STRING = "-39.921234,-27.546989,-38.275653,-26.794235";
        runTransform();
    }

    @Test
    public void testExtraLines() {
        LOGGER.log(Level.INFO, "testExtraLines()");
        BBOX_STRING = "-81.982735,51.944581,-80.588352,52.468305";
        runTransform();
    }

    @Test
    public void test100kmV() {
        LOGGER.log(Level.INFO, "test100kmV()");
        BBOX_STRING = "-39.502220,61.783807,-38.443997,62.098470";
        runTransform();
    }

    @Test
    public void testBreakingLines() {
        LOGGER.log(Level.INFO, "testBreakingLines()");
        BBOX_STRING = "-73.107265,12.509297,-27.282962,34.250179";
        runTransform();
    }

    public void runTransform() {
        map.clear();
        map.add("BBOX", BBOX_STRING);
        stub(uriInfo.getQueryParameters()).toReturn(map);
        res.setUriInfo(uriInfo);
        res.getGridKml();
    }
}
