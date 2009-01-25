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

    private static long NEGATIVE_COUNTER = -1;
    /**
     * Type of issue
     */
    private String type;

    /**
     * How to resolve the issue
     */
    private String remediation;

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
/*
        sb.append( type );
        sb.append( " issue on " );
        sb.append( getAbout().getClass().getSimpleName() );
        sb.append( " " );
        sb.append( getAbout().getName() );
        sb.append( "(" );
        sb.append( getAbout().getId() );
        sb.append( ")" );
        sb.append( ": " );
*/
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

    /**
     * {@inheritDoc}
     */
    public String getLabel(int maxLength) {
        return StringUtils.abbreviate( getDescription(), maxLength );
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
}
