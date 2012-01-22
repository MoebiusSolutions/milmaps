package com.moesol.gwt.maps.client.stats;

import java.util.HashSet;
import java.util.Set;

import com.moesol.gwt.maps.client.WallClock;

public enum Sample {
	ROOT,
	MAP_UPDATE_VIEW,
	MAP_FULL_UPDATE,
	MAP_PARTIAL_UPDATE,
	BEST_LAYER,
	MAP_POSITION_ICONS,
	DECLUTTER_LABELS,
	INCREMENTAL_DECLUTTER,
	ARRANGE_TILES,
	LAYER_UPDATE_VIEW,
	LAYER_POSITION_IMAGES,
	LAYER_HIDE_IMAGES,
	TILE_IMG_ENGINE_FIND_OR_CREATE,
	USE_IMAGE,
	CREATE_IMAGE;
	
	private int numCalls;
	private int millisSum;
	private final WallClock wallClock = new WallClock();
	private final Set<Sample> children = new HashSet<Sample>();
	private static Sample current = ROOT;
	private Sample previous = null;
	
	public void beginSample() {
		previous = current;
		current = this;
		previous.children.add(this);
		wallClock.start();
	}
	
	public void endSample() {
		wallClock.stop();
		current = previous;
		previous = null;
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

	public Set<Sample> getChildren() {
		return children;
	}
	
	public static void accept(SampleVisitor visitor) {
		traverse(0, ROOT, visitor);
	}
	
	private static void traverse(int indent, Sample node, SampleVisitor visitor) {
		for (Sample s : node.getChildren()) {
			visitor.visit(indent, s);
			traverse(indent + 1, s, visitor);
		}
	}

}