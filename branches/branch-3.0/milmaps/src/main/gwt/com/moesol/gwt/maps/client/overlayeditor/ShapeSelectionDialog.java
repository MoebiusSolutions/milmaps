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
import com.moesol.gwt.maps.client.graphics.NewArcTool;
import com.moesol.gwt.maps.client.graphics.NewBoxTool;
import com.moesol.gwt.maps.client.graphics.NewCircleTool;
import com.moesol.gwt.maps.client.graphics.NewEllipseTool;
import com.moesol.gwt.maps.client.graphics.NewFreeFormTool;
import com.moesol.gwt.maps.client.graphics.NewLineTool;
import com.moesol.gwt.maps.client.graphics.NewRectTool;
import com.moesol.gwt.maps.client.graphics.NewTriangleTool;
import com.moesol.gwt.maps.client.graphics.SelectShape;

public class ShapeSelectionDialog extends DialogBox {
	public static String[] obj = {"BackToMap", "Arc", "Box", "Circle", 
								  "Ellipse", "Free Form", "Line",
								  "Rectangle", "Triangle",
								  "SelectTool", "Do Something" };
	private final IShapeEditor m_shapeEditor;
	
	public ShapeSelectionDialog(IShapeEditor se) {
		m_shapeEditor = se;
        //final DialogBox me = this;
        setText("Graphic Selection");
        setGlassEnabled(true);
        ListBox list = getListBox(true);
        list.setItemSelected(0, true);
        setWidget(list);
        list.setSelectedIndex(obj.length-1);
    }
    private int m_selectedItem = 0;
    
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
        listBox.addChangeHandler(new ChangeHandler(){
        	@Override
        	public void onChange(ChangeEvent event){
	        		m_selectedItem = listBox.getSelectedIndex();
	        		if (m_selectedItem > 0){
	        			IShapeTool st = createShapeTool(m_shapeEditor, obj[m_selectedItem]);
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
        if(strShape.compareTo(obj[1]) == 0){
        	return new NewArcTool(editor);
        }
        
        if(strShape.compareTo(obj[2]) == 0){
        	return new NewBoxTool(editor);
        }
        
        if(strShape.compareTo(obj[3]) == 0){
        	return new NewCircleTool(editor);
        }
        
        if(strShape.compareTo(obj[4]) == 0){
        	return new NewEllipseTool(editor);
        }
        
        if(strShape.compareTo(obj[5]) == 0){
        	return new NewFreeFormTool(editor);
        }
        
        if(strShape.compareTo(obj[6]) == 0){
        	return new NewLineTool(editor);
        }
        
        if(strShape.compareTo(obj[7]) == 0){
        	return new NewRectTool(editor);
        }
        
        if(strShape.compareTo(obj[8]) == 0){
        	return new NewTriangleTool(editor);
        }
        
        if(strShape.compareTo(obj[9]) == 0){
        	return new SelectShape(editor);
        }
        return null;
    }
}
