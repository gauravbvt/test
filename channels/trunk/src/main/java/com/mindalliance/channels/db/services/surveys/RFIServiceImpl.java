package com.mindalliance.channels.db.services.surveys;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.db.data.surveys.AnswerSet;
import com.mindalliance.channels.db.data.surveys.QRFI;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.Questionnaire;
import com.mindalliance.channels.db.data.surveys.RFI;
import com.mindalliance.channels.db.data.surveys.RFIForward;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.repositories.RFIRepository;
import com.mindalliance.channels.db.services.AbstractDataService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/1/13
 * Time: 10:12 AM
 */
@Component
public class RFIServiceImpl extends AbstractDataService<RFI> implements RFIService {

    @Autowired
    private RFIRepository repository;

    @Override
    public void save( RFI rfi ) {
        repository.save( rfi );
    }

    @Override
    public RFI load( String uid ) {
        return repository.findOne( uid );
    }

    @Override
    public List<RFI> select( CommunityService communityService, RFISurvey rfiSurvey ) {
        QRFI qrfi = QRFI.rFI;
        return toList(
                repository.findAll(
                        qrfi.classLabel.eq( RFI.class.getSimpleName() )
                                .and( qrfi.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qrfi.rfiSurveyUid.eq( rfiSurvey.getUid() ) ),
                        qrfi.created.desc()
                )
        );
    }

    @Override
    public int getRFICount( CommunityService communityService, Questionnaire questionnaire ) {
        QRFI qrfi = QRFI.rFI;
        List<RFI> results = toList(
                repository.findAll(
                        qrfi.classLabel.eq( RFI.class.getSimpleName() )
                                .and( qrfi.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qrfi.questionnaireUid.eq( questionnaire.getUid() ) )
                )
        );
        return results.size();
    }

    @Override
    public void makeOrUpdateRFI( CommunityService communityService,
                                 String username,
                                 RFISurvey rfiSurvey,
                                 UserRecord userInfo,
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
    public void nag( CommunityService communityService, String username, RFISurvey rfiSurvey, UserRecord userInfo ) {
        RFI rfi = find( communityService, rfiSurvey, userInfo.getUsername() );
        if ( rfi != null ) {
            rfi.nag();
            save( rfi );
        }
    }

    @Override
    public RFI find( CommunityService communityService, RFISurvey rfiSurvey, String surveyedUsername ) {
        QRFI qrfi = QRFI.rFI;
        return repository.findOne(
                qrfi.classLabel.eq( RFI.class.getSimpleName() )
                        .and( qrfi.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                        .and( qrfi.rfiSurveyUid.eq( rfiSurvey.getUid() ) )
                        .and( qrfi.surveyedUsername.eq( surveyedUsername ) )
        );
    }

    @Override
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
    public List<RFI> listActiveRFIs( final CommunityService communityService ) {
        QRFI qrfi = QRFI.rFI;
        List<RFI> results = toList(
                repository.findAll(
                        qrfi.classLabel.eq( RFI.class.getSimpleName() )
                                .and( qrfi.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                )
        );
        return (List<RFI>) CollectionUtils.select(
                results,
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
    public List<RFI> listUserActiveRFIs( final CommunityService communityService, ChannelsUser user ) {
        return (List<RFI>) CollectionUtils.select(
                listUserRFIs( communityService, user ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return rfi.isActive( communityService );
                    }
                } );
    }

    private List<RFI> listUserRFIs( CommunityService communityService, ChannelsUser user ) {
        QRFI qrfi = QRFI.rFI;
        return toList(
                repository.findAll(
                        qrfi.classLabel.eq( RFI.class.getSimpleName() )
                                .and( qrfi.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qrfi.surveyedUsername.eq( user.getUsername() ) )
                )
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<RFI> listOngoingUserRFIs( final CommunityService communityService, ChannelsUser user ) {
        return (List<RFI>) CollectionUtils.select(
                listUserRFIs( communityService, user ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return rfi.isOngoing( communityService );
                    }
                } );
    }

    @Override
    public void toggleDecline( RFI r, String reason ) {
        RFI rfi = refresh( r );
        if ( rfi != null ) {
            rfi.setDeclined( !rfi.isDeclined() );
            rfi.setReasonDeclined( reason );
            save( rfi );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<RFI> listRequestedNags( CommunityService communityService ) {
        QRFI qrfi = QRFI.rFI;
        List<RFI> results = toList(
                repository.findAll(
                        qrfi.classLabel.eq( RFI.class.getSimpleName() )
                                .and( qrfi.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qrfi.naggingRequested.isTrue() )
                )
        );
        return (List<RFI>) CollectionUtils.select(
                results,
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
    public List<RFI> listApproachingDeadline( CommunityService communityService, long warningDelay ) {
        Date now = new Date();
        Date warningBound = new Date( now.getTime() + warningDelay );
        QRFI qrfi = QRFI.rFI;
        List<RFI> results = toList(
                repository.findAll(
                        qrfi.classLabel.eq( RFI.class.getSimpleName() )
                                .and( qrfi.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qrfi.deadline.isNotNull() )
                                .and( qrfi.deadline.after( now ) )
                                .and( qrfi.deadline.before( warningBound ) )
                )
        );
        return (List<RFI>) CollectionUtils.select(
                results,
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
    public List<RFI> listNewRFIs( CommunityService communityService ) {
        QRFI qrfi = QRFI.rFI;
        List<RFI> results = toList(
                repository.findAll(
                        qrfi.classLabel.eq( RFI.class.getSimpleName() )
                                .and( qrfi.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                )
        );
        return (List<RFI>) CollectionUtils.select(
                results,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return !rfi.isDeclined() && !rfi.isNotificationSent( RFI.NEW );
                    }
                } );
    }

    @Override
    public AnswerSet findAnswerSet( RFI r, final Question question ) {
        RFI rfi = refresh( r );
        if ( rfi != null ) {
            return (AnswerSet) CollectionUtils.find(
                    rfi.getAnswerSets(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (AnswerSet) object ).getQuestionUid().equals( question.getUid() );
                        }
                    }
            );
        } else {
            return null;
        }
    }

    @Override
    public void saveAnswerSet( AnswerSet answerSet, RFI r, Question question ) {
        RFI rfi = refresh( r );
        if ( rfi != null ) {
            answerSet.setQuestionUid( question.getUid() );
            rfi.addAnswerSet( answerSet );
        }
        save( rfi );
    }

    @Override
    public List<RFIForward> selectForwards( CommunityService communityService, RFISurvey rfiSurvey ) {
        Query query = new Query(
                where( "classLabel" ).is( RFI.class.getSimpleName() )
                        .and( "communityUid" ).is( communityService.getPlanCommunity().getUri() )
                        .and( "rfiSurveyUid" ).is( rfiSurvey.getUid() ) );
        query.fields().include( "forwards" );
        List<RFI> rfis = getDb().find(
                query,
                RFI.class
        );
        List<RFIForward> forwards = new ArrayList<RFIForward>();
        for ( RFI rfi : rfis ) {
            forwards.addAll( rfi.getForwards() );
        }
        return forwards;
    }

    @Override
    public void saveRFIForward( RFIForward forward ) {
        Query query = new Query( where( "classLabel" ).is( RFI.class.getSimpleName() )
                .and( "_id" ).is( new ObjectId( forward.getRfiUid() ) ) );
        RFI rfi = getDb().findOne( query, RFI.class );
        if ( rfi != null ) {
            rfi.addForwarding( forward );
            save( rfi );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<RFIForward> findForwardsTo( final String email, RFISurvey rfiSurvey ) {
        Query query = new Query(
                where( "classLabel" ).is( RFI.class.getSimpleName() )
                        .and( "rfiSurveyUid" ).is( rfiSurvey.getUid() ) );
        query.fields().include( "forwards" );
        List<RFI> rfis = getDb().find(
                query,
                RFI.class
        );
        List<RFIForward> forwards = new ArrayList<RFIForward>();
        for ( RFI rfi : rfis ) {
            forwards.addAll(
                    (List<RFIForward>) CollectionUtils.select(
                            rfi.getForwards(),
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object object ) {
                                    return ( (RFIForward) object ).getForwardToEmail().equals( email );
                                }
                            }
                    )
            );
        }
        return forwards;
    }

    @Override
    public int getAnswerCount( Question question ) {
        // How many RFIs have an answer to the qustion?
        Query query = new Query(
                where( "classLabel" ).is( RFI.class.getSimpleName() )
                        .and( "answerSets" ).elemMatch( where( "questionUid" ).is( question.getUid() ) ) );
        query.fields().include( "_id" );
        return getDb().find( query, RFI.class ).size();
    }

    @Override
    public List<AnswerSet> selectAnswerSets( RFI r ) {
        RFI rfi = refresh( r );
        if ( rfi != null ) {
            return rfi.getAnswerSets();
        } else {
            return new ArrayList<AnswerSet>();
        }
    }

    @Override
    public List<String> findForwarderUsernames( RFI r ) {
        RFI rfi = refresh( r );
        List<String> forwarders = new ArrayList<String>();
        if ( rfi != null ) {
            for ( RFIForward forward : rfi.getForwards() ) {
                forwarders.add( forward.getUsername() );
            }
        }
        return forwarders;
    }

    @Override
    public List<String> findForwardedTo( RFI r ) {
        RFI rfi = refresh( r );
        List<String> forwardEmails = new ArrayList<String>();
        if ( rfi != null ) {
            for ( RFIForward forward : rfi.getForwards() ) {
                forwardEmails.add( forward.getForwardToEmail() );
            }
        }
        return forwardEmails;
    }

}
