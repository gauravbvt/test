package com.mindalliance.channels.db.services.messages;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.messages.UserMessage;
import com.mindalliance.channels.db.services.DataService;

import java.util.Date;
import java.util.Iterator;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 3:44 PM
 */
public interface UserMessageService extends DataService<UserMessage>, MessageOutboxService  {

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

}
