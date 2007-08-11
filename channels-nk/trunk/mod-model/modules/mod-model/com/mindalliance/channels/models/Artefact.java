// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.models;

import com.mindalliance.channels.support.GUID;

/**
 * Some material object produced by a task.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Artefact extends Product {

    /**
     * Default constructor.
     */
    public Artefact() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Artefact( GUID guid ) {
        super( guid );
    }

    // No properties

}
