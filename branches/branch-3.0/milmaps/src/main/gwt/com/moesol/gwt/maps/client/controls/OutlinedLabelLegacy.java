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


import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

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
            root.getElement().getStyle().setProperty("position", "relative");
//            text.getElement().setAttribute("style", "position:absolute;z-index:1;left:1px;top:0px;");
            text.getElement().getStyle().setProperty("position", "absolute");
            text.getElement().getStyle().setProperty("zIndex", "1");
            text.getElement().getStyle().setProperty("left", "1px");
            text.getElement().getStyle().setProperty("top", "0px");
//            top.getElement().setAttribute("style", "position:absolute;left:1px;top:-1px;");
            top.getElement().getStyle().setProperty("position", "absolute");
            top.getElement().getStyle().setProperty("left", "1px");
            top.getElement().getStyle().setProperty("top", "-1px");
//            bottom.getElement().setAttribute("style", "position:absolute;left:1px;top:1px");
            bottom.getElement().getStyle().setProperty("position", "absolute");
            bottom.getElement().getStyle().setProperty("left", "1px");
            bottom.getElement().getStyle().setProperty("top", "1px");
//            right.getElement().setAttribute("style", "position:absolute;left:2px;top:0px;");
            right.getElement().getStyle().setProperty("position", "absolute");
            right.getElement().getStyle().setProperty("left", "2px");
            right.getElement().getStyle().setProperty("top", "0px");
//            left.getElement().setAttribute("style", "position:absolute;left:0px;top:0px;");
            left.getElement().getStyle().setProperty("position", "absolute");
            left.getElement().getStyle().setProperty("left", "0px");
            left.getElement().getStyle().setProperty("top", "0px");
//            topLeft.getElement().setAttribute("style", "position:absolute;left:0px;top:-1px;");
            topLeft.getElement().getStyle().setProperty("position", "absolute");
            topLeft.getElement().getStyle().setProperty("left", "0px");
            topLeft.getElement().getStyle().setProperty("top", "-1px");
//            bottomLeft.getElement().setAttribute("style", "position:absolute;left:0px;top:1px;");
            bottomLeft.getElement().getStyle().setProperty("position", "absolute");
            bottomLeft.getElement().getStyle().setProperty("left", "0px");
            bottomLeft.getElement().getStyle().setProperty("top", "1px");
//            topRight.getElement().setAttribute("style", "position:absolute;left:2px;top:-1px;");
            topRight.getElement().getStyle().setProperty("position", "absolute");
            topRight.getElement().getStyle().setProperty("left", "2px");
            topRight.getElement().getStyle().setProperty("top", "-1px");
//            bottomRight.getElement().setAttribute("style", "position:absolute;left:2px;top:1px;");
            bottomRight.getElement().getStyle().setProperty("position", "absolute");
            bottomRight.getElement().getStyle().setProperty("left", "2px");
            bottomRight.getElement().getStyle().setProperty("top", "1px");
            
            text.setStyleName("map-OutlinedLabelText");
            top.setStyleName("map-OutlinedLabelOutline");
            top.addStyleName("map-OutlineUp");
            bottom.setStyleName("map-OutlinedLabelOutline");
            right.setStyleName("map-OutlinedLabelOutline");
            left.setStyleName("map-OutlinedLabelOutline");
            topLeft.setStyleName("map-OutlinedLabelOutline");
            bottomLeft.setStyleName("map-OutlinedLabelOutline");
            bottomLeft.addStyleName("map-OutlineDown");
            bottomLeft.addStyleName("map-OutlineLeft");
            topRight.setStyleName("map-OutlinedLabelOutline");
            bottomRight.setStyleName("map-OutlinedLabelOutline");
            
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
        
	/**
	 * Set the text value of the label while allowing for the inclusion of HTML tags.
	 * @param value
	 */
	public void setHtml(String value) {
		text.setHTML(value);
		top.setHTML(value);
		right.setHTML(value);
		bottom.setHTML(value);
		left.setHTML(value);
		topLeft.setHTML(value);
		bottomRight.setHTML(value);
		topRight.setHTML(value);
		bottomLeft.setHTML(value);
	}
}
