package com.moesol.gwt.maps.client.tms;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.moesol.gwt.maps.client.LayerSet;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class TileMapServiceActivity extends AbstractActivity implements
		TileMapServiceView.Presenter {

	private static final String SERVICE_BASE_URL = "http://localhost:8080/geowebcache/service/tms/1.0.0";
	private static final String SERVICE_ARCGIS_URL = "http://services.arcgisonline.com/ArcGIS/rest/services/I3_Imagery_Prime_World_2D/MapServer/tile";
	private static TileMapServiceMetadata data;
	private static TileMapMetadata selectedAvailableItem;
	private static TileMapMetadata selectedDisplayedItem;
	private TileMapServicePlace place;
	private ArrayList<TileMapMetadata> displayedItems = new ArrayList<TileMapMetadata>();

	public TileMapServiceActivity() {
	}

	public void setPlace(TileMapServicePlace place) {
		this.place = place;
	}

	@Inject
	EventBus eventBus;

	@Inject
	PlaceController placeController;

	@Inject
	TileMapServiceView view;

	@Inject
	MapView mapView;

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		// mapView.setCenter(place.getLatitude(), place.getLongitude());
		// mapView.getProjection().setLevel(place.getLevel());

		view.setPresenter(this);

		view.removeAllDisplayedItems();

		view.setAddAvailableItemEnabled(selectedAvailableItem != null);
		view.setRemoveDisplayedItemEnabled(selectedDisplayedItem != null);

		if (data == null) {
			TileMapServiceMetadata.getMetadata("test-tms.xml",
					new TileMapServiceMetadata.RequestCallback() {
						@Override
						public void onSuccess(TileMapServiceMetadata metadata) {
							data = metadata;
							metadata.setUrl(SERVICE_BASE_URL);
							view.displayData(metadata.getTileMaps());
							setLayers();
						}
					});
		} else {
			setLayers();
		}
	}
	
	private void addArcGisBMNG(){
		LayerSet layerSet = new LayerSet();
		layerSet.setServer(SERVICE_ARCGIS_URL);
		layerSet.setZeroTop(true);
		layerSet.setData("");
	    layerSet.setStartLevelTileDimensionsInDeg(180.0,180.0);
	    layerSet.withPixelWidth(512).setPixelHeight(512);
	    layerSet.setLevelRange(-2, 8);
	    //mapView.getProjection().setBaseScaleForDegWidth(180.0, 512);

		TileMapMetadata tileMapMetadata = new TileMapMetadata();
		tileMapMetadata.setId("I3_Imagery_Prime_World_2D");
		tileMapMetadata.setSrs("EPSG:4326");
		tileMapMetadata.setTileImageFormat("png");
		tileMapMetadata.setTilePixelHeight(512);
		tileMapMetadata.setTilePixelWidth(512);
		//tileMapMetadata.setTileSets(null);
		tileMapMetadata.setTitle("I3_Imagery_Prime_World_2D");
		tileMapMetadata.setUrl(SERVICE_ARCGIS_URL);
		tileMapMetadata.setVisible(false);
		//displayedItems.add(tileMapMetadata);
		//addDisplayedItem(tileMapMetadata);
		layerSet.setUrlPattern("{server}/{level}/{y}/{x}."
				+ tileMapMetadata.getTileImageFormat());
		mapView.addLayer(layerSet);	
	}

	private void setLayers() {
		mapView.clearLayers();
		addArcGisBMNG();
		for (String layerId : place.getLayers()) {
			TileMapMetadata tileMapMetadata = data.findTileMapMetadata(layerId);

			if (tileMapMetadata != null) {
				LayerSet ls = new LayerSet();
				ls.setServer(data.getUrl() + "/" + layerId);
				ls.setData("");
			    ls.setStartLevelTileDimensionsInDeg(90.0, 90.0);
			    ls.setStartLevel(1);
			    ls.withPixelWidth(256).setPixelHeight(256);
			    ls.setAlwaysDraw(true);
			    ls.setBackgroundMapFlag(false);
			    ls.setZIndex(4);
			    ls.setLevelRange(-2, 12);
				displayedItems.add(tileMapMetadata);
				addDisplayedItem(tileMapMetadata);
				ls.setUrlPattern("{server}/{level}/{x}/{y}."
						+ tileMapMetadata.getTileImageFormat());
				IProjection p = mapView.getProjection();
				mapView.addLayer(ls);
			}
		}

		mapView.updateView();
		view.setRemoveAllDisplayedItemsEnabled(displayedItems.size() > 0);
		setMoveDisplayedItemUi(displayedItems.indexOf(selectedDisplayedItem));
	}

	private void addDisplayedItem(TileMapMetadata tileMapMetadata) {
		view.removeDisplayedItem(tileMapMetadata);
		view.addDisplayedItem(tileMapMetadata);
	}

	@Override
	public void onRemoveAllDisplayedItems() {
		view.removeAllDisplayedItems();
		view.addAvailableItems(displayedItems);
		displayedItems.clear();
		view.setRemoveDisplayedItemEnabled(false);
		view.setRemoveAllDisplayedItemsEnabled(false);
		eventBus.fireEvent(new TileMapRemoveAllEvent());
		goToNewPlace();
		selectedDisplayedItem = null;
	}

	private void goToNewPlace() {
		placeController.goTo(new TileMapServicePlace(getLayerIds(), mapView
				.getCenter().getPhi(AngleUnit.DEGREES), mapView.getCenter()
				.getLambda(AngleUnit.DEGREES), mapView.getViewport()
				.getLevel()));
	}

	@Override
	public void onRemoveDisplayedItem() {
		if (selectedDisplayedItem != null) {
			displayedItems.remove(selectedDisplayedItem);
			view.removeDisplayedItem(selectedDisplayedItem);
			view.addAvailableItem(selectedDisplayedItem);
			view.setRemoveDisplayedItemEnabled(false);
			view.setRemoveAllDisplayedItemsEnabled(displayedItems.size() > 0);
			eventBus.fireEvent(new TileMapRemoveEvent(selectedDisplayedItem));
			goToNewPlace();
			selectedDisplayedItem = null;
		}
	}

	@Override
	public void onAddDisplayedItem() {
		if (selectedAvailableItem != null
				&& !displayedItems.contains(selectedAvailableItem)) {
			displayedItems.add(selectedAvailableItem);
			view.addDisplayedItem(selectedAvailableItem);
			view.removeAvailableItem(selectedAvailableItem);
			view.setAddAvailableItemEnabled(false);
			view.setRemoveAllDisplayedItemsEnabled(true);
			eventBus.fireEvent(new TileMapAddEvent(selectedAvailableItem));
			goToNewPlace();
			selectedAvailableItem = null;
		}
	}

	private String[] getLayerIds() {
		String[] layerId = new String[displayedItems.size()];
		for (int i = 0; i < displayedItems.size(); i++) {
			layerId[i] = displayedItems.get(i).getId();
		}
		return layerId;
	}

	@Override
	public void onAvailableSelected(TileMapMetadata selectedObject) {
		selectedAvailableItem = selectedObject;
		view.setAddAvailableItemEnabled(selectedAvailableItem != null);
	}

	@Override
	public void onDisplayedSelected(TileMapMetadata selectedObject) {
		selectedDisplayedItem = selectedObject;
		view.setRemoveDisplayedItemEnabled(selectedDisplayedItem != null);
		
		int index = displayedItems.indexOf(selectedObject);
		setMoveDisplayedItemUi(index);
	}

	private void setMoveDisplayedItemUi(int index) {
		view.setMoveDisplayedItemUpEnabled(index > 0);
		view.setMoveDisplayedItemDownEnabled(index > -1 && index < displayedItems.size() - 1);
	}

	@Override
	public void onMoveDisplayedItemDown() {
		int i = displayedItems.indexOf(selectedDisplayedItem);
		if (i < displayedItems.size() - 1) {
			Collections.swap(displayedItems, i, i + 1);
			goToNewPlace();
		}
	}

	@Override
	public void onMoveDisplayedItemUp() {
		int i = displayedItems.indexOf(selectedDisplayedItem);
		if (i > 0) {
			Collections.swap(displayedItems, i, i - 1);
			goToNewPlace();
		}
	}
}
