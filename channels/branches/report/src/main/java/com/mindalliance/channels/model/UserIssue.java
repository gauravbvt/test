package com.mindalliance.channels.model;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

/**
 * A user provided issue
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 11:57:45 AM
 */
public class UserIssue extends ModelObject implements Issue {

    /**
     * Max length of name
     */
    private static final int MAX_LENGTH = 40;

    /**
     * The identifiable object the issue is about
     */
    private ModelObject about;

    /**
     * Remediation
     */
    private String remediation = "";

    /**
     * Name of user who created or last modified the issue
     */
    private String reportedBy = "";
    /**
     * Type of issue.
     */
    private String type = Issue.VALIDITY;

    /**
     * The issue's severity
     */
    private Level severity = Level.Low;

    public UserIssue() {
    }

    /**
     * Create a user issue.
     *
     * @param mo a model object
     */
    public UserIssue( ModelObject mo ) {
        this.about = mo;
        setDescription( "(No description)" );
    }

    /**
     * {@inheritDoc}
     */
    public String getTypeName() {
        return "user issue";
    }

    public ModelObject getAbout() {
        return about;
    }

    void setAbout( ModelObject about ) {
        this.about = about;
    }

    public String getRemediation() {
        return remediation;
    }

    public void setRemediation( String remediation ) {
        this.remediation = remediation == null ? "" : remediation;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy( String reportedBy ) {
        this.reportedBy = reportedBy;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    @Override
    public String getName() {
        return getLabel( MAX_LENGTH );
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel( int maxLength ) {
        return StringUtils.abbreviate( MessageFormat.format( "({0}) {1}",
                severity.getNegativeLabel(), getDescription() ), maxLength );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDetected() {
        return false;
    }

    public Level getSeverity() {
        return severity;
    }

    public void setSeverity( Level severity ) {
        this.severity = severity;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWaived() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        // User issues are removed, not waived.
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String getKind() {
        return "user";
    }

    /**
     * {@inheritDoc}
     */
    public String waivedString() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public String getWaivedString() {
        return Boolean.toString( isWaived() );
    }

    /**
     * {@inheritDoc}
     */
    public String getDetectorLabel() {
        return "User defined";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndefined() {
        return super.isUndefined()
                && remediation.isEmpty();
    }

}
