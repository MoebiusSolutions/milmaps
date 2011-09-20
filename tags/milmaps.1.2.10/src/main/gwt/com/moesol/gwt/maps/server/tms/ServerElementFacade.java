package com.moesol.gwt.maps.server.tms;

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
