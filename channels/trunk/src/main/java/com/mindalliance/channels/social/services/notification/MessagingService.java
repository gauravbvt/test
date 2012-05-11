package com.mindalliance.channels.social.services.notification;

import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.social.services.SurveysDAO;

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
     * @param planService a query service
     * @param surveysDAO   the surveys DAO
     * @return a boolean indicating success
     */
    boolean sendMessage(
            Messageable messageable,
            String topic,
            PlanService planService,
            SurveysDAO surveysDAO );

    /**
     * Send reports to users.
     *
     * @param messageables a list of messageables
     * @param topic        a messageable topic
     * @param planService a query service
     * @param surveysDAO   the surveys DAO
     * @return a boolean indicating success
     */
    boolean sendReport(
            List<ChannelsUserInfo> recipients,
            List<? extends Messageable> messageables,
            String topic,
            PlanService planService,
            SurveysDAO surveysDAO );

    /**
     * Whether the messaging service is internal to Channels.
     *
     * @return a boolean
     */
    boolean isInternal();
}
