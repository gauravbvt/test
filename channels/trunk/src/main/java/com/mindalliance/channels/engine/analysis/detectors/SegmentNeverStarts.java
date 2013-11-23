package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * An initiated plan segment can never be caused according to the plan.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 11, 2009
 * Time: 2:52:56 PM
 */
public class SegmentNeverStarts extends AbstractIssueDetector {

    public SegmentNeverStarts() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        Plan plan = queryService.getPlan();
        Event event = segment.getEvent();
        Phase phase = segment.getPhase();
        boolean isIncident = plan.isIncident( event );
        boolean canStart =  phase.isPreEvent()
                                || isIncident && phase.isConcurrent()
                                || queryService.isInitiated( segment )
                                || phase.isPostEvent() && event.isSelfTerminating();

        if ( !canStart ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, segment );
            String eventName = segment.getEvent().getName();
            String description = "The segment may never start";
            String remediation = "";
            if ( !isIncident && phase.isConcurrent() ) {
                description += " because it is not in response to an incident " +
                        "and no other segment causes the event \""
                        + eventName + "\"";
                remediation = "Make the event \"" + eventName + "\" an incident\n" +
                        "or have a task in another segment cause the event.";
            } else if ( phase.isPostEvent() ) {
                description += " because no task in other segments terminates event \""
                        + eventName
                        + "\"";
                remediation = "Make the event \"" + eventName + "\" self-terminating"
                                + "\nor have at least one task in another segment terminate it.";
            }
            description += ".";
            issue.setDescription( description );
            assert !remediation.isEmpty();
            issue.setRemediation( remediation );
            issue.setSeverity( Level.Medium );
            issues.add( issue );
        }
        return issues;
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

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Segment's scenario may never be initiated";
    }

    /**
      * {@inheritDoc}
      */
    public boolean canBeWaived() {
        return true;
    }
}
