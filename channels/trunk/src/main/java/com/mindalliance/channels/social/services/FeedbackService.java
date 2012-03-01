package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.UserMessage;

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
            Plan plan,
            Feedback.Type type,
            String topic,
            String content,
            boolean urgent );

    void sendFeedback(
            String username,
            Plan plan,
            Feedback.Type type,
            String topic,
            String text,
            boolean urgent,
            ModelObject mo );

    List<Feedback> listNotYetNotifiedNormalFeedbacks( Plan plan );

    List<Feedback> listNotYetNotifiedUrgentFeedbacks( );
    
    void addReplyTo( Feedback feedback, UserMessage reply, UserMessageService messageService );

    /**
     * Select feedbacks that are not replies to other feedbacks.
     * @param urgentOnly true if urgent only
     * @param unresolvedOnly true if unresolved only
     * @param notRepliedToOnly true if not replied to only
     * @param topic topic or null if any topic
     * @param containing substring of the contents or null if any
     * @return
     */
    List<Feedback> selectInitialFeedbacks(
            Boolean urgentOnly,
            Boolean unresolvedOnly,
            Boolean notRepliedToOnly,
            String topic,
            String containing );
}
