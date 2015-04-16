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

import com.google.gwt.user.client.ui.IsWidget;

public interface TileMapServiceView extends IsWidget {
	
	public void setPresenter(Presenter presenter);
	public void displayData(TileMapMetadata[] metadata);
	
	public interface Presenter {

		void onRemoveAllDisplayedItems();

		void onRemoveDisplayedItem();

		void onAddDisplayedItem();

		void onAvailableSelected(TileMapMetadata selectedObject);

		void onDisplayedSelected(TileMapMetadata selectedObject);

		void onMoveDisplayedItemDown();

		void onMoveDisplayedItemUp();
	}

	public void removeAllDisplayedItems();
	public void removeDisplayedItem(TileMapMetadata metadata);
	public void addDisplayedItem(TileMapMetadata selectedAvailableItem);
	public void addAvailableItem(TileMapMetadata selectedDisplayedItem);
	public void removeAvailableItem(TileMapMetadata selectedAvailableItem);
	public void addAvailableItems(ArrayList<TileMapMetadata> displayedItems);
	public void setAddAvailableItemEnabled(boolean enabled);
	public void setRemoveDisplayedItemEnabled(boolean b);
	public void setRemoveAllDisplayedItemsEnabled(boolean b);
	public void setMoveDisplayedItemUpEnabled(boolean b);
	public void setMoveDisplayedItemDownEnabled(boolean b);
}
