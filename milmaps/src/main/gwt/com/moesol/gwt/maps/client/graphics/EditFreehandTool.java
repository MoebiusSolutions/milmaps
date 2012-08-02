/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public class EditFreehandTool extends FreeXEditTool{
		private Freehand m_freeHand = null;

		public EditFreehandTool(IShapeEditor se) {
			super(se);
		}
		
		@Override
		public void hilite() {
			m_editor.renderObjects();
			drawHandles();
		}
		
		@Override
		public void handleMouseDown(Event event) {
			// Get Selected Anchor
			int x = event.getClientX();
			int y = event.getClientY();
			m_mouseDown = true;
			GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
			m_anchorTool = m_freeHand.getAnchorByPosition(gc);
			if(m_anchorTool == null){
				if (m_cntrlKeydown && !m_shiftKeydown){
					int j = m_freeHand.pointHitSegment(x,y);
					if (j < m_freeHand.size()){
						m_freeHand.insertVertex(j, x, y);
						m_editor.clearCanvas().renderObjects();
						drawHandles();
					}
				}
				else{
					m_freeHand.selected(false);
					m_editor.clearCanvas().renderObjects();
					m_editor.setShapeTool(new SelectShape(m_editor));
				}
			}
			else{
				if (m_cntrlKeydown && m_shiftKeydown){
					m_freeHand.removeVertex((AbstractPosTool)m_anchorTool);
					m_editor.clearCanvas().renderObjects();
					drawHandles();
					m_anchorTool = null;
				}
			}
		}

		@Override
		public void handleMouseMove(Event event) {
			if (m_mouseDown == true){
				if (m_anchorTool != null){
					m_anchorTool.handleMouseMove(event);
					m_editor.clearCanvas().renderObjects();
					drawHandles();
				}
			}
		}

		@Override
		public void handleMouseUp(Event event) {
			m_mouseDown = false;
			if (m_anchorTool != null){
				m_editor.renderObjects();
				m_anchorTool.handleMouseUp(event);
			}	
		}

		@Override
		public void handleMouseOut(Event event) {
			if (m_anchorTool != null){
				m_anchorTool.handleMouseOut(event);
			}
		}

		@Override
		public void done() {
			m_editor.clearCanvas().renderObjects();
		}
		
		@Override
		public void setShape(IShape shape){
			m_freeHand = (Freehand)shape; 
			m_freeForm = m_freeHand;
		}

		@Override
		public IShape getShape() {
			return (IShape)m_freeHand;
		}
}
