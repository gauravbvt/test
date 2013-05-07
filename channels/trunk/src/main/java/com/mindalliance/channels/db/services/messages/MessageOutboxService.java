package com.mindalliance.channels.db.services.messages;

import com.mindalliance.channels.db.data.messages.UserMessage;

import java.util.Iterator;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/29/13
 * Time: 8:29 PM
 */
public interface MessageOutboxService {

    Iterator<UserMessage> listMessagesToSend( String planCommunityUri );

    void markSent( UserMessage userMessage );
}
