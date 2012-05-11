package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.UserMessage;

import java.util.Date;
import java.util.Iterator;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 3:39 PM
 */
public interface UserMessageService extends GenericSqlService<UserMessage, Long> {

    void sendMessage( UserMessage message, boolean emailIt );
    
    void deleteMessage( UserMessage message );
    
    Iterator<UserMessage> getReceivedMessages( String username, String planUri );

    Iterator<UserMessage> getSentMessages( String username, String planUri );
    
    Date getWhenLastChanged( String planUri );
    
    Date getWhenLastReceived( String username, String planUri );

    void markSent( UserMessage message );

    Iterator<UserMessage> listMessagesToSend( String planUri );

    void markToNotify( UserMessage message );
}
