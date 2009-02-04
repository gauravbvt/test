package com.mindalliance.channels;

import org.apache.commons.lang.StringUtils;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

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
    private static int MAX_LENGTH = 40;
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

    public UserIssue() {
    }

    /**
     * Create a user issue
     *
     * @param about a model object
     */
    public UserIssue( ModelObject about ) {
        super();
        this.about = about;
        setDescription( "(No description)" );
    }

    @ManyToOne
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
        this.remediation = remediation;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy( String reportedBy ) {
        this.reportedBy = reportedBy;
    }

    @Transient
    public String getType() {
        return "From user";
    }

    @Transient
    public String getName() {
        return getLabel( MAX_LENGTH );
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel( int maxLength ) {
        return StringUtils.abbreviate( getDescription(), maxLength );
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isDetected() {
        return false;
    }
}
