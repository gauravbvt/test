/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.geo.GeoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract IssueDetector class.
 */
public abstract class AbstractIssueDetector implements IssueDetector {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractIssueDetector.class );

    /**
     * An analyst.
     */
    private Analyst analyst;

    /**
     * A geo service.
     */
    private GeoService geoService;

    /**
     * Attachment manager.
     */
    private AttachmentManager attachmentManager;

    @Override
    public abstract List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject );

    @Override
    public abstract boolean appliesTo( ModelObject modelObject );

    @Override
    public abstract String getTestedProperty();

    @Override
    public String getKind() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean canBeWaived() {
        // default
        return false;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject, String property ) {
        return appliesTo( modelObject ) && property != null && isPropertySpecific() && property.equals(
                getTestedProperty() );
    }

    public Analyst getAnalyst() {
        return analyst;
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
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
     * @param queryService the query service
     * @param type a string
     * @param about a model object
     * @return a detected issue
     */
    protected DetectedIssue makeIssue( QueryService queryService, String type, ModelObject about ) {
        DetectedIssue issue = new DetectedIssue( type, about );
        initializeDetectedIssue( queryService, issue );
        if ( !about.isWaived( getKind() ) )
            LOG.debug( "Detected issue: " + getKind() + " on " + about + '(' + about.getId() + ')' );
        return issue;
    }

    protected abstract String getKindLabel();

    /**
     * Make detected issue.
     *
     * @param queryService the query service
     * @param type a string
     * @param about a model object
     * @param property a string
     * @return a detected issue
     */
    protected DetectedIssue makeIssue( QueryService queryService, String type, ModelObject about, String property ) {
        DetectedIssue issue = new DetectedIssue( type, about, property );
        initializeDetectedIssue( queryService, issue );
        if ( !about.isWaived( getKind() ) )
            LOG.debug( "Detected issue: " + getKind() + " on " + about + '(' + about.getId() + ')' + ':'
                       + issue.getProperty() );
        return issue;
    }

    private void initializeDetectedIssue( QueryService queryService, DetectedIssue issue ) {
        issue.setKind( getKind() );
        issue.setDetectorLabel( getKindLabel() );
        issue.setCanBeWaived( canBeWaived() );
        issue.setDefaultRemediators( queryService.findAllPlanners() );
        issue.setDetectorTags( getTags() );
    }

    // Default
    protected List<String> getTags() {
        return new ArrayList<String> ();
    }

    @Override
    public boolean isPropertySpecific() {
        return getTestedProperty() != null;
    }

    /**
     * Get the severity of failing a sharing flow.
     *
     * @param queryService for queries
     * @param flow a sharing flow
     * @return an issue level
     */
    protected static Level computeSharingFailureSeverity( QueryService queryService, Flow flow ) {
        return queryService.computeSharingPriority( flow );
    }

    /**
     * Get the severity of failing a task.
     *
     * @param queryService for queries
     * @param part a part
     * @return an issue level
     */
    protected static Level computeTaskFailureSeverity( QueryService queryService, Part part ) {
        return queryService.computePartPriority( part );
    }

    @Override
    public boolean isApplicable( ModelObject modelObject, boolean waived, boolean propertySpecific ) {
        return appliesTo( modelObject ) && modelObject.isWaived( getKind() ) == waived
               && isPropertySpecific() == propertySpecific;
    }
}
