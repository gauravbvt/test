// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

/**
 * Something about which information can be communicated.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Describable {

    /**
     * Get information that can be known about something.
     */
    Information getDescriptor();

}
