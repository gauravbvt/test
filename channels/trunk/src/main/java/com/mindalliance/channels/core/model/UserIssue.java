package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.community.CommunityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.Map;

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

    public static String classLabel() {
        return "issues";
    }

    @Override
    public String getClassLabel() {
        return classLabel();
    }

    @Override
    public boolean isSegmentObject() {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public String getTypeName() {
        return "issue";
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
        return "Issue reported by template developer";
    }

    @Override
    public boolean isValidity() {
        return getType().equals( Issue.VALIDITY );
    }

    @Override
    public boolean isCompleteness() {
        return getType().equals( Issue.COMPLETENESS );
    }

    @Override
    public boolean isRobustness() {
        return getType().equals( Issue.ROBUSTNESS );
    }

    @Override
    public boolean hasTag( final String tag ) {
        return CollectionUtils.exists(
                getTags(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((Tag)object).getName().equals( tag );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndefined() {
        return super.isUndefined()
                && remediation.isEmpty();
    }

    @Override
    public Map<String, Object> mapState() {
        Map<String, Object> state = super.mapState();
        state.put( "type", getType() );
        state.put( "remediation", getRemediation() );
        state.put( "severity", getSeverity() );
        state.put( "reportedBy", getReportedBy() );
        return state;
    }

    @Override
    public void initFromMap( Map<String, Object> state, CommunityService communityService ) {
        super.initFromMap( state, communityService );
        setType( (String) state.get( "type" ) );
        setRemediation( (String) state.get( "remediation" ) );
        setSeverity( (Level) state.get( "severity" ) );
        setReportedBy( (String) state.get( "reportedBy" ) );
    }
}
