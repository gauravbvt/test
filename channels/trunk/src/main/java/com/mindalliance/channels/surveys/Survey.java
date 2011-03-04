package com.mindalliance.channels.surveys;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Plan;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * A survey.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 21, 2009
 * Time: 7:23:40 AM
 */
public class Survey implements Identifiable, Serializable {
    /**
     * Max title length.
     */
    private static final int MAX_TITLE_LENGTH = 80;

    /**
     * Unknown survey.
     */
    public static Survey UNKNOWN;
    /**
     * Unknown survey id.
     */
    public static final long UNKNOWN_ID = Long.MIN_VALUE;

    static {
        UNKNOWN = new Survey();
        UNKNOWN.setId( UNKNOWN_ID );
    }

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

    public boolean isUnknown() {
        return id == UNKNOWN_ID;
    }

    /**
     * The current status of the survey.
     */
    private Status status;
    /**
     * id of registered survey (launched or closed). Negative if not registered.
     */
    private long id = -1L;
    /**
     * Soec of the issue the survey is about.
     */
    private IssueSpec issueSpec;
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
    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat( "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US );
    /**
     * Survey data.
     */
    private SurveyData surveyData;

    public Survey() {
        status = Status.In_design;
    }

    public Survey( Issue issue ) {
        this();
        issueSpec = new IssueSpec( issue );
        userName = User.current().getUsername();
    }

    public long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus( Status status ) {
        this.status = status;
    }

    public IssueSpec getIssueSpec() {
        return issueSpec;
    }

    public void setIssueSpec( IssueSpec issueSpec ) {
        this.issueSpec = issueSpec;
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

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return "Survey by " + userName + " about " + getTitle() + " with deadline " + getDeadlineText();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return getTitle();
    }

    /**
     * {@inheritDoc}
     */
    public String getTypeName() {
        return "Survey";
    }

    /**
     * Add a user to the survey.
     *
     * @param usernames a collection of strings
     */
    public void addContacts( Collection<String> usernames ) {
        for ( String username : usernames ) {
            addContact( username );
        }
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
                contacts,
                new Predicate() {
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

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if ( isUnknown() ) {
            sb.append( "(unknown)" );
        } else {
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
                sb.append( URLEncoder.encode( issueSpec.toString(), "UTF-8" ) );
            } catch ( UnsupportedEncodingException e ) {
                throw new RuntimeException( " Failed to encode" );
            }
        }
        return sb.toString();
    }

    public static Survey fromString( String s ) {
        try {
            Survey survey = new Survey();
            StringTokenizer tokens = new StringTokenizer( s, "," );
            long id = Long.parseLong( tokens.nextToken() );
            survey.setId( id );
            Survey.Status status = Survey.Status.valueOf( tokens.nextToken() );
            survey.setStatus( status );
            survey.setUserName( tokens.nextToken() );
            survey.setIssuer( tokens.nextToken() );
            survey.setCreationDate( dateFormat.parse( tokens.nextToken() ) );
            String token = tokens.nextToken();
            if ( !token.equals( "0" ) ) {
                survey.setLaunchDate( dateFormat.parse( token ) );
            }
            token = tokens.nextToken();
            if ( !token.equals( "0" ) ) {
                survey.setClosedDate( dateFormat.parse( token ) );
            }
            String contactsString = tokens.nextToken();
            StringTokenizer contacts = new StringTokenizer( contactsString, ":" );
            while ( contacts.hasMoreTokens() ) {
                survey.addContact( Contact.fromString( contacts.nextToken() ) );
            }
            String encoded = tokens.nextToken();
            survey.setIssueSpec( IssueSpec.fromString( URLDecoder.decode( encoded, "UTF-8" ) ) );
            return survey;
        } catch ( Exception e ) {
            throw new RuntimeException( "Can't decode issue spec", e );
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
     * Get short title for the survey.
     *
     * @return a string
     */
    public String getTitle() {
        String title;
        if ( isAboutDetectedIssue() )
            title = issueSpec.getDetectorLabel();
        else
            title = StringUtils.abbreviate(
                    issueSpec.getDescription(),
                    MAX_TITLE_LENGTH );
        return title;
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

    private boolean isAboutDetectedIssue() {
        Long userIssueId = issueSpec.getUserIssueId();
        return userIssueId == null;
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
                contacts,
                PredicateUtils.invokerPredicate( "isContacted" ) ).size();
    }

    public int getToBeContactedCount() {
        return CollectionUtils.select(
                contacts,
                PredicateUtils.invokerPredicate( "isToBeContacted" ) ).size();
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

}
