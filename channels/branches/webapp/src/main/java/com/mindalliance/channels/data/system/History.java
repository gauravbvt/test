// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import java.util.Map;

import com.mindalliance.channels.data.user.VersionHistory;
import com.mindalliance.channels.services.HistoryService;
import com.mindalliance.channels.util.GUID;

/**
 * System' audit log and element lifecycle.
 *
 * @todo Access to system usage log, system metrics over time,
 * system events...
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class History extends AbstractQueryable implements HistoryService {

    private Map<GUID,VersionHistory> versionHistories;

    /**
     * Default constructor.
     */
    public History() {
    }

    /**
     * Default constructor.
     * @param system the system
     */
    public History( System system ) {
        super( system );
    }

    /**
     * Return the version histories.
     */
    public Map<GUID,VersionHistory> getVersionHistories() {
        return versionHistories;
    }

    /**
     * Set the version histories.
     * @param versionHistories the elementHistories to set
     */
    public void setVersionHistories(
            Map<GUID,VersionHistory> versionHistories ) {
        this.versionHistories = versionHistories;
    }
}
