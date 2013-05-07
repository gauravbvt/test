package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.RFIService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 2:21 PM
 */
/*@Repository*/
public class RFIServiceImpl extends GenericSqlServiceImpl<RFI, Long> implements RFIService {

    public RFIServiceImpl() {
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<RFI> select( CommunityService communityService, RFISurvey rfiSurvey ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.eq( "rfiSurvey", rfiSurvey ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<RFI>) criteria.list();
    }

    @Override
    @Transactional( readOnly = true )
    public int getRFICount( CommunityService communityService, final Questionnaire questionnaire ) {
        List<RFI> rfis = list();
        return CollectionUtils.select(
                rfis,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return rfi.getRfiSurvey().getQuestionnaire().equals( questionnaire );
                    }
                }

        ).size();
    }

    @Override
    @Transactional
    public void makeOrUpdateRFI(
            CommunityService communityService,
            String username,
            RFISurvey rfiSurvey,
            ChannelsUserInfo userInfo,
            Organization organization,
            String title,
            Role role,
            Date deadlineDate ) {
        String surveyedUsername = userInfo.getUsername();
        RFI rfi = find( communityService, rfiSurvey, surveyedUsername );
        if ( rfi == null ) {
            rfi = new RFI( username, communityService.getPlanCommunity() );
            rfi.setSurveyedUsername( surveyedUsername );
            if ( organization != null )
                rfi.setOrganizationId( organization.getId() );
            if ( role != null )
                rfi.setRoleId( role.getId() );
            rfi.setRfiSurvey( rfiSurvey );
        }
        rfi.setTitle( title );
        rfi.setDeadline( deadlineDate );
        save( rfi );
    }


    @Override
    @Transactional
    public void nag(
            CommunityService communityService,
            String username,
            RFISurvey rfiSurvey,
            ChannelsUserInfo userInfo ) {
        RFI rfi = find( communityService, rfiSurvey, userInfo.getUsername() );
        if ( rfi != null ) {
            rfi.nag();
            save( rfi );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public RFI find(
            CommunityService communityService,
            RFISurvey rfiSurvey,
            String surveyedUsername ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.eq( "rfiSurvey", rfiSurvey ) );
        criteria.add( Restrictions.eq( "surveyedUsername", surveyedUsername ) );
        List<RFI> rfis = criteria.list();
        assert rfis.size() <= 1;
        return rfis.size() > 0
                ? rfis.get( 0 )
                : null;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<String> findParticipants( CommunityService communityService, RFISurvey rfiSurvey ) {
        Set<String> usernames = new HashSet<String>();
        List<RFI> rfis = select( communityService, rfiSurvey );
        for ( RFI rfi : rfis ) {
            usernames.add( rfi.getSurveyedUsername() );
        }
        return new ArrayList<String>( usernames );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFI> listActiveRFIs( final CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        return (List<RFI>) CollectionUtils.select(
                criteria.list(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return rfi.isActive( communityService );
                    }
                } );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFI> listUserActiveRFIs(
            final CommunityService communityService,
            ChannelsUser user) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.eq( "surveyedUsername", user.getUsername() ) );
        return (List<RFI>) CollectionUtils.select(
                criteria.list(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return rfi.isActive( communityService );
                    }
                } );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFI> listOngoingUserRFIs(
            final CommunityService communityService,
            ChannelsUser user
    ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.eq( "surveyedUsername", user.getUsername() ) );
        return (List<RFI>) CollectionUtils.select(
                criteria.list(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return rfi.isOngoing( communityService );
                    }
                } );
    }

    @Override
    @Transactional
    public void toggleDecline( RFI rfi, String reason ) {
        rfi.setDeclined( !rfi.isDeclined() );
        rfi.setReasonDeclined( reason );
        save( rfi );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFI> listRequestedNags( CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.eq( "naggingRequested", true ) );
        return  (List<RFI>) CollectionUtils.select(
                criteria.list(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return !rfi.isNotificationSent( RFI.NAG );
                    }
                } );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFI> listApproachingDeadline( CommunityService communityService, long warningDelay ) {
        Date now = new Date( );
        Date warningBound = new Date( now.getTime() + warningDelay );
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.between( "deadline", now, warningBound ) );
        return  (List<RFI>) CollectionUtils.select(
                criteria.list(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return !rfi.isNotificationSent( RFI.DEADLINE );
                    }
                } );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFI> listNewRFIs( CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        return  (List<RFI>) CollectionUtils.select(
                criteria.list(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return !rfi.isDeclined() && !rfi.isNotificationSent( RFI.NEW );
                    }
                } );
    }


}
