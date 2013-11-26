package com.mindalliance.channels.core.model;

import java.io.Serializable;
import java.util.List;

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
     * Type of issue having to do with validity.
     */
    String VALIDITY = "Validity";
    /**
     * Type of having having to do with completeness.
     */
    String COMPLETENESS = "Completeness";
    /**
     * Type of issue having to do with robustness.
     */
    String ROBUSTNESS = "Robustness";
    /**
     * All possible types of issues.
     */
    String[] TYPES = new String[]{VALIDITY, COMPLETENESS, ROBUSTNESS};

    /**
     * The identifiable the issue is about.
     *
     * @return an identifiable
     */
    Identifiable getAbout();

    /**
     * The type of issue (validity, completeness or robustness).
     *
     * @return a String
     */
    String getType();

    /**
     * The description of the issue.
     *
     * @return a String
     */
    String getDescription();

    /**
     * Set description.
     *
     * @param description a String
     */
    void setDescription( String description );

    /**
     * How to remediate the issue.
     *
     * @return a String
     */
    String getRemediation();

    /**
     * Set remediation.
     *
     * @param remediation a String
     */
    void setRemediation( String remediation );

    /**
     * The name of who reported or last modified the issue.
     *
     * @return a String
     */
    String getReportedBy();

    /**
     * Set name of user who reported the issue.
     *
     * @param reportedBy a user name
     */
    void setReportedBy( String reportedBy );

    /**
     * Get a string of maximum length describing the issue.
     *
     * @param maxLength maximum length
     * @return a String
     */
    String getLabel( int maxLength );

    /**
     * Whether the issue is automatically detected (versus added by a user).
     *
     * @return a boolean
     */
    boolean isDetected();

    /**
     * Get the issue's severity.
     *
     * @return a Level (Low, Medium or High)
     */
    Level getSeverity();

    /**
     * Set the issue's severity.
     *
     * @param severity the severity level
     */
    void setSeverity( Level severity );

    /**
     * Whether an issue is waived.
     *
     * @return a boolean
     */
    boolean isWaived();

    /**
     * This issue is of a kind that can be waived.
     *
     * @return a boolean
     */
    boolean canBeWaived();

    /**
     * Get the kind of issue (either user issue or a detection).
     *
     * @return a string
     */
    String getKind();

    /**
     * Get a string denoting whether this issues is waived.
     *
     * @return a string
     */
    String waivedString();

    /**
     * Get a "true" or "false" string denoting whether this issues is waived.
     *
     * @return a string
     */
    String getWaivedString();

    /**
     * A label for the detector of the issue.
     *
     * @return a String
     */
    String getDetectorLabel();

    /**
     * Whether is a validity issue.
     *
     * @return a boolean
     */
    boolean isValidity();

    /**
     * Whether is a completeness issue.
     *
     * @return a boolean
     */
    boolean isCompleteness();

    /**
     * Whether is a robustness issue.
     *
     * @return a boolean
     */
    boolean isRobustness();

    /**
     * Whether an issue has a given tag
     * @param tag
     * @return a boolean
     */
    boolean hasTag( String tag );

    /**
     * Get a list of remediation options
     * @return a list of strings
     */
    List<String> getRemediationOptions();
}
