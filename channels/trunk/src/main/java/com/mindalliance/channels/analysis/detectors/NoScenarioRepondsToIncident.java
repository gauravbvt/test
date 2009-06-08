package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * No scenario responds to an incident.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2009
 * Time: 10:28:42 AM
 */
public class NoScenarioRepondsToIncident extends AbstractIssueDetector {

    public NoScenarioRepondsToIncident() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Plan plan = (Plan) modelObject;
        for ( Event incident : plan.getIncidents() ) {
            boolean responded = false;
            Iterator<Scenario> scenarios = getQueryService().list( Scenario.class ).iterator();
            while ( !responded && scenarios.hasNext() ) {
                Scenario scenario = scenarios.next();
                if ( scenario.getEvent() == incident ) responded = true;
            }
            if ( !responded ) {
                DetectedIssue issue = makeIssue( Issue.COMPLETENESS, plan );
                issue.setDescription( "No scenario responds to incident \"" + incident.getName() + "\"." );
                issue.setRemediation( "Define a scenario that responds to it, "
                        + "or remove its incident status." );
                issue.setSeverity( ( Issue.Level.Major ) );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Plan;
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
        return "No scenario for event";
    }
}
