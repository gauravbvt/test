package com.mindalliance.channels.social;

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

    Iterator<CommandEvent> getCommandEvents();

    PresenceEvent findLatestPresence( String username );
}
