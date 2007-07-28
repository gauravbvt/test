// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.discoveries;

import java.util.List;

import com.mindalliance.channels.data.models.Known;

/**
 * Something in the real world that's knowable and thus about which
 * information can be communicated.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Knowable {

    /**
     * Return Known assertions.
     */
    List<Known> getKnownAssertions();

}
