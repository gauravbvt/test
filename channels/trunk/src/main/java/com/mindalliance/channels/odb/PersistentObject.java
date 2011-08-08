/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL.
 */

package com.mindalliance.channels.odb;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class PersistentObject implements Serializable {

    private final Date date;
    private final String id;

    public PersistentObject() {
        date = new Date();
        id = UUID.randomUUID().toString();
    }

    public Date getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return "at " + getDateString();
    }

    public String getDateString() {
        return DateFormat.getInstance().format( date );
    }

    public String getShortTimeElapsedString() {
        Date end = new Date();
        long diffInSeconds = ( end.getTime() - date.getTime() ) / 1000;
        /* sec */
        long seconds = ( diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds );
        /* min */
        long minutes = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        long hours = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        long days = diffInSeconds / 24;

        StringBuilder sb = new StringBuilder();
        if ( days > 0 ) {
            sb.append( days );
            sb.append( " day" );
            sb.append( days > 1 ? "s" : "" );
        }
        if ( hours > 0 ) {
            if ( sb.length() == 0 ) {
                sb.append( hours );
                sb.append( " hour" );
                sb.append( hours > 1 ? "s" : "" );
            }
        }
        if ( minutes > 0 ) {
            if ( sb.length() == 0 ) {
                sb.append( minutes );
                sb.append( " minute" );
                sb.append( minutes > 1 ? "s" : "" );
            }
        }
        if ( sb.length() == 0 ) {
            sb.append( seconds );
            sb.append( " second" );
            sb.append( seconds > 1 ? "s" : "" );
        }
        sb.append( " ago" );
        return sb.toString();
    }

    public String getLongTimeElapsedString() {
        Date end = new Date();
        long diffInSeconds = ( end.getTime() - date.getTime() ) / 1000;
        /* sec */
        long seconds = diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* min */
        long minutes = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        long hours = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        long days = diffInSeconds / 24;

        StringBuilder sb = new StringBuilder();
        if ( days > 0 ) {
            sb.append( days );
            sb.append( " day" );
            sb.append( days > 1 ? "s" : "" );
        }
        if ( hours > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( hours );
            sb.append( " hour" );
            sb.append( hours > 1 ? "s" : "" );
        }
        if ( minutes > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( minutes );
            sb.append( " minute" );
            sb.append( minutes > 1 ? "s" : "" );
        }
        if ( sb.length() == 0 || seconds > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( seconds );
            sb.append( " second" );
            sb.append( seconds > 1 ? "s" : "" );
        }
        sb.append( " ago" );
        return sb.toString();
    }


}
