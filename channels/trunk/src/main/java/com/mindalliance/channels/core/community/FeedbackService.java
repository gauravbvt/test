package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.orm.service.GenericSqlService;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/12
 * Time: 9:46 AM
 */
public interface FeedbackService extends GenericSqlService<Feedback, Long> {

    void sendFeedback(
            String username,
            String planUri,
            Feedback.Type type,
            String topic,
            String content,
            boolean urgent );

    void sendFeedback(
            String username,
            String planUri,
            Feedback.Type type,
            String topic,
            String content,
            boolean urgent,
            ModelObject about );

    List<Feedback> listNotYetNotifiedNormalFeedbacks( String planUri );

    List<Feedback> listNotYetNotifiedUrgentFeedbacks( );

}
