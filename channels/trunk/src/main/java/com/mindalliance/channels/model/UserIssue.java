package com.mindalliance.channels.model;

import org.apache.commons.lang.StringUtils;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.text.MessageFormat;

/**
 * A user provided issue
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 11:57:45 AM
 */
@Entity
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
    private Issue.Level severity = Issue.Level.Minor;

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
        setReportedBy( User.current().getUsername() );
    }

    /**
     * {@inheritDoc}
     */
    public String getTypeName() {
        return "user issue";
    }

    @ManyToOne( fetch = FetchType.LAZY )
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
    @Transient
    public String getName() {
        return getLabel( MAX_LENGTH );
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel( int maxLength ) {
        return StringUtils.abbreviate( MessageFormat.format( "({0}) {1}",
                severity, getDescription() ), maxLength );
    }

    /**
     * {@inheritDoc}
     */
    @Transient
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
    @Transient
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
    @Transient
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
    @Transient
    public String getWaivedString() {
        return Boolean.toString( isWaived() );
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getDetectorLabel() {
        return "User defined";
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isUndefined() {
        return super.isUndefined()
                && remediation.isEmpty();
    }


}
