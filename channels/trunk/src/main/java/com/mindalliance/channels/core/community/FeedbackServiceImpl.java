package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/12
 * Time: 10:42 AM
 */
@Repository
public class FeedbackServiceImpl extends GenericSqlServiceImpl<Feedback, Long> implements FeedbackService {

    @Override
    @Transactional
    public void sendFeedback( 
            String username, 
            String planUri, 
            Feedback.Type type, 
            String topic, 
            String content, 
            boolean urgent ) {
        Feedback feedback = new Feedback( username, planUri, type );
        feedback.setTopic( topic );
        feedback.setContent( content );
        feedback.setUrgent( urgent );
        save( feedback );
    }

    @Override
    @Transactional
    public void sendFeedback(
            String username, 
            String planUri, 
            Feedback.Type type, 
            String topic, 
            String content, 
            boolean urgent, 
            ModelObject about ) {
        Feedback feedback = new Feedback( username, planUri, type );
        feedback.setTopic( topic );
        feedback.setContent( content );
        feedback.setUrgent( urgent );
        feedback.setAbout( new ModelObjectRef( about ).asString() );
        save( feedback );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Feedback> listNotYetNotifiedNormalFeedbacks( String planUri ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.isNull( "whenNotified" ) );
        criteria.add( Restrictions.eq( "planUri", planUri ) );
        criteria.add( Restrictions.eq( "urgent", false ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<Feedback>) criteria.list();
    }

    @Override
    public List<Feedback> listNotYetNotifiedUrgentFeedbacks( ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.isNull( "whenNotified" ) );
        criteria.add( Restrictions.eq( "urgent", true ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<Feedback>) criteria.list();
    }
}
