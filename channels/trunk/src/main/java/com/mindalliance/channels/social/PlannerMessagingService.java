package com.mindalliance.channels.social;

import java.util.Date;
import java.util.Iterator;

/**
 * Planner messaging service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2010
 * Time: 2:04:18 PM
 */
public interface PlannerMessagingService {

    public static final String PLANNERS = "__planners__";
    public static final String USERS = "__users__";

    boolean sendMessage( PlannerMessage message, boolean emailIt, String urn );

    void deleteMessage( PlannerMessage message, String urn );

    Iterator<PlannerMessage> getReceivedMessages( String urn );

    Iterator<PlannerMessage> getSentMessages( String urn );

    Date getWhenLastChanged( String urn );

    Date getWhenLastReceived( String urn );

    boolean email( PlannerMessage message, String urn );

}
