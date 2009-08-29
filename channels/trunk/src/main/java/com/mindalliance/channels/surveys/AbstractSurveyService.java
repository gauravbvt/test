package com.mindalliance.channels.surveys;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.SurveyService;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ScenarioObject;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.util.FileUserDetailsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
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
abstract public class AbstractSurveyService implements SurveyService, InitializingBean {
    /**
     * Surveys.
     */
    Map<Plan, List<Survey>> surveys = new HashMap<Plan, List<Survey>>();
    /**
     * Query service.
     */
    private QueryService queryService;

    private FileUserDetailsService userDetailsService;
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
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( AbstractSurveyService.class );
    /**
     * Survey records file name.
     */
    private String surveysFile = "surveys";

    public AbstractSurveyService() {
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

    public void setUserDetailsService( FileUserDetailsService userDetailsService ) {
        this.userDetailsService = userDetailsService;
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

    public String getDefaultEmailAddress() {
        return defaultEmailAddress;
    }

    public void setDefaultEmailAddress( String defaultEmailAddress ) {
        this.defaultEmailAddress = defaultEmailAddress;
    }

    public void afterPropertiesSet() throws Exception {
        for ( Plan plan : planManager.getPlans() )
            loadSurveys( plan );
    }

    /**
     * {@inheritDoc}
     */
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
        } catch ( Exception e ) {
            LOG.error( "Failed to load survey records", e );
        } finally {
            if ( in != null ) try {
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
            File file = new File( getDataDirectory( getPlan() ), surveysFile );
            out = new PrintWriter( new FileWriter( file ) );
            for ( Survey survey : getSurveys( getPlan() ) ) {
                out.println( survey.toString() );
            }
        } catch ( Exception e ) {
            LOG.error( "Unable to save survey records in " + surveysFile + ".", e );
        } finally {
            if ( out != null ) out.close();
        }
    }

    private Plan getPlan() {
        return planManager.getCurrentPlan();
    }

    private File getDataDirectory( Plan plan ) throws IOException, NotFoundException {
        return planManager.getDao( plan ).getDataDirectory();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSurveyed( Issue issue ) {
        return findLatestSurvey( issue ) != null;
    }

    @SuppressWarnings( "unchecked" )
    private Survey findLatestSurvey( final Issue issue ) {
        Survey latestSurvey = null;
        List<Survey> planSurveys = (List<Survey>) CollectionUtils.select(
                getSurveys( getPlan() ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Survey) obj ).getIssueSpec().matches( issue );
                    }
                }
        );
        if ( !planSurveys.isEmpty() ) {
            Collections.sort(
                    planSurveys,
                    new Comparator<Survey>() {
                        public int compare( Survey survey, Survey other ) {
                            return survey.getCreationDate().compareTo( other.getCreationDate() ) * -1;

                        }
                    } );
            latestSurvey = planSurveys.get( 0 );
        }
        return latestSurvey;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRelevant( Survey survey ) {
        return findIssue( survey ) != null;
    }

    /**
     * Find current issue the survey is about.
     *
     * @param survey a survey
     * @return an issue or null
     */
    protected Issue findIssue( final Survey survey ) {
        Issue issue = null;
        try {
            ModelObject mo = queryService.find( ModelObject.class, survey.getIssueSpec().getAboutId() );
            issue = (Issue) CollectionUtils.find(
                    // exclude property-specific, exclude waived
                    analyst.listIssues( mo, false, false ),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return survey.getIssueSpec().matches( (Issue) obj );
                        }
                    }
            );
        } catch ( NotFoundException e ) {
            LOG.warn( "Model object the surveyed issue is about does not exist anymore." );
        }
        return issue;
    }

    /**
     * {@inheritDoc}
     */
    public Survey getOrCreateSurvey( Issue issue ) throws SurveyException {
        Survey survey = findLatestSurvey( issue );
        if ( survey == null ) {
            survey = new Survey( issue );
            survey.setCreationDate( new Date() );
            long id = registerSurvey( survey );
            survey.setId( id );
            survey.setStatus( Survey.Status.In_design );
            addContacts( survey, getDefaultContacts( survey, issue ) );
            addSurvey( getPlan(), survey );
            save();
        }
        if ( survey.getSurveyData() == null ) {
            survey.setSurveyData( getSurveyData( survey ) );
        }
        return survey;
    }

    protected Map<String, Object> getSurveyContext( Survey survey, Issue issue ) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put( "about", getAboutText( issue ) );
        context.put( "scenario", getScenarioText( issue ) );
        context.put( "plan", survey.getPlanText() );
        context.put( "deadline", survey.getDeadlineText() );
        context.put( "issue", getIssueDescriptionText( issue ) );
        context.put( "options", getRemediationOptions( issue ) );
        context.put( "issuer", getIssuerName( survey ) );
        context.put( "email", getIssuerEmail( survey ) );
        context.put( "timestamp", survey.getTimestamp() );
        context.put( "survey", survey.getTitle() );
        return context;
    }

    protected List<String> getRemediationOptions( Issue issue ) {
        try {
            List<String> options = new ArrayList<String>();
            BufferedReader reader = new BufferedReader( new StringReader( issue.getRemediation() ) );
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                options.add( optionize( line ) );
            }
            return options;
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    private String optionize( String line ) {
        String option = StringUtils.trim( line );
        if ( option.startsWith( "or" ) ) {
            option = option.substring( 2 );
        }
        option = StringUtils.trim( option );
        if ( option.endsWith( "." ) ) {
            option = option.substring( 0, option.length() - 1 );
        }
        option = StringUtils.capitalize( option );
        return option;
    }

    private List<String> getDefaultContacts( Survey survey, Issue issue ) {
        List<String> contacts = new ArrayList<String>();
        if ( issue.isDetected() ) {
            contacts.addAll( ( (DetectedIssue) issue ).getDefaultRemediators() );
        } else {
            // By default all planners
            contacts.addAll( queryService.findAllPlanners() );
        }
        contacts.remove( survey.getUserName() );
        return contacts;
    }


    /**
     * {@inheritDoc}
     */
    public void addContacts( Survey survey, List<String> usernames ) throws SurveyException {
        for ( String username : usernames ) {
            survey.addContact( username );
        }
        if ( survey.isLaunched() ) {
            inviteNewContacts( survey );
        }
        save();
    }

    /**
     * {@inheritDoc}
     */
    public void addContact( Survey survey, String username ) throws SurveyException {
        List<String> usernames = new ArrayList<String>();
        usernames.add( username );
        addContacts( survey, usernames );
    }


    /**
     * {@inheritDoc}
     */
    public void deleteSurvey( Survey survey ) throws SurveyException {
        if ( survey.isRegistered() ) throw new SurveyException( "Can't delete registered survey." );
        getSurveys( getPlan() ).remove( survey );
        save();
    }

    /**
     * {@inheritDoc}
     */
    public void launchSurvey( Survey survey ) throws SurveyException {
        if ( !survey.isRegistered() ) throw new SurveyException( "Survey not registered." );
        if ( survey.isClosed() || survey.isLaunched() ) throw new SurveyException( "Survey already launched." );
        doLaunchSurvey( survey );
        survey.setStatus( Survey.Status.Launched );
        survey.setLaunchDate( new Date() );
        save();
    }

    /**
     * {@inheritDoc}
     */
    public void closeSurvey( Survey survey ) throws SurveyException {
        if ( !survey.isLaunched() ) throw new SurveyException( "Survey not launched." );
        if ( survey.isClosed() ) throw new SurveyException( "Survey already launched." );
        doCloseSurvey( survey );
        survey.setStatus( Survey.Status.Closed );
        save();
    }

    public String getIssueDescriptionText( Issue issue ) {
        return issue.getDescription();
    }

    /**
     * Get html text for the model object the issue surveyd is about.
     *
     * @param issue an issue
     * @return a string
     */
    public String getAboutText( Issue issue ) {
        ModelObject about = issue.getAbout();
        String type = about.getTypeName();
        return type + " \"<strong>" + about.getLabel() + "</strong>\"";
    }

    /**
     * Get plain text for the model object the issue surveyd is about.
     *
     * @param issue an issue
     * @return a string
     */
    public String getAboutPlainText( Issue issue ) {
        ModelObject about = issue.getAbout();
        String type = about.getTypeName();
        return type + " \"" + about.getLabel() + "\"";
    }

    public String getScenarioText( Issue issue ) {
        ModelObject mo = issue.getAbout();
        if ( mo instanceof ScenarioObject ) {
            return ( (ScenarioObject) mo ).getScenario().getName();
        } else {
            return null;
        }
    }

    protected String getIssuerName( Survey survey ) {
        User user = userDetailsService.getUserNamed( survey.getUserName() );
        if ( user == null )
            return "unknown user";
        else
            return user.getFullName();
    }

    protected String getParticipantsText( Survey survey ) {
        StringBuilder sb = new StringBuilder();
        List<String> names = new ArrayList<String>();
        for ( Contact contact : survey.getContacts() ) {
            User user = userDetailsService.getUserNamed( contact.getUsername() );
            if ( user != null ) {
                names.add( user.getNormalizedFullName() );
            }
        }
        Collections.sort( names );
        Iterator<String> iter = names.iterator();
        while ( iter.hasNext() ) {
            sb.append( iter.next() );
            if ( iter.hasNext() ) sb.append( ", " );
        }
        return sb.toString();
    }

    protected String getIssuerEmail( Survey survey ) {
        User user = getUser( survey.getUserName() );
        if ( user == null )
            return defaultEmailAddress;
        else
            return user.getEmail();
    }

    protected void inviteNewContacts( Survey survey ) throws SurveyException {
        for ( Contact contact : survey.getContacts() ) {
            if ( !contact.isContacted() ) {
                emailInvitationTo( contact, survey );
            }
        }
    }

    private void emailInvitationTo( Contact contact, Survey survey ) throws SurveyException {
        User user = getUser( contact.getUsername() );
        if ( user == null ) throw new SurveyException( "Unknown contact " + contact.getUsername() );
        User issuer = getUser( survey.getUserName() );
        if ( issuer == null ) throw new SurveyException( "Unknown issuer " );
        Issue issue = findIssue( survey );
        if ( issue == null ) throw new SurveyException( "Unknown issue" );
        try {
            Map<String, Object> context = getInvitationContext( user, issuer, survey, issue );
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo( user.getEmail() );
            email.setSubject( "Survey: " + survey.getPlanText() );
            email.setFrom( issuer.getEmail() );
            email.setReplyTo( issuer.getEmail() );
            String invitation = resolveTemplate( "invitation.vm", context );
            email.setText( invitation );
            mailSender.send( email );
        } catch ( Exception e ) {
            throw new SurveyException( "Failed to send invitation to " + user.getUsername() );
        }
    }

    private Map<String, Object> getInvitationContext(
            User user,
            User issuer,
            Survey survey,
            Issue issue ) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put( "user", user );
        context.put( "issuer", issuer );
        context.put( "survey", survey );
        context.put( "surveyUrl", survey.getSurveyData().getPublishLink() );
        context.put( "issue", getIssueDescriptionText( issue ) );
        context.put( "scenario", getScenarioText( issue ) );
        context.put( "about", getAboutPlainText( issue ) );
        return context;
    }

    protected User getUser( String username ) {
        return userDetailsService.getUserNamed( username );
    }

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
            throw new RuntimeException( "Failed to read string resource " + name, e );
        }
    }

    protected String resolveTemplate(
            String template,
            Map<String, Object> context ) throws SurveyException {
        try {
            return VelocityEngineUtils.mergeTemplateIntoString(
                    velocityEngine,
                    template,
                    context );
        } catch ( Exception e ) {
            throw new SurveyException( "Failed to process template", e );
        }
    }


    abstract protected long registerSurvey( Survey survey ) throws SurveyException;

    abstract protected void doLaunchSurvey( Survey survey ) throws SurveyException;

    abstract protected void doCloseSurvey( Survey survey ) throws SurveyException;
}
