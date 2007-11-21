package com.mindalliance.geonames.aspect;

import com.mindalliance.geonames.Geonames;

public class GeonamesAspect implements IAspectGeonames {
	private Geonames t;

	public GeonamesAspect(Geonames t) {
		this.t = t;
	}

	public Geonames getGeonames() {
		return t;
	}
}
