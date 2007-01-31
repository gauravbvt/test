// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.GUID;

/**
 * A domain or sub-domain of human endeavor.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Domain extends AbstractNamedObject {

    private Domain parent;

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Domain( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of parent.
     */
    public Domain getParent() {
        return this.parent;
    }

    /**
     * Set the value of parent.
     * @param parent The new value of parent
     */
    public void setParent( Domain parent ) {
        this.parent = parent;
    }
}
