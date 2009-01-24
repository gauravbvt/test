package com.mindalliance.channels;

import java.io.Serializable;

/**
 * Detected or user issue
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 11:48:27 AM
 */
public interface Issue extends Identifiable, Serializable {
    /**
     * The model object the issue is about
     * @return a ModelObject
     */
    ModelObject getAbout();

    /**
     * The type of issue
     * @return a String
     */
    String getType();

    /**
     * The description of the issue
     * @return a String
     */
    String getDescription();

    /**
     * How to remediate the issue
     * @return a String
     */
    String getRemediation();

    /**
     * The name of who reported or last modified the issue
     * @return a String
     */
    String getReportedBy();

    /**
     * Get a string of maximum length describing the issue
     * @param maxLength maximum length
     * @return a String
     */
    String getLabel(int maxLength);

    /**
     * Whether the issue is automatically detected (versus added by a user)
     * @return a boolean
     */
    boolean isDetected();
    
}
