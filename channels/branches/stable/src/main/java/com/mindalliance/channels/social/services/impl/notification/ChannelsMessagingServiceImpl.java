package com.mindalliance.channels.social.services.impl.notification;

import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.social.services.SurveysDAO;
import com.mindalliance.channels.social.services.notification.ChannelsMessagingService;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.springframework.stereotype.Component;

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
    public boolean sendMessage(
            Messageable messageable,
            String topic,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        return false;  //Todo
    }

    @Override
    public boolean sendReport(
            List<ChannelsUserInfo> recipients,
            List<? extends Messageable> messageables,
            String topic,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        return false;  //Todo
    }

    @Override
    public boolean isInternal() {
        return true;
    }
}
