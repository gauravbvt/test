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

    public enum Level {
        /**
         * A minor issue
         */
        Minor,
        /**
         * A major issue
         */
        Major,
        /**
         * A severe issue
         */
        Severe;

        /**
         * A string represneting the severity level
         * @return a String
         */
        public String getLabel() {
            return this.toString();
        }

        /**
         * A sortable value
         * @return an int
         */
        public int getOrdinal() {
            return this.ordinal();
        }
    }

    /**
     * Type of issue having to do with the definition of a model object
     */
    String DEFINITION = "Definition";
    /**
     * Type of having having to do with a flow of information
     */
    String FLOW = "Flow";
    /**
     * Type of issue having to do with a scenario as a whole
     */
    String STRUCTURAL = "Structural";
    /**
     * All possible types of issues
     */
    String[] TYPES = new String[]{DEFINITION, FLOW, STRUCTURAL};

    /**
     * The identifiable object the issue is about
     *
     * @return a ModelObject
     */
    ModelObject getAbout();

    /**
     * The type of issue
     *
     * @return a String
     */
    String getType();

    /**
     * The description of the issue
     *
     * @return a String
     */
    String getDescription();

    /**
     * Set description
     *
     * @param description a String
     */
    void setDescription( String description );

    /**
     * How to remediate the issue
     *
     * @return a String
     */
    String getRemediation();

    /**
     * Set remediation
     *
     * @param remediation a String
     */
    void setRemediation( String remediation );

    /**
     * The name of who reported or last modified the issue
     *
     * @return a String
     */
    String getReportedBy();

    /**
     * Set name of user who reported the issue
     *
     * @param reportedBy a user name
     */
    void setReportedBy( String reportedBy );

    /**
     * Get a string of maximum length describing the issue
     *
     * @param maxLength maximum length
     * @return a String
     */
    String getLabel( int maxLength );

    /**
     * Whether the issue is automatically detected (versus added by a user)
     *
     * @return a boolean
     */
    boolean isDetected();

    /**
     * Get the issue's severity
     *
     * @return a Level (Low, Medium or High)
     */
    Level getSeverity();

    /**
     * Set the issue's severity
     *
     * @param severity the severity level
     */
    void setSeverity( Level severity );

}
