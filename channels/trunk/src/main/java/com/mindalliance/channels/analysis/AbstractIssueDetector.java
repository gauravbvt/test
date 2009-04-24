package com.mindalliance.channels.analysis;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.pages.Project;

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

    /**
     * Get data query object.
     * @return a data query object
     */
    protected DataQueryObject getDqo() {
        return Project.getProject().getDqo();
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
