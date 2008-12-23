package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;

/**
 * A flow panel with a delete checkbox.
 */
public interface Deletable {

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

    /**
     * @return the underlying flow
     */
    Flow getFlow();
}
