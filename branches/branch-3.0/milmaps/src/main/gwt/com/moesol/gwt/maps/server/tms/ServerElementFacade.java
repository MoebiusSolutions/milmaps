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


import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.moesol.gwt.maps.shared.tms.ElementFacade;

//The code here is nearly identical to ClientElementFacade, see ElementFacade for
//justification
public class ServerElementFacade implements ElementFacade {
	private Element wrappedElement;
	
	public ServerElementFacade(Element elementToWrap) {
		wrappedElement = elementToWrap;
	}
	
	@Override
	public String getAttribute(String name) {
		return wrappedElement.getAttribute(name);
	}

	@Override
	public NodeListFacade getElementsByTagName(String tagName) {
		return new NodeListFacade(wrappedElement.getElementsByTagName(tagName));
	}
	
	private static class NodeListFacade implements ElementFacade.NodeListFacade {
		private NodeList wrappedList;
		
		public NodeListFacade(NodeList listToWrap) {
			wrappedList = listToWrap;
		}

		@Override
		public int getLength() {
			if (wrappedList == null) {
				return 0;
			}
			return wrappedList.getLength();
		}

		@Override
		public ElementFacade item(int index) {
			if (wrappedList == null) {
				throw new IndexOutOfBoundsException();
			}
			Node node = wrappedList.item(index);
			assert node instanceof Element;
			return new ServerElementFacade((Element)node);
		}
	}
}
