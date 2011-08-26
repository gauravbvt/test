package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * Event (anonymous) phase in segment context never started.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/13/11
 * Time: 10:13 AM
 */
public class SegmentContextNeverCaused extends AbstractIssueDetector {

    public SegmentContextNeverCaused() {
    }

       /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        List<EventTiming> eventTimings = segment.getContext();
        for ( EventTiming eventTiming : eventTimings ) {
            Event event = eventTiming.getEvent();
            Phase.Timing phaseTiming = eventTiming.getTiming();
            if ( phaseTiming == Phase.Timing.Concurrent ) {
                if ( !canStart( event ) )  {
                    Issue issue = makeIssue( Issue.COMPLETENESS, segment );
                    issue.setDescription( "The segment context \""
                            + eventTiming
                            + "\" may never come to be because event \"" + event + "\" never happens." );
                    issue.setRemediation("Make event \"" + event + "\" an incident"
                            + "\nor have a task in another segment cause it.");
                    issue.setSeverity( Level.Medium );
                    issues.add( issue );
                }
            } else {
                assert phaseTiming == Phase.Timing.PostEvent;
                if ( !canTerminate( event ) ) {
                    if ( !canStart( event ) ) {
                        Issue issue = makeIssue( Issue.COMPLETENESS, segment );
                        issue.setDescription( "The segment context \""
                                + eventTiming
                                + "\" may never come to be because event \""
                                + event
                                + "\" never happens and thus can't terminate." );
                        issue.setRemediation("Make sure event\""
                                + event
                                + "\" can first start by making it an incident"
                            + "\nor by having a task in another segment cause it.");
                        issue.setSeverity( Level.Medium );
                        issues.add( issue );
                    } else {
                        Issue issue = makeIssue( Issue.COMPLETENESS, segment );
                        issue.setDescription( "The segment context \""
                                + eventTiming
                                + "\" may never come to be because event \""
                                + event
                                + "\" never terminates." );
                        issue.setRemediation( "Make event \"" + event + "\" self-terminating"
                                + "\nor have at least one task in a co-event segment addressing the event terminate its own segment.");
                        issue.setSeverity( Level.Medium );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    private boolean canStart( Event event ) {
        return getPlan().isIncident( event )
                || !getQueryService().findCausesOf( event ).isEmpty();
    }

    private boolean canTerminate( Event event ) {
        return canStart( event )
                && ( event.isSelfTerminating()
                || !getQueryService().findTerminatorsOf( event ).isEmpty() );
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

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Segment context may never come to be";
    }

    /**
      * {@inheritDoc}
      */
    public boolean canBeWaived() {
        return true;
    }
}
