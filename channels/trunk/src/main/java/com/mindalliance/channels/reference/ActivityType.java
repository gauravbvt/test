// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.reference;

/**
 * A kind of activity.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public abstract class ActivityType extends Type {

    /**
     * Default constructor.
     */
    public ActivityType() {
        super();
    }

    /**
     * Default constructor.
     * @param name the name of this activity type
     */
    public ActivityType( String name ) {
        super( name );
    }

}
