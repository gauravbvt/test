// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.discoveries;

import java.util.List;

import com.mindalliance.channels.data.models.Assertion;

/**
 * Something that can be the object of assertions.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Assertable {

    /**
     * Return the assertions attached to this object.
     */
    List<Assertion> getAssertions();

}
