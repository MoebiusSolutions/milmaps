/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.server.tms;

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


import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.moesol.gwt.maps.shared.tms.RemoteTileMapService;
import com.moesol.gwt.maps.shared.tms.TileMapMetadata;
import com.moesol.gwt.maps.shared.tms.TileMapServiceMetadata;

/**
 * Allows cross-domain TMS service calls.
 */
public class RemoteTileMapServiceImpl extends RemoteServiceServlet implements
		RemoteTileMapService {
	private static final long serialVersionUID = 1L;

	@Override
	public TileMapMetadata getMapMetadata(final String serviceUrl,
			final String mapId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TileMapServiceMetadata getServiceMetadata(final String serviceUrl) {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(serviceUrl);
			
			return TileMapServiceMetadata.parse(new ServerElementFacade(document.getDocumentElement()));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
