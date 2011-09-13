package com.mindalliance.channels.social;

import com.mindalliance.channels.core.command.CommandListener;
import com.mindalliance.channels.core.command.PresenceListener;
import com.mindalliance.channels.core.model.Plan;

import java.util.Date;
import java.util.Iterator;

/**
 * Planning event service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2010
 * Time: 2:03:58 PM
 */
public interface PlanningEventService extends CommandListener, PresenceListener {

    Date getWhenLastChanged( Plan plan );

    Iterator<CommandEvent> getCommandEvents( Plan plan );

    PresenceEvent findLatestPresence( String username, Plan plan );

    boolean isActive( String username, Plan plan );
}
