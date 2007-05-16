/*
 * Created on Apr 25, 2007
 */
package com.mindalliance.channels.services;

import com.mindalliance.channels.data.reference.Typology;

public interface LibraryService extends Service {

    /**
     * Get a typology by name. Create it if none is found.
     * 
     * @param typologyName
     * @return
     */
    Typology getTypology( String typologyName );

}
