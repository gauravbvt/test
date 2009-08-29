package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Scenario does not terminate on its own and no part terminates it.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 3:01:54 PM
 */
public class ScenarioEventNeverEnds extends AbstractIssueDetector {

    public ScenarioEventNeverEnds() {
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
        return "Event never ended";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        if ( !scenario.getEvent().isSelfTerminating() ) {
            boolean terminated = false;
            Iterator<Part> parts = scenario.parts();
            while ( !terminated && parts.hasNext() ) {
                Part part = parts.next();
                terminated = part.isTerminatesEvent();
            }
            if ( !terminated ) {
                Issue issue = makeIssue( Issue.COMPLETENESS, scenario );
                issue.setDescription( "The scenario never ends the event it responds to." );
                issue.setRemediation( "Have the event end on its own\n"
                        + "have at least one task in the scenario terminate it." );
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
