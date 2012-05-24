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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * A label with an outline.
 */
public class OutlinedLabel extends Widget {

	private static OutlinedLabelUiBinder uiBinder = GWT
			.create(OutlinedLabelUiBinder.class);
	
	@UiField
	DivElement text;
	
	@UiField
	DivElement top;
	
	@UiField
	DivElement right;
	
	@UiField
	DivElement bottom;
	
	@UiField
	DivElement left;
	
	@UiField
	DivElement topLeft;
	
	@UiField
	DivElement bottomRight;
	
	@UiField
	DivElement bottomLeft;
	
	@UiField
	DivElement topRight;

	interface OutlinedLabelUiBinder extends
			UiBinder<Element, OutlinedLabel> {
	}

	public OutlinedLabel() {
		setElement(uiBinder.createAndBindUi(this));
	}

	/**
	 * Set the text value of the label.
	 * @param value
	 */
	public void setText(String value) {
		String safeValue = SafeHtmlUtils.htmlEscape(value);
		text.setInnerHTML(safeValue);
		top.setInnerHTML(safeValue);
		right.setInnerHTML(safeValue);
		bottom.setInnerHTML(safeValue);
		left.setInnerHTML(safeValue);
		topLeft.setInnerHTML(safeValue);
		bottomRight.setInnerHTML(safeValue);
		topRight.setInnerHTML(safeValue);
		bottomLeft.setInnerHTML(safeValue);
	}
        
	/**
	 * Set the text value of the label while allowing for the inclusion of HTML tags.
	 * @param value
	 */
	public void setHtml(String value) {
		text.setInnerHTML(value);
		top.setInnerHTML(value);
		right.setInnerHTML(value);
		bottom.setInnerHTML(value);
		left.setInnerHTML(value);
		topLeft.setInnerHTML(value);
		bottomRight.setInnerHTML(value);
		topRight.setInnerHTML(value);
		bottomLeft.setInnerHTML(value);
	}
}
