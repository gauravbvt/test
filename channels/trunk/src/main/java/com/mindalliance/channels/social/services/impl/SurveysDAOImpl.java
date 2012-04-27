package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.social.model.rfi.AnswerSet;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.AnswerSetService;
import com.mindalliance.channels.social.services.QuestionService;
import com.mindalliance.channels.social.services.QuestionnaireService;
import com.mindalliance.channels.social.services.RFIService;
import com.mindalliance.channels.social.services.RFISurveyService;
import com.mindalliance.channels.social.services.SurveysDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/19/12
 * Time: 10:33 AM
 */
@Service
public class SurveysDAOImpl implements SurveysDAO {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( SurveysDAOImpl.class );


    @Autowired
    private RFISurveyService rfiSurveyService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RFIService rfiService;

    @Autowired
    private AnswerSetService answerSetService;

    @Override
    @Transactional
    public RFISurvey getOrCreateRemediationSurvey(
            String username,
            Plan plan,
            QueryService queryService,
            Issue issue ) {
        RFISurvey survey = rfiSurveyService.findRemediationSurvey(
                plan,
                issue,
                queryService
        );
        if ( survey == null ) {
            Questionnaire questionnaire = findOrCreateRemediationQuestionnaire(
                    username,
                    plan,
                    issue );
            survey = new RFISurvey( plan, username );
            survey.setQuestionnaire( questionnaire );
            survey.setMoRef( issue.getAbout() );
            rfiSurveyService.save( survey );
        }
        return survey;
    }

    private Questionnaire findOrCreateRemediationQuestionnaire(
            String username,
            Plan plan,
            Issue issue ) {
        Questionnaire questionnaire = questionnaireService.findRemediationQuestionnaire( plan, issue );
        if ( questionnaire == null ) {
            questionnaire = new Questionnaire( plan, username );
            questionnaire.setName( Questionnaire.makeRemediationName( issue ) );
            questionnaire.setAbout( Questionnaire.makeRemediationAbout( issue ) );
            questionnaire.setStatus( Questionnaire.Status.ACTIVE );
            questionnaire.setIssueRemediated( issue );
            questionnaireService.save( questionnaire );
            List<Question> questions = makeRemediationQuestions( questionnaire, username, issue );
            int index = 0;
            for ( Question question : questions ) {
                question.setIndex( index );
                index++;
                question.setActivated( true );
                questionService.save( question );
            }
        }
        return questionnaire;
    }

    private List<Question> makeRemediationQuestions( Questionnaire questionnaire, String username, Issue issue ) {
        List<Question> questions = new ArrayList<Question>();
        ModelObject mo = issue.getAbout();
        // Intro
        Question intro = new Question( username, questionnaire );
        intro.setType( Question.Type.STATEMENT );
        String severity = issue.getSeverity().getNegativeLabel().toLowerCase();
        intro.setText(
                "Your help is requested to resolve "
                + ( ChannelsUtils.startsWithVowel( severity ) ? "an " : "a" )
                        + severity
                        + " issue that was detected about "
                        + mo.getKindLabel()
                        + " \""
                        + mo.getLabel()
                        + "\": "
                        + issue.getDetectorLabel() );
        questions.add( intro );
        // Description
        Question description = new Question( username, questionnaire );
        description.setType( Question.Type.STATEMENT );
        description.setText( issue.getDescription() );
        questions.add(  description );
        // Remediation options (open, multiple choice)
        List<String> options = getRemediationOptions( issue );
        Question remediationOptions = new Question( username, questionnaire );
        remediationOptions.setText( "Please choose solutions that you believe are appropriate:");
        remediationOptions.setType( Question.Type.CHOICE );
        remediationOptions.setOpenEnded( true );
        remediationOptions.setMultipleAnswers( true );
        remediationOptions.setAnswerChoices( options );
        remediationOptions.setAnswerRequired( true );
        questions.add( remediationOptions );
        // Other comments
        Question comment = new Question( username, questionnaire );
        comment.setType( Question.Type.SHORT_FORM );
        comment.setMultipleAnswers( false );
        comment.setText( "Other solutions, comments?" );
        comment.setAnswerRequired( false );
        questions.add( comment );
        // Thank you
        Question thanks = new Question( username, questionnaire );
        thanks.setType( Question.Type.STATEMENT );
        thanks.setText( "Thank you!" );
        questions.add( thanks );
        //
        return questions;
    }


    private List<String> getRemediationOptions( Issue issue ) {
        try {
            List<String> options = new ArrayList<String>();
            BufferedReader reader = new BufferedReader( new StringReader( issue.getRemediation() ) );
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                options.add( optionize( line ) );
            }
            options.add( "Other (see below)" );
            return options;
        } catch ( IOException e ) {
            LOG.error( "Failed to get remediation options", e );
            throw new RuntimeException( e );
        }
    }

    private String optionize( String line ) {
        String option = StringUtils.trim( line );
        if ( option.startsWith( "or" ) )
            option = option.substring( 2 );
        option = StringUtils.trim( option );
        if ( option.endsWith( "." ) )
            option = option.substring( 0, option.length() - 1 );
        option = StringUtils.capitalize( option );
        return option;
    }

    @Override
    @Transactional( readOnly = true )
    public int countUnanswered( Plan plan, ChannelsUser user, QueryService queryService, Analyst analyst ) {
        return CollectionUtils.select(
                rfiService.listUserActiveRFIs( plan, user, queryService, analyst ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return isUnanswered( rfi );
                    }
                }
        ).size();
    }


    private boolean isUnanswered( RFI rfi ) {
        return answerSetService.select( rfi ).isEmpty();
    }

    @Override
    @Transactional( readOnly = true )
    public int countIncomplete( Plan plan, ChannelsUser user, QueryService queryService, Analyst analyst ) {
        return findIncompleteRFIs( plan, user, queryService, analyst ).size();
    }

    @Override
    @Transactional( readOnly = true )
    public int countLate( Plan plan, ChannelsUser user, final QueryService queryService, final Analyst analyst ) {
        return CollectionUtils.select(
                rfiService.listUserActiveRFIs( plan, user, queryService, analyst ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return rfi.isLate( queryService, analyst );
                    }
                }
        ).size();
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<RFI> findIncompleteRFIs( Plan plan, ChannelsUser user, QueryService queryService, Analyst analyst ) {
        return (List<RFI>)CollectionUtils.select(
                rfiService.listUserActiveRFIs( plan, user, queryService, analyst ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return !isCompleted( rfi );
                    }
                } );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<RFI> findCompletedRFIs( Plan plan, ChannelsUser user, QueryService queryService, Analyst analyst ) {
        return (List<RFI>)CollectionUtils.select(
                rfiService.listUserActiveRFIs( plan, user, queryService, analyst ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return isCompleted( rfi );
                    }
                } );

    }

    @Override
    @Transactional( readOnly = true )
    public boolean isCompleted( final RFI rfi ) {
        RFISurvey survey = rfi.getRfiSurvey();
        Questionnaire questionnaire = survey.getQuestionnaire();
        List<Question> questions = questionService.listQuestions( questionnaire );
        // No required, unanswered questions
        return !CollectionUtils.exists(
                questions,
                // unanswered, required question
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Question question = (Question) object;
                        if ( question.isAnswerRequired() ) {
                            AnswerSet answerSet = answerSetService.findAnswers( rfi, question );
                            return answerSet == null || answerSet.isEmpty();
                        } else {
                            return false;
                        }

                    }
                }
        );
    }


    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<RFI> findDeclinedRFIs( Plan plan, ChannelsUser user, QueryService queryService, Analyst analyst ) {
        return (List<RFI>)CollectionUtils.select(
                rfiService.listOngoingUserRFIs( plan, user, queryService, analyst ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return rfi.isDeclined();
                    }
                } );
    }

    @Override
    @Transactional( readOnly = true )
    public int getRequiredQuestionCount( RFI rfi ) {
        return getRequiredQuestions( rfi ).size();
    }

    @SuppressWarnings( "unchecked" )
    private List<Question> getRequiredQuestions( RFI rfi ) {
        RFISurvey survey = rfi.getRfiSurvey();
        rfiSurveyService.refresh( survey );
        Questionnaire questionnaire = survey.getQuestionnaire();
        return (List<Question>)CollectionUtils.select(
                questionnaire.getQuestions(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Question question = (Question)object;
                        questionService.refresh( question );
                        return question.isAnswerRequired();
                    }
                }
        );
    }

    @Override
    @Transactional( readOnly = true )
    public int getRequiredAnswersCount( RFI rfi ) {
        int count = 0;
        for ( Question question : getRequiredQuestions( rfi )) {
            AnswerSet answerSet = answerSetService.findAnswers( rfi, question );
            if ( answerSet != null ) count++;
        }
        return count;
    }

    @Override
    @Transactional( readOnly = true )
    public int getOptionalQuestionCount( RFI rfi ) {
        return getOptionalQuestions( rfi ).size();
    }

    @Override
    @Transactional( readOnly = true )
    public int getOptionalAnswersCount( RFI rfi ) {
        int count = 0;
        for ( Question question : getOptionalQuestions( rfi )) {
            AnswerSet answerSet = answerSetService.findAnswers( rfi, question );
            if ( answerSet != null ) count++;
        }
        return count;
    }

    @SuppressWarnings( "unchecked" )
    private List<Question> getOptionalQuestions( RFI rfi ) {
        RFISurvey survey = rfi.getRfiSurvey();
        rfiSurveyService.refresh( survey );
        Questionnaire questionnaire = survey.getQuestionnaire();
        return (List<Question>)CollectionUtils.select(
                questionnaire.getQuestions(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Question question = (Question)object;
                        questionService.refresh( question );
                        return !question.isAnswerRequired();
                    }
                }
        );
    }


    @Override
    @Transactional( readOnly = true )
    public String findResponseMetrics( Plan plan, final RFISurvey rfiSurvey ) {
        List<RFI> surveyedRFIs = rfiService.select( plan, rfiSurvey );
        Integer[] counts = new Integer[3];
        counts[0] = CollectionUtils.countMatches(
                surveyedRFIs,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Questionnaire questionnaire = rfiSurvey.getQuestionnaire();
                        return questionnaire.isOptional() || isCompleted( (RFI) object );
                    }
                }
        );
        counts[1] = CollectionUtils.countMatches(
                surveyedRFIs,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (RFI) object ).isDeclined();  //To change body of implemented methods use File | Settings | File Templates.
                    }
                }
        );
        counts[2] = surveyedRFIs.size() - ( counts[0] + counts[1] );
        return new MessageFormat( "{0}c {1}d {2}i" ).format( counts );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFI> findAnsweringRFIs( Plan plan, RFISurvey rfiSurvey ) {
        List<RFI> rfis = rfiService.select( plan, rfiSurvey );
        return (List<RFI>) CollectionUtils.select(
                rfis,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !answerSetService.select( (RFI) object ).isEmpty();
                    }
                }
        );
    }


}
