/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.controls;

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
