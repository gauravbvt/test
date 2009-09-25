package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * There is no scenario for an event phase.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 24, 2009
 * Time: 3:50:44 PM
 */
public class NoScenarioForEventPhase extends AbstractIssueDetector {

    public NoScenarioForEventPhase() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Plan plan = (Plan)modelObject;
        List<Scenario> scenarios = getQueryService().list(Scenario.class);
        for ( final Phase phase : plan.getPhases() ) {
            for ( final Event event : getQueryService().list( Event.class  )) {
                boolean exists = CollectionUtils.exists(
                     scenarios,
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                Scenario scenario = (Scenario)obj;
                                return scenario.getPhase().equals( phase )
                                        && scenario.getEvent().equals(event);
                            }
                        }
                );
                if (!exists) {
                    Issue issue = makeIssue(Issue.COMPLETENESS, plan);
                    issue.setDescription( "No scenario for phase "
                            + phase.getName()
                            + " of event "
                            + event.getName() );
                    issue.setRemediation( "Add a scenario for phase "
                            + phase.getName()
                            + " of event "
                            + event.getName() );
                    issue.setSeverity( Issue.Level.Major );
                    issues.add( issue );
                }
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
        return "No scenario for event phase";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
