package com.mindalliance.channels.analysis;

import com.mindalliance.channels.model.ModelObject;

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

    /**
     * Issue detectors registered with the scenario analyst.
     */
    private List<IssueDetector> issueDetectors = new ArrayList<IssueDetector>();

    public DefaultScenarioAnalyst() {
    }

    /**
     * Use all applicable issue detectors to find issues about a model object
     *
     * @param modelObject -- the model object being analyzed
     * @return an iterator of issues
     */
    public Iterator<Issue> findIssues( ModelObject modelObject ) {
        // Todo -- implement lazy iteration
        List<Issue> issues = new ArrayList<Issue>();
        for ( IssueDetector detector : issueDetectors ) {
            if ( detector.appliesTo( modelObject ) ) {
                Issue issue = detector.detectIssue( modelObject );
                if ( issue != null ) issues.add( issue );
            }
        }
        return issues.iterator();
    }

    /**
     * Use all applicable issue detectors to find issues about a model object's property
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the name of a property of the model object
     * @return an iterator on issues detected
     */
    public Iterator<Issue> findIssues( ModelObject modelObject, String property ) {
        // Todo -- implement lazy iteration
        List<Issue> issues = new ArrayList<Issue>();
        for ( IssueDetector detector : issueDetectors ) {
            if ( detector.appliesTo( modelObject, property ) ) {
                Issue issue = detector.detectIssue( modelObject );
                if ( issue != null ) issues.add( issue );
            }
        }
        return issues.iterator();
    }

    /**
     * Tests whether a model object has issues
     *
     * @param modelObject -- the model object being analyzed
     * @return whether a model object has issues
     */
    public boolean hasIssues( ModelObject modelObject ) {
        return findIssues( modelObject ).hasNext();
    }

    /**
     * Tests whether a specifi property of a model object has issues
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the specifiec property being analyzed
     * @return whether a specifi property of a model object has issues
     */
    public boolean hasIssues( ModelObject modelObject, String property ) {
        return findIssues( modelObject ).hasNext();
    }

    /**
     * Produces an aggregate description of issues detected about a model object
     *
     * @param modelObject -- the model object being analyzed
     * @return an aggregate description of issues or an empty string if none
     */
    public String getIssuesSummary( ModelObject modelObject ) {
        Iterator<Issue> issues = findIssues( modelObject );
        return summarize( issues );
    }

    /**
     * Produces an aggregate description of issues detected about a specific property
     * of a model object
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the name of a property
     * @return an aggregate description of issues or an empty string if none
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
            if ( issues.hasNext() ) sb.append( '\n' );
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
