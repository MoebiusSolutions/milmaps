package com.moesol.gwt.maps.client.controls;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * A label with an outline.
 */
public class OutlinedLabelLegacy extends FlowPanel {
	
        HTML text, top, right, bottom, left, topLeft, bottomRight, bottomLeft, topRight;

	public OutlinedLabelLegacy() {
            
            text = new HTML();
            top = new HTML();
            bottom = new HTML();
            right = new HTML();
            left = new HTML();
            topLeft = new HTML();
            bottomLeft = new HTML();
            topRight = new HTML();
            bottomRight = new HTML();
            
            FlowPanel root = new FlowPanel();
            root.getElement().setAttribute("style", "position:relative");
            text.getElement().setAttribute("style", "position:absolute;z-index:1;left:1px;top:0px;");
            top.getElement().setAttribute("style", "position:absolute;left:1px;top:-1px;");
            bottom.getElement().setAttribute("style", "position:absolute;left:1px;top:1px");
            right.getElement().setAttribute("style", "position:absolute;left:2px;top:0px;");
            left.getElement().setAttribute("style", "position:absolute;left:0px;top:0px;");
            topLeft.getElement().setAttribute("style", "position:absolute;left:0px;top:-1px;");
            bottomLeft.getElement().setAttribute("style", "position:absolute;left:0px;top:1px;");
            topRight.getElement().setAttribute("style", "position:absolute;left:2px;top:-1px;");
            bottomRight.getElement().setAttribute("style", "position:absolute;left:2px;top:1px;");
            
            text.getElement().setAttribute("class", "map-OutlinedLabelText");
            top.getElement().setAttribute("class", "map-OutlinedLabelOutline");
            bottom.getElement().setAttribute("class", "map-OutlinedLabelOutline");
            right.getElement().setAttribute("class", "map-OutlinedLabelOutline");
            left.getElement().setAttribute("class", "map-OutlinedLabelOutline");
            topLeft.getElement().setAttribute("class", "map-OutlinedLabelOutline");
            bottomLeft.getElement().setAttribute("class", "map-OutlinedLabelOutline");
            topRight.getElement().setAttribute("class", "map-OutlinedLabelOutline");
            bottomRight.getElement().setAttribute("class", "map-OutlinedLabelOutline");
            
            root.add(text);
            root.add(top);
            root.add(bottom);
            root.add(right);
            root.add(left);
            root.add(topLeft);
            root.add(bottomLeft);
            root.add(topRight);
            root.add(bottomRight);
            
            this.add(root);
	}

	/**
	 * Set the text value of the label.
	 * @param value
	 */
	public void setText(String value) {
		String safeValue = SafeHtmlUtils.htmlEscape(value);
		text.setHTML(safeValue);
		top.setHTML(safeValue);
		right.setHTML(safeValue);
		bottom.setHTML(safeValue);
		left.setHTML(safeValue);
		topLeft.setHTML(safeValue);
		bottomRight.setHTML(safeValue);
		topRight.setHTML(safeValue);
		bottomLeft.setHTML(safeValue);
	}
}
