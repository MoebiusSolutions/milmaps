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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.moesol.gwt.maps.client.GeodeticCoords;

@RemoteServiceRelativePath("tagControl")
public interface TagControlService extends RemoteService {

    boolean saveTagToDisk(String name, GeodeticCoords gc);

    boolean deleteTagFromDisk(String name);
    
    Tag[] loadTagsFromDisk() throws Exception;
}
