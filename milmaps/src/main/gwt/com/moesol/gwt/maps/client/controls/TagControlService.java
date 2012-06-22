package com.moesol.gwt.maps.client.controls;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.moesol.gwt.maps.client.GeodeticCoords;

@RemoteServiceRelativePath("tagControl")
public interface TagControlService extends RemoteService {

    boolean saveTagToDisk(String name, GeodeticCoords gc);

    boolean deleteTagFromDisk(String name);
    
    Tag[] loadTagsFromDisk() throws Exception;
}
