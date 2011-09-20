package com.moesol.gwt.maps.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Simple GWT json overlay type to allow JSON configuration of LayerSet's
 */
public class LayerSetJson extends JavaScriptObject {
	// Overlay types always have protected, zero-arg ctors
	protected LayerSetJson() { }
	
	public final native String getServer(String def) /*-{ return this.server || def; }-*/;
	public final native String getData(String def) /*-{ return this.data || def; }-*/;
	public final native int getSkipLevels(int def) /*-{ return this.skipLevels || def; }-*/;
	public final native String getUrlPattern(String def) /*-{ return this.urlPattern || def; }-*/;
	public final native boolean isZeroTop(boolean def) /*-{ return this.zeroTop != undefined ? this.zeroTop : def; }-*/;
	public final native boolean isAlwaysDraw(boolean def) /*-{ return this.alwaysDraw != undefined ? this.alwaysDraw : def; }-*/;
	public final native boolean isUseToScale(boolean def) /*-{ return this.useToScale != undefined ? this.useToScale : def; }-*/;
	public final native boolean isAutoRefreshOnTimer(boolean def) /*-{ return this.autoRefreshOnTimer != undefined ? this.autoRefreshOnTimer : def; }-*/;
	public final native int getEpsg(int def) /*-{ return this.epsg || def; }-*/;
	public final native int getMinLevel(int def) /*-{ return this.minLevel || def; }-*/;
	public final native int getMaxLevel(int def) /*-{ return this.maxLevel || def; }-*/;
	public final native int getZIndex(int def) /*-{ return this.zIndex || def; }-*/;
	public final native int getStartLevel(int def) /*-{ return this.startLevel || def; }-*/;
	public final native int getPixelWidth(int def) /*-{ return this.pixelWidth || def; }-*/;
	public final native int getPixelHeight(int def) /*-{ return this.pixelHeight || def; }-*/;
	public final native double getStartLevelTileWidthInDeg(double def) /*-{
		return this.startLevelTileWidthInDeg || def; }-*/;
	public final native double getStartLevelTileHeightInDeg(double def) /*-{
		return this.startLevelTileHeightInDeg || def; }-*/;
	
	public final LayerSet toLayerSet() {
		LayerSet layerSet = new LayerSet();
		layerSet
			.withServer(getServer(layerSet.getServer()))
			.withData(getData(layerSet.getData()))
			.withSkipLevels(getSkipLevels(layerSet.getSkipLevels()))
			.withUrlPattern(getUrlPattern(layerSet.getUrlPattern()))
			.withZeroTop(isZeroTop(layerSet.isZeroTop()))
			.withAlwaysDraw(isAlwaysDraw(layerSet.isAlwaysDraw()))
			.withUseToScale(isUseToScale(layerSet.useToScale()))
			.withAutoRefreshOnTimer(isAutoRefreshOnTimer(layerSet.isAutoRefreshOnTimer()))
			.withEpsg(getEpsg(layerSet.getEpsg()))
			.withZIndex(getZIndex(layerSet.getZIndex()))
			.withStartLevel(getStartLevel(layerSet.getStartLevel()))
			.withPixelWidth(getPixelWidth(layerSet.getPixelWidth()))
			.withPixelHeight(getPixelHeight(layerSet.getPixelHeight()))
			.withStartLevelTileWidthInDeg(getStartLevelTileWidthInDeg(layerSet.getStartLevelTileWidthInDeg()))
			.withStartLevelTileHeightInDeg(getStartLevelTileHeightInDeg(layerSet.getStartLevelTileHeightInDeg()))
			.setLevelRange(getMinLevel(layerSet.getMinLevel()), getMaxLevel(layerSet.getMaxLevel()));
		return layerSet;
	}
}
