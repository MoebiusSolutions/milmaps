/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.tms;

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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.uibinder.client.UiHandler;

public class TileMapServiceListView extends Composite implements
		TileMapServiceView {

	private static final Binder binder = GWT.create(Binder.class);

	CellList<TileMapMetadata> availableList;
	CellList<TileMapMetadata> displayedList;

	ListDataProvider<TileMapMetadata> displayedListData = new ListDataProvider<TileMapMetadata>();
	ListDataProvider<TileMapMetadata> availableListData = new ListDataProvider<TileMapMetadata>();

	@UiField
	HorizontalPanel horizontalPanel;
	@UiField
	ScrollPanel leftScrollPanel;
	@UiField
	ScrollPanel rightScrollPanel;
	@UiField
	VerticalPanel buttonPanel;
	@UiField
	Button addButton;
	@UiField
	Button removeButton;
	@UiField
	Button removeAllButton;
	@UiField Button moveUpButton;
	@UiField Button moveDownButton;

	private Presenter presenter;

	interface Binder extends UiBinder<Widget, TileMapServiceListView> {
	}

	interface ListResources extends CellList.Resources {
		@Override
		@Source({ CellList.Style.DEFAULT_CSS, "CellList.css" })
		CellList.Style cellListStyle();
	}

	private Comparator<TileMapMetadata> comparator = new Comparator<TileMapMetadata>() {
		@Override
		public int compare(TileMapMetadata o1, TileMapMetadata o2) {
			int compareTitles = o1.getTitle().compareTo(o2.getTitle());

			if (compareTitles != 0)
				return compareTitles;

			int compareSrs = o1.getSrs().compareTo(o2.getSrs());

			if (compareSrs != 0)
				return compareSrs;

			return o1.getTileImageFormat().compareTo(o2.getTileImageFormat());
		}
	};

	public TileMapServiceListView() {
		initWidget(binder.createAndBindUi(this));

		Cell<TileMapMetadata> cell = new AbstractCell<TileMapMetadata>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context ctx,
					TileMapMetadata value, SafeHtmlBuilder sb) {
				sb.appendEscaped(value.getTitle());
				sb.appendEscaped(" (");
				sb.appendEscaped(value.getSrs());
				sb.appendEscaped(", ");
				sb.appendEscaped(value.getTileImageFormat());
				sb.appendEscaped(")");
			}
		};

		ListResources listResources = GWT.create(ListResources.class);

		ProvidesKey<TileMapMetadata> keyProvider = new ProvidesKey<TileMapMetadata>() {
			@Override
			public Object getKey(TileMapMetadata item) {
				return item.getUrl();
			}
		};
		availableList = new CellList<TileMapMetadata>(cell, listResources,
				keyProvider);
		final SingleSelectionModel<TileMapMetadata> availableSelectionModel = new SingleSelectionModel<TileMapMetadata>();
		availableSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						presenter.onAvailableSelected(availableSelectionModel
								.getSelectedObject());
					}
				});
		availableList.setSelectionModel(availableSelectionModel);
		availableListData.addDataDisplay(availableList);

		availableList.setHeight("100%");
		availableList.setWidth("200px");

		leftScrollPanel.add(availableList);

		displayedList = new CellList<TileMapMetadata>(cell, listResources,
				keyProvider);
		displayedList.setHeight("100%");
		displayedList.setWidth("200px");
		final SingleSelectionModel<TileMapMetadata> displayedSelectionModel = new SingleSelectionModel<TileMapMetadata>();
		displayedSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						presenter.onDisplayedSelected(displayedSelectionModel
								.getSelectedObject());
					}
				});
		displayedList.setSelectionModel(displayedSelectionModel);
		displayedListData.addDataDisplay(displayedList);

		rightScrollPanel.add(displayedList);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void displayData(TileMapMetadata[] metadata) {
		for (int i = 0; i < metadata.length; i++) {
			TileMapMetadata item = metadata[i];

			// TODO: need some kind of configuration for supported formats,
			// probably stored
			// in the MapView, rather than hard-coded here
			if (item.getTileImageFormat().equals("png")
					&& item.getSrs().endsWith("4326")) {
				availableListData.getList().add(item);
			}
		}
		sortAvailable();
	}

	@Override
	public void removeAllDisplayedItems() {
		displayedListData.getList().clear();
	}

	@Override
	public void removeDisplayedItem(TileMapMetadata metadata) {
		displayedListData.getList().remove(metadata);
	}

	@Override
	public void addDisplayedItem(TileMapMetadata selectedAvailableItem) {
		displayedListData.getList().add(selectedAvailableItem);
	}

	@Override
	public void addAvailableItem(TileMapMetadata selectedDisplayedItem) {
		availableListData.getList().add(selectedDisplayedItem);
		sortAvailable();
	}

	@Override
	public void removeAvailableItem(TileMapMetadata selectedAvailableItem) {
		availableListData.getList().remove(selectedAvailableItem);
	}

	@Override
	public void addAvailableItems(ArrayList<TileMapMetadata> displayedItems) {
		availableListData.getList().addAll(displayedItems);
		sortAvailable();
	}

	private void sortAvailable() {
		Collections.sort(availableListData.getList(), comparator);
	}

	@Override
	public void setAddAvailableItemEnabled(boolean enabled) {
		addButton.setEnabled(enabled);
	}

	@Override
	public void setRemoveDisplayedItemEnabled(boolean b) {
		removeButton.setEnabled(b);
	}

	@Override
	public void setRemoveAllDisplayedItemsEnabled(boolean b) {
		removeAllButton.setEnabled(b);
	}

	@UiHandler("addButton")
	void onAddButtonClick(ClickEvent event) {
		presenter.onAddDisplayedItem();
	}

	@UiHandler("removeButton")
	void onRemoveButtonClick(ClickEvent event) {
		presenter.onRemoveDisplayedItem();
	}

	@UiHandler("removeAllButton")
	void onRemoveAllButtonClick(ClickEvent event) {
		presenter.onRemoveAllDisplayedItems();
	}

	@UiHandler("moveDownButton")
	void onMoveDownButtonClick(ClickEvent event) {
		presenter.onMoveDisplayedItemDown();
	}
	
	@UiHandler("moveUpButton")
	void onMoveUpButtonClick(ClickEvent event) {
		presenter.onMoveDisplayedItemUp();
	}

	@Override
	public void setMoveDisplayedItemUpEnabled(boolean b) {
		moveUpButton.setEnabled(b);
	}

	@Override
	public void setMoveDisplayedItemDownEnabled(boolean b) {
		moveDownButton.setEnabled(b);
	}
}
