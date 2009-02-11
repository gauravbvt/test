package com.mindalliance.channels;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
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
@Embeddable
public class Delay implements Comparable, Serializable {

    /**
     * Time units
     */
    public enum Unit {
        /**
         * Seconds
         */
        seconds,
        /**
         * Minutes
         */
        minutes,
        /**
         * Hours
         */
        hours,
        /**
         * Days
         */
        days,
        /**
         * Weeks
         */
        weeks
    }

    private static String[] numbers = {
            "zero","one","two","three","four","five","six",
            "seven","eight","nine","ten","eleven","twelve",
            "thirteen","fourteen","fifteen","sixteen","seventeen",
            "eighteen","nineteen","twenty"
    };

    /**
     * Time unit
     */
    private Unit unit = Unit.seconds;

    /**
     * Amount of a unit of time
     */
    private int amount;

    public Delay() {
    }

    public Delay( int amount, Unit unit ) {
        this.amount = amount;
        this.unit = unit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount( int amount ) {
        this.amount = Math.max( 0, amount );
    }

    @Enumerated( EnumType.STRING )
    public Unit getUnit() {
        return unit;
    }

    public void setUnit( Unit unit ) {
        this.unit = unit;
    }

    /**
     * List of time units as strings
     *
     * @return list of strings
     */
    @Transient
    public List<Unit> getUnits() {
        return Arrays.asList( Unit.values() );
    }

    /**
     * The duration in seconds
     *
     * @return an int
     */
    @Transient
    public int getSeconds() {
        int factor;
        switch ( unit ) {
            case seconds:
                factor = 1;
                break;
            case minutes:
                factor = 60;
                break;
            case hours:
                factor = 60 * 60;
                break;
            case days:
                factor = 24 * 60 * 60;
                break;
            case weeks:
                factor = 7 * 24 * 60 * 60;
                break;
            default:
                throw new IllegalStateException( "Unknown unit" );
        }
        return amount * factor;
    }

    /**
     * Getting the amount as string
     *
     * @return a string
     */
    @Transient
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
        return Math.max(0, Arrays.asList( numbers ).indexOf( s.trim().toLowerCase() ));
    }


    /**
     * {@inheritDoc}
     */
    public String toString() {
        if (amount == 0) return "immediately";
        StringBuilder sb = new StringBuilder();
        sb.append( amount );
        sb.append( " " );
        String units = unit.toString();
        if ( amount <= 1 ) {
            sb.append( units.substring( 0, units.length() - 1 ) );
        } else {
            sb.append( units );
        }
        return sb.toString();
    }

    /**
     * Parse string into Delay.
     * Creates delay of zero if string is invalid
     * @param s a delay as string
     * @return a Delay
     */
    public static Delay parse( String s ) {
        Delay delay = new Delay();
        if (s.equalsIgnoreCase("immediately")) return delay;
        try {
            String[] items = s.split( " " );
            if ( items.length == 2 ) {
                int amount = Integer.parseInt( items[0] );
                String unitString = items[1];
                if ( !unitString.endsWith( "s" ) ) unitString += "s";
                Unit unit = Unit.valueOf( unitString );
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
        return new Integer( getSeconds() ).compareTo( other.getSeconds() );
    }


}
