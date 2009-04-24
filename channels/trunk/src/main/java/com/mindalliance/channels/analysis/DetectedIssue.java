package com.mindalliance.channels.analysis;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Issue;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Transient;

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
     * The kind of detector that found this issue.
     */
    private String kind;
    /**
     * Type of issue
     */
    private String type;

    /**
     * How to resolve the issue
     */
    private String remediation;
    /**
     * Whether the kind of this issue can be waived.
     * Set by detector.
     */
    private boolean canBeWaived;
    /**
     * The issue's severity
     */
    private Issue.Level severity = Issue.Level.Minor;

    /**
     * Constructor
     *
     * @param type -- the type of issue
     * @param mo   -- the ModelObject the issue is about
     */
    public DetectedIssue( String type, ModelObject mo ) {
        super( mo );
        this.type = type;
    }

    /**
     * Constructor
     *
     * @param type     -- the type of issue
     * @param mo       -- the ModelObject the issue is about
     * @param property -- the problematic property
     */
    public DetectedIssue( String type, ModelObject mo, String property ) {
        super( mo, property );
        this.type = type;
    }

    public String getKind() {
        return kind;
    }

    /**
     * {inheritDoc}
     */
    public String waivedString() {
        return isWaived() ? "waived" : "";
    }

    public void setKind( String kind ) {
        this.kind = kind;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public boolean isCanBeWaived() {
        return canBeWaived;
    }

    public void setCanBeWaived( boolean canBeWaived ) {
        this.canBeWaived = canBeWaived;
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
        throw new IllegalStateException( "Can't set the reporter of a detected issue " );
    }

    public void setRemediation( String remediation ) {
        this.remediation = remediation;
    }

    public String getLabel() {
        return "("
                + ( isWaived() ? "Waived" : severity )
                + ") "
                + getDescription();
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel( int maxLength ) {
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
     *
     * @return a String
     */
    public String getName() {
        return getLabel( Integer.MAX_VALUE );
    }

    /**
     * {@inheritDoc}
     */
    public Level getSeverity() {
        return severity;
    }

    public void setSeverity( Level severity ) {
        this.severity = severity;
    }

    public boolean canBeWaived() {
        return canBeWaived;
    }

    /**
     * Whether this issue is waived.
     *
     * @return a boolean
     */
    @Transient
    public boolean isWaived() {
        return getAbout().isWaived( getKind() );
    }

}
