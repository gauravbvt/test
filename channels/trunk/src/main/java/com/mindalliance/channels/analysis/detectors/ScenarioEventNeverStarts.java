package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * A scenario that is not an incident is never initiated by a part form another scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 11, 2009
 * Time: 2:41:55 PM
 */
public class ScenarioEventNeverStarts extends AbstractIssueDetector {

    public ScenarioEventNeverStarts() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        Plan plan = Channels.getPlan();
        if ( !plan.isIncident( scenario.getEvent() ) && !getQueryService().isInitiated( scenario ) ) {
            Issue issue = makeIssue( Issue.STRUCTURAL, scenario );
            issue.setDescription( "The scenario is in response to an event that never happens." );
            issue.setRemediation( "Make the event in question an incident, or make sure at least one"
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
