package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * A problem uncovered about a model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 9:30:12 AM
 */
public class DetectedIssue extends AnalysisObject implements Issue {

    /**
     * Id counter.
     */
    private static long NEGATIVE_COUNTER = Long.MAX_VALUE;
    /**
     * The kind of detector that found this issue.
     */
    private String kind;
    /**
     * User names of the default remediators.
     */
    private List<String> defaultRemediators;

    /**
     * Detector tags.
     */
    private List<String> detectorTags;
    /**
     * Label for the detector.
     */
    private String detectorLabel;
    /**
     * Type of issue.
     */
    private String type;

    /**
     * How to resolve the issue.
     */
    private String remediation;
    /**
     * Whether the kind of this issue can be waived.
     * Set by detector.
     */
    private boolean canBeWaived;
    /**
     * The issue's severity.
     */
    private Level severity = Level.Low;

    public DetectedIssue() { };

    /**
     * Constructor.
     *
     * @param type -- the type of issue
     * @param identifiable   -- what the issue is about
     */
    public DetectedIssue( String type, Identifiable identifiable ) {
        super( identifiable );
        this.type = type;
    }

    /**
     * Constructor.
     *
     * @param type     -- the type of issue
     * @param identifiable       -- what the issue is about
     * @param property -- the problematic property
     */
    public DetectedIssue( String type, Identifiable identifiable, String property ) {
        super( identifiable, property );
        this.type = type;
    }

    public static String classLabel() {
        return "issues";
    }

    public String getClassLabel() {
        return classLabel();
    }

    @Override
    public String getKindLabel() {
        return getTypeName();
    }

    /**
     * {@inheritDoc}
     */
    public String getTypeName() {
        return "detected issue";
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    public String getKind() {
        return kind;
    }

    public String getDetectorLabel() {
        return detectorLabel;
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
    public boolean hasTag( String tag ) {
        return getDetectorTags().contains( tag );
    }

    @Override
    public List<String> getRemediationOptions() {
        return Arrays.asList( getRemediation().split( "\\n" ) );
    }

    public void setDetectorLabel( String detectorLabel ) {
        this.detectorLabel = detectorLabel;
    }

    public List<String> getDefaultRemediators() {
        return defaultRemediators;
    }

    public void setDefaultRemediators( List<String> defaultRemediators ) {
        this.defaultRemediators = defaultRemediators;
    }

    public List<String> getDetectorTags() {
        return detectorTags;
    }

    public void setDetectorTags( List<String> detectorTags ) {
        this.detectorTags = detectorTags;
    }

    /**
     * {@inheritDoc}
     */
    public String waivedString() {
        return isWaived() ? "waived" : "";
    }

    /**
     * {@inheritDoc}
     */
    public String getWaivedString() {
        return Boolean.toString( isWaived() );
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
                + ( isWaived() ? "Waived" : severity.getNegativeLabel() )
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
    public boolean isWaived() {
        return AbstractIssueDetector.isWaived( getAbout(), getKind() );
    }


}
