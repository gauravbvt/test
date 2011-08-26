package com.mindalliance.channels.surveys;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.dao.PlanListener;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.engine.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Abstract survey service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 21, 2009
 * Time: 1:53:35 PM
 */
public abstract class AbstractSurveyService implements SurveyService, InitializingBean {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractSurveyService.class );

    /**
     * Surveys.
     */
    Map<Plan, List<Survey>> surveys = new HashMap<Plan, List<Survey>>();

    /**
     * Query service.
     */
    private QueryService queryService;

    private UserService userService;

    /**
     * Default email address for survey help.
     */
    private String defaultEmailAddress;

    /**
     * Analyst.
     */
    private Analyst analyst;

    /**
     * Mail sender.
     */
    private MailSender mailSender;

    /**
     * Velocity engine.
     */
    private VelocityEngine velocityEngine;

    /**
     * Plan manager.
     */
    private PlanManager planManager;

    /**
     * Survey records file name.
     */
    private String surveysFile = "surveys";

    protected AbstractSurveyService() {
    }

    private Survey makeSurvey( Survey.Type type, Identifiable identifiable ) throws SurveyException {
        Survey survey = null;
        switch ( type ) {
            case Remediation:
                if ( identifiable instanceof Issue ) {
                    survey = new IssueRemediationSurvey( (Issue) identifiable );
                }
                break;
            // todo -- add other types of surveys here
        }
        if ( survey == null ) {
            throw new SurveyException( "No known survey for " + type + " and " + identifiable );
        } else {
            for ( String username : survey.getDefaultContacts( analyst ) ) {
                survey.addContact( username );
            }
            return survey;
        }

    }

    public String getSurveysFile() {
        return surveysFile;
    }

    public void setSurveysFile( String surveysFile ) {
        this.surveysFile = surveysFile;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    public void setUserDetailsService( UserService userService ) {
        this.userService = userService;
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }

    public void setMailSender( MailSender mailSender ) {
        this.mailSender = mailSender;
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    public void setVelocityEngine( VelocityEngine engine ) {
        velocityEngine = engine;
    }

    public UserService getUserService() {
        return userService;
    }

    @Override
    public String getDefaultEmailAddress( Plan plan ) {
        String planValue = plan.getSurveyDefaultEmailAddress();
        return planValue.isEmpty() ? defaultEmailAddress : planValue;
    }

    public void setDefaultEmailAddress( String val ) {
        this.defaultEmailAddress = val == null ? "" : val;
    }

    /**
     * Called by Spring after properties have been set.
     */
    @Override
    public void afterPropertiesSet() {
        planManager.addListener(
                new PlanListener() {
                    @Override
                    public void aboutToProductize( Plan devPlan ) {
                        for ( Survey survey : getSurveys( devPlan ) )
                            try {
                                closeSurvey( survey, devPlan );
                            } catch ( SurveyException e ) {
                                LOG.error( "Unable to close survey", e );
                            }
                    }

                    @Override
                    public void productized( Plan plan ) {
                    }

                    @Override
                    public void created( Plan devPlan ) {
                        loadSurveys( devPlan );
                    }

                    @Override
                    public void loaded( PlanDao planDao ) {
                        loadSurveys( planDao.getPlan() );
                    }

                    @Override
                    public void aboutToUnload( PlanDao planDao ) {
                        Plan plan = planDao.getPlan();
                        for ( Survey survey : getSurveys( plan ) )
                            try {
                                closeSurvey( survey, plan );
                            } catch ( SurveyException e ) {
                                LOG.error( "Unable to close survey", e );
                            }
                    }
                } );

        for ( Plan plan : planManager.getPlans() )
            if ( plan.isDevelopment() ) {
                loadSurveys( plan );
            }
    }

    private void loadSurveys( Plan plan ) {
        File file;
        BufferedReader in = null;
        try {
            File dataDirectory = getDataDirectory( plan );
            file = new File( dataDirectory, surveysFile );
            if ( !file.exists() ) {
                file.createNewFile();
            } else {
                in = new BufferedReader( new FileReader( file ) );
                String line;
                while ( ( line = in.readLine() ) != null ) {
                    Survey survey = Survey.fromString( line );
                    addSurvey( plan, survey );
                }
            }
            LOG.debug( "Survey records loaded" );
        } catch ( Exception e ) {
            LOG.error( "Failed to load survey records", e );
        } finally {
            if ( in != null )
                try {
                    in.close();
                } catch ( IOException e ) {
                    LOG.error( "Unable to close survey records file.", e );
                }
        }
    }

    private void addSurvey( Plan plan, Survey survey ) {
        List<Survey> planSurveys = getSurveys( plan );
        planSurveys.add( survey );
    }

    private List<Survey> getSurveys( Plan plan ) {
        List<Survey> planSurveys = surveys.get( plan );
        if ( planSurveys == null ) {
            planSurveys = new ArrayList<Survey>();
            surveys.put( plan, planSurveys );
        }
        return planSurveys;
    }

    private void save() {
        PrintWriter out = null;
        try {
            File file = new File( getDataDirectory( User.plan() ), surveysFile );
            out = new PrintWriter( new FileWriter( file ) );
            for ( Survey survey : getSurveys( User.plan() ) ) {
                out.println( survey.toString() );
            }
            LOG.info( "Survey records saved" );
        } catch ( Exception e ) {
            LOG.error( "Unable to save survey records in " + surveysFile + ".", e );
        } finally {
            if ( out != null )
                out.close();
        }
    }

    private File getDataDirectory( Plan plan ) {
        return planManager.getVersion( plan ).getVersionDirectory();
    }

    @Override
    public boolean isSurveyed( Survey.Type type, Identifiable identifiable ) {
        return findOpenSurvey( type, identifiable ) != null;
    }

    @SuppressWarnings( "unchecked" )
    private Survey findOpenSurvey( final Survey.Type type, final Identifiable identifiable ) {
        Survey latestSurvey = null;
        List<Survey> planSurveys = (List<Survey>) CollectionUtils.select(
                getSurveys( User.plan() ), new Predicate() {
                    @Override
                    public boolean evaluate( Object obj ) {
                        Survey survey = (Survey) obj;
                        return !survey.isClosed() && survey.matches( type, identifiable );
                    }
                } );
        if ( !planSurveys.isEmpty() ) {
            Collections.sort(
                    planSurveys, new Comparator<Survey>() {
                        @Override
                        public int compare( Survey survey, Survey other ) {
                            return survey.getCreationDate().compareTo( other.getCreationDate() ) * -1;
                        }
                    } );
            latestSurvey = planSurveys.get( 0 );
        }
        return latestSurvey;
    }

    @Override
    public boolean isRelevant( Survey survey ) {
        return findIdentifiable( survey ) != null;
    }

    @Override
    public Identifiable findIdentifiable( final Survey survey ) {
        try {
            return survey.findIdentifiable( analyst );
        } catch ( NotFoundException e ) {
            LOG.warn( "What the survey is about does not exist anymore." );
            return null;
        }
    }

    @Override
    public Survey getOrCreateSurvey( Survey.Type type, Identifiable identifiable, Plan plan ) throws SurveyException {
        Survey survey = findOpenSurvey( type, identifiable );
        if ( survey == null ) {
            survey = makeSurvey( type, identifiable );
            survey.setCreationDate( new Date() );
            long id = registerSurvey( survey, plan );
            survey.setId( id );
            survey.setStatus( Survey.Status.In_design );
            survey.setIssuer( getIssuerName( survey ) );
            addSurvey( User.plan(), survey );
            save();
        }
        survey.updateSurveyData( this, plan );
        return survey;
    }

    @Override
    public void inviteContacts( Survey survey, List<String> usernames, Plan plan ) throws SurveyException {
        if ( survey.isLaunched() ) {
            inviteNewContacts( survey, plan );
        }
        save();
    }

    @Override
    public void deleteSurvey( Survey survey ) throws SurveyException {
        if ( !survey.canBeCancelled() )
            throw new SurveyException( "Can't cancel survey." );
        if ( survey.isRegistered() ) {
            unregisterSurvey( survey );
        }
        getSurveys( User.plan() ).remove( survey );
        save();
    }

    /**
     * Unregister a survey.
     *
     * @param survey a survey
     * @throws SurveyException if service call fails
     */
    protected abstract void unregisterSurvey( Survey survey ) throws SurveyException;

    @Override
    public void launchSurvey( Survey survey, Plan plan ) throws SurveyException {
        if ( !survey.isRegistered() )
            throw new SurveyException( "Survey not registered." );
        if ( survey.isClosed() || survey.isLaunched() )
            throw new SurveyException( "Survey already launched." );
        try {
            doLaunchSurvey( survey, plan );
            survey.setStatus( Survey.Status.Launched );
            survey.setLaunchDate( new Date() );
            survey.resetData();
            // Invite to-be-contacted users
            inviteNewContacts( survey, plan );
        } finally {
            // save no matter what - launch may succeed while invitations fail
            save();
        }
    }

    @Override
    public void closeSurvey( Survey survey, Plan plan ) throws SurveyException {
        if ( survey.isLaunched() ) {
            if ( survey.isClosed() )
                throw new SurveyException( "Survey already closed." );
            doCloseSurvey( survey, plan );
            survey.setStatus( Survey.Status.Closed );
            survey.setClosedDate( new Date() );
            save();
        }
    }

    @Override
    public List<Survey> getSurveys() {
        return new ArrayList<Survey>( surveys.get( User.plan() ) );
    }


    protected String getIssuerName( Survey survey ) {
        User user = userService.getUserNamed( survey.getUserName() );
        if ( user == null )
            return "unknown user";
        else
            return user.getFullName();
    }

    /*   protected String getParticipantsText( Survey survey ) {
            StringBuilder sb = new StringBuilder();
            List<String> names = new ArrayList<String>();
            for ( Contact contact : survey.getContacts() ) {
                User user = userService.getUserNamed( contact.getUsername() );
                if ( user != null ) {
                    names.add( user.getNormalizedFullName() );
                }
            }
            Collections.sort( names );
            Iterator<String> iter = names.iterator();
            while ( iter.hasNext() ) {
                sb.append( iter.next() );
                if ( iter.hasNext() )
                    sb.append( ", " );
            }
            return sb.toString();
        }
    */

    protected void inviteNewContacts( Survey survey, Plan plan ) throws SurveyException {
        for ( Contact contact : survey.getContacts() ) {
            if ( contact.isToBeContacted() ) {
                emailInvitationTo( contact, survey, plan );
                contact.setContacted();
            }
        }
    }

    private void emailInvitationTo( Contact contact, Survey survey, Plan plan ) throws SurveyException {
        User user = getUser( contact.getUsername() );
        if ( user == null )
            throw new SurveyException( "Unknown contact " + contact.getUsername() );
        User issuer = getUser( survey.getUserName() );
        if ( issuer == null )
            throw new SurveyException( "Unknown issuer " );
        Identifiable identifiable = findIdentifiable( survey );
        if ( identifiable == null )
            throw new SurveyException( "Unknown identifiable" );
        try {
            survey.updateSurveyData( this, plan );
            Map<String, Object> context = survey.getInvitationContext( this, user, issuer, plan );
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo( user.getEmail() );
            email.setSubject( "Survey: " + survey.getPlanText() );
            email.setFrom( issuer.getEmail() );
            email.setReplyTo( issuer.getEmail() );
            String invitation = resolveTemplate( survey.getInvitationTemplate(), context );
            email.setText( invitation );
            mailSender.send( email );
            LOG.info( "Invitation sent to " + user.getEmail() );
        } catch ( Exception e ) {
            LOG.error( "Failed to email invitation to " + contact, e );
            throw new SurveyException( "Failed to send invitation to " + user.getUsername() );
        }
    }


    protected User getUser( String username ) {
        return userService.getUserNamed( username );
    }

/*
    protected String getStringResource( String name ) {
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream( name );
            BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
            StringBuilder sb = new StringBuilder();
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
            return sb.toString();
        } catch ( IOException e ) {
            LOG.error( "Failed to read string resource " + name, e );
            throw new RuntimeException( "Failed to read string resource " + name, e );
        }
    }
*/

    protected String resolveTemplate(
            String template, Map<String, Object> context ) throws SurveyException {
        try {
            return VelocityEngineUtils.mergeTemplateIntoString(
                    velocityEngine, template, context );
        } catch ( Exception e ) {
            LOG.error( "Failed to process template", e );
            throw new SurveyException( "Failed to process template", e );
        }
    }

    public String getTypeName() {
        return "survey service";
    }

    public Analyst getAnalyst() {
        return analyst;
    }

    @Override
    public List<SurveyResponse> findSurveysResponses( User user, int maxNumber, boolean showCompleted ) throws SurveyException {
        List<SurveyResponse> surveyResponses = new ArrayList<SurveyResponse>();
        List<Survey> surveys = findOpenSurveysFor( user );
        Collections.sort( surveys, new Comparator<Survey>() {
            @Override
            public int compare( Survey s1, Survey s2 ) {
                return s2.getLaunchDate().compareTo( s1.getLaunchDate() );
            }
        } );
        Iterator<Survey> surveyIterator = surveys.iterator();
        int n = 0;
        while ( n < maxNumber && surveyIterator.hasNext() ) {
            Survey survey = surveyIterator.next();
            SurveyResponse surveyResponse = findSurveyResponse( survey, user );
            if ( showCompleted || !surveyResponse.isComplete() ) {
                survey.updateSurveyData( this, user.getPlan() );
                surveyResponses.add( surveyResponse );
                n++;
            }
        }
        return surveyResponses;
    }


    @SuppressWarnings( "unchecked" )
    private List<Survey> findOpenSurveysFor( User user ) {
        final String userName = user.getUsername();
        return (List<Survey>) CollectionUtils.select(
                getSurveys( user.getPlan() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Survey survey = (Survey) object;
                        return survey.isLaunched() && survey.hasContact( userName );
                    }
                }
        );
    }


    protected abstract long registerSurvey( Survey survey, Plan plan ) throws SurveyException;

    protected abstract void doLaunchSurvey( Survey survey, Plan plan ) throws SurveyException;

    protected abstract void doCloseSurvey( Survey survey, Plan plan ) throws SurveyException;

    protected abstract SurveyResponse findSurveyResponse( Survey survey, User user ) throws SurveyException;
}

