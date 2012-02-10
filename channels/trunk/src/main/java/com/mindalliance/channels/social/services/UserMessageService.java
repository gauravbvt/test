package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
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

    public static final String PLANNERS = "__planners__";
    public static final String USERS = "__users__";

    boolean sendMessage( UserMessage message, boolean emailIt, ChannelsUser sender );
    
    void deleteMessage( UserMessage message );
    
    Iterator<UserMessage> getReceivedMessages( String username, String planUri );

    Iterator<UserMessage> getSentMessages( String username, String planUri );
    
    Date getWhenLastChanged( String planUri );
    
    Date getWhenLastReceived( String username, String planUri );

    boolean email( UserMessage message, ChannelsUser user );
}
