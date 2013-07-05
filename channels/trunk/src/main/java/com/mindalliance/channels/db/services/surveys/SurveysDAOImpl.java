package com.mindalliance.channels.db.services.surveys;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.surveys.Answer;
import com.mindalliance.channels.db.data.surveys.AnswerSet;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.Questionnaire;
import com.mindalliance.channels.db.data.surveys.RFI;
import com.mindalliance.channels.db.data.surveys.RFIForward;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.surveys.RFIsPage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/1/13
 * Time: 10:17 AM
 */
@Component
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
    private RFIService rfiService;

    @Autowired
    private UserRecordService userDao;

    @Autowired
    private PlanCommunityManager planCommunityManager;

    @Override

    public RFISurvey getOrCreateRemediationSurvey(
            String username,
            CommunityService communityService,
            Issue issue ) {
        synchronized ( communityService.getPlanCommunity() ) {
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
    }

    private Questionnaire findOrCreateRemediationQuestionnaire(
            String username,
            CommunityService communityService,
            Issue issue ) {
        synchronized ( communityService.getPlanCommunity() ) {
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
                    questionnaireService.saveQuestion( question );
                }
            }
            return questionnaire;
        }
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
        return rfiService.selectAnswerSets( rfi ).isEmpty();
    }

    @Override

    public int countIncomplete( CommunityService communityService, ChannelsUser user ) {
        return findIncompleteRFIs( communityService, user ).size();
    }

    @Override

    public int countLate( final CommunityService communityService, ChannelsUser user ) {
        return CollectionUtils.select(
                rfiService.listUserActiveRFIs( communityService, user ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFI rfi = (RFI) object;
                        return isOverdue( communityService, rfi );
                    }
                }
        ).size();
    }

    @Override

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

    public boolean isCompleted( final RFI rfi ) {
        RFISurvey survey = rfiSurveyService.load( rfi.getRfiSurveyUid() );
        Questionnaire questionnaire = questionnaireService.load( survey.getQuestionnaireUid() );
        List<Question> questions = questionnaireService.listQuestions( questionnaire.getUid() );
        // No required, unanswered questions
        return !CollectionUtils.exists(
                questions,
                // unanswered, required question
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Question question = (Question) object;
                        if ( question.isAnswerRequired() ) {
                            AnswerSet answerSet = rfiService.findAnswerSet( rfi, question );
                            return answerSet == null || answerSet.isEmpty();
                        } else {
                            return false;
                        }

                    }
                }
        );
    }


    @Override

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

    public int getRequiredQuestionCount( RFI rfi ) {
        return getRequiredQuestions( rfi ).size();
    }

    @SuppressWarnings( "unchecked" )
    private List<Question> getRequiredQuestions( RFI rfi ) {
        RFISurvey survey = rfiSurveyService.load( rfi.getRfiSurveyUid() );
        Questionnaire questionnaire = questionnaireService.load( survey.getQuestionnaireUid() );
        return (List<Question>) CollectionUtils.select(
                questionnaireService.listQuestions( questionnaire.getUid() ),
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

    public int getRequiredAnswersCount( RFI rfi ) {
        int count = 0;
        for ( Question question : getRequiredQuestions( rfi ) ) {
            AnswerSet answerSet = rfiService.findAnswerSet( rfi, question );
            if ( answerSet != null && !answerSet.isEmpty() ) count++;
        }
        return count;
    }

    @Override
    public int getOptionalQuestionCount( RFI rfi ) {
        return getOptionalQuestions( rfi ).size();
    }

    @Override
    public int getOptionalAnswersCount( RFI rfi ) {
        int count = 0;
        for ( Question question : getOptionalQuestions( rfi ) ) {
            AnswerSet answerSet = rfiService.findAnswerSet( rfi, question );
            if ( answerSet != null ) count++;
        }
        return count;
    }

    @SuppressWarnings( "unchecked" )
    private List<Question> getOptionalQuestions( RFI rfi ) {
        RFISurvey survey = rfiSurveyService.load( rfi.getRfiSurveyUid() );
        Questionnaire questionnaire = questionnaireService.load( survey.getQuestionnaireUid() );
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
    public Map<String, Integer> findResponseMetrics( CommunityService communityService, final RFISurvey rfiSurvey ) {
        List<RFI> surveyedRFIs = rfiService.select( communityService, rfiSurvey );
        Map<String, Integer> metrics = new HashMap<String, Integer>();
        int completed = CollectionUtils.countMatches(
                surveyedRFIs,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Questionnaire questionnaire = questionnaireService.load( rfiSurvey.getQuestionnaireUid() );
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
    public List<RFI> findAnsweringRFIs( CommunityService communityService, RFISurvey rfiSurvey ) {
        List<RFI> rfis = rfiService.select( communityService, rfiSurvey );
        return (List<RFI>) CollectionUtils.select(
                rfis,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !rfiService.selectAnswerSets( (RFI) object ).isEmpty();
                    }
                }
        );
    }

    @Override
    public int getPercentCompletion( RFI rfi ) {
        return Math.round( ( (float) getRequiredAnswersCount( rfi ) / getRequiredQuestionCount( rfi ) ) * 100 );
    }

    @Override
    public boolean isOverdue( CommunityService communityService, RFI rfi ) {
        return rfi.isLate( communityService )
                && !isCompleted( rfi );
    }

    @Override
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
                AnswerSet answerSet = rfiService.findAnswerSet( rfi, question );
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
    @SuppressWarnings( "unchecked" )
    public List<Question> listAnswerableQuestions( RFISurvey rfiSurvey ) {
        Questionnaire questionnaire = questionnaireService.load( rfiSurvey.getQuestionnaireUid() );
        return (List<Question>) CollectionUtils.select(
                questionnaireService.listQuestions( questionnaire.getUid() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Question) object ).isAnswerable();
                    }
                }
        );

    }

    @Override
    public long getPercentRequiredQuestionsAnswered( RFI rfi ) {
        return Math.round( ( getRequiredAnswersCount( rfi ) / ( 1.0 * getRequiredQuestions( rfi ).size() ) ) * 100 );
    }

    @Override
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
    @SuppressWarnings( "unchecked" )
    public List<RFIForward> findAllRFIForwards( CommunityService communityService, RFISurvey rfiSurvey ) {
        return rfiService.selectForwards( communityService, rfiSurvey );
    }

    @Override
    public List<String> forwardRFI(
            CommunityService communityService,
            ChannelsUser user,
            RFI rfi,
            List<String> forwardedTo,
            String message ) {
        synchronized ( communityService.getPlanCommunity() ) {
            List<String> alreadyForwardedTo = rfiService.findForwardedTo( rfi );
            RFISurvey rfiSurvey = rfiSurveyService.load( rfi.getRfiSurveyUid() );
            List<String> emailsOfParticipants = getParticipantsEmails( communityService, rfiSurvey );
            List<String> actualForwards = new ArrayList<String>();
            for ( String email : forwardedTo ) {
                if ( !alreadyForwardedTo.contains( email ) && !emailsOfParticipants.contains( email ) ) {
                    RFIForward forward = new RFIForward( communityService.getPlanCommunity(), user, rfi, email, message );
                    actualForwards.add( email );
                    alreadyForwardedTo.add( email );
                    // Create new user if needed. Remember generated password.
                    UserRecord forwardedToUser = userDao.getOrMakeUserFromEmail( email, communityService.getPlanService() );
                    if ( forwardedToUser != null ) {
                        RFI newRFI = new RFI( rfi );
                        newRFI.setSurveyedUsername( userDao.getUserWithIdentity( email ).getUsername() );
                        rfiService.save( newRFI );
                        rfiService.saveRFIForward( forward );
                    }
                }
            }
            return actualForwards;
        }
    }

    @Override
    public UserRecord getForwarder( RFIForward rfiForward ) {
        ChannelsUser user = userDao.getUserWithIdentity( rfiForward.getUsername() );
        return user != null
                ? user.getUserRecord()
                : null;
    }

    @Override
    public List<RFIForward> getForwardingsOf( RFI rfi ) {
        String surveyedUsername = rfi.getSurveyedUsername();
        ChannelsUser surveyedUser = userDao.getUserWithIdentity( surveyedUsername );
        if ( surveyedUser != null ) {
            RFISurvey rfiSurvey = rfiSurveyService.load( rfi.getRfiSurveyUid() );
            return rfiService.findForwardsTo( surveyedUser.getEmail(), rfiSurvey );
        } else {
            return new ArrayList<RFIForward>();
        }
    }

    @Override
    public String makeURL( CommunityService communityService, RFI rfi, ChannelsUser surveyedUser ) {
        PlanCommunity planCommunity = planCommunityManager.findPlanCommunity(
                communityService.getPlan(),
                surveyedUser );
        String serverUrl = communityService.getPlanService().getServerUrl();
        return serverUrl
                + ( serverUrl.endsWith( "/" ) ? "" : "/" )
                + RFIsPage.SURVEYS
                + "?"
                + AbstractChannelsWebPage.COMMUNITY_PARM
                + "="
                + planCommunity.getUri()
                + "&"
                + RFIsPage.RFI_PARM
                + "=" + rfi.getUid();
    }

    private List<String> getParticipantsEmails( CommunityService communityService, RFISurvey rfiSurvey ) {
        List<String> emails = new ArrayList<String>();
        List<RFI> rfis = rfiService.select( communityService, rfiSurvey );
        for ( RFI rfi : rfis ) {
            ChannelsUser user = userDao.getUserWithIdentity( rfi.getSurveyedUsername() );
            if ( user != null ) emails.add( user.getEmail() );
        }
        return emails;
    }

    @Override
    public Questionnaire findQuestionnaire( String questionnaireUid ) {
        return questionnaireService.load( questionnaireUid );
    }

    @Override
    public RFISurvey findRFISurvey( String rfiSurveyUid ) {
        return rfiSurveyService.load( rfiSurveyUid );
    }
}
