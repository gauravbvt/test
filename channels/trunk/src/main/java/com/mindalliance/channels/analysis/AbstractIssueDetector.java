package com.mindalliance.channels.analysis;

import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.GeoService;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Abstract IssueDetector class.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 1:39:47 PM
 */
public abstract class AbstractIssueDetector implements IssueDetector {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractIssueDetector.class );
    /**
     * A query service.
     */
    private QueryService queryService;
    /**
     * A geo service.
     */
    private GeoService geoService;
    /**
     * Attachment manager.
     */
    private AttachmentManager attachmentManager;

    /**
     * {@inheritDoc}
     */
    public abstract List<Issue> detectIssues( ModelObject modelObject );

    /**
     * {@inheritDoc}
     */
    public abstract boolean appliesTo( ModelObject modelObject );

    /**
     * {@inheritDoc}
     */
    public abstract String getTestedProperty();

    /**
     * {@inheritDoc}
     */
    public String getKind() {
        return getClass().getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        // default
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject, String property ) {
        return appliesTo( modelObject )
                && property != null
                && isPropertySpecific()
                && property.equals( getTestedProperty() );
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    /**
     * Get query service.
     *
     * @return a query service
     */
    protected QueryService getQueryService() {
        return queryService;
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public void setAttachmentManager( AttachmentManager attachmentManager ) {
        this.attachmentManager = attachmentManager;
    }

    public GeoService getGeoService() {
        return geoService;
    }

    public void setGeoService( GeoService geoService ) {
        this.geoService = geoService;
    }

    /**
     * Make detected issue.
     *
     * @param type  a string
     * @param about a model object
     * @return a detected issue
     */
    protected DetectedIssue makeIssue( String type, ModelObject about ) {
        DetectedIssue issue = new DetectedIssue( type, about );
        initializeDetectedIssue( issue );
        if ( !about.isWaived( getKind() ) )
            LOG.debug( "Detected issue: "
                    + getKind()
                    + " on " + issue.getAbout() + "(" + issue.getAbout().getId() + ")" );
        return issue;
    }

    protected abstract String getLabel();

    /**
     * Make detected issue.
     *
     * @param type     a string
     * @param about    a model object
     * @param property a string
     * @return a detected issue
     */
    protected DetectedIssue makeIssue( String type, ModelObject about, String property ) {
        DetectedIssue issue = new DetectedIssue( type, about, property );
        initializeDetectedIssue( issue );
        if ( !about.isWaived( getKind() ) )
            LOG.debug( "Detected issue: "
                    + getKind()
                    + " on " + issue.getAbout() + "(" + issue.getAbout().getId() + ")" + ":" + issue.getProperty() );
        return issue;
    }

    private void initializeDetectedIssue( DetectedIssue issue ) {
        issue.setKind( getKind() );
        issue.setDetectorLabel( getLabel() );
        issue.setCanBeWaived( canBeWaived() );
        issue.setDefaultRemediators( getDefaultRemediators() );
    }

    private List<String> getDefaultRemediators() {
        return queryService.findAllPlanners();
    }

    /**
     * Get current plan.
     *
     * @return a plan
     */
    protected Plan getPlan() {
        return queryService.getCurrentPlan();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPropertySpecific() {
        return getTestedProperty() != null;
    }

    /**
     * Get the severity of failing a sharing flow.
     * @param flow a sharing flow
     * @return an issue level
     */
    protected Severity getFailureSeverity( Flow flow ) {
        assert flow.isSharing();
        return getQueryService().getPartPriority( (Part) flow.getTarget() );
    }

    /**
     * Get the severity of failing a task.
     * @param part a part
     * @return an issue level
     */
    protected Severity getFailureSeverity( Part part ) {
        return getQueryService().getPartPriority( part );
    }

}
