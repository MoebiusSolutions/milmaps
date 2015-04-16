/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

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


import com.google.gwt.event.dom.client.KeyCodes;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;

public abstract class AbstractShape implements IShape{
	protected static int TRANSLATE_HANDLE_OFFSET_X = 20;
	protected String m_parentGuid = "NO_GUID";
	protected String m_guid = "NO_GUID";
	protected String m_type;
	protected String m_color = "rgb(255, 255, 255)";
	protected boolean m_bSeletected = false;
	protected boolean m_needsUpdate = false;
	
	protected boolean m_ctrlKeydown = false;
	protected boolean m_shiftKeydown = false;
	protected boolean m_newShape = true;
	
	protected static final RangeBearingS m_rb = new RangeBearingS();
	
	protected ICoordConverter m_convert;
	
	// Shape interface implementation
	public void setCoordConverter(ICoordConverter cc) {
		m_convert = cc;
	}
	
	public void setKeyboardFlags(boolean altKey, boolean shiftKey) {
		m_ctrlKeydown = altKey;
		m_shiftKeydown = shiftKey;
	}
	
	public boolean getKeyDownFlag(int keyCode){
		if(keyCode == KeyCodes.KEY_CTRL){
			return m_ctrlKeydown;
		}
		else if(keyCode == KeyCodes.KEY_SHIFT){
			return m_shiftKeydown;
		}
		return false;
	}
	
	@Override
	public void setNew(boolean newShape){
		m_newShape = newShape;
	}
	
	@Override
    public boolean isNew(){ return m_newShape; }
	
	@Override
	public String getColor() {
		return m_color;
	}

	@Override
	public void setColor(String color) {
		m_color = color;
	}
	
	@Override
	public void setParentGuid(String guid) {
		m_parentGuid = guid;
	}
	
	@Override
	public String getParentGuid() {
		return m_parentGuid;
	}
	
	@Override
	public void setGuid(String guid) {
		m_guid = guid;
	}
	
	@Override
	public String getGuid() {
		return m_guid;
	}
	
	@Override
	public void setType(String type) {
		m_type = type;
	}
	
	@Override
	public String getType() {
		return m_type;
	}
	
	@Override
	public IShape selected(boolean selected) {
		m_bSeletected = selected;
		return (IShape)this;
	}
	
	@Override
	public boolean isSelected() {
		return m_bSeletected;
	}
	
	@Override
	public boolean needsUpdate() {
		if (m_needsUpdate) {
			m_needsUpdate = false;
			return true;
		}
		return false;
	}
}
