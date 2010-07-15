// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.dao;

import java.io.Serializable;

/**
 * Required command interface from the point of view of a journal.
 */
public interface JournalCommand extends Serializable {

    /**
     * Whether the command's execution should be remembered.
     * @return a boolean
     */
    boolean isMemorable();

    /**
     * Whether the execution of the command forces an immediate snapshot.
     * @return a boolean
     */
    boolean forcesSnapshot();
}
