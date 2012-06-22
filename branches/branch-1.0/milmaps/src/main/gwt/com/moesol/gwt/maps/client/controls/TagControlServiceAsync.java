package com.moesol.gwt.maps.client.controls;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.moesol.gwt.maps.client.GeodeticCoords;

public interface TagControlServiceAsync {

    void saveTagToDisk(String name, GeodeticCoords gc, AsyncCallback<Boolean> callback);

    void deleteTagFromDisk(String name, AsyncCallback<Boolean> callback);
    
    void loadTagsFromDisk(AsyncCallback<Tag[]> callback);
}
