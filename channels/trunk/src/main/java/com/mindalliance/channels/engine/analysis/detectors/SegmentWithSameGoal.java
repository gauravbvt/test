package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;

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
    protected String getKindLabel() {
        return "Segment has same goal as another segment";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        for ( Segment other : queryService.list( Segment.class ) ) {
            if ( !segment.equals( other )
                    && segment.getEvent().equals( other.getEvent() )
                    && sameLevel( segment.getEventLevel(), other.getEventLevel() )
                    && segment.getPhase().equals( other.getPhase() ) ) {
                Set<Goal> sharedGoals = new HashSet<Goal>();
                for ( Goal goal : segment.getGoals() ) {
                    if ( other.getGoals().contains( goal ) ) {
                        sharedGoals.add( goal );
                    }
                }
                for ( Goal sharedGoal : sharedGoals ) {
                    DetectedIssue issue = makeIssue( communityService, Issue.VALIDITY, segment );
                    issue.setDescription( "This segment is for the same event phase as \""
                            + other.getName()
                            + "\" and both have a common goal: \"" + sharedGoal.getLabel()
                            + "\" by "
                            + ( sharedGoal.getOrganization() != null
                            ? sharedGoal.getOrganization().getName()
                            : "all" )
                            + "."
                    );
                    issue.setRemediation( "Merge the two segments\n"
                            + "or move all tasks that achieve the common goal from one segment to the other and remove the goal." );
                    issue.setSeverity( Level.Medium );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    private boolean sameLevel( Level level, Level other ) {
        return
                level == null && other == null
                        || level != null && other != null && level.equals( other );
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Segment;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }
}
