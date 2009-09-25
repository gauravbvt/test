package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Phase;
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
public class ScenarioNeverStarts extends AbstractIssueDetector {

    public ScenarioNeverStarts() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        Plan plan = getPlan();
        Event event = scenario.getEvent();
        Phase phase = scenario.getPhase();
        boolean isIncident = plan.isIncident( event );
        if ( !( isIncident && phase.isConcurrent()
                ||
                ( isIncident || !getQueryService().findCausesOf( event ).isEmpty() )
                        && event.isSelfTerminating() && phase.isPostEvent() )
                && !getQueryService().isInitiated( scenario ) ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, scenario );
            issue.setDescription( "The scenario may never start"
                    + " because it is not in response to an incident and no other scenario causes it to start." );
            issue.setRemediation( "Make this scenario in response to an incident\n"
                    + "or make sure that a task in another scenario causes this scenario to start." );
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

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Scenario may never starts";
    }
}
