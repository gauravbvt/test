// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.definitions;

import java.util.Collection;

import com.mindalliance.channels.support.Service;

/**
 * The library service.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface LibraryService extends Service {

    /**
     * Get a typology by name. Create it if none is found.
     * @param typologyName the name
     */
    Typology getTypology( String typologyName );

    /**
     * Return all typologies managed by this service.
     */
    Collection<Typology> getTypologies();
}
