package com.mindalliance.channels.model;

import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 8:01:24 PM
 */
public interface Identifiable extends Serializable {
    /**
     * Get id
     *
     * @return an id
     */
    long getId();

    /**
     * A short description
     *
     * @return a String
     */
    String getDescription();

    /**
     * A name
     *
     * @return a String
     */
    String getName();

    /**
     * Get type of object.
     *
     * @return a string
     */
    String getTypeName();
}
