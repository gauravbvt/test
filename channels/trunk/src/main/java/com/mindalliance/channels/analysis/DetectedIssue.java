package com.mindalliance.channels.analysis;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Issue;
import org.apache.commons.lang.StringUtils;

/**
 * A problem uncovered about a model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 9:30:12 AM
 */
public class DetectedIssue extends AnalysisObject implements Issue {

    private static long NEGATIVE_COUNTER = Long.MAX_VALUE;
    /**
     * Type of issue
     */
    private String type;

    /**
     * How to resolve the issue
     */
    private String remediation;
    /**
     * The issue's severity
     */
    private Issue.Level severity = Issue.Level.Minor;

    /**
     * Constructor
     *
     * @param type  -- the type of issue
     * @param about -- the ModelObject the issue is about
     */
    public DetectedIssue( String type, ModelObject about ) {
        super( about );
        this.type = type;
    }

    /**
     * Constructor
     *
     * @param type     -- the type of issue
     * @param about    -- the ModelObject the issue is about
     * @param property -- the problematic property
     */
    public DetectedIssue( String type, ModelObject about, String property ) {
        super( about, property );
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    /**
     * To String
     *
     * @return a string
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getDescription() );
        if ( remediation != null ) {
            sb.append( " (" );
            sb.append( remediation );
            sb.append( ')' );
        }
        return sb.toString();
    }

    public String getRemediation() {
        return remediation;
    }

    /**
     * {@inheritDoc}
     */
    public String getReportedBy() {
        return "Channels";
    }

    /**
     * Set name of user who reported the issue
     *
     * @param reportedBy a user name
     */
    public void setReportedBy( String reportedBy ) {
        throw new IllegalStateException( "Can't set the reporter of a detected issue ");
    }

    public void setRemediation( String remediation ) {
        this.remediation = remediation;
    }

    public String getLabel() {
       return "(" + severity +") " + getDescription();
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel(int maxLength) {
        return StringUtils.abbreviate( getLabel(), maxLength );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDetected() {
        return true;
    }

    /**
     * Get transient id (unique among detected issues)
     *
     * @return an id
     */
    public long getId() {
        return NEGATIVE_COUNTER--;
    }

    /**
     * Return a name
     * @return a String
     */
    public String getName() {
        return getLabel( Integer.MAX_VALUE );
    }

    public Level getSeverity() {
        return severity;
    }

    public void setSeverity( Level severity ) {
        this.severity = severity;
    }
}
