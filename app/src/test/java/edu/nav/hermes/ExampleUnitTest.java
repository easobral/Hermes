package edu.nav.hermes;

import org.junit.Test;
import org.osmdroid.util.GeoPoint;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    static final double LAT_STEP = 0.02;
    static final double LON_STEP = 0.01;

    private String getFileFromGeopoint(GeoPoint point) {
        Integer x = (int) Math.floor(point.getLatitude() / LAT_STEP);
        Integer y = (int) Math.floor(point.getLongitude() / LON_STEP);
        return "" + x + "_" + y;
    }

    @Test
    public void geoPointFileTest() throws Exception {



    }
}