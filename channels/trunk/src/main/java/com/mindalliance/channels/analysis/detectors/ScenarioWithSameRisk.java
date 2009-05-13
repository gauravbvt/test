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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        for ( Scenario other : getQueryService().list( Scenario.class ) ) {
            if ( scenario != other && scenario.getEvent() == other.getEvent() ) {
                Set<Risk> sharedRisks = new HashSet<Risk>();
                for ( Risk risk : scenario.getRisks() ) {
                    if ( other.getRisks().contains( risk ) ) {
                        sharedRisks.add( risk );
                    }
                }
                for ( Risk sharedRisk : sharedRisks ) {
                    DetectedIssue issue = new DetectedIssue( Issue.STRUCTURAL, scenario );
                    issue.setDescription( "This scenario responds to the same event as scenario \""
                            + other.getName()
                            + "\" and both address the same risk: \"" + sharedRisk.getLabel()
                            + "\" impacting "
                            + ( sharedRisk.getOrganization() != null
                                    ? sharedRisk.getOrganization().getName()
                                    : "all" )
                            + "."
                    );
                    issue.setRemediation( "Consider merging the two scenarios, "
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
