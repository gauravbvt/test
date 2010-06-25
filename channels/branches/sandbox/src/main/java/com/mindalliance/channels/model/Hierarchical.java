package com.mindalliance.channels.model;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 25, 2009
 * Time: 2:49:37 PM
 */
public interface Hierarchical extends Identifiable {

    /**
     * Get immediate superiors.
     *
     * @return a list of hierarchical model objects
     */
    List<Hierarchical> getSuperiors();
}
