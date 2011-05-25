package com.moesol.maps.server.tms;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.moesol.gwt.maps.shared.BoundingBox;
import com.moesol.gwt.maps.shared.Feature;
import com.moesol.gwt.maps.shared.Tile;

/**
 * Decorator for AbstractTileResource, extended by classes that are additionally
 * capable of fulfilling requests for feature data.
 */
public abstract class AbstractFeatureTileResource extends AbstractTileResource {

	/**
	 * Get the features that contain the specified point.
	 * @param zoomLevel The zoom level to consider. This is important, as features
	 * can just be points represented by an icon, thats size is dependent on the
	 * pixels/degree.
	 * @param lat The latitude of the point.
	 * @param lng The longitude of the point.
	 * @return A JSON array string representing the hit features.
	 */
	@GET
	@Path("{zoomLevel}/{lat}/{lng}/features")
	@Produces({ "application/json" })
	public String getHitFeatures(@PathParam("zoomLevel") int zoomLevel,
			@PathParam("lat") double lat, @PathParam("lng") double lng) {
		try {
			Tile tile = createTile(zoomLevel, 0, 0);

			StringBuilder jsonBuilder = new StringBuilder("[");
			IFeatureTileRenderer renderer = getFeatureTileRenderer();
			boolean featuresAdded = false;
			for (Feature feature : renderer.getHitFeatures(tile, lat, lng)) {
				featuresAdded = true;
				jsonBuilder.append("{");
				appendProperty(jsonBuilder, "title", feature.getTitle(), false);
				appendProperty(jsonBuilder, "icon", feature.getIcon(), false);
				appendProperty(jsonBuilder, "lat", feature.getLat(), false);
				appendProperty(jsonBuilder, "lng", feature.getLng(), true);
				jsonBuilder.append("},");
			}

			if (featuresAdded) {
				jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
			}

			jsonBuilder.append("]");
			return jsonBuilder.toString();
		} catch (Throwable t) {
			Logger.getLogger(AbstractFeatureTileResource.class.getName())
					.log(Level.SEVERE,
							String.format(
									"Unexpected exception caught in getHitFeatures(%1$s %2$s %3$s)",
									zoomLevel, lat, lng));
			throw new WebApplicationException(t,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get a bounding box around the identified features.
	 * 
	 * @param featureIdsString A comma-separated list of feature IDs
	 * @return A JSON object string representing the tightest bound
	 * around the features' center points.
	 */
	@GET
	@Path("bbox")
	@Produces({ "application/json" })
	public String getBoundingBoxForFeatures(
			@QueryParam("features") String featureIdsString) {
		String[] featureIds = featureIdsString.split(",");
		BoundingBox boundingBox = getFeatureTileRenderer()
				.getBoundingBoxForFeatures(featureIds);

		return createBoundingBoxJson(boundingBox);
	}

	String createBoundingBoxJson(BoundingBox boundingBox) {
		String json = String
				.format("{\"minLat\":%1$s,\"minLng\":%2$s,\"maxLat\":%3$s,\"maxLng\":%4$s}",
						boundingBox.getMinLat(), boundingBox.getMinLon(),
						boundingBox.getMaxLat(), boundingBox.getMaxLon());
		return json;
	}

	private void appendProperty(StringBuilder builder, String name,
			Object value, boolean last) {
		builder.append("\"");
		builder.append(name);
		builder.append("\":\"");
		builder.append(value);
		builder.append("\"");
		if (!last) {
			builder.append(",");
		}
	}

	/**
	 * Subclasses must implement this method to return their IFeatureTileRenderer.
	 * This method is required since this is a resource class, created by the
	 * JAX-RS framework, thus passing the renderer to the constructor was not an option.
	 * @return The renderer.
	 */
	public abstract IFeatureTileRenderer getFeatureTileRenderer();
}
