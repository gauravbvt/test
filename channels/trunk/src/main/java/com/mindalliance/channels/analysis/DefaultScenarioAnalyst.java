package com.mindalliance.channels.analysis;

import com.mindalliance.channels.ModelObject;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Default implementation of ScenarioAnalyst
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 10:07:27 AM
 */
// TODO - cache detected issues, reset cache according to changes to the model
public class DefaultScenarioAnalyst implements ScenarioAnalyst {

    private static final String DESCRIPTION_SEPARATOR = " -- ";

    /**
     * Issue detectors registered with the scenario analyst.
     */
    private List<IssueDetector> issueDetectors = new ArrayList<IssueDetector>();

    public DefaultScenarioAnalyst() {
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Issue> findIssues( ModelObject modelObject, boolean all ) {
        return new IssueIterator( issueDetectors, modelObject, all );
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Issue> findIssues( ModelObject modelObject, String property ) {
        return new IssueIterator( issueDetectors, modelObject, property );
    }


    /**
     * {@inheritDoc}
     */
    public List<Issue> listIssues( ModelObject modelObject, boolean all ) {
        List<Issue> issues = new ArrayList<Issue>();
        Iterator<Issue> iterator = findIssues( modelObject, all );
        while (iterator.hasNext()) issues.add( iterator.next() );
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> listIssues( ModelObject modelObject, String property ) {
        List<Issue> issues = new ArrayList<Issue>();
        Iterator<Issue> iterator = findIssues( modelObject, property );
        while (iterator.hasNext()) issues.add( iterator.next() );
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasIssues( ModelObject modelObject, boolean all ) {
        return findIssues( modelObject, all ).hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasIssues( ModelObject modelObject, String property ) {
        return findIssues( modelObject, property ).hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public String getIssuesSummary( ModelObject modelObject, boolean all ) {
        Iterator<Issue> issues = findIssues( modelObject, all );
        return summarize( issues );
    }

    /**
     * {@inheritDoc}
     */
    public String getIssuesSummary( ModelObject modelObject, String property ) {
        Iterator<Issue> issues = findIssues( modelObject, property );
        return summarize( issues );
    }

    /**
     * Aggregate the descriptions of issues
     *
     * @param issues -- an iterator on issues
     * @return a string summarizing the issues
     */
    private String summarize( Iterator<Issue> issues ) {
        StringBuilder sb = new StringBuilder();
        while ( issues.hasNext() ) {
            Issue issue = issues.next();
            sb.append( issue.getDescription() );
            if ( issues.hasNext() ) sb.append( DESCRIPTION_SEPARATOR );
        }
        return sb.toString();
    }

    /**
     * Sets the list of issue detectors used for analysis
     *
     * @param issueDetectors -- a list of issue detectors
     */
    public void setIssueDetectors( List<IssueDetector> issueDetectors ) {
        this.issueDetectors = issueDetectors;
    }
}
