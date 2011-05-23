package com.moesol.gwt.maps.shared.tms;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("tms")
public interface RemoteTileMapService extends TileMapService, RemoteService {

}
