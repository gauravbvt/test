/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.surveys;

import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserDao;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * A survey.
 */
abstract public class Survey implements Identifiable, Serializable {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( Survey.class );

    /**
     * Max title length.
     */
    public static final int MAX_TITLE_LENGTH = 80;

    /**
     * The current status of the survey.
     */
    private Status status;

    /**
     * id of registered survey (launched or closed). Negative if not registered.
     */
    private long id = -1L;

    /**
     * Name of the user who created or last configured the survey.
     */
    private String userName;

    /**
     * Users included in the survey.
     */
    private Set<Contact> contacts = new HashSet<Contact>();

    /**
     * Date of creation
     */
    private Date creationDate;

    /**
     * Date when the survey was launched.
     */
    private Date launchDate;

    /**
     * Date of creation
     */
    private Date closedDate;

    /**
     * Time interval from launchDate to deadline.
     */
    private Long timeToDeadline;

    /**
     * Full name of issuer.
     */
    private String issuer;

    /**
     * Simple date format.
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat( "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US );

    /**
     * Survey data.
     */
    private SurveyData surveyData;

    /**
     * Unknown survey.
     */
    public static Survey UNKNOWN = UnknownSurvey.getInstance();

    public abstract Identifiable findIdentifiable( Analyst analyst, QueryService queryService )
            throws NotFoundException;

    public abstract boolean matches( Type type, Identifiable identifiable );

    public abstract String getInvitationTemplate();

    public abstract String getSurveyTemplate();

    protected abstract List<String> getDefaultContacts( Analyst analyst, QueryService queryService );

    public abstract String getTitle();

    protected abstract String getIdentifiableSpecs();

    protected abstract void setIdentifiableSpecs( String specs );

    public abstract Identifiable getAbout( Analyst analyst, QueryService queryService );

    public abstract String getSurveyType();

    /**
     * The status of a survey.
     */
    public enum Status implements Serializable {
        Created( "Created" ),
        In_design( "New" ),
        Launched( "Launched" ),
        Closed( "Closed" );

        private String label;

        Status( String label ) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum Type implements Serializable {
        /**
         * Issue remediation.
         */
        Remediation,
        /**
         * Unknown.
         */
        Unknown
    }

    public boolean isUnknown() {
        return false;
    }

    public Survey() {
        status = Status.In_design;
        userName = User.current().getUsername();
    }

    @Override
    public long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus( Status status ) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer( String issuer ) {
        this.issuer = issuer;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate( Date creationDate ) {
        this.creationDate = creationDate;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate( Date launchDate ) {
        this.launchDate = launchDate;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate( Date closedDate ) {
        this.closedDate = closedDate;
    }

    public Long getTimeToDeadline() {
        return timeToDeadline;
    }

    public void setTimeToDeadline( Long timeToDeadline ) {
        this.timeToDeadline = timeToDeadline;
    }

    public Set<Contact> getContacts() {
        return new HashSet<Contact>( contacts );
    }

    public void setContacts( Set<Contact> contacts ) {
        this.contacts = contacts;
    }

    public SurveyData getSurveyData() {
        return surveyData;
    }

    @Override
    public String getDescription() {
        return "Survey by " + userName + " about " + getTitle() + " with deadline " + getDeadlineText();
    }

    @Override
    public String getName() {
        return getTitle();
    }

    @Override
    public String getTypeName() {
        return "Survey";
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    /**
     * Add a user to the survey.
     *
     * @param contacts a collection of contacts
     */
    public void addContacts( Collection<Contact> contacts ) {
        this.contacts.addAll( contacts );
    }

    public void addContact( String username ) {
        Contact contact = new Contact( username );
        contact.setToBeContacted();
        contacts.add( contact );
    }

    public void removeContact( String username ) {
        Contact contact = getContact( username );
        if ( contact != null )
            contacts.remove( contact );
    }

    public Contact getContact( final String username ) {
        return (Contact) CollectionUtils.find(
                contacts, new Predicate() {
            @Override
            public boolean evaluate( Object obj ) {
                return ( (Contact) obj ).getUsername().equals( username );
            }
        } );
    }

    private void addContact( Contact contact ) {
        contacts.add( contact );
    }

    /**
     * Whether the survey is registered with the survey service.
     *
     * @return a boolean
     */
    public boolean isRegistered() {
        return id >= 0;
    }

    /**
     * Survey can be launched?
     *
     * @return a boolean
     */
    public boolean canBeLaunched() {
        return isRegistered() && !isLaunched() && !isClosed();
    }

    /**
     * Survey can be cancelled?
     *
     * @return a boolean
     */
    public boolean canBeCancelled() {
        return !isLaunched() && !isClosed();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if ( isUnknown() ) {
            sb.append( "(unknown)" );
        } else {
            sb.append( getClass().getSimpleName() );
            sb.append( ',' );
            sb.append( id );
            sb.append( ',' );
            sb.append( status.name() );
            sb.append( ',' );
            sb.append( userName );
            sb.append( ',' );
            sb.append( issuer );
            sb.append( ',' );
            sb.append( dateFormat.format( creationDate ) );
            sb.append( ',' );
            if ( launchDate != null )
                sb.append( dateFormat.format( launchDate ) );
            else
                sb.append( "0" );
            sb.append( ',' );
            if ( closedDate != null )
                sb.append( dateFormat.format( closedDate ) );
            else
                sb.append( "0" );
            sb.append( ',' );

            for ( Contact contact : contacts ) {
                sb.append( contact.toString() );
                sb.append( ':' );
            }
            sb.append( ',' );
            try {
                sb.append( URLEncoder.encode( getIdentifiableSpecs(), "UTF-8" ) );
            } catch ( UnsupportedEncodingException e ) {
                throw new RuntimeException( " Failed to encode" );
            }
        }
        return sb.toString();
    }

    @SuppressWarnings( "unchecked" )
    public static Survey fromString( String s ) {
        try {
            StringTokenizer tokens = new StringTokenizer( s, "," );
            String surveyClassName = Survey.class.getPackage().getName() + "." + tokens.nextToken();
            long id = Long.parseLong( tokens.nextToken() );
            Survey.Status status = Survey.Status.valueOf( tokens.nextToken() );
            String userName = tokens.nextToken();
            String issuer = tokens.nextToken();
            Date creationDate = dateFormat.parse( tokens.nextToken() );
            String token = tokens.nextToken();
            Date launchDate = !token.equals( "0" ) ? dateFormat.parse( token ) : null;
            token = tokens.nextToken();
            Date closedDate = !token.equals( "0" ) ? dateFormat.parse( token ) : null;
            String contactsString = tokens.nextToken();
            List<Contact> contacts = new ArrayList<Contact>();
            StringTokenizer tokenizer = new StringTokenizer( contactsString, ":" );
            while ( tokenizer.hasMoreTokens() ) {
                contacts.add( Contact.fromString( tokenizer.nextToken() ) );
            }
            String specs = URLDecoder.decode( tokens.nextToken(), "UTF-8" );
            Class<? extends Survey> clazz = (Class<? extends Survey>) Survey.class.getClassLoader()
                    .loadClass( surveyClassName );
            Survey survey = clazz.newInstance();
            survey.setId( id );
            survey.setStatus( status );
            survey.setUserName( userName );
            survey.setIssuer( issuer );
            survey.setCreationDate( creationDate );
            survey.setLaunchDate( launchDate );
            survey.setClosedDate( closedDate );
            survey.addContacts( contacts );
            survey.setIdentifiableSpecs( specs );
            return survey;
        } catch ( Exception e ) {
            LOG.warn( "Can't load survey", e );
            return null;
        }
    }

    /**
     * Whether survey is launched.
     *
     * @return a boolean
     */
    public boolean isLaunched() {
        return status == Status.Launched;
    }

    /**
     * Whether survey is closed.
     *
     * @return a boolean
     */
    public boolean isClosed() {
        return status == Status.Closed;
    }

    /**
     * Get registration title for the survey (includes plan uri).
     *
     * @param planUri a string
     * @return a string
     */
    public String getRegistrationTitle( String planUri ) {
        return getTitle() + " (in " + planUri + ")";
    }

    public String getPlanText() {
        return User.current().getPlan().getName();
    }

    public String getDeadlineText() {
        if ( timeToDeadline == null ) {
            return "at your ealiest convenience";
        } else {
            return "by " + dateFormat.format( getDeadline() );
        }
    }

    private Date getDeadline() {
        assert timeToDeadline != null;
        return new Date( creationDate.getTime() + timeToDeadline );
    }

    public String getFormattedCreationDate() {
        return dateFormat.format( creationDate );
    }

    public int getContactedCount() {
        return CollectionUtils.select(
                contacts, PredicateUtils.invokerPredicate( "isContacted" ) ).size();
    }

    public int getToBeContactedCount() {
        return CollectionUtils.select(
                contacts, PredicateUtils.invokerPredicate( "isToBeContacted" ) ).size();
    }

    public boolean updateSurveyData( SurveyService surveyService, Plan plan ) {
        if ( surveyData == null ) {
            try {
                surveyData = surveyService.getSurveyData( this, plan );
            } catch ( SurveyException e ) {
                return false;
            }
            setStatus( surveyData.getStatus() );
        }
        return true;
    }

    public void updateContact( String username, Contact.Status newStatus ) {
        if ( newStatus == Contact.Status.None ) {
            removeContact( username );
        } else {
            Contact contact = getContact( username );
            if ( contact == null ) {
                Contact newContact = new Contact( username );
                newContact.setStatus( newStatus );
                addContact( newContact );
            } else {
                contact.setStatus( newStatus );
            }
        }
    }

    public void setSurveyData( SurveyData surveyData ) {
        this.surveyData = surveyData;
    }

    public void resetData() {
        surveyData = null;
    }

    public String getFormattedStatusDate() {
        if ( isLaunched() )
            return dateFormat.format( launchDate );
        else if ( isClosed() )
            return dateFormat.format( closedDate );
        else
            return dateFormat.format( creationDate );
    }

    protected Map<String, Object> getSurveyContext( SurveyService surveyService, Plan plan,
                                                    QueryService queryService ) {
        User issuer = getIssuer( surveyService.getUserDao() );
        Map<String, Object> context = new HashMap<String, Object>();
        context.put( "plan", getPlanText() );
        context.put( "deadline", getDeadlineText() );
        context.put(
                "issuer", issuer != null ? issuer.getFullName() : "(unknown)" );
        context.put(
                "email", issuer != null ? issuer.getEmail() : surveyService.getDefaultEmailAddress( plan ) );
        context.put( "survey", getTitle() );
        return context;
    }

    protected User getIssuer( UserDao userDao ) {
        return userDao.getUserNamed( getUserName() );
    }

    public Map<String, Object> getInvitationContext( SurveyService surveyService, User user, User issuer, Plan plan,
                                                     QueryService queryService ) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put( "user", user );
        context.put( "issuer", issuer );
        context.put( "survey", this );
        context.put( "surveyUrl", getSurveyLink( user ) );
        return context;
    }

    public String getSurveyLink( User user ) {
        return getSurveyData() == null
                ? ""
                : getSurveyData().getPublishLink() + "?sgUID=" + user.getEmail();
    }

    public boolean hasContact( final String userName ) {
        return CollectionUtils.exists(
                contacts, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (Contact) object ).getUsername().equals( userName );
            }
        } );
    }

    public boolean equals( Object other ) {
        return other instanceof Survey && ( (Survey) other ).getId() == getId();
    }
    
    public int hashCode() {
        return 31 + new Long( getId() ).hashCode();
    }
}
