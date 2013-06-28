package com.mindalliance.channels.social.services.impl.notification;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.social.services.notification.ChannelsMessagingService;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Channels messaging service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/8/12
 * Time: 11:37 AM
 */
@Component
public class ChannelsMessagingServiceImpl extends AbstractMessageServiceImpl implements ChannelsMessagingService {

    @Override
    public List<String> sendMessage(
            Messageable messageable,
            String topic,
            CommunityService communityService ) {
        return new ArrayList<String>(  );
    }

    @Override
    public boolean sendReport(
            List<UserRecord> recipients,
            List<? extends Messageable> messageables,
            String topic,
            CommunityService communityService ) {
        return false;  //Todo
    }

    @Override
    public boolean isInternal() {
        return true;
    }
}
