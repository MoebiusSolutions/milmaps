package com.moesol.gwt.maps.client.stats;

import com.moesol.gwt.maps.client.WallClock;

public enum Sample {
	MAP_UPDATE_VIEW,
	BEST_LAYER,
	MAP_POSITION_ICONS,
	DECLUTTER_LABELS,
	ARRANGE_TILES,
	LAYER_UPDATE_VIEW,
	LAYER_POSITION_IMAGES,
	LAYER_HIDE_IMAGES,
	TILE_IMG_ENGINE_FIND_OR_CREATE,
	USE_IMAGE,
	CREATE_IMAGE;
	
	private int numCalls;
	private int millisSum;
	private WallClock wallClock = new WallClock();
	
	public void beginSample() {
		wallClock.start();
	}
	
	public void endSample() {
		wallClock.stop();
		numCalls++;
		millisSum += wallClock.getDuration();
	}
	
	public void reset() {
		numCalls = 0;
		millisSum = 0;
	}
	
	public int getNumCalls() {
		return numCalls;
	}

	public int getMillisSum() {
		return millisSum;
	}
	
	public double getAvgCallMillis() {
		return millisSum / (double)numCalls;
	}

}