// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.search.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * The search window manager.
 * @author eric
 * @version $Revision:$
 */
public class SearchWindowManager implements EventListener {

    private static Log logger = LogFactory.getLog( SearchWindowManager.class );

    /**
     * Default constructor.
     */
    public SearchWindowManager() {
        logger.debug( "constructing SearchWindowManager" );
    }

    /**
     * Overriden from SearchWindowManager.
     * @see org.zkoss.zk.ui.event.EventListener#isAsap()
     */
    public boolean isAsap() {
        return true;
    }

    /**
     * Overriden from SearchWindowManager.
     * @see EventListener#onEvent(org.zkoss.zk.ui.event.Event)
     * @param event the event
     */
    public void onEvent( Event event ) {
        logger.debug(
            "search event %%^|||^%% in SearchWindowManager.onEvent" );
        try {
            new SearchWindow();
        } catch ( InterruptedException trouble ) {
            logger.error( "Trouble", trouble );
        }
    }
}
