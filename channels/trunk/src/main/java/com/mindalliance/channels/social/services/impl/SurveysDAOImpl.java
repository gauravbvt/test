package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.surveys.RFIsPage;
import com.mindalliance.channels.social.model.rfi.Answer;
import com.mindalliance.channels.social.model.rfi.AnswerSet;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFIForward;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.AnswerService;
import com.mindalliance.channels.social.services.AnswerSetService;
import com.mindalliance.channels.social.services.QuestionService;
import com.mindalliance.channels.social.services.QuestionnaireService;
import com.mindalliance.channels.social.services.RFIForwardService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Autowired
    private AnswerService answerService;

    @Autowired
    private RFIForwardService rfiForwardService;

    @Autowired
    private ChannelsUserDao userDao;

    @Override
    @Transactional
    public RFISurvey getOrCreateRemediationSurvey(
            String username,
            CommunityService communityService,
            Issue issue ) {
        RFISurvey survey = rfiSurveyService.findRemediationSurvey(
                communityService,
                issue
        );
        if ( survey == null ) {
            Questionnaire questionnaire = findOrCreateRemediationQuestionnaire(
                    username,
                    communityService,
                    issue );
            survey = new RFISurvey( communityService.getPlanCommunity(), username );
            survey.setQuestionnaire( questionnaire );
            survey.setMoRef( issue.getAbout() );
            rfiSurveyService.save( survey );
        }
        return survey;
    }

    private Questionnaire findOrCreateRemediationQuestionnaire(
            String username,
            CommunityService communityService,
            Issue issue ) {
        Questionnaire questionnaire = questionnaireService.findRemediationQuestionnaire( communityService, issue );
        if ( questionnaire == null ) {
            questionnaire = new Questionnaire( communityService.getPlanCommunity(), username );
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
        questions.add( description );
        // Remediation options (open, multiple choice)
        List<String> options = getRemediationOptions( issue );
        Question remediationOptions = new Question( username, questionnaire );
        remediationOptions.setText( "Please choose solutions that you believe are appropriate:" );
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
            options.add( "Other (please elaborate below)" );
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
    public int countUnanswered( CommunityService communityService, ChannelsUser user ) {
        return CollectionUtils.select(
                rfiService.listUserActiveRFIs( communityService, user ),
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
    public int countIncomplete( CommunityService communityService, ChannelsUser user ) {
        return findIncompleteRFIs( communityService, user ).size();
    }

    @Override
    @Transactional( readOnly = true )
    public int countLate( final CommunityService communityService, ChannelsUser user ) {
        return CollectionUtils.select(
                rfiService.listUserActiveRFIs( communityService, user ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return isOverdue( communityService, rfi);
                    }
                }
        ).size();
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<RFI> findIncompleteRFIs( CommunityService communityService, ChannelsUser user ) {
        return (List<RFI>) CollectionUtils.select(
                rfiService.listUserActiveRFIs( communityService, user ),
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
    public List<RFI> findCompletedRFIs( CommunityService communityService, ChannelsUser user ) {
        return (List<RFI>) CollectionUtils.select(
                rfiService.listUserActiveRFIs( communityService, user ),
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
                            AnswerSet answerSet = answerSetService.findAnswerSet( rfi, question );
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
    public List<RFI> findDeclinedRFIs( CommunityService communityService, ChannelsUser user ) {
        return (List<RFI>) CollectionUtils.select(
                rfiService.listOngoingUserRFIs( communityService, user ),
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
        Questionnaire questionnaire = survey.getQuestionnaire();
        return (List<Question>) CollectionUtils.select(
                questionService.listQuestions( questionnaire ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Question question = (Question) object;
                        return question.isAnswerRequired();
                    }
                }
        );
    }

    @Override
    @Transactional( readOnly = true )
    public int getRequiredAnswersCount( RFI rfi ) {
        int count = 0;
        for ( Question question : getRequiredQuestions( rfi ) ) {
            AnswerSet answerSet = answerSetService.findAnswerSet( rfi, question );
            if ( answerSet != null && !answerSet.isEmpty() ) count++;
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
        for ( Question question : getOptionalQuestions( rfi ) ) {
            AnswerSet answerSet = answerSetService.findAnswerSet( rfi, question );
            if ( answerSet != null ) count++;
        }
        return count;
    }

    @SuppressWarnings( "unchecked" )
    private List<Question> getOptionalQuestions( RFI rfi ) {
        RFISurvey survey = rfi.getRfiSurvey();
        rfiSurveyService.refresh( survey );
        Questionnaire questionnaire = survey.getQuestionnaire();
        return (List<Question>) CollectionUtils.select(
                questionnaire.getQuestions(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Question question = (Question) object;
                        return question.isAnswerable() && !question.isAnswerRequired();
                    }
                }
        );
    }


    @Override
    @Transactional( readOnly = true )
    public Map<String, Integer> findResponseMetrics( CommunityService communityService, final RFISurvey rfiSurvey ) {
        List<RFI> surveyedRFIs = rfiService.select( communityService, rfiSurvey );
        Map<String, Integer> metrics = new HashMap<String, Integer>();
        int completed = CollectionUtils.countMatches(
                surveyedRFIs,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Questionnaire questionnaire = rfiSurvey.getQuestionnaire();
                        return isOptional( questionnaire ) || isCompleted( (RFI) object );
                    }
                }
        );
        int declined = CollectionUtils.countMatches(
                surveyedRFIs,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (RFI) object ).isDeclined();  //To change body of implemented methods use File | Settings | File Templates.
                    }
                }
        );
        int incomplete = surveyedRFIs.size() - ( completed + declined );
        metrics.put( "completed", completed );
        metrics.put( "declined", declined );
        metrics.put( "incomplete", incomplete );
        return metrics;
    }

    private boolean isOptional( Questionnaire questionnaire ) {
        return !CollectionUtils.exists(
                questionnaire.getQuestions(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Question) object ).isAnswerRequired();
                    }
                }
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFI> findAnsweringRFIs( CommunityService communityService, RFISurvey rfiSurvey ) {
        List<RFI> rfis = rfiService.select( communityService, rfiSurvey );
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

    @Override
    public int getPercentCompletion( RFI rfi ) {
        return Math.round( ( (float) getRequiredAnswersCount( rfi ) / getRequiredQuestionCount( rfi ) ) * 100 );
    }

    @Override
    @Transactional
    public void saveAnswerSet( AnswerSet answerSet ) {
        List<Answer> removedAnswers = answerSet.deleteRemovedOrEmptyAnswers();
        for ( Answer answer : removedAnswers ) {
            answerService.delete( answer );
        }
        if ( answerSet.isEmpty() ) {
            answerSetService.delete( answerSet );
        } else {
            answerSetService.save( answerSet );
        }
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isOverdue( CommunityService communityService, RFI rfi ) {
        return rfi.isLate( communityService )
                && !isCompleted( rfi );
    }

    @Override
    @Transactional( readOnly = true )
    public Map<String, Set<String>> processAnswers(
            CommunityService communityService,
            RFISurvey rfiSurvey,
            Question question,
            boolean sharedOnly,
            String excludedUsername ) {
        Map<String, Set<String>> results = new HashMap<String, Set<String>>();
        List<RFI> rfis = findAnsweringRFIs( communityService, rfiSurvey );
        List<Answer> openAnswers = new ArrayList<Answer>();
        List<Answer> anonymousAnswers = new ArrayList<Answer>();
        // collect answers
        for ( RFI rfi : rfis ) {
            if ( excludedUsername == null || !rfi.getSurveyedUsername().equals( excludedUsername ) ) {
                AnswerSet answerSet = answerSetService.findAnswerSet( rfi, question );
                if ( answerSet != null && ( !sharedOnly || answerSet.isShared() ) ) {
                    if ( question.isMultipleAnswers() ) {
                        if ( answerSet.isAnonymous() )
                            anonymousAnswers.addAll( answerSet.getValidAnswers() );
                        else
                            openAnswers.addAll( answerSet.getValidAnswers() );
                    } else {
                        Answer answer = answerSet.getAnswer();
                        if ( answer != null && !answer.wasRemoved() ) {
                            if ( answerSet.isAnonymous() )
                                anonymousAnswers.add( answer );
                            else
                                openAnswers.add( answer );
                        }
                    }
                }
            }
        }
        // process answers
        for ( Answer answer : openAnswers ) {
            String text = answer.getText();
            String username = answer.getUsername();
            Set<String> usernames = results.get( text );
            if ( usernames == null ) {
                usernames = new HashSet<String>();
                results.put( text, usernames );
            }
            usernames.add( username );
        }
        for ( Answer answer : anonymousAnswers ) {
            String text = answer.getText();
            String username = ChannelsUser.ANONYMOUS_USERNAME;
            Set<String> usernames = results.get( text );
            if ( usernames == null ) {
                usernames = new HashSet<String>();
                results.put( text, usernames );
            }
            usernames.add( username );
        }
        return results;
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<Question> listAnswerableQuestions( RFISurvey rfiSurvey ) {
        Questionnaire questionnaire = rfiSurvey.getQuestionnaire();
        return (List<Question>) CollectionUtils.select(
                questionService.listQuestions( questionnaire ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Question) object ).isAnswerable();
                    }
                }
        );

    }

    @Override
    @Transactional( readOnly = true )
    public long getPercentRequiredQuestionsAnswered( RFI rfi ) {
        return Math.round( ( getRequiredAnswersCount( rfi ) / ( 1.0 * getRequiredQuestions( rfi ).size() ) ) * 100 );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<RFI> listIncompleteActiveRFIs( CommunityService communityService ) {
        List<RFI> activeRFIs = rfiService.listActiveRFIs( communityService );
        return (List<RFI>) CollectionUtils.select(
                activeRFIs,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !isCompleted( (RFI) object );
                    }
                } );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<RFI> findAllCompletedRFIs( CommunityService communityService, RFISurvey rfiSurvey ) {
        return (List<RFI>) CollectionUtils.select(
                rfiService.select( communityService, rfiSurvey ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isCompleted( (RFI) object );
                    }
                } );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<RFI> findAllIncompleteRFIs( CommunityService communityService, RFISurvey rfiSurvey ) {
        return (List<RFI>) CollectionUtils.select(
                rfiService.select( communityService, rfiSurvey ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return !rfi.isDeclined() && !isCompleted( rfi );
                    }
                } );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<RFI> findAllDeclinedRFIs( CommunityService communityService, RFISurvey rfiSurvey ) {
        return (List<RFI>) CollectionUtils.select(
                rfiService.select( communityService, rfiSurvey ),
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
    @SuppressWarnings( "unchecked" )
    public List<RFIForward> findAllRFIForwards( CommunityService communityService, RFISurvey rfiSurvey ) {
        return rfiForwardService.select( communityService, rfiSurvey );
    }

    @Override
    @Transactional
    public List<String> forwardRFI(
            CommunityService communityService,
            ChannelsUser user,
            RFI rfi,
            List<String> forwardedTo,
            String message ) {
        List<String> alreadyForwardedTo = rfiForwardService.findForwardedTo( rfi );
        List<String> emailsOfParticipants = getParticipantsEmails( communityService, rfi.getRfiSurvey() );
        List<String> actualForwards = new ArrayList<String>();
        for ( String email : forwardedTo ) {
            if ( !alreadyForwardedTo.contains( email ) && !emailsOfParticipants.contains( email ) ) {
                RFIForward forward = new RFIForward( communityService.getPlanCommunity(), user, rfi, email, message );
                actualForwards.add( email );
                alreadyForwardedTo.add( email );
                // Create new user if needed. Remember generated password.
                ChannelsUserInfo forwardedToUser = userDao.getOrMakeUserFromEmail( email, communityService.getPlanService() );
                if ( forwardedToUser != null ) {
                    RFI newRFI = new RFI( rfi );
                    newRFI.setSurveyedUsername( userDao.getUserNamed( email ).getUsername() );
                    rfiService.save( newRFI );
                    rfiForwardService.save( forward );
                }
            }
        }
        return actualForwards;
    }

    @Override
    @Transactional( readOnly = true )
    public ChannelsUserInfo getForwarder( RFIForward rfiForward ) {
        ChannelsUser user = userDao.getUserNamed( rfiForward.getUsername() );
        return user != null
                ? user.getUserInfo()
                : null;
    }

    @Override
    @Transactional( readOnly = true )
    public List<RFIForward> getForwardingsOf( RFI rfi ) {
        String surveyedUsername = rfi.getSurveyedUsername();
        ChannelsUser surveyedUser = userDao.getUserNamed( surveyedUsername );
        if ( surveyedUser != null ) {
            RFISurvey rfiSurvey = rfi.getRfiSurvey();
            return rfiForwardService.findForwardsTo( surveyedUser.getEmail(), rfiSurvey );
        } else {
            return new ArrayList<RFIForward>();
        }
    }

    @Override
    public String makeURL( PlanService planService, RFI rfi ) {
        Plan plan = planService.getPlan();
        String serverUrl = planService.getServerUrl();
        return planService.getServerUrl()
                + ( serverUrl.endsWith( "/" ) ? "" : "/" )
                + "surveys?"
                + AbstractChannelsWebPage.PLAN_PARM
                + "="
                + plan.getUri()
                + "&"
                + AbstractChannelsWebPage.VERSION_PARM
                + "="
                + plan.getVersion()
                + "&"
                + RFIsPage.RFI_PARM
                + "=" + rfi.getId();
    }

    private List<String> getParticipantsEmails( CommunityService communityService, RFISurvey rfiSurvey ) {
        List<String> emails = new ArrayList<String>();
        List<RFI> rfis = rfiService.select( communityService, rfiSurvey );
        for ( RFI rfi : rfis ) {
            ChannelsUser user = userDao.getUserNamed( rfi.getSurveyedUsername() );
            if ( user != null ) emails.add( user.getEmail() );
        }
        return emails;
    }

}
