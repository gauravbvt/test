package com.mindalliance.channels.playbook.support;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 17, 2008
 * Time: 4:05:38 PM
 */
public enum Level {

    NONE, LOW, MEDIUM, HIGH, VERY_HIGH;

    public String toString() {
        if (this == NONE) return "none";
        else if (this == LOW) return "low";
        else if (this == MEDIUM) return "medium";
        else if (this == HIGH) return "high";
        else return "very high";
    }
}
