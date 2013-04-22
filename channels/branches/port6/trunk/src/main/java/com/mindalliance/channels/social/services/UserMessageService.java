package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.Feedback;
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
    
    Iterator<UserMessage> getReceivedMessages( String username, CommunityService communityService );

    Iterator<UserMessage> getSentMessages( String username, CommunityService communityService );
    
    Date getWhenLastChanged( String planUri );
    
    Date getWhenLastReceived( String username, CommunityService communityService );

    void markSent( UserMessage message );

    Iterator<UserMessage> listMessagesToSend( String planUri );

    void markToNotify( UserMessage message );

    int countNewFeedbackReplies( CommunityService communityService, ChannelsUser user );

    void markFeedbackRepliesRead( Feedback feedback );
}
