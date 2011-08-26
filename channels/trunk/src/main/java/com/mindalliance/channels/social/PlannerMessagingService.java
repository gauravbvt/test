package com.mindalliance.channels.social;

import com.mindalliance.channels.core.model.Plan;

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

    boolean sendMessage( PlannerMessage message, boolean emailIt, Plan plan );

    void deleteMessage( PlannerMessage message, Plan plan );

    Iterator<PlannerMessage> getReceivedMessages( Plan plan );

    Iterator<PlannerMessage> getSentMessages( Plan plan);

    Date getWhenLastChanged( Plan plan);

    Date getWhenLastReceived( Plan plan);

    boolean email( PlannerMessage message, Plan plan );

}
