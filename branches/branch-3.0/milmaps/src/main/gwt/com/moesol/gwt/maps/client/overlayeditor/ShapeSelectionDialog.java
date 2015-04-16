/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.overlayeditor;

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



import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.moesol.gwt.maps.client.graphics.IShapeEditor;
import com.moesol.gwt.maps.client.graphics.IShapeTool;
import com.moesol.gwt.maps.client.graphics.NewArcTool;
import com.moesol.gwt.maps.client.graphics.NewArrowTool;
import com.moesol.gwt.maps.client.graphics.NewBoxTool;
import com.moesol.gwt.maps.client.graphics.NewCircleTool;
import com.moesol.gwt.maps.client.graphics.NewEllipseTool;
import com.moesol.gwt.maps.client.graphics.NewPolygonTool;
import com.moesol.gwt.maps.client.graphics.NewFreehandTool;
import com.moesol.gwt.maps.client.graphics.NewLineTool;
import com.moesol.gwt.maps.client.graphics.NewRectangleTool;
import com.moesol.gwt.maps.client.graphics.NewSectorTool;
import com.moesol.gwt.maps.client.graphics.NewTriangleTool;
import com.moesol.gwt.maps.client.graphics.SelectShape;

public class ShapeSelectionDialog extends DialogBox {
	private final IShapeEditor m_shapeEditor;
	
	public ShapeSelectionDialog(IShapeEditor se) {
		m_shapeEditor = se;
        setText("New Graphic Test");
        setGlassEnabled(true);
        ListBox list = getListBox(true);
        list.setItemSelected(0, true);
        setWidget(list);
        list.setSelectedIndex(Obj.name.length-1);
    }
    private int m_selectedItem = 0;
    
    public String getSelectedItem(){ return Obj.name[m_selectedItem]; }
    
    
    
    private ListBox getListBox(boolean dropdown){
        final ListBox listBox = new ListBox();
        //widget.addStyleName("demo-ListBox");
        for(int i = 0; i < Obj.name.length; i++){
        	listBox.addItem(Obj.name[i]);
        }
        if(!dropdown){
        	listBox.setVisibleItemCount(3);
        }
        listBox.addChangeHandler(new ChangeHandler(){
        	@Override
        	public void onChange(ChangeEvent event){
	        		m_selectedItem = listBox.getSelectedIndex();
	        		if (m_selectedItem > 0){
	        			IShapeTool st = createShapeTool(m_shapeEditor, Obj.name[m_selectedItem]);
	        			m_shapeEditor.setShapeTool(st);
	        			m_shapeEditor.setEventFocus(true);
	        		}
	        		else{
	        			m_shapeEditor.setShapeTool(null);
	        			m_shapeEditor.setEventFocus(false);        			
	        		}
	        		hideDialog();
	        	}	
        	});
       return listBox;
    }
    
    private void hideDialog(){
    	this.hide();
    }
    
    public IShapeTool createShapeTool(IShapeEditor editor, String strShape) {
        if(strShape.compareTo(Obj.name[1]) == 0){
        	return new NewArcTool(editor);
        }
        
        if(strShape.compareTo(Obj.name[2]) == 0){
        	return new NewArrowTool(editor);
        }
        
        if(strShape.compareTo(Obj.name[3]) == 0){
        	return new NewBoxTool(editor);
        }
        
        if(strShape.compareTo(Obj.name[4]) == 0){
        	return new NewCircleTool(editor);
        }
        
        if(strShape.compareTo(Obj.name[5]) == 0){
        	return new NewEllipseTool(editor);
        }
        
        if(strShape.compareTo(Obj.name[6]) == 0){
        	return new NewPolygonTool(editor);
        }
        
        if(strShape.compareTo(Obj.name[7]) == 0){
        	return new NewFreehandTool(editor);
        }
        
        if(strShape.compareTo(Obj.name[8]) == 0){
        	return new NewLineTool(editor);
        }
        
        if(strShape.compareTo(Obj.name[9]) == 0){
        	return new NewRectangleTool(editor);
        }
        
        if(strShape.compareTo(Obj.name[10]) == 0){
        	return new NewSectorTool(editor);
        }
        
        if(strShape.compareTo(Obj.name[11]) == 0){
        	return new NewTriangleTool(editor);
        }
        
        if(strShape.compareTo(Obj.name[12]) == 0){
        	return new SelectShape(editor);
        }
        return null;
    }
}
