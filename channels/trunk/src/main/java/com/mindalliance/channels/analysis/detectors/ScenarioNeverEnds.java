package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * Scenario might not terminate.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 3:01:54 PM
 */
public class ScenarioNeverEnds extends AbstractIssueDetector {

    public ScenarioNeverEnds() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Scenario may never end";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        Phase phase = scenario.getPhase();
        Event event = scenario.getEvent();
        if ( !( phase.isConcurrent() && event.isSelfTerminating()
                || phase.isPreEvent() && getPlan().isIncident( event ) )
                && getQueryService().findTerminators( scenario ).isEmpty() ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, scenario );
            issue.setDescription( "The scenario may never end." );
            issue.setRemediation( "Have the event end on its own if the scenario is for a concurrent phase\n"
                    +"or have the event be an incident if the scenario is for a pre-event phase\n "
                    + "or have at least one task in the scenario terminate the scenario." );
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
