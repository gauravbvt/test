package com.mindalliance.channels.db.services.surveys;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.surveys.QQuestionnaire;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.Questionnaire;
import com.mindalliance.channels.db.repositories.QuestionnaireRepository;
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
 * Date: 4/30/13
 * Time: 3:59 PM
 */
@Component
public class QuestionnaireServiceImpl
        extends AbstractDataService<Questionnaire>
        implements QuestionnaireService {

    @Autowired
    private QuestionnaireRepository repository;

    @Autowired
    private RFISurveyService rfiSurveyService;

    @Autowired
    private RFIService rfiService;

    public void save( Questionnaire questionnaire ) {
        repository.save( questionnaire );
    }

    @Override
    public Questionnaire load( String uid ) {
        return repository.findOne( uid );
    }

    @Override
    public List<Questionnaire> select( CommunityService communityService,
                                       String about,
                                       Questionnaire.Status status,
                                       boolean includeRemediation ) {
        QQuestionnaire qQuestionnaire = QQuestionnaire.questionnaire;
        BooleanBuilder bb = new BooleanBuilder();
        bb.and( qQuestionnaire.classLabel.eq( Questionnaire.class.getSimpleName() ) );
        if ( about != null && !about.isEmpty() ) {
            bb.and( qQuestionnaire.about.eq( about ) );
        }
        if ( status != null ) {
            bb.and( qQuestionnaire.status.eq( status ) );
        }
        if ( !includeRemediation ) {
            bb.and( qQuestionnaire.issueKind.isNull() );
        }
        return toList(
                repository.findAll(
                        qQuestionnaire.communityUri.eq( communityService.getPlanCommunity().getUri() )
                                .and( bb ),
                        qQuestionnaire.created.desc()
                )
        );
    }

    @Override
    public List<Questionnaire> findApplicableQuestionnaires( CommunityService communityService, ModelObject modelObject ) {
        QQuestionnaire qQuestionnaire = QQuestionnaire.questionnaire;
        return toList(
                repository.findAll(
                        qQuestionnaire.classLabel.eq( Questionnaire.class.getSimpleName() )
                                .and( qQuestionnaire.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qQuestionnaire.about.eq( modelObject.getClassLabel() ) )
                                .and( qQuestionnaire.status.eq( Questionnaire.Status.ACTIVE ) )
                                .and( qQuestionnaire.remediatedModelObjectRefString.isNull() ),
                        qQuestionnaire.created.desc()
                )
        );
    }

    @Override
    public Questionnaire findRemediationQuestionnaire( CommunityService communityService, Issue issue ) {
        QQuestionnaire qQuestionnaire = QQuestionnaire.questionnaire;
        return repository.findOne(
                qQuestionnaire.classLabel.eq( Questionnaire.class.getSimpleName() )
                        .and( qQuestionnaire.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                        .and( qQuestionnaire.about.eq( Questionnaire.makeRemediationAbout( issue ) ) )
                        .and( qQuestionnaire.issueKind.eq( issue.getKind() ) )
                        .and( qQuestionnaire.remediatedModelObjectRefString.eq(
                                new ModelObjectRef( issue.getAbout() ).asString() ) )
                        .and( qQuestionnaire.status.eq( Questionnaire.Status.ACTIVE ) )
        );
    }

    @Override
    public boolean deleteIfAllowed( CommunityService communityService, Questionnaire q ) {
        Questionnaire questionnaire = load( q.getUid() );
        if ( questionnaire != null && !questionnaire.isActive() && !isUsed( communityService, questionnaire ) ) {
            repository.delete( questionnaire );
            return true;
        } else
            return false;

    }

    private boolean isUsed( CommunityService communityService, Questionnaire questionnaire ) {
        return !rfiSurveyService.findSurveys( communityService, questionnaire ).isEmpty();
    }

    @Override
    public void deleteQuestion( Question question ) {
        Questionnaire questionnaire = load( question.getQuestionnaireUid() );
        if ( questionnaire != null ) {
            questionnaire.removeQuestion( question );
            save( questionnaire );
        }
/*

        DBObject toDelete = new BasicDBObject( "_id", new ObjectId( question.getUid() ) );
        getDb().updateFirst(
                new Query( where( "_id" ).is( new ObjectId( question.getQuestionnaireUid() ) ) ),
                new Update().pull( "questions", toDelete ),
                Questionnaire.class
        );
*/
    }

    @Override
    public void saveQuestion( Question question ) {
        getDb().updateFirst(
                new Query( where( "_id" ).is( new ObjectId( question.getQuestionnaireUid() ) ) ),
                new Update().addToSet( "questions", question ),
                Questionnaire.class
        );
    }

    @Override
    public void updateQuestion( Question question ) {
        synchronized ( this ) {
            deleteQuestion( question );
            saveQuestion( question );
        }
    }

    @Override
    public Question refreshQuestion( final Question question ) {
        Query query = new Query( where( "questions" ).elemMatch( where( "_id" ).is( new ObjectId( question.getUid() ) ) ) );
        query.fields().include( "questions" );
        Questionnaire questionnaire = getDb().findOne(
                query,
                Questionnaire.class
        );
        return questionnaire == null ? null : questionnaire.findQuestionWithUid( question.getUid() );
    }

    @Override
    public void addAnswerChoice( Question question, String choice ) {
        getDb().updateFirst(
                new Query( where( "_id" ).is( new ObjectId( question.getQuestionnaireUid() ) )
                        .and( "questions._id" ).is( new ObjectId( question.getUid() ) ) ),
                new Update().addToSet( "questions.$.answerChoices", choice ),
                Questionnaire.class
        );
    }

    @Override
    public List<Question> listQuestions( String questionnaireUid ) {
        Questionnaire questionnaire = load( questionnaireUid );
        return questionnaire.getQuestions();
    }

    @Override
    public void moveUp( Question q ) {
        synchronized ( this ) {
            Question question = refresh( q );
            if ( question != null ) {
                Questionnaire questionnaire = load( question.getQuestionnaireUid() );
                if ( questionnaire != null ) {
                    questionnaire.moveUpQuestion( question );
                    save( questionnaire );
                }
            }
        }
    }

    private int getIndexOf( Question question ) {
        Questionnaire questionnaire = load( question.getQuestionnaireUid() );
        return questionnaire.getQuestions().indexOf( question );
    }

    @Override
    public void moveDown( final Question q ) {
        synchronized ( this ) {
            Question question = refresh( q );
            if ( question != null ) {
                Questionnaire questionnaire = load( question.getQuestionnaireUid() );
                if ( questionnaire != null ) {
                    questionnaire.moveDownQuestion( question );
                    save( questionnaire );
                }
            }
        }
    }

    private Question refresh( final Question q ) {
        Questionnaire questionnaire = load( q.getQuestionnaireUid() );
        if ( questionnaire != null )
            return (Question) CollectionUtils.find(
                    questionnaire.getQuestions(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Question) object ).getUid().equals( q.getUid() );
                        }
                    } );
        else
            return null;
    }

    @Override
    public Question addNewQuestion( ChannelsUser user, Questionnaire qr ) {
        Questionnaire questionnaire = load( qr.getUid() );
        if ( questionnaire != null ) {
            Question question = new Question( user.getUsername(), questionnaire );
            questionnaire.addQuestion( question );
            save( questionnaire );
            return question;
        } else {
            return null;
        }

    }

    @Override
    public void moveUpAnswerChoice( Question q, String choice ) {
        Question question = refresh( q );
        if ( question != null ) {
            List<String> newChoices = ChannelsUtils.moveUp( choice, question.getAnswerChoices() );
            getDb().updateFirst(
                    new Query( where( "_id" ).is( new ObjectId( q.getQuestionnaireUid() ) )
                            .and( "questions._id" ).is( new ObjectId( q.getUid() ) ) ),
                    new Update().set( "questions.$.answerChoices", newChoices ),
                    Questionnaire.class
            );
        }
    }

    @Override
    public void moveDownAnswerChoice( Question q, String choice ) {
        Question question = refresh( q );
        if ( question != null ) {
            List<String> newChoices = ChannelsUtils.moveDown( choice, question.getAnswerChoices() );
            getDb().updateFirst(
                    new Query( where( "_id" ).is( new ObjectId( q.getQuestionnaireUid() ) )
                            .and( "questions._id" ).is( new ObjectId( q.getUid() ) ) ),
                    new Update().set( "questions.$.answerChoices", newChoices ),
                    Questionnaire.class
            );
        }
    }

    @Override
    public void deleteAnswerChoice( Question q, String choice ) {
        Question question = refresh( q );
        if ( question != null ) {
            List<String> newChoices = question.getAnswerChoices();
            newChoices.remove( choice );
            getDb().updateFirst(
                    new Query( where( "_id" ).is( new ObjectId( q.getQuestionnaireUid() ) )
                            .and( "questions._id" ).is( new ObjectId( q.getUid() ) ) ),
                    new Update().set( "questions.*.answerChoices", newChoices ),
                    Questionnaire.class
            );
        }
    }

    @Override
    public boolean deleteQuestionsIfUnanswered( Questionnaire qr ) {
        synchronized ( this ) {
            Questionnaire questionnaire = load( qr.getUid() );
            if ( questionnaire != null ) {
                List<Question> questions = listQuestions( questionnaire.getUid() );
                boolean answered = CollectionUtils.exists(
                        questions,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return rfiService.getAnswerCount( (Question) object ) > 0;
                            }
                        }
                );
                if ( !answered ) {
                    for ( Question question : questions ) {
                        deleteQuestion( question );
                    }
                    return true;
                }
            }
            return false;
        }
    }
}

