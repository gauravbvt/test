package com.mindalliance.channels;

import org.apache.commons.lang.StringUtils;

/**
 * A user provided issue
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 11:57:45 AM
 */
public class UserIssue extends ModelObject implements Issue, Deletable {
    /**
     * Max length of name
     */
    private static int MAX_LENGTH = 40;
    /**
     * The model object the issue is about
     */
    private ModelObject about;
    /**
     * Remediation
     */
    private String remediation;
    /**
     * Name of user who created or last modified the issue
     */
    private String reportedBy;
    /**
     * Whether marked for deletion
     */
    private boolean markedForDeletion;

    /**
     * Create a user issue
     * @param about a model object
     */
    public UserIssue( ModelObject about ) {
        super();
        this.about = about;
    }

    public ModelObject getAbout() {
        return about;
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

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion( boolean markedForDeletion ) {
        this.markedForDeletion = markedForDeletion;
    }

    public String getType() {
        return "User";
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return StringUtils.abbreviate( getDescription()  + " [" + getReportedBy() + "]", MAX_LENGTH );
    }
}
