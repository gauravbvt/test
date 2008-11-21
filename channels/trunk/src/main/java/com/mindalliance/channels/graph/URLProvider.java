package com.mindalliance.channels.graph;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 1:34:02 PM
 */
public interface URLProvider<V,E> {

    String getVertexURL(V vertex);  // returns null if none

    String getEdgeURL(E edge); // returns null if none
}
