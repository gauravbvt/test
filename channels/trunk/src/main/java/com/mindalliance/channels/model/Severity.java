package com.mindalliance.channels.model;

/**
 * Severity level.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Feb 26, 2010
* Time: 12:01:35 PM
*/
public enum Severity {
    /**
     * Minor.
     */
    Minor,
    /**
     * Major.
     */
    Major,
    /**
     * Severe.
     */
    Severe;

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
}
