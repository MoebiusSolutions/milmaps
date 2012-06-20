/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.controls;

import com.google.gwt.user.client.ui.Composite;

public class TextControl  extends Composite {
	private final OutlinedLabel m_textLabel = new OutlinedLabel();
	public TextControl() {
		initWidget(m_textLabel);
		addStyleName("map-TextControl");
	}
	public void setText(String text){
		
		if(text == null){
			throw new IllegalArgumentException("TextControl received null text");
		}
		m_textLabel.setText(text);
	}
}
