package com.mindalliance.channels.graph.diagrams;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.HashMap;
import java.util.Map;

/**
 * DirectedMultigraph with properties.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/19/11
 * Time: 9:02 PM
 */
public class DirectedMultiGraphWithProperties<V,E> extends DirectedMultigraph<V,E> {

    private Map<String,Object> properties = new HashMap<String, Object>();

    public DirectedMultiGraphWithProperties( EdgeFactory<V, E> ef ) {
        super( ef );
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperty( String name, Object value ) {
        properties.put( name, value );
    }

    public Object getProperty( String name ) {
        return properties.get( name );
    }
}
