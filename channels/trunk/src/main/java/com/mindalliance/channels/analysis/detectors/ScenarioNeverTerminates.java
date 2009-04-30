package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Scenario does not terminate on its own and no part terminates it.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 3:01:54 PM
 */
public class ScenarioNeverTerminates extends AbstractIssueDetector {

    public ScenarioNeverTerminates() {
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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        if ( !scenario.isSelfTerminating() ) {
            boolean terminated = false;
            Iterator<Part> parts = scenario.parts();
            while ( !terminated && parts.hasNext() ) {
                Part part = parts.next();
                terminated = part.isTerminatesScenario();
            }
            if ( !terminated ) {
                Issue issue = makeIssue( Issue.STRUCTURAL, scenario );
                issue.setDescription( "The scenario never ends." );
                issue.setRemediation( "Have the scenario end on its own"
                        + " or make sure at least one task terminates it." );
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
