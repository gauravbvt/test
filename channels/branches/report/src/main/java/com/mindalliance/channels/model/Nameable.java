package com.mindalliance.channels.model;

import java.io.Serializable;

/**
 * Has a name.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 4, 2010
 * Time: 10:29:22 AM
 */
public interface Nameable extends Serializable {
    /**
     * Get name.
     *
     * @return a String
     */
    String getName();
}
