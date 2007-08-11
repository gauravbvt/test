// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.models;

import com.mindalliance.channels.support.GUID;

/**
 * An assertion about an object element.
 * @see AssertedObject
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public abstract class Assertion extends StorylineElement {

    /**
     * Default constructor.
     */
    public Assertion() {
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Assertion( GUID guid ) {
        super( guid );
    }
}
