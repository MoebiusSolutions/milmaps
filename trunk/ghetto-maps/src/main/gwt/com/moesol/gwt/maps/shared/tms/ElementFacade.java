package com.moesol.gwt.maps.shared.tms;

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
