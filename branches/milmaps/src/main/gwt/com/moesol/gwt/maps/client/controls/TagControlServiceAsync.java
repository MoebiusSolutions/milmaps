/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client.controls;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.moesol.gwt.maps.client.GeodeticCoords;

public interface TagControlServiceAsync {

    void saveTagToDisk(String name, GeodeticCoords gc, AsyncCallback<Boolean> callback);

    void deleteTagFromDisk(String name, AsyncCallback<Boolean> callback);
    
    void loadTagsFromDisk(AsyncCallback<Tag[]> callback);
}
