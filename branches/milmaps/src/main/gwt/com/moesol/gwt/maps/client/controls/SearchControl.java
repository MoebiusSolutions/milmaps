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
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A general search control.
 */
public class SearchControl extends Composite implements MouseOutHandler,
		HasMouseOutHandlers {

	private static SearchControlUiBinder uiBinder = GWT
			.create(SearchControlUiBinder.class);

	interface SearchControlUiBinder extends UiBinder<Widget, SearchControl> {
	}

	@UiField
	TextBox input;

	@UiField
	HTML button;

	private boolean m_inputFocused;

	private FocusHandler m_inputFocusHandler = new FocusHandler() {
		@Override
		public void onFocus(FocusEvent event) {
			m_inputFocused = true;
		}
	};

	private BlurHandler m_inputBlurHandler = new BlurHandler() {
		@Override
		public void onBlur(BlurEvent event) {
			m_inputFocused = false;
			doMouseOut();
		}
	};

	public SearchControl() {
		initWidget(uiBinder.createAndBindUi(this));

		input.addFocusHandler(m_inputFocusHandler);
		input.addBlurHandler(m_inputBlurHandler);

		addMouseOutHandler(this);
	}

	@UiHandler("button")
	public void onButtonMouseDown(MouseDownEvent e) {
//		Widget w = (Widget) e.getSource();
//		w.addStyleName("map-SearchControlButtonMouseDown");

		fireSearchEvent();
	}

	private void fireSearchEvent() {
		String inputValue = input.getValue();
		if (inputValue != null && !inputValue.isEmpty()) {
			SearchEvent event = new SearchEvent(inputValue);
			fireEvent(event);
		}
	}

//	@UiHandler("button")
//	public void onButtonMouseUp(MouseUpEvent e) {
//		Widget w = (Widget) e.getSource();
//		w.removeStyleName("map-SearchControlButtonMouseDown");
//	}

	@UiHandler("button")
	public void onButtonMouseOver(MouseOverEvent e) {
		Widget w = (Widget) e.getSource();
		w.addStyleName("map-SmallButtonMouseOver");
		input.getElement().getStyle().setDisplay(Display.BLOCK);
	}
	
	@UiHandler("input")
	public void onInputKeyboardEvent(KeyPressEvent e) {
		if (e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			fireSearchEvent();
			doMouseOut();
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		if (!m_inputFocused) {
			doMouseOut();
		}
	}

	private void doMouseOut() {
		button.removeStyleName("map-SmallButtonMouseOver");
		input.getElement().getStyle().setDisplay(Display.NONE);
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	/**
	 * Add a SearchHandler for when the user submits a query.
	 * @param handler The handler to add.
	 * @return
	 */
	public HandlerRegistration addSearchHandler(SearchHandler handler) {
		return addHandler(handler, SearchEvent.TYPE);
	}
}
