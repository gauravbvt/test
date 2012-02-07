package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.command.CommandListener;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.ExecutedCommand;

import java.util.Date;
import java.util.Iterator;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 3:39 PM
 */
public interface ExecutedCommandService extends CommandListener, GenericSqlService<ExecutedCommand, Long> {

    /**
     * Get an iterator on the command events recorded for a plan.
     *
     * @param planUri a string
     * @return an iterator on command events
     */
    Iterator<ExecutedCommand> getExecutedCommands( String planUri );

    /**
     * When command execution data for a plan was last modified since server is up.
     *
     * @param planUri a string
     * @return a date
     */
    Date getWhenLastChanged( String planUri );
}
