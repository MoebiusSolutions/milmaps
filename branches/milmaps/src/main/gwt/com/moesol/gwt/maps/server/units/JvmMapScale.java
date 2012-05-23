package com.moesol.gwt.maps.server.units;

import com.moesol.gwt.maps.client.units.MapScale;


/**
 * Use for unit tests. Override toString with jmv friendly version.
 * @author hastings
 */
public class JvmMapScale extends MapScale {

	public static void init() {
		MapScale.DEFAULT= new Factory() {
			@Override
			public MapScale make(double scale) {
				return new JvmMapScale(scale);
			}
		};
	}
		
	JvmMapScale(double scale) {
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
	
}
