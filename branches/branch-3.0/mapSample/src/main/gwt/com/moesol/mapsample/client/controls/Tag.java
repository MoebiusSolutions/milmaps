/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.mapsample.client.controls;

import com.moesol.gwt.maps.client.GeodeticCoords;
import java.io.Serializable;

public class Tag implements Serializable {

    private String mName;
    private GeodeticCoords mGeo;
    private String mSymbol;

    public Tag(String name, GeodeticCoords geo, String symbol) {
        mName = name;
        mGeo = geo;
        mSymbol = symbol;
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

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        mSymbol = symbol;
    }
}
