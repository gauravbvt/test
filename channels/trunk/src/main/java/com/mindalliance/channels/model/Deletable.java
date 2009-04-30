package com.mindalliance.channels.model;

import java.io.Serializable;

/**
 * Something that can be marked for deletion
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 21, 2009
 * Time: 1:38:43 PM
 */
public interface Deletable extends Serializable {

    /**
     * True when underlying flow should be deleted.
     * @return if item should be deleted on submit
     */
    boolean isMarkedForDeletion();

    /**
     * Mark the underlying flow as a candidate for deletion.
     * @param delete true to delete; false to keep
     */
    void setMarkedForDeletion( boolean delete );


}
