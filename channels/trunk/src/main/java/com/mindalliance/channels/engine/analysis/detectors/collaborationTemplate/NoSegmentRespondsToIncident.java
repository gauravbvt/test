package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * No plan segment responds to an incident.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2009
 * Time: 10:28:42 AM
 */
public class NoSegmentRespondsToIncident extends AbstractIssueDetector {

    public NoSegmentRespondsToIncident() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Plan plan = (Plan) modelObject;
        for ( Event incident : plan.getIncidents() ) {
            boolean responded = false;
            Iterator<Segment> segments = queryService.list( Segment.class ).iterator();
            while ( !responded && segments.hasNext() ) {
                Segment segment = segments.next();
                Event event = segment.getEvent();
                if ( event != null && event.equals(incident) ) responded = true;
            }
            if ( !responded ) {
                DetectedIssue issue = makeIssue( communityService, Issue.COMPLETENESS, plan );
                issue.setDescription( "No segment responds to incident \"" + incident.getName() + "\"." );
                issue.setRemediation( "Define a segment that responds to it\n"
                        + "remove the event's incident status." );
                issue.setSeverity( ( Level.High ) );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Plan;
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
        return "No segment addresses an incident in scope";
    }
}
