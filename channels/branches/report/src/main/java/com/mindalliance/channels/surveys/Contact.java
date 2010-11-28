package com.mindalliance.channels.surveys;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * A contact in a survey.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 23, 2009
 * Time: 5:04:55 PM
 */
public class Contact implements Serializable {

    public enum Status {
        None( "None" ),
        To_be_contacted( "To be contacted" ),
        Contacted( "Contacted" ),
        In_progress( "In progress" ),
        Completed( "Completed" ),
        Abandonned( "Abandonned" );

        private String label;

        Status( String label ) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

    }

    private String username;
    private Status status = Status.None;

    public Contact() {
    }

    public Contact( String username ) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus( Status status ) {
        this.status = status;
    }

    public boolean isContacted() {
        return status == Status.Contacted;
    }

    public void setContacted(  ) {
        status = Status.Contacted;
    }

    public boolean isToBeContacted() {
        return status == Status.To_be_contacted;
    }

    public void setToBeContacted(  ) {
        status = Status.To_be_contacted;
    }

    public boolean isCompleted() {
        return status == Status.Completed;
    }

    public void setCompleted(  ) {
        status = Status.Completed;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( username );
        sb.append( '|' );
        sb.append( status.name() );
        return sb.toString();
    }

    public static Contact fromString( String s ) {
        StringTokenizer tokens = new StringTokenizer( s, "|" );
        Contact contact = new Contact();
        contact.setUsername( tokens.nextToken() );
        contact.setStatus( Status.valueOf( tokens.nextToken() ));
        return contact;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( !( obj instanceof Contact ) ) return false;
        Contact other = (Contact) obj;
        return username != null
                && other.getUsername() != null
                && username.equals( other.getUsername() );
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        if ( username == null )
            return 0;
        else
            return username.hashCode();
    }
}
