/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.shared.tms;

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


/*
 * Class to simplified common interface to client and server side
 * XML Element objects, which, while they implement the same logical
 * interface, unfortunately, don't implement the same
 * Java interface. 
 * 
 * An alternative would have been to create an adapter
 * to adapt the org.w3c.dom.Element interface to the GWT Element
 * interface, but it would have been a lot more code, implementing
 * many unused methods.
 * 
 * Extending on of the ElementImpl implementations and implementing
 * the GWT Element interface would have just been a few lines of
 * code, but that wasn't a good solution either because that would
 * tightly couple the project to a particular DOM implementation.
 * 
 */
public interface ElementFacade {
	public String getAttribute(String name);
	public NodeListFacade getElementsByTagName(String tagName);
	
	public interface NodeListFacade {
		public int getLength();
		public ElementFacade item(int index);
	}
}
