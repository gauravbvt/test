package com.mindalliance.channels.core.model;

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
     * High.
     */
    High,
    /**
     * Highest
     */
    Highest;

    /**
     * A string representing the severity level.
     *
     * @return a String
     */
    public String getLabel() {
        return name();
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

    public String getNegativeLabel() {
        switch ( this ) {
            case Low:
                return "Minor";
            case Medium:
                return "Major";
            case High:
                return "Severe";
            case Highest:
                return "Extreme";
            default:
                return "UNKNOWN";
        }
    }

    public static boolean isSubsumedBy( Level level, Level otherLevel ) {
        return level == null && otherLevel == null
                || otherLevel == null
                || level != null && level.compareTo( otherLevel ) >= 0;
    }
}
