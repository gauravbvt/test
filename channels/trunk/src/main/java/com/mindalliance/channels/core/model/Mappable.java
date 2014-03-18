package com.mindalliance.channels.core.model;

import java.io.Serializable;
import java.util.Map;

/**
 * An object convertible to a serializable map and that can be reconstituted from it.
 * Used by commands to store serializable state.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2009
 * Time: 7:52:23 PM
 */
public interface Mappable extends Serializable {

    /**
     * Add attributes to a map.
     * @param map the map
     */
    void map( Map<String,Object> map );

}
