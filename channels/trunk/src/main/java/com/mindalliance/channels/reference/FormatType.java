// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.reference;

/**
 * A kind of information format.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public abstract class FormatType extends Type {

    /**
     * Default constructor.
     */
    public FormatType() {
        super();
    }

    /**
     * Default constructor.
     * @param name the name of thius format
     */
    public FormatType( String name ) {
        super( name );
    }
}
