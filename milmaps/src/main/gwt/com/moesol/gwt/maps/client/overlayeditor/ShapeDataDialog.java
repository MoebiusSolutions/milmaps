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
import com.moesol.gwt.maps.client.graphics.Arc;
import com.moesol.gwt.maps.client.graphics.IShape;
import com.moesol.gwt.maps.client.graphics.IShapeEditor;
import com.moesol.gwt.maps.client.graphics.IShapeTool;
import com.moesol.gwt.maps.client.graphics.NewArcTool;
import com.moesol.gwt.maps.client.graphics.NewArrowTool;
import com.moesol.gwt.maps.client.graphics.NewBoxTool;
import com.moesol.gwt.maps.client.graphics.NewCircleTool;
import com.moesol.gwt.maps.client.graphics.NewEllipseTool;
import com.moesol.gwt.maps.client.graphics.NewFreeFormTool;
import com.moesol.gwt.maps.client.graphics.NewFreehandTool;
import com.moesol.gwt.maps.client.graphics.NewLineTool;
import com.moesol.gwt.maps.client.graphics.NewRectTool;
import com.moesol.gwt.maps.client.graphics.NewSectorTool;
import com.moesol.gwt.maps.client.graphics.NewTriangleTool;
import com.moesol.gwt.maps.client.graphics.SelectShape;
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

	public IShapeTool createShapeTool(IShapeEditor editor, String strShape) {
		if (strShape.compareTo(Obj.name[1]) == 0) {
			return createArc();
		}

		if (strShape.compareTo(Obj.name[2]) == 0) {
			return new NewArrowTool(editor);
		}

		if (strShape.compareTo(Obj.name[3]) == 0) {
			return new NewBoxTool(editor);
		}

		if (strShape.compareTo(Obj.name[4]) == 0) {
			return new NewCircleTool(editor);
		}

		if (strShape.compareTo(Obj.name[5]) == 0) {
			return new NewEllipseTool(editor);
		}

		if (strShape.compareTo(Obj.name[6]) == 0) {
			return new NewFreeFormTool(editor);
		}

		if (strShape.compareTo(Obj.name[7]) == 0) {
			return new NewFreehandTool(editor);
		}

		if (strShape.compareTo(Obj.name[8]) == 0) {
			return new NewLineTool(editor);
		}

		if (strShape.compareTo(Obj.name[9]) == 0) {
			return new NewRectTool(editor);
		}

		if (strShape.compareTo(Obj.name[10]) == 0) {
			return new NewSectorTool(editor);
		}

		if (strShape.compareTo(Obj.name[11]) == 0) {
			return new NewTriangleTool(editor);
		}

		if (strShape.compareTo(Obj.name[12]) == 0) {
			return new SelectShape(editor);
		}
		return null;
	}
	
	protected IShapeTool createArc(double lat, double lng){
		Arc arc = new Arc();
		GeodeticCoords cent = new GeodeticCoords(lng,lat,AngleUnit.DEGREES);
		//arc.setCenter(cent).setRadiusPos(radPos);
		return null;
	}
}
