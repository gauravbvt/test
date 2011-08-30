/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core;

import java.io.Serializable;
import java.util.Date;

/**
 * ...
 */
public interface PersistentObject extends Serializable {

    /**
     * Get the date associated with this object.
     * @return a creation date, usually
     */
    Date getDate();

    /**
     * Get the unique id of this object.
     * @return a string
     */
    String getId();
}
