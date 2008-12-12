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
     * Use all applicable issue detectors to find issues about a model object
     *
     * @param modelObject -- the model object being analyzed
     * @param all -- all issues or only those that are not specific to a property
     * @return an iterator of issues
     */
    public Iterator<Issue> findIssues( ModelObject modelObject, boolean all ) {
        return new IssueIterator( issueDetectors, modelObject, all );
    }

    /**
     * Use all applicable issue detectors to find issues about a model object's property
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the name of a property of the model object
     * @return an iterator on issues detected
     */
    public Iterator<Issue> findIssues( ModelObject modelObject, String property ) {
        return new IssueIterator( issueDetectors, modelObject, property );
    }

    /**
     * Tests whether a model object has issues
     *
     * @param modelObject -- the model object being analyzed
     * @param all -- all issues or only those that are not specific to a property
     * @return whether a model object has issues
     */
    public boolean hasIssues( ModelObject modelObject, boolean all ) {
        return findIssues( modelObject, all ).hasNext();
    }

    /**
     * Tests whether a specifi property of a model object has issues
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the specifiec property being analyzed
     * @return whether a specifi property of a model object has issues
     */
    public boolean hasIssues( ModelObject modelObject, String property ) {
        return findIssues( modelObject, property ).hasNext();
    }

    /**
     * Produces an aggregate description of issues detected about a model object
     *
     * @param modelObject -- the model object being analyzed
     * @param all -- all issues or only those that are not specific to a property
     * @return an aggregate description of issues or an empty string if none
     */
    public String getIssuesSummary( ModelObject modelObject, boolean all ) {
        Iterator<Issue> issues = findIssues( modelObject, all );
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
