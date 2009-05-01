package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Scenario;

import java.util.List;
import java.util.ArrayList;

/**
 * An initiated scenario can never be caused according to the plan.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 11, 2009
 * Time: 2:52:56 PM
 */
public class InitiatedScenarioNeverStarted extends AbstractIssueDetector {

    public InitiatedScenarioNeverStarted() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        if ( !scenario.isIncident() && scenario.isInitiated() ) {
            if ( !getQueryService().findIfScenarioStarted( scenario ) ) {
                Issue issue = makeIssue( Issue.STRUCTURAL, scenario );
                issue.setDescription( "The scenario can not be caused by planned tasks"
                        + " because none of the tasks that cause it are started." );
                issue.setRemediation( "Ensure that tasks that cause this scenario can start,"
                        + " or make this scenario an unplanned incident or situation." );
                issue.setSeverity( Issue.Level.Major );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Scenario;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }
}
