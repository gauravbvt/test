/*
 * Created on Apr 28, 2007
 */
package com.mindalliance.channels.services.base;

import com.mindalliance.channels.data.elements.reference.Typology;
import com.mindalliance.channels.data.system.Library;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.services.LibraryService;

/**
 * Implementation of the Library service.
 * 
 * @author jf
 */
public class LibraryServiceImpl extends AbstractService implements
        LibraryService {

    public LibraryServiceImpl( SystemService systemService ) {
        super( systemService );
    }

    private Library getLibrary() {
        return getSystem().getLibrary();
    }

    // TODO - transactional
    public Typology getTypology( String name ) {
        Typology typology = getLibrary().getTypology( name );
        if ( typology == null ) {
            typology = new Typology( name );
            getLibrary().addTypology( typology );
        }
        return typology;
    }

}
