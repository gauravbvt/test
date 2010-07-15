package com.mindalliance.channels.model;

import java.util.Map;

/**
 * An object convertible to and reconstitutable from a safely serializable map.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2009
 * Time: 7:52:23 PM
 */
public interface Mappable {

    /**
     * Add attributes to a map.
     * @param map the map
     */
    void map( Map<String,Object> map );

}
