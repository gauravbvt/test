/*
 * Created on Apr 28, 2007
 */
package com.mindalliance.channels.services.base;

import com.mindalliance.channels.data.system.Directory;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.services.DirectoryService;

/**
 * Implementation of the Directory service.
 * 
 * @author jf
 */
public class DirectoryServiceImpl extends AbstractService implements
        DirectoryService {

    public DirectoryServiceImpl( SystemService systemService ) {
        super( systemService );
    }

    private Directory getDirectory() {
        return getSystem().getDirectory();
    }

}
