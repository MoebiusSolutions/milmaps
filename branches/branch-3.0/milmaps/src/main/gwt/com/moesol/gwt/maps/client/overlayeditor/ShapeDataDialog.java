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
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.graphics.Arc;
import com.moesol.gwt.maps.client.graphics.Arrow;
import com.moesol.gwt.maps.client.graphics.Box;
import com.moesol.gwt.maps.client.graphics.Circle;
import com.moesol.gwt.maps.client.graphics.Ellipse;
import com.moesol.gwt.maps.client.graphics.FreeForm;
import com.moesol.gwt.maps.client.graphics.Freehand;
import com.moesol.gwt.maps.client.graphics.IShape;
import com.moesol.gwt.maps.client.graphics.IShapeEditor;
import com.moesol.gwt.maps.client.graphics.IShapeTool;
import com.moesol.gwt.maps.client.graphics.Line;
import com.moesol.gwt.maps.client.graphics.NewArcTool;
import com.moesol.gwt.maps.client.graphics.NewArrowTool;
import com.moesol.gwt.maps.client.graphics.NewBoxTool;
import com.moesol.gwt.maps.client.graphics.NewCircleTool;
import com.moesol.gwt.maps.client.graphics.NewEllipseTool;
import com.moesol.gwt.maps.client.graphics.NewFreeFormTool;
import com.moesol.gwt.maps.client.graphics.NewFreehandTool;
import com.moesol.gwt.maps.client.graphics.NewLineTool;
import com.moesol.gwt.maps.client.graphics.NewRectangleTool;
import com.moesol.gwt.maps.client.graphics.NewSectorTool;
import com.moesol.gwt.maps.client.graphics.NewTriangleTool;
import com.moesol.gwt.maps.client.graphics.Rectangle;
import com.moesol.gwt.maps.client.graphics.Sector;
import com.moesol.gwt.maps.client.graphics.SelectShape;
import com.moesol.gwt.maps.client.graphics.Triangle;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Bearing;
import com.moesol.gwt.maps.client.units.Bearing.Builder;
import com.moesol.gwt.maps.client.units.Distance;

public class ShapeDataDialog extends DialogBox {
	private final IShapeEditor m_shapeEditor;

	public ShapeDataDialog(IShapeEditor se) {
		m_shapeEditor = se;
		setText("Graphic Shape Data Test");
		setGlassEnabled(true);
		ListBox list = getListBox(true);
		list.setItemSelected(0, true);
		setWidget(list);
		list.setSelectedIndex(Obj.name.length - 1);
	}

	private int m_selectedItem = 0;

	public String getSelectedItem() {
		return Obj.name[m_selectedItem];
	}

	private ListBox getListBox(boolean dropdown) {
		final ListBox listBox = new ListBox();
		// widget.addStyleName("demo-ListBox");
		for (int i = 0; i < Obj.name.length; i++) {
			listBox.addItem(Obj.name[i]);
		}
		if (!dropdown) {
			listBox.setVisibleItemCount(3);
		}
		listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				m_selectedItem = listBox.getSelectedIndex();
				if (m_selectedItem > 0) {
					IShapeTool st = createShapeTool(m_shapeEditor,
							Obj.name[m_selectedItem]);
					m_shapeEditor.setShapeTool(st);
					m_shapeEditor.setEventFocus(true);
				} else {
					m_shapeEditor.setShapeTool(null);
					m_shapeEditor.setEventFocus(false);
				}
				hideDialog();
				m_shapeEditor.renderObjects();
			}
		});
		return listBox;
	}

	private void hideDialog() {
		this.hide();
	}
	
	protected IShapeTool createArc(){
		GeodeticCoords cent = new GeodeticCoords(10,10,AngleUnit.DEGREES);
		Bearing startBrg = Bearing.builder(). value(200).degrees().build();
		Bearing endBrg = Bearing.builder().value(70).degrees().build();
		Distance rad = Distance.builder().value(4000).kilometers().build();
		return Arc.create(m_shapeEditor, cent, startBrg, endBrg, rad);
	}
	
	protected IShapeTool createArrow(){
		GeodeticCoords[] pos = new GeodeticCoords[4];
		for ( int i = 0; i < 4; i++){
			pos[i] = new GeodeticCoords(-120 + 4*i, 34 - 4*i, AngleUnit.DEGREES); 
		}
		Distance width = Distance.builder().value(400).kilometers().build();
		return Arrow.create(m_shapeEditor, width, pos);
	}
	
	protected IShapeTool createBox(){
		GeodeticCoords cent = new GeodeticCoords(10,10,AngleUnit.DEGREES);
		Bearing brg = Bearing.builder(). value(200).degrees().build();
		Distance smj = Distance.builder().value(2000).kilometers().build();
		Distance smn = Distance.builder().value(1000).kilometers().build();
		return Box.create(m_shapeEditor, cent, brg, smj, smn);
	}
	
	protected IShapeTool createCircle(){
		GeodeticCoords cent = new GeodeticCoords(10,10,AngleUnit.DEGREES);
		Distance rad = Distance.builder().value(2000).kilometers().build();
		return Circle.create(m_shapeEditor, cent, rad);
	}
	
	protected IShapeTool createEllipse(){
		GeodeticCoords cent = new GeodeticCoords(10,10,AngleUnit.DEGREES);
		Bearing brg = Bearing.builder(). value(100).degrees().build();
		Distance smj = Distance.builder().value(2000).kilometers().build();
		Distance smn = Distance.builder().value(1000).kilometers().build();
		return Ellipse.create(m_shapeEditor, cent, brg, smj, smn);
	}
	
	protected IShapeTool createFreeForm(){
		GeodeticCoords[] pos = new GeodeticCoords[4];
		for ( int i = 0; i < 4; i++){
			pos[i] = new GeodeticCoords(-120 + 4*i, 34 - 4*i, AngleUnit.DEGREES); 
		}
		return FreeForm.create(m_shapeEditor, pos);
	}

	protected IShapeTool createFreehand(){
		GeodeticCoords[] pos = new GeodeticCoords[4];
		for ( int i = 0; i < 4; i++){
			pos[i] = new GeodeticCoords(-120 + 4*i, 34 - 4*i, AngleUnit.DEGREES); 
		}
		return Freehand.create(m_shapeEditor, pos);
	}
	
	protected IShapeTool createLine(){
		GeodeticCoords start = new GeodeticCoords(-120,30,AngleUnit.DEGREES);
		GeodeticCoords end = new GeodeticCoords(-100,10,AngleUnit.DEGREES);
		return Line.create(m_shapeEditor, start,end);
	}
	
	protected IShapeTool createRect(){
		GeodeticCoords start = new GeodeticCoords(-120,30,AngleUnit.DEGREES);
		GeodeticCoords end = new GeodeticCoords(-100,10,AngleUnit.DEGREES);
		return Rectangle.create(m_shapeEditor, start,end);
	}
	
	protected IShapeTool createSector(){
		RangeBearingS rb = new RangeBearingS();
		GeodeticCoords cent = new GeodeticCoords(10,10,AngleUnit.DEGREES);
		GeodeticCoords startRB = rb.gcPointFrom(cent, 10, 1000);
		GeodeticCoords endRB = rb.gcPointFrom(cent, 100, 1500);
		return Sector.create(m_shapeEditor, cent, startRB, endRB);
	}
	
	protected IShapeTool createTriangle(){
		GeodeticCoords[] pos = new GeodeticCoords[3];
		pos[0] = new GeodeticCoords(-120, 34, AngleUnit.DEGREES);
		pos[1] = new GeodeticCoords(-120 + 8, 34 - 8, AngleUnit.DEGREES);
		pos[2] = new GeodeticCoords(-120 + 16, 34 + 8, AngleUnit.DEGREES);

		return Triangle.create(m_shapeEditor, pos);
	}

	public IShapeTool createShapeTool(IShapeEditor editor, String strShape) {
		if (strShape.compareTo(Obj.name[1]) == 0) {
			return createArc();
		}

		if (strShape.compareTo(Obj.name[2]) == 0) {
			return createArrow();
		}

		if (strShape.compareTo(Obj.name[3]) == 0) {
			return createBox();
		}

		if (strShape.compareTo(Obj.name[4]) == 0) {
			return createCircle();
		}

		if (strShape.compareTo(Obj.name[5]) == 0) {
			return createEllipse();
		}

		if (strShape.compareTo(Obj.name[6]) == 0) {
			return createFreeForm();
		}

		if (strShape.compareTo(Obj.name[7]) == 0) {
			return createFreehand();
		}

		if (strShape.compareTo(Obj.name[8]) == 0) {
			return createLine();
		}

		if (strShape.compareTo(Obj.name[9]) == 0) {
			return createRect();
		}

		if (strShape.compareTo(Obj.name[10]) == 0) {
			return createSector();
		}

		if (strShape.compareTo(Obj.name[11]) == 0) {
			return createTriangle();
		}

		if (strShape.compareTo(Obj.name[12]) == 0) {
			return new SelectShape(editor);
		}
		return null;
	}
	
}
