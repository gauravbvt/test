package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Scenario;

import java.util.List;
import java.util.ArrayList;

/**
 * A scenario that is not an incident is never initiated by a part form another scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 11, 2009
 * Time: 2:41:55 PM
 */
public class NonIncidentScenarioNeverInitiated extends AbstractIssueDetector {

    public NonIncidentScenarioNeverInitiated() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        if ( !scenario.isIncident() && !scenario.isInitiated() ) {
            Issue issue = makeIssue( Issue.STRUCTURAL, scenario );
            issue.setDescription( "The scenario is never caused by a task even though"
                    + " it is expected to be (it is not an incident)." );
            issue.setRemediation( "Make the scenario an incident, or make sure at least one"
                    + " task in another scenario causes it." );
            issue.setSeverity( Issue.Level.Major );
            issues.add( issue );
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
