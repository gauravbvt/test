package com.mindalliance.channels.analysis.graph;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.Serializable;
import java.util.Collections;
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

    private final Map<String,Serializable> properties = new HashMap<String, Serializable>();

    public DirectedMultiGraphWithProperties( EdgeFactory<V, E> ef ) {
        super( ef );
    }

    public Map<String, Serializable> getProperties() {
        return Collections.unmodifiableMap( properties );
    }

    public void setProperty( String name, Serializable value ) {
        properties.put( name, value );
    }

    public Object getProperty( String name ) {
        return properties.get( name );
    }
}
