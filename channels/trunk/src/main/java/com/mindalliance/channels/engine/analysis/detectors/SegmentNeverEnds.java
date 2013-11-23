package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Plan segment might not terminate.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 3:01:54 PM
 */
public class SegmentNeverEnds extends AbstractIssueDetector {

    public SegmentNeverEnds() {
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
        return "Segment's scenario may never end";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        Phase phase = segment.getPhase();
        Event event = segment.getEvent();
        if ( !( phase.isConcurrent() && event.isSelfTerminating()
                || phase.isPreEvent() && queryService.getPlan().isIncident( event ) )
                && queryService.findTerminators( segment ).isEmpty() ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, segment );
            issue.setDescription( "\"" + segment.getPhaseEventTitle() + "\" is never ended." );
            String remediation;
            if ( segment.getPhase().isConcurrent() ) {
                remediation = "Have event \"" + segment.getEvent().getName() + "\" end on its own"
                        + "\nor have at least one task in the segment terminate it.";
            } else if (segment.getPhase().isPreEvent() ) {
                  remediation = "Make event \"" 
                          + segment.getEvent().getName()
                          + "\"  an incident (it can occur on its own)."
                          + "\nor have at least one task in another segment start event.";
            } else {
                // post-event
                remediation = "Have at least one task in the segment terminate event \""
                        + segment.getEvent().getName() + "\".";
            }
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
}
