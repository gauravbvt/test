package com.mindalliance.channels.core.util;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/13
 * Time: 9:53 AM
 */
abstract public class AbstractIssueWrapper implements Issue {

    public static final String WAIVED = "Yes";
    public static final String NOT_WAIVED = "No";
    public static final String NA_WAIVED = "N/A";
    public static final String[] WAIVED_STATES = {NOT_WAIVED, WAIVED, NA_WAIVED}; // Order matters

    private Issue issue;

    public AbstractIssueWrapper( Issue issue ) {
        this.issue = issue;
    }

    public Issue getIssue() {
        return issue;
    }

    abstract public String getWaivedString(   );

    abstract public boolean isWaived(  );

    abstract public String getLabel( int maxLength );


    public String getWaivedString( CommunityService communityService ) {
        return getWaivedString();
    }

    public boolean isWaived( CommunityService communityService  ) {
        return isWaived();
    }

    public String getLabel( int maxLength, CommunityService communityService ) {
        return getLabel( maxLength );
    }

    @Override
    public boolean canBeWaived() {
        return issue.canBeWaived();
    }

    @Override
    public Identifiable getAbout() {
        return issue.getAbout();
    }

    @Override
    public String getType() {
        return issue.getType();
    }

    @Override
    public String getDescription() {
        return issue.getDescription();
    }

    @Override
    public void setDescription( String description ) {
        issue.setDescription( description );
    }

    @Override
    public String getRemediation() {
        return issue.getRemediation();
    }

    @Override
    public void setRemediation( String remediation ) {
        issue.setRemediation( remediation );
    }

    @Override
    public String getReportedBy() {
        return issue.getReportedBy();
    }

    @Override
    public void setReportedBy( String reportedBy ) {
        issue.setReportedBy( reportedBy );
    }

    @Override
    public boolean isDetected() {
        return issue.isDetected();
    }

    @Override
    public Level getSeverity() {
        return issue.getSeverity();
    }

    @Override
    public void setSeverity( Level severity ) {
        issue.setSeverity( severity );
    }

    @Override
    public String getKind() {
        return issue.getKind();
    }

    @Override
    public String getDetectorLabel() {
        return issue.getDetectorLabel();
    }

    @Override
    public boolean isValidity() {
        return issue.isValidity();
    }

    @Override
    public boolean isCompleteness() {
        return issue.isCompleteness();
    }

    @Override
    public boolean isRobustness() {
        return issue.isRobustness();
    }

    @Override
    public boolean hasTag( String tag ) {
        return issue.hasTag( tag );
    }

    @Override
    public List<String> getRemediationOptions() {
        return issue.getRemediationOptions();
    }

    @Override
    public String getWaivability() {
        return issue.getWaivability();
    }

    @Override
    public long getId() {
        return issue.getId();
    }

    @Override
    public String getTypeName() {
        return issue.getTypeName();
    }

    @Override
    public boolean isModifiableInProduction() {
        return issue.isModifiableInProduction();
    }

    @Override
    public String getClassLabel() {
        return issue.getClassLabel();
    }

    @Override
    public String getKindLabel() {
        return issue.getKindLabel();
    }

    @Override
    public String getUid() {
        return issue.getUid();
    }

    @Override
    public String getName() {
        return issue.getName();
    }
}
