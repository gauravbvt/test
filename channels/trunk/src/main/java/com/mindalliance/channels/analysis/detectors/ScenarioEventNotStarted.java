package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * An initiated scenario can never be caused according to the plan.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 11, 2009
 * Time: 2:52:56 PM
 */
public class ScenarioEventNotStarted extends AbstractIssueDetector {

    public ScenarioEventNotStarted() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        Plan plan = getPlan();
        if ( !plan.isIncident( scenario.getEvent() ) && getQueryService().isInitiated( scenario ) ) {
            if ( !getQueryService().findIfScenarioStarted( scenario ) ) {
                Issue issue = makeIssue( Issue.COMPLETENESS, scenario );
                issue.setDescription( "The scenario would never start"
                        + " because no other scenario causes the event it responds to." );
                issue.setRemediation( "Ensure that tasks that cause the event in question are actually started,"
                        + " or make the event unplanned by adding it to the plan as an incident." );
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

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Event never caused";
    }
}
