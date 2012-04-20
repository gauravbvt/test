package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.IssueRemediationSurveysDAO;
import com.mindalliance.channels.social.services.QuestionService;
import com.mindalliance.channels.social.services.QuestionnaireService;
import com.mindalliance.channels.social.services.RFISurveyService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
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
public class IssueRemediationSurveysDAOImpl implements IssueRemediationSurveysDAO {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( IssueRemediationSurveysDAOImpl.class );


    @Autowired
    private RFISurveyService rfiSurveyService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionService questionService;

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
            questionnaire.setName( "Resolving: " + issue.getDetectorLabel() );
            questionnaire.makeAboutRemediation( issue );
            questionnaire.setStatus( Questionnaire.Status.ACTIVE );
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


}
