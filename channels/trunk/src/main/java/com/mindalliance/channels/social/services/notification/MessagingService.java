package com.mindalliance.channels.social.services.notification;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.users.UserRecord;

import java.util.List;

/**
 * Messaging service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/8/12
 * Time: 10:46 AM
 */
public interface MessagingService {

    /**
     * Send messages to users.
     *
     * @param messageable  a messageable
     * @param topic        a messageable topic
     * @param communityService a plan community service
     * @return usernames message was successfully sent to
     */
    List<String> sendMessage(
            Messageable messageable,
            String topic,
            CommunityService communityService );

    /**
     * Send reports to users.
     *
     * @param messageables a list of messageables
     * @param topic        a messageable topic
     * @param communityService a plan community service
     * @return a boolean indicating success
     */
    boolean sendReport(
            List<UserRecord> recipients,
            List<? extends Messageable> messageables,
            String topic,
            CommunityService communityService );

    /**
     * Whether the messaging service is internal to Channels.
     *
     * @return a boolean
     */
    boolean isInternal();

    /**
     * Send an in
     * @param fromUser a user
     * @param emailAddress  an email address
     * @param message a message
     * @return a boolean indicating success
     */
    boolean sendInvitation(
            ChannelsUser fromUser,
            String emailAddress,
            String message);

}
