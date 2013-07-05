package com.mindalliance.channels.db.services.surveys;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.db.data.surveys.QRFISurvey;
import com.mindalliance.channels.db.data.surveys.Questionnaire;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.db.repositories.RFISurveyRepository;
import com.mindalliance.channels.db.services.AbstractDataService;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/1/13
 * Time: 10:11 AM
 */
@Component
public class RFISurveyServiceImpl extends AbstractDataService<RFISurvey> implements RFISurveyService {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private RFISurveyRepository repository;

    @Override
    public void save( RFISurvey rfiSurvey ) {
        repository.save( rfiSurvey );
    }

    @Override
    public RFISurvey load( String uid ) {
        return repository.findOne( uid );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RFISurvey> listActive( final CommunityService communityService ) {
        QRFISurvey qRFISurvey = QRFISurvey.rFISurvey;
        List<RFISurvey> results = toList(
                repository.findAll(
                        qRFISurvey.classLabel.eq( RFISurvey.class.getSimpleName() )
                                .and( qRFISurvey.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qRFISurvey.closed.isFalse() ) )
        );
        return (List<RFISurvey>) CollectionUtils.select(
                results,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (RFISurvey) object ).isOngoing( communityService );
                    }
                } );
    }

    @Override
    public List<RFISurvey> select( CommunityService communityService, boolean onlyOpen, String about ) {
        QRFISurvey qRFISurvey = QRFISurvey.rFISurvey;
        BooleanBuilder bb = new BooleanBuilder();
        bb.and( qRFISurvey.classLabel.eq( RFISurvey.class.getSimpleName() ) );
        if ( onlyOpen )
            bb.and( qRFISurvey.closed.isFalse() );
        if ( about != null && !about.isEmpty() ) {
            bb.and( qRFISurvey.about.eq( about ) );
        }
        return toList(
                repository.findAll(
                        bb
                                .and( qRFISurvey.communityUri.eq( communityService.getPlanCommunity().getUri() ) ),
                        qRFISurvey.created.desc() )
        );
    }

    @Override
    public List<RFISurvey> select( CommunityService communityService, ModelObject modelObject ) {
        QRFISurvey qRFISurvey = QRFISurvey.rFISurvey;
        return toList(
                repository.findAll(
                        qRFISurvey.classLabel.eq( RFISurvey.class.getSimpleName() )
                                .and( qRFISurvey.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qRFISurvey.moRef.eq( new ModelObjectRef( modelObject ).asString() ) ),
                        qRFISurvey.created.desc() )
        );
    }

    @Override
    public RFISurvey launch( CommunityService communityService,
                             String username,
                             Questionnaire q,
                             ModelObject modelObject ) {
        synchronized ( communityService.getPlanCommunity() ) {
            Questionnaire questionnaire = questionnaireService.refresh( q );
            if ( questionnaire != null && questionnaire.isActive() ) {
                RFISurvey rfiSurvey = new RFISurvey(
                        communityService.getPlanCommunity(),
                        username
                );
                rfiSurvey.setQuestionnaire( questionnaire );
                rfiSurvey.setMoRef( modelObject );
                save( rfiSurvey );
                return rfiSurvey;
            } else {
                return null;
            }
        }
    }

    @Override
    public RFISurvey findRemediationSurvey( CommunityService communityService, final Issue issue ) {
        List<RFISurvey> surveys = select( communityService, false, Questionnaire.makeRemediationAbout( issue ) );
        return (RFISurvey) CollectionUtils.find(
                surveys,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFISurvey survey = (RFISurvey) object;
                        Questionnaire questionnaire = questionnaireService.load( survey.getQuestionnaireUid() );
                        return questionnaire != null && questionnaire.isAboutRemediation( issue );
                    }
                }
        );
    }


    @Override
    public List<RFISurvey> findSurveys( CommunityService communityService, Questionnaire questionnaire ) {
        QRFISurvey qRFISurvey = QRFISurvey.rFISurvey;
        return toList(
                repository.findAll(
                        qRFISurvey.classLabel.eq( RFISurvey.class.getSimpleName() )
                                .and( qRFISurvey.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qRFISurvey.questionnaireUid.eq( questionnaire.getUid() ) ),
                        qRFISurvey.created.desc()
                )
        );
    }

    @Override
    public void toggleActivation( RFISurvey rfiSurvey ) {
        boolean toggled = !rfiSurvey.isClosed();
        getDb().updateFirst(
                new Query( where( "_id" ).is( new ObjectId( rfiSurvey.getUid() ) ) ),
                new Update().set( "closed", toggled ),
                RFISurvey.class
        );
        rfiSurvey.setClosed( toggled );
    }

}
