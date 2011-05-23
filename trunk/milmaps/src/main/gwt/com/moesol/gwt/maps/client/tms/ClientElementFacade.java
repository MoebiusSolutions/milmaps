package com.moesol.gwt.maps.client.tms;

import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.moesol.gwt.maps.shared.tms.ElementFacade;


// The code here is nearly identical to ServerElementFacade, see ElementFacade for
// justification (there might be a slightly better factoring, however)
class ClientElementFacade implements ElementFacade {
	private Element wrappedElement;
	
	public ClientElementFacade(Element elementToWrap) {
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
			return new ClientElementFacade((Element)node);
		}
	}
}
