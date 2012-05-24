/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.controls;

import com.moesol.gwt.maps.client.GeodeticCoords;
import java.io.Serializable;

public class Tag implements Serializable {

    private String mName;
    private GeodeticCoords mGeo;

    public Tag(String name, GeodeticCoords geo) {
        mName = name;
        mGeo = geo;
    }

    public Tag() {
    }

    public String getName() {
        return mName;
    }

    public GeodeticCoords getGeodeticCoords() {
        return mGeo;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setGeodeticCoords(GeodeticCoords geo) {
        mGeo = geo;
    }

    @Override
    public String toString() {
        return mName + ": " + mGeo;
    }
}
