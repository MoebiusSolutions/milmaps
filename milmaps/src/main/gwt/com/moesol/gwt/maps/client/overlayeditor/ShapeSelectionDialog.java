/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.overlayeditor;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.moesol.gwt.maps.client.graphics.IShapeEditor;
import com.moesol.gwt.maps.client.graphics.IShapeTool;
import com.moesol.gwt.maps.client.graphics.NewCircleTool;
import com.moesol.gwt.maps.client.graphics.SelectShape;

public class ShapeSelectionDialog extends DialogBox {
	private final IShapeEditor m_shapeEditor;
	
	public ShapeSelectionDialog(IShapeEditor se) {
		m_shapeEditor = se;
        final DialogBox me = this;
        setText("Graphic Selection");
        setGlassEnabled(true);
        ListBox list = getListBox(true);
        list.setItemSelected(0, true);
        setWidget(list);
    }
    private int m_selectedItem;
    public static String[] obj = {"Arc", "Box", "Circle", "Ellipse","SelectTool" };
    
    public String getSelectedItem(){ return obj[m_selectedItem]; }
    
    
    private ListBox getListBox(boolean dropdown){
        final ListBox listBox = new ListBox();
        //widget.addStyleName("demo-ListBox");
        for(int i = 0; i < obj.length; i++){
        	listBox.addItem(obj[i]);
        }
        if(!dropdown){
        	listBox.setVisibleItemCount(3);
        }
        listBox.setItemSelected(0, true);
        listBox.addChangeHandler(new ChangeHandler(){
        	public void onChange(ChangeEvent event)
        	{
        		m_selectedItem = listBox.getSelectedIndex();
        		if (m_selectedItem > -1){
        			IShapeTool st = createShapeTool(m_shapeEditor, obj[m_selectedItem]);
        			m_shapeEditor.setShapeTool(st);
        			m_shapeEditor.setEventFocus(true);
        			hideDialog();
        		}
        	}
        	});
       return listBox;
    }
    
    private void hideDialog(){
    	this.hide();
    }
    
    public IShapeTool createShapeTool(IShapeEditor editor, String strShape) {
        if(strShape.compareTo(obj[2]) == 0){
        	return new NewCircleTool(editor);
        }
        if(strShape.compareTo(obj[4]) == 0){
        	return new SelectShape(editor);
        }
        return null;
    }
}
