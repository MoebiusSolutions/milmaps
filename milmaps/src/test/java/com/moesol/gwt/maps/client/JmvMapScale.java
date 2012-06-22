package com.moesol.gwt.maps.client;

/**
 * Use for unit tests. Override toString with jmv friendly version.
 * @author hastings
 */
public class JmvMapScale extends MapScale {

	public JmvMapScale(double scale) {
		super(scale);
	}

	@Override
	public String toString() {
		double bottom = 1/asDouble();
		if (bottom >= ONE_MILLION) {
			return String.format("1:%1.1fM", bottom / ONE_MILLION);
		} else if (bottom >= ONE_THOUSAND) {
			return String.format("1:%1.1fK", bottom / ONE_THOUSAND);
		} else {
			return String.format("1:%1.1f", bottom);
		}
	}
	
	public static MapScale parse(String string) {
		return new JmvMapScale(MapScale.parse(string).asDouble());
	}

}
