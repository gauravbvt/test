package com.mindalliance.channels.social;

import com.mindalliance.channels.model.ModelObject;

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

    PlannerMessage broadcastMessage( String text );

    PlannerMessage broadcastMessage ( String text, ModelObject about );

    PlannerMessage sendMessage( String text, String toUserName );

    PlannerMessage sendMessage ( String text, ModelObject about, String toUserName );

    PlannerMessage getMessage( String messageId );

    void markAsRead( String messageId );

    PlannerMessageStatus getMessageStatus( String messageId );

    Iterator<PlannerMessage> getMessages( );
    
    void deleteMessage( String messageId );

    int getUnreadCount();
}
