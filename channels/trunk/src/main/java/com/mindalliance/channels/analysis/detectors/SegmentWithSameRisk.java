package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Segment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A plan segment responds to the smae event and addresse the same risk as another.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 12, 2009
 * Time: 4:20:00 PM
 */
public class SegmentWithSameRisk extends AbstractIssueDetector {

    public SegmentWithSameRisk() {
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
        return "Plan segment overlaps another";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        for ( Segment other : getQueryService().list( Segment.class ) ) {
            if ( !segment.equals( other )
                    && segment.getEvent().equals( other.getEvent() )
                    && segment.getPhase().equals( other.getPhase() ) ) {
                Set<Risk> sharedRisks = new HashSet<Risk>();
                for ( Risk risk : segment.getRisks() ) {
                    if ( other.getRisks().contains( risk ) ) {
                        sharedRisks.add( risk );
                    }
                }
                for ( Risk sharedRisk : sharedRisks ) {
                    DetectedIssue issue = makeIssue( Issue.VALIDITY, segment );
                    issue.setDescription( "This plan segment is for the same event phase as \""
                            + other.getName()
                            + "\" and both address a common risk: \"" + sharedRisk.getLabel()
                            + "\" impacting "
                            + ( sharedRisk.getOrganization() != null
                            ? sharedRisk.getOrganization().getName()
                            : "all" )
                            + "."
                    );
                    issue.setRemediation( "Consider merging the two plan segments\n"
                            + "or moving the tasks that mitigate the common risk from one plan segment to the other." );
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
        return modelObject instanceof Segment;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }
}
