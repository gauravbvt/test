package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * A scenario is about an event that is never caused.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 11, 2009
 * Time: 2:41:55 PM
 */
public class ScenarioEventNeverCaused extends AbstractIssueDetector {

    public ScenarioEventNeverCaused() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        Event event = scenario.getEvent();
        Plan plan = getPlan();
        if ( !plan.isIncident( event )
                && getQueryService().findCausesOf( event ).isEmpty() ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, scenario );
            issue.setDescription( "The scenario is about an event that may never be caused." );
            issue.setRemediation( "Make the event in question an incident\n"
                    +"or make sure at least one task in another scenario causes it." );
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
        return "Event may never be caused";
    }
}
