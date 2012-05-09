package com.mindalliance.channels.social.services.notification;

import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Plan;

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
     * @param messageable a messageable
     * @return a boolean indicating success
     */
    boolean sendMessage( Messageable messageable );

    /**
     * Send reports to users.
     *
     *
     * @param plan a plan
     * @param messageables a list of messageables
     * @return a boolean indicating success
     */
    boolean sendReport( Plan plan, List<ChannelsUserInfo> recipients, List<? extends Messageable> messageables );

    /**
     * Whether the messaging service is internal to Channels.
     *
     * @return a boolean
     */
    boolean isInternal();
}
