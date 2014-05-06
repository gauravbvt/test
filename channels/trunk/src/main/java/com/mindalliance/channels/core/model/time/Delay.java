package com.mindalliance.channels.core.model.time;

import com.mindalliance.channels.core.model.Copyable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * A duration
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 29, 2009
 * Time: 1:59:28 PM
 */
public class Delay implements Comparable, Copyable, Serializable {

    private static String[] numbers = {
            "zero", "one", "two", "three", "four", "five", "six",
            "seven", "eight", "nine", "ten", "eleven", "twelve",
            "thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
            "eighteen", "nineteen", "twenty"
    };

    /**
     * Time unit
     */
    private TimeUnit unit = TimeUnit.Second;

    /**
     * Amount of a unit of time
     */
    private int amount;

    public Delay() {
    }

    public Delay( Delay delay ) {
        this.amount = delay.getAmount();
        this.unit = delay.getUnit();
    }

    public Delay( int amount, TimeUnit unit ) {
        this.amount = amount;
        this.unit = unit;
    }

    public Copyable copy() {
        return new Delay( this );
    }

    /**
     * Get the smallest of two delays.
     *
     * @param delay a delay
     * @param other a delay
     * @return a delay
     */
    public static Delay min( Delay delay, Delay other ) {
        return delay.compareTo( other ) <= 0 ? delay : other;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount( int amount ) {
        this.amount = Math.max( 0, amount );
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit( TimeUnit unit ) {
        this.unit = unit;
    }

    /**
     * List of time units as strings
     *
     * @return list of strings
     */
    public List<TimeUnit> getUnits() {
        return Arrays.asList( TimeUnit.values() );
    }

    /**
     * The duration in seconds
     *
     * @return an int
     */
    public int getSeconds() {
        int factor;
        switch ( unit ) {
            case Second:
                factor = 1;
                break;
            case Minute:
                factor = 60;
                break;
            case Hour:
                // 3600
                factor = 60 * 60;
                break;
            case Day:
                // 86400
                factor = 24 * 60 * 60;
                break;
            case Week:
                // 604800
                factor = 7 * 24 * 60 * 60;
                break;
            case Month:
                // 604800 * 4
                factor = 7 * 24 * 60 * 60 * 4; // 4 weeks in a month
                break;
            case Year:
                // 604800 * 52
                factor = 7 * 24 * 60 * 60 * 52; // 52 weeks in a year
                break;
            default:
                throw new IllegalStateException( "Unknown unit" );
        }
        return amount * factor;
    }

    public boolean isImmediate() {
        return amount == 0;
    }

    /**
     * Getting the amount as string
     *
     * @return a string
     */
    public String getAmountString() {
        return "" + amount;
    }

    /**
     * Setting the amount from string
     *
     * @param value the amount as string
     */

    public void setAmountString( String value ) {
        try {
            amount = Integer.parseInt( value );
        } catch ( NumberFormatException e ) {
            amount = translateAmount( value );
        }
    }

    private int translateAmount( String s ) {
        if ( s== null || s.isEmpty() )
            return 0;
        else
            return Math.max( 0, Arrays.asList( numbers ).indexOf( s.trim().toLowerCase() ) );
    }


    /**
     * {@inheritDoc}
     */
    public String toString() {
        if ( amount == 0 ) return "immediately";
        StringBuilder sb = new StringBuilder();
        int rest = getSeconds();
        if ( rest >= 604800 ) {
            int weeks = rest / 604800;
            sb.append( weeks );
            sb.append( weeks > 1 ? " weeks" : " week" );
            rest = rest % 604800;
        }
        if ( rest >= 86400 ) {
            int days = rest / 86400;
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( days );
            sb.append( days > 1 ? " days" : " day" );
            rest = rest % 86400;
        }
        if ( rest >= 3600 ) {
            int hours = rest / 3600;
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( hours );
            sb.append( hours > 1 ? " hours" : " hour" );
            rest = rest % 3600;
        }
        if ( rest >= 60 ) {
            int mins = rest / 60;
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( mins );
            sb.append( mins > 1 ? " minutes" : " minute" );
            rest = rest % 60;
        }
        if ( rest > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( rest );
            sb.append( rest > 1 ? " seconds" : " second" );
        }
        return sb.toString();
    }

    /**
     * Parse string into Delay.
     * Creates delay of zero if string is invalid
     *
     * @param s a delay as string
     * @return a Delay
     */
    public static Delay parse( String s ) {
        Delay delay = new Delay();
        if ( s.equalsIgnoreCase( "immediately" ) ) return delay;
        try {
            String[] items = s.split( " " );
            if ( items.length == 2 ) {
                int amount = Integer.parseInt( items[0] );
                String unitString = items[1];
                TimeUnit unit = TimeUnit.parse( unitString );
                delay = new Delay( amount, unit );
            } else {
                throw new IllegalArgumentException( "Invalid delay string " + s );
            }
        } catch ( IllegalArgumentException e ) {
            e.printStackTrace();
        }
        return delay;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo( Object obj ) {
        Delay other = (Delay) obj;
        int seconds = getSeconds();
        int otherSeconds = other.getSeconds();
        return seconds == otherSeconds
                ? 0
                : seconds < otherSeconds
                ? -1
                : 1;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( obj instanceof Delay ) {
            Delay other = (Delay) obj;
            return getSeconds() == other.getSeconds();
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + getSeconds();
        return hash;
    }

    /**
     * Add to another delay.
     *
     * @param delay a delay
     * @return a new delay
     */
    public Delay add( Delay delay ) {
        return new Delay( getSeconds() + delay.getSeconds(), TimeUnit.Second );
    }

    /**
     * Subtract another delay.
     * Floor the result at 0.
     *
     * @param delay a delay
     * @return a new delay
     */
    public Delay subtract( Delay delay ) {
        return new Delay( Math.max( 0, getSeconds() - delay.getSeconds() ), TimeUnit.Second );
    }

    /**
     * Whether this delay is shorter than another.
     *
     * @param other a delay
     * @return a boolean
     */
    public boolean shorterThan( Delay other ) {
        return this.compareTo( other ) < 0;
    }


}
