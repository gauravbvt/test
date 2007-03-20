// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.reference;

/**
 * A kind of title type.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public abstract class TitleType extends Type {

    /**
     * Default constructor.
     */
    public TitleType() {
    }

    /**
     * Default constructor.
     * @param name the name of this title
     */
    public TitleType( String name ) {
        super( name );
    }
}
