package com.mindalliance.channels.surveys;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.User;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
     * The status of a survey.
     */
    public enum Status {
        Created( "Created" ),
        In_design( "In Design" ),
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
     * Time interval from launchDate to deadline.
     */
    private Long timeToDeadline;
    /**
     * Simple date format.
     */
    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat( "EEE MMM dd HH:mm:ss zzz yyyy" );
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

    public Long getTimeToDeadline() {
        return timeToDeadline;
    }

    public void setTimeToDeadline( Long timeToDeadline ) {
        this.timeToDeadline = timeToDeadline;
    }

    public Set<Contact> getContacts() {
        return contacts;
    }

    public void setContacts( Set<Contact> contacts ) {
        this.contacts = contacts;
    }

    public SurveyData getSurveyData() {
        return surveyData;
    }

    public void setSurveyData( SurveyData surveyData ) {
        this.surveyData = surveyData;
        setStatus( surveyData.getStatus() );
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
            contacts.add( new Contact( username ) );
        }
    }

    public void addContact( String username ) {
        contacts.add( new Contact( username ) );
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
     * Survey can be launched.
     *
     * @return a boolean
     */
    public boolean canBeLaunched() {
        return isRegistered() && !isLaunched() && !isClosed();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( id );
        sb.append( ',' );
        sb.append( status.name() );
        sb.append( ',' );
        sb.append( userName );
        sb.append( ',' );
        sb.append( dateFormat.format( creationDate ) );
        sb.append( ',' );
        if ( launchDate != null )
            sb.append( dateFormat.format( launchDate ) );
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
            survey.setCreationDate( dateFormat.parse( tokens.nextToken() ) );
            String token = tokens.nextToken();
            if ( !token.equals( "0" ) ) {
                survey.setLaunchDate( dateFormat.parse( token ) );
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
            throw new RuntimeException( "Can't decode issue spec" );
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

    public String getTitle() {
        return issueSpec.getDetectorLabel();
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

    public String getTimestamp() {
        if ( isLaunched() || isClosed() )
            return dateFormat.format( launchDate );
        else
            return dateFormat.format( creationDate );
    }

}
