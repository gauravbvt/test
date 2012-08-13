package com.mindalliance.channels.core.model;

import java.io.Serializable;
import java.util.List;

/**
 * A geolocatable object.
 */
public interface GeoLocatable extends Serializable {

    /**
     * Get geolocation if known, else null.
     *
     * @return a geo location
     */
    Place getPlaceBasis();

    /**
     * Get a geo marker's label.
     *
     * @return a string
     */
    String getGeoMarkerLabel();

    /**
     * Get implied geolocatables.
     *
     * @return a list of geolocatables
     */
    List<? extends GeoLocatable> getImpliedGeoLocatables();
}
