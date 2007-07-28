// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

/**
 * A kind of application event on an element.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public enum ElementEvent {
    /** Catch all. */
    ANY,

    /** Element was created. */
    CREATED,

    /** Element was modified. */
    MODIFIED,

    /** Element was deleted. */
    DELETED,

    /** Element is being discussed. */
    DISCUSSED
};
