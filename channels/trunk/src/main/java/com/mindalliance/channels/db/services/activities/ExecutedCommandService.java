package com.mindalliance.channels.db.services.activities;

import com.mindalliance.channels.core.command.CommandListener;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.db.data.activities.ExecutedCommand;
import com.mindalliance.channels.db.services.DataService;
import com.mindalliance.channels.db.services.PageableDataService;

import java.util.Date;
import java.util.Iterator;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/18/13
 * Time: 5:27 PM
 */
public interface ExecutedCommandService extends CommandListener, PageableDataService<ExecutedCommand> {

    /**
     * Get an iterator on the command events recorded for a plan community.
     *
     * @param planCommunity a planCommunity
     * @return an iterator on command events
     */
    Iterator<ExecutedCommand> getExecutedCommands( PlanCommunity planCommunity );

    /**
     * When command execution data for a plan was last modified since server is up.
     *
     * @param planCommunityUri a string
     * @return a date
     */
    Date getWhenLastChanged( String planCommunityUri );

}
