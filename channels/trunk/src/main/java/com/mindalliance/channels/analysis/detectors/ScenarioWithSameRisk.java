package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Scenario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A scenario responds to the smae event and addresse the same risk as another.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 12, 2009
 * Time: 4:20:00 PM
 */
public class ScenarioWithSameRisk extends AbstractIssueDetector {

    public ScenarioWithSameRisk() {
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
        return "Scenario overlap";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        for ( Scenario other : getQueryService().list( Scenario.class ) ) {
            if ( !scenario.equals( other )
                    && scenario.getEvent().equals( other.getEvent() )
                    && scenario.getPhase().equals( other.getPhase() ) ) {
                Set<Risk> sharedRisks = new HashSet<Risk>();
                for ( Risk risk : scenario.getRisks() ) {
                    if ( other.getRisks().contains( risk ) ) {
                        sharedRisks.add( risk );
                    }
                }
                for ( Risk sharedRisk : sharedRisks ) {
                    DetectedIssue issue = makeIssue( Issue.VALIDITY, scenario );
                    issue.setDescription( "This scenario is for the same event phase as scenario \""
                            + other.getName()
                            + "\" and both address a common risk: \"" + sharedRisk.getLabel()
                            + "\" impacting "
                            + ( sharedRisk.getOrganization() != null
                            ? sharedRisk.getOrganization().getName()
                            : "all" )
                            + "."
                    );
                    issue.setRemediation( "Consider merging the two scenarios\n"
                            + "or moving the parts that mitigate the common risk from one scenario to the other." );
                    issue.setSeverity( Issue.Level.Minor );
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
        return modelObject instanceof Scenario;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }
}
