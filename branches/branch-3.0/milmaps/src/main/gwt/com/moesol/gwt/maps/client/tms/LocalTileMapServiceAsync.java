/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.tms;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.moesol.gwt.maps.shared.tms.TileMapMetadata;
import com.moesol.gwt.maps.shared.tms.TileMapServiceAsync;
import com.moesol.gwt.maps.shared.tms.TileMapServiceMetadata;

/**
 * Retrieves TMS metadata from a same-domain source
 */
public class LocalTileMapServiceAsync implements TileMapServiceAsync {

	@Override
	public void getMapMetadata(final String serviceUrl, final String mapId,
			final AsyncCallback<TileMapMetadata> callback) {

	}

	@Override
	public void getServiceMetadata(final String serviceUrl,
			final AsyncCallback<TileMapServiceMetadata> callback) {
		final RequestBuilder requestBuilder = new RequestBuilder(
				RequestBuilder.GET, serviceUrl);
		requestBuilder.setHeader("Accept", "text/xml");
		try {
			requestBuilder.sendRequest(null,
					new com.google.gwt.http.client.RequestCallback() {
						@Override
						public void onError(final Request request,
								final Throwable exception) {
							// TODO: do something more useful here
							Window.alert("error in TileMapServiceMetadata.getMetadata");
						}

						@Override
						public void onResponseReceived(
								final Request request,
								final com.google.gwt.http.client.Response response) {
							Document doc = XMLParser.parse(response.getText());
							callback.onSuccess(TileMapServiceMetadata
									.parse(new ClientElementFacade(doc
											.getDocumentElement())));
						}
					});
		} catch (final RequestException e) {
			callback.onFailure(e);
		}
	}

}
