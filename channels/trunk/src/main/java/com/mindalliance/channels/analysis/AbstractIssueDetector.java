package com.mindalliance.channels.analysis;

import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;

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
     * A query service
     */
    private QueryService queryService;

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
                && property.equals( getTestedProperty() );
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    /**
     * Get query service.
     * @return a query service
     */
    protected QueryService getQueryService() {
        return queryService;
    }

    /**
     * Get attachment manager.
     * @return an attachment manager
     */
    protected AttachmentManager getAttachmentManager() {
         return queryService.getChannels().getAttachmentManager();
     }



    /**
     * Make detected issue.
     * @param type a string
     * @param about a model object
     * @return a detected issue
     */
    protected DetectedIssue makeIssue( String type, ModelObject about ) {
        DetectedIssue issue = new DetectedIssue( type, about );
        issue.setKind( getKind() );
        issue.setCanBeWaived( canBeWaived() );
        return issue;
    }

    /**
     * Make detected issue.
     * @param type a string
     * @param about a model object
     * @param property a string
     * @return a detected issue
     */
    protected DetectedIssue makeIssue( String type, ModelObject about, String property ) {
        DetectedIssue issue = new DetectedIssue( type, about, property );
        issue.setKind( getClass().getSimpleName() );
        return issue;
    }

}
