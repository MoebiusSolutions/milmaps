package com.moesol.gwt.maps.client.tms;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.moesol.gwt.maps.shared.Feature;

public class FeatureReader {
	public static class Builder {
		private final String m_server;
		private final String m_urlPattern;
		private String m_root;
		private String m_titlePropertyName = "title";
		private String m_iconPropertyName = "icon";
		private String m_latPropertyName = "lat";
		private String m_lngPropertyName = "lng";
		private PropertyConverter m_iconPropertyConverter;

		public Builder(String server, String urlPattern) {
			m_server = server;
			m_urlPattern = urlPattern;
		}

		public Builder root(String root) {
			m_root = root;
			return this;
		}

		public Builder titleProperty(String titleProperty) {
			m_titlePropertyName = titleProperty;
			return this;
		}

		public Builder latProperty(String latProperty) {
			m_latPropertyName = latProperty;
			return this;
		}

		public Builder lngProperty(String lngProperty) {
			m_lngPropertyName = lngProperty;
			return this;
		}

		public Builder iconProperty(String iconProperty) {
			m_iconPropertyName = iconProperty;
			return this;
		}

		public Builder iconProperty(String iconProperty,
				PropertyConverter converter) {
			m_iconPropertyName = iconProperty;
			m_iconPropertyConverter = converter;
			return this;
		}

		public FeatureReader build() {
			String baseUrlPattern = m_urlPattern.replace("{server}", m_server);
			return new FeatureReader(baseUrlPattern, m_root,
					m_titlePropertyName, m_latPropertyName, m_lngPropertyName,
					m_iconPropertyName, m_iconPropertyConverter);
		}
	}

	private final String m_root;

	private final String m_titlePropertyName;
	private final String m_iconPropertyName;

	private final String m_baseUrlPattern;

	private PropertyConverter m_iconPropertyConverter;

	private String m_latPropertyName;

	private String m_lngPropertyName;

	private FeatureReader(String baseUrlPattern, String root,
			String titleAttributeName, String latAttributeName,
			String lngAttributeName, String iconAttributeName,
			PropertyConverter iconPropertyConverter) {
		m_baseUrlPattern = baseUrlPattern;
		m_root = root;
		m_titlePropertyName = titleAttributeName;
		m_latPropertyName = latAttributeName;
		m_lngPropertyName = lngAttributeName;
		m_iconPropertyName = iconAttributeName;
		m_iconPropertyConverter = iconPropertyConverter;
	}

	public void getFeatures(int level, double lat, double lng,
			final AsyncCallback<List<Feature>> callback) {
		String url = m_baseUrlPattern.replace("{level}", String.valueOf(level))
				.replace("{lat}", String.valueOf(lat))
				.replace("{lng}", String.valueOf(lng));
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET,
				url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request,
						Response response) {
					callback.onSuccess(parseFeatures(response.getText()));
				}

				@Override
				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}

	private List<Feature> parseFeatures(String text) {
		ArrayList<Feature> features = new ArrayList<Feature>();
		try {
			JSONArray array = null;
			JSONValue parsedValue = JSONParser.parseStrict(text);
			if (m_root != null) {
				// should be an object with an array with property m_root
				JSONObject rootObj = parsedValue.isObject();
				if (rootObj != null) {
					JSONValue arrayValue = rootObj.get(m_root);
					if (arrayValue != null) {
						array = arrayValue.isArray();
					}
				}
			} else {
				array = parsedValue.isArray();
			}

			if (array != null) {
				for (int i = 0; i < array.size(); i++) {
					JSONObject obj = (JSONObject) array.get(i);

					String title = getStringProperty(m_titlePropertyName, obj);
					double lat = getDoubleProperty(m_latPropertyName, obj);
					double lng = getDoubleProperty(m_lngPropertyName, obj);
					String iconUrl = getStringProperty(m_iconPropertyName, obj);
					if (m_iconPropertyConverter != null) {
						iconUrl = m_iconPropertyConverter.convert(iconUrl);
					}
					Feature feature = new Feature.Builder(title, lat, lng)
							.iconUrl(iconUrl).build();
					features.add(feature);
				}
			}
		} catch (Throwable t) {
			Logger.getLogger(FeatureReader.class.getName()).log(Level.SEVERE,
					"Failed parsing features", t);
		}

		return features;
	}

	private double getDoubleProperty(String propertyName, JSONObject obj) {
		JSONValue jsonValue = obj.get(propertyName);
		if (jsonValue != null) {
			JSONNumber jsonNumber = jsonValue.isNumber();
			if (jsonNumber != null) {
				return jsonNumber.doubleValue();
			}
		}

		return 0.0;
	}

	private String getStringProperty(String propertyName, JSONObject obj) {
		JSONValue jsonValue = obj.get(propertyName);
		if (jsonValue != null) {
			JSONString jsonString = jsonValue.isString();
			if (jsonString != null) {
				return jsonString.stringValue();
			}
		}

		return null;
	}

	public interface PropertyConverter {
		public String convert(String value);
	}
}
