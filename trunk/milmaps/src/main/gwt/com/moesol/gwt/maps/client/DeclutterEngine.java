package com.moesol.gwt.maps.client;

import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.moesol.gwt.maps.client.json.DeclutterCellSizeJson;
import com.moesol.gwt.maps.client.json.DeclutterSearchOffsetJson;
import com.moesol.gwt.maps.client.stats.Sample;
import com.moesol.gwt.maps.client.util.BitSet2;

/**
 * Declutter using O(n) grid layout.
 * 
 * @author hastings
 */
public class DeclutterEngine {
	/*
	 * The declutter search uses offsets from SEARCH_ROW_OFFSETS and SEARCH_COL_OFFSETS.
	 * The size of SEARCH_ROW_OFFSETS and SEARCH_COL_OFFSETS should be the same.
	 * This search pattern tries to put all the labels on the right vertically, then tries the same on the left.
	 */
	int[] searchRowOffsets = {
		0, -1, -2, -3, -4, 1, 2, 3, 4, -5, -6, -7, -8, 5, 6, 7, 8,
		0, -1, -2, -3, -4, 1, 2, 3, 4, -5, -6, -7, -8, 5, 6, 7, 8,
	};
	int[] searchColOffsets = {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	};
	int cellWidth = 4; // px
	int cellHeight = 8; // px
	
	private final IMapView m_mapView;
	//private BitSet m_bitSet;
	private BitSet2 m_bitSet;
	int m_nRowsInView;
	int m_nColsInView;
	private int m_iconCenterRow;
	private int m_iconStartRow;
	private int m_iconEndRow;
	private int m_iconStartCol;
	private int m_iconEndCol;
	private ViewCoords m_iconCenter;
	
	// TODO This group for incremental declutter refactor to IncrementalDeclutter class...
	private static final long INCREMENT_SLICE_MILLS = 100;
	private List<Icon> iconList;
	private int markingIconIndex = -1;
	private int searchLabelIndex = 0;
	private int positionIconIndex = 0;
	private IconEngine iconEngine;
	private long incrementStart;
	private final Timer m_timer = new Timer() {
		@Override
		public void run() {
			runIncrement();
		}
	};
	
	static class GridCoords {
		final int row;
		final int col;
		public GridCoords(int r, int c) {
			row = r;
			col = c;
		}
		@Override
		public String toString() {
			return "GridCoords [row=" + row + ", col=" + col + "]";
		}
	}

	public DeclutterEngine(IMapView mapView) {
		m_mapView = mapView;
	}

	/**
	 * Pass in custom declutter search configuration
	 * @param offsets
	 */
	public void setSearchOffsets(JsArray<DeclutterSearchOffsetJson> offsets) {
		if (offsets == null) {
			return;
		}
		int[] newColOffsets = new int[offsets.length()];
		int[] newRowOffsets = new int[offsets.length()];
		for (int i = 0; i < offsets.length(); i++) {
			DeclutterSearchOffsetJson searchOffsetJson = offsets.get(i);
			newColOffsets[i] = searchOffsetJson.getColOffset();
			newRowOffsets[i] = searchOffsetJson.getRowOffset();
		}
		searchColOffsets = newColOffsets;
		searchRowOffsets = newRowOffsets;
	}
	/**
	 * Pass in custom cell size configuration
	 * @param cellSize
	 */
	public void setCellSize(DeclutterCellSizeJson cellSize) {
		cellWidth = cellSize.getWidth();
		cellHeight = cellSize.getHeight();
	}

	/**
	 * Support unit test, should match logic in incrementalDeclutter sans icon engine calls.
	 * @param icons
	 */
	public void declutter(List<Icon> icons) {
		Sample.DECLUTTER_LABELS.beginSample();
		
		makeBitSet();
		
		for (Icon i : icons) {
			markOneIcon(i);
		}
		for (Icon i : icons) {
			searchOneLabel(i);
		}
		
		Sample.DECLUTTER_LABELS.endSample();
	}

	/**
	 * Declutter icons but use a timer to continue work if we take more than 100 ms.
	 * 
	 * @param icons
	 * @param iconEngine
	 */
	public void incrementalDeclutter(List<Icon> icons, IconEngine iconEngine) {
		m_timer.cancel();
		markingIconIndex = -1;
		searchLabelIndex = 0;
		positionIconIndex = 0;
		iconList = icons;
		this.iconEngine = iconEngine;
		runIncrement();
	}
	
	protected void runIncrement() {
		Sample.INCREMENTAL_DECLUTTER.beginSample();
		try {
			incrementStart = System.currentTimeMillis();
			
			if (markingIconIndex < 0) {
				makeBitSet();
				markingIconIndex = 0;
			}
			while (markingIconIndex < iconList.size() && haveMoreTime()) {
				markOneIcon(iconList.get(markingIconIndex));
				markingIconIndex++;
			}
			while (searchLabelIndex < iconList.size() && haveMoreTime()) {
				searchOneLabel(iconList.get(searchLabelIndex));
				searchLabelIndex++;
			}
			while (positionIconIndex < iconList.size() && haveMoreTime()) {
				iconEngine.positionOneIcon(iconList.get(positionIconIndex));
				positionIconIndex++;
			}
			if (!haveMoreTime()) {
				m_timer.schedule(10); // Come back in 10 ms...
			}
			
		} finally {
			Sample.INCREMENTAL_DECLUTTER.endSample();
		}
	}

	private boolean haveMoreTime() {
		long now = System.currentTimeMillis();
		if (now - incrementStart < INCREMENT_SLICE_MILLS) {
			return true;
		}
		return false;
	}

	/**
	 * Marks the bits that the icon (symbol) will occupy. We assume
	 * the icon offset centers the icon. If not we might be able to
	 * use the image width/height.
	 * 
	 * @param icon
	 */
	private void markOneIcon(Icon icon) {
		computeIconBounds(icon);

		int startRow = Math.max(0, m_iconStartRow);
		int endRow = Math.min(m_nRowsInView - 1, m_iconEndRow);

		int startCol = Math.max(0, m_iconStartCol);
		int endCol = Math.min(m_nColsInView - 1, m_iconEndCol);
		
		for (int r = startRow; r <= endRow; r++) {
			for (int c = startCol; c <= endCol; c++) {
				m_bitSet.set(r * m_nColsInView + c);
			}
		}
	}

	private void computeIconBounds(Icon icon) {
		m_iconCenter = computeIconCenterViewCoords(icon);
		ViewCoords topLeft = m_iconCenter.translate(icon.getIconOffset());
		ViewCoords bottomRight = topLeft.translate(icon.getImage().getOffsetWidth(), icon.getImage().getOffsetHeight());

		GridCoords centerGC = computeIconCenterGridCoords(m_iconCenter);
		GridCoords topLeftGC = computeIconCenterGridCoords(topLeft);
		GridCoords bottomRightGC = computeIconCenterGridCoords(bottomRight);
		
		m_iconCenterRow = centerGC.row;
		m_iconStartRow = Math.min(topLeftGC.row, bottomRightGC.row);
		m_iconEndRow = Math.max(topLeftGC.row, bottomRightGC.row);
		m_iconStartCol = Math.min(topLeftGC.col, bottomRightGC.col);
		m_iconEndCol = Math.max(topLeftGC.col, bottomRightGC.col);
	}

	int makeBitSet() {
		ViewPort viewport = m_mapView.getViewport();
		m_nRowsInView = roundUp(viewport.getHeight(), cellHeight);
		m_nColsInView = roundUp(viewport.getWidth(), cellWidth);
		int numBits = m_nRowsInView * m_nColsInView;
		m_bitSet = new BitSet2(numBits);
		return numBits;
	}
	
	private void searchOneLabel(Icon icon) {
		Label label = icon.getLabel();
		if (label == null) {
			return;
		}
		searchSlots(icon);
	}

	private void searchSlots(Icon icon) {
		computeIconBounds(icon);
		
		int nLabelCols = computeLabelColumnSpan(icon);
		int nLabelRows = computeLabelRowSpan(icon);
		for (int i = 0; i < searchRowOffsets.length; i++) {
			int rowOffset = searchRowOffsets[i];
			int colOffset = searchColOffsets[i];

			int startRow = m_iconCenterRow + rowOffset;
			int startCol;
			if (colOffset < 0) {
				startCol = m_iconStartCol + colOffset - nLabelCols;
				colOffset -= nLabelCols;
			} else {
				startCol = m_iconEndCol + colOffset + 1;
			}
			
			if (searchOneSlot(startRow, startCol, nLabelRows, nLabelCols)) {
				moveDeclutterOffset(icon, startRow, startCol);
				return;
			}
		}
		// No cell found, move offscreen.
		moveDeclutterOffset(icon, m_nRowsInView + 1, m_nColsInView + 1);
	}

	ViewCoords computeIconCenterViewCoords(Icon icon) {
		WorldCoords wc = m_mapView.getProjection().geodeticToWorld(icon.getLocation());
		ViewCoords vc = m_mapView.getViewport().worldToView(wc, true);
		return new ViewCoords(vc.getX(), vc.getY());
	}
	
	private void moveDeclutterOffset(Icon icon, int startRow, int startCol) {
		int targetY = startRow * cellHeight;
		int targetX = startCol * cellWidth;
		
		int offsetY = targetY - m_iconCenter.getY();
		int offsetX = targetX - m_iconCenter.getX();

		icon.setDeclutterOffset(new ViewCoords(offsetX, offsetY));
	}

	private boolean searchOneSlot(int startRow, int startCol, int nLabelRows, int nLabelCols) {
		if (startRow < 0) {
			return false;
		}
		if (startCol < 0) {
			return false;
		}
		int endRows = startRow + nLabelRows;
		int endCols = startCol + nLabelCols;
		if (endRows >= m_nRowsInView) {
			return false;
		}
		if (endCols >= m_nColsInView) {
			return false;
		}
		
		for (int r = startRow; r < endRows; r++) {
			for (int c = startCol; c < endCols; c++) {
				if (isCellUsed(r, c)) {
					// Collision
					return false;
				}
			}
		}
		
		// OK we found a slot mark it used.
		for (int r = startRow; r < endRows; r++) {
			for (int c = startCol; c < endCols; c++) {
				m_bitSet.set(r * m_nColsInView + c);
			}
		}
		return true;
	}

	public boolean isCellUsed(int row, int column) {
		return m_bitSet.get(row * m_nColsInView + column);
	}

	private GridCoords computeIconCenterGridCoords(ViewCoords vc) {
		GridCoords rc = new GridCoords(vc.getY() / cellHeight, vc.getX() / cellWidth);
		return rc;
	}

	private int computeLabelColumnSpan(Icon icon) {
		return roundUp(icon.getLabel().getOffsetWidth(), cellWidth);
	}

	private int computeLabelRowSpan(Icon icon) {
		return roundUp(icon.getLabel().getOffsetHeight(), cellHeight);
	}

	static int roundUp(int total, int dividend) {
		return (total + (dividend - 1)) / dividend;
	}
}
