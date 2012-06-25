/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.algorithms.RngBrg;

public class Circle extends AbstractShape {
	private static final int NUM_CIR_PTS = 50;
	private static final RangeBearingS m_rb = new RangeBearingS();
	private final AnchorHandle m_centerHandle = new AnchorHandle();
	private final AnchorHandle m_radHandle = new AnchorHandle();
	private GeodeticCoords m_center = null;
	private GeodeticCoords m_radiusPos = null;
	private RngBrg m_radRngBrg = null;
	private ViewCoords[] m_pts = null;
	//private boolean m_mouseDown = true;
	private IAnchorTool m_radiusTool = null;
	private IAnchorTool m_centerTool = null;
	

	public Circle(){
		m_id = "Circle";
		m_pts = new ViewCoords[NUM_CIR_PTS];
	}
	
	private void checkForException() {
		if (m_convert == null) {
			throw new IllegalStateException("Circle: m_convert = null");
		}
		if (m_center == null) {
			throw new IllegalStateException("Circle: m_center = null");
		}
	}

	private void setRadiusFromPix(int x, int y) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if (m_radiusPos == null || !m_radiusPos.equals(gc)) {
			m_radiusPos = gc;
			upDateRngBrg();
			m_needsUpdate = true;
		}
	}

	private void upDateRngBrg() {
		checkForException();
		m_radRngBrg = m_rb.RngBrgFromTo(m_center, m_radiusPos);
	}

	public IAnchorTool getRadiusAnchorTool() {
		if (m_radiusTool == null) {
			m_radiusTool = new IAnchorTool() {
				@Override
				public boolean handleMouseDown(MouseDownEvent event) {
					//m_mouseDown = true;
					return true;
				}

				@Override
				public boolean handleMouseMove(MouseMoveEvent event) {
					//if (m_mouseDown == true) {
						int x = event.getX();
						int y = event.getY();
						setRadiusFromPix(x, y);
					//}
					return true;
				}

				@Override
				public boolean handleMouseUp(MouseUpEvent event) {
					//m_mouseDown = false;
					int x = event.getX();
					int y = event.getY();
					setRadiusFromPix(x, y);
					upDateRngBrg();
					return true;
				}

				@Override
				public boolean handleMouseOut(MouseOutEvent event) {
					//m_mouseDown = false;
					upDateRngBrg();
					return true;
				}

				@Override
				public void done() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					ViewCoords vc = m_convert.geodeticToView(gc);
					ViewCoords radPt = m_convert.geodeticToView(m_radiusPos);
					return Func.isClose(radPt, vc, 4);
				}
			};
		}
		return m_radiusTool;
	}

	private void setCenterFromPix(int x, int y) {
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if (!m_center.equals(gc)) {
			m_center = gc;
			m_needsUpdate = true;
		}
	}

	private void moveRadiusPos() {
		if (m_radRngBrg != null) {
			double rng = m_radRngBrg.getRanegKm();
			double brg = m_radRngBrg.getBearing();
			m_radiusPos = m_rb.gcPointFrom(m_center, brg, rng);
		}
	}

	public IAnchorTool getCenterAnchorTool() {
		if (m_centerTool == null) {
			m_centerTool = new IAnchorTool() {
				@Override
				public boolean handleMouseDown(MouseDownEvent event) {
					return false;
				}

				@Override
				public boolean handleMouseMove(MouseMoveEvent event) {
					int x = event.getX();
					int y = event.getY();
					setCenterFromPix(x, y);
					moveRadiusPos();
					return true;
				}

				@Override
				public boolean handleMouseUp(MouseUpEvent event) {
					int x = event.getX();
					int y = event.getY();
					setCenterFromPix(x, y);
					moveRadiusPos();
					return true;
				}

				@Override
				public boolean handleMouseOut(MouseOutEvent event) {
					return false;
				}

				@Override
				public void done() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					ViewCoords vc = m_convert.geodeticToView(gc);
					ViewCoords centPt = m_convert.geodeticToView(m_center);
					return Func.isClose( centPt, vc, 4);
				}
			};
		}
		return m_centerTool;
	}

	private void createBoundary() {
		checkForException();
		double degInc = 360.0 / (NUM_CIR_PTS - 1);
		double distKm = m_radRngBrg.getRanegKm();
		for (int i = 0; i < NUM_CIR_PTS - 1; i++) {
			double brng = degInc * i;
			GeodeticCoords gc = m_rb.gcPointFrom(m_center, brng, distKm);
			m_pts[i] = m_convert.geodeticToView(gc);
		}
		m_pts[NUM_CIR_PTS - 1] = m_pts[0];
	}

	private void drawBoundary(Context2d context) {
		context.beginPath();
		context.setStrokeStyle(m_color);
		context.setLineWidth(2);
		createBoundary();
		context.moveTo(m_pts[0].getX(), m_pts[0].getY());
		for (int i = 1; i < NUM_CIR_PTS; i++) {
			context.lineTo(m_pts[i].getX(), m_pts[i].getY());
		}
		context.closePath();
		context.stroke();
	}


	@Override
	public IShape erase(Context2d ct) {
		//_erase(ct);
		return this;
	}
	
	@Override
	public IShape render(Context2d ct) {
		drawBoundary(ct);
		return this;
	}
	
	@Override
	public IShape drawHandles(Context2d context) {
		if (context != null) {
			// Center Handle
			GeodeticCoords gc = getCenter();
			ViewCoords vc = m_convert.geodeticToView(gc);
			m_centerHandle.setCenter(vc.getX(), vc.getY());
			m_centerHandle.draw(context);
			// Radius handle
			gc = getRadiusPos();
			vc = m_convert.geodeticToView(gc);
			m_radHandle.setCenter(vc.getX(), vc.getY()).draw(context);
		}
		return this;
	}

	public GeodeticCoords getRadiusPos() {
		return m_radiusPos;
	}

	public void setRadiusPos(GeodeticCoords radPos) {
		m_radiusPos = radPos;
	}

	public Circle withRadiusPos(GeodeticCoords radPos) {
		setRadiusPos(radPos);
		return this;
	}

	public GeodeticCoords getCenter() {
		return m_center;
	}

	public void setCenter(GeodeticCoords center) {
		m_center = center;
	}

	public Circle withCenter(GeodeticCoords center) {
		setCenter(center);
		return this;
	}


	public boolean ptCloseToEdge(int px, int py, double eps) {
		if (m_pts != null) {
			int n = m_pts.length;
			if (n > 3) {
				for (int i = 0; i < n - 1; i++) {
					double dist = Func.ptLineDist(m_pts[i], m_pts[i+1], px, py);
					if (dist < eps) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean positionTouches(GeodeticCoords position) {
		checkForException();
		ViewCoords vc = m_convert.geodeticToView(position);
		ViewCoords centPix = m_convert.geodeticToView(m_center);
		if (Func.isClose(centPix, vc, Func.PIX_SELECT_TOLERANCE)) {
			return true;
		}
		return ptCloseToEdge( vc.getX(), vc.getY(), Func.PIX_SELECT_TOLERANCE);
	}

	@Override
	public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
		checkForException();
		IAnchorTool tool = getRadiusAnchorTool();
		if (tool.isSlected(position)){
			return tool;
		}
		tool = getCenterAnchorTool();
		if (tool.isSlected(position)){
			return tool;
		}
		return null;
	}
}
