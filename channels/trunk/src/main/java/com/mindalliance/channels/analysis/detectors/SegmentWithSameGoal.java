package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Segment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A plan segment responds to the same event and ahas the same goal as another.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 12, 2009
 * Time: 4:20:00 PM
 */
public class SegmentWithSameGoal extends AbstractIssueDetector {

    public SegmentWithSameGoal() {
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
                Set<Goal> sharedGoals = new HashSet<Goal>();
                for ( Goal goal : segment.getGoals() ) {
                    if ( other.getGoals().contains( goal ) ) {
                        sharedGoals.add( goal );
                    }
                }
                for ( Goal sharedGoal : sharedGoals ) {
                    DetectedIssue issue = makeIssue( Issue.VALIDITY, segment );
                    issue.setDescription( "This plan segment is for the same event phase as \""
                            + other.getName()
                            + "\" and both have a common goal: \"" + sharedGoal.getLabel()
                            + "\" by "
                            + ( sharedGoal.getOrganization() != null
                            ? sharedGoal.getOrganization().getName()
                            : "all" )
                            + "."
                    );
                    issue.setRemediation( "Consider merging the two plan segments\n"
                            + "or moving the tasks that achieve the same goal from one plan segment to the other." );
                    issue.setSeverity( Level.Low );
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
