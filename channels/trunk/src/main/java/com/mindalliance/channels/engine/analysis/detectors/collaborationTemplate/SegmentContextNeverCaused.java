/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Phase.Timing;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Event (anonymous) phase in segment context never started.
 */
public class SegmentContextNeverCaused extends AbstractIssueDetector {

    public SegmentContextNeverCaused() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        List<EventTiming> eventTimings = segment.getContext();
        for ( EventTiming eventTiming : eventTimings ) {
            Event event = eventTiming.getEvent();
            Timing phaseTiming = eventTiming.getTiming();
            if ( phaseTiming == Timing.Concurrent ) {
                if ( !canStart( queryService, event ) ) {
                    Issue issue = makeIssue( communityService, Issue.COMPLETENESS, segment );
                    issue.setDescription(
                            "The segment context \"" + eventTiming + "\" may never come to be because event \"" + event
                            + "\" never happens." );
                    issue.setRemediation( "Make event \"" + event + "\" an incident"
                                          + "\nor have a task in another segment cause it." );
                    issue.setSeverity( Level.Medium );
                    issues.add( issue );
                }
            } else {
                assert phaseTiming == Timing.PostEvent;
                if ( !canTerminate( queryService, event ) ) {
                    if ( canStart( queryService, event ) ) {
                        Issue issue = makeIssue( communityService, Issue.COMPLETENESS, segment );
                        issue.setDescription(
                                "The segment context \"" + eventTiming + "\" may never come to be because event \""
                                + event + "\" never terminates." );
                        issue.setRemediation( "Make event \"" + event + "\" self-terminating"
                                              + "\nor have at least one task in a co-event segment addressing the event terminate its own segment." );
                        issue.setSeverity( Level.Medium );
                        issues.add( issue );
                    } else {
                        Issue issue = makeIssue( communityService, Issue.COMPLETENESS, segment );
                        issue.setDescription(
                                "The segment context \"" + eventTiming + "\" may never come to be because event \""
                                + event + "\" never happens and thus can't terminate." );
                        issue.setRemediation(
                                "Make sure event\"" + event + "\" can first start by making it an incident"
                                + "\nor by having a task in another segment cause it." );
                        issue.setSeverity( Level.Medium );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    private boolean canStart( QueryService queryService, Event event ) {
        return queryService.getPlan().isIncident( event ) || !queryService.findCausesOf( event ).isEmpty();
    }

    private boolean canTerminate( QueryService queryService, Event event ) {
        return canStart( queryService, event ) && ( event.isSelfTerminating()
                                                    || !queryService.findTerminatorsOf( event ).isEmpty() );
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Segment;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Segment context may never come to be";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
