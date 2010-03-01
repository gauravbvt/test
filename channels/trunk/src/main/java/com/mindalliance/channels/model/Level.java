package com.mindalliance.channels.model;

/**
 * Generic level.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 26, 2010
 * Time: 12:13:49 PM
 */
public enum Level {
    /**
     * Low.
     */
    Low,
    /**
     * Major.
     */
    Medium,
    /**
     * Severe.
     */
    High;

    /**
     * A string representing the severity level.
     *
     * @return a String
     */
    public String getLabel() {
        String label = toString();
        if ( label.endsWith( "." ) ) {
            return label;
        } else {
            return label + ".";
        }
    }

    /**
     * A sortable value.
     *
     * @return an int
     */
    public int getOrdinal() {
        return ordinal();
    }

    /**
     * Get name.
     *
     * @return a string
     */
    public String getName() {
        return toString();
    }

    public String negative() {
        switch ( this ) {
            case Low:
                return "Minor";
            case Medium:
                return "Major";
            case High:
                return "Severe";
            default:
                return "UNKNOWN";
        }
    }
}
