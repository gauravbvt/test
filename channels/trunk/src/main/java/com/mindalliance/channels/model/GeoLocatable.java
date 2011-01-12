package com.mindalliance.channels.model;

import com.mindalliance.channels.query.QueryService;

import java.io.Serializable;
import java.util.List;

/**
 * A geolocatable object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 18, 2009
 * Time: 12:53:44 PM
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
     * @param queryService the associated query service
     * @return a string
     */
    String getGeoMarkerLabel( QueryService queryService );

    /**
     * Get implied geolocatables.
     *
     * @param queryService a query service
     * @return a list of geolocatables
     */
    List<? extends GeoLocatable> getImpliedGeoLocatables( QueryService queryService );
}
