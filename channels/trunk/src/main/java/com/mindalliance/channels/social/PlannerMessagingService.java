package com.mindalliance.channels.social;

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

    void sendMessage( PlannerMessage message );

    PlannerMessage getMessage( String messageId );

    Iterator<PlannerMessage> getReceivedMessages( );
    
    void deleteMessage( String messageId );

    Iterator<PlannerMessage> getSentMessages();
}
