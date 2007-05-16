/*
 * Created on Apr 28, 2007
 */
package com.mindalliance.channels.services.base;

import com.mindalliance.channels.data.system.History;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.services.HistoryService;

/**
 * Implementation of the History service.
 * 
 * @author jf
 */
public class HistoryServiceImpl extends AbstractService implements
        HistoryService {

    public HistoryServiceImpl( SystemService systemService ) {
        super( systemService );
    }

    private History getHistory() {
        return getSystem().getHistory();
    }

}
