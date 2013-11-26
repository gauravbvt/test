package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * There is no plan segment for an event phase.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 24, 2009
 * Time: 3:50:44 PM
 */
public class NoSegmentForEventPhase extends AbstractIssueDetector {

    public NoSegmentForEventPhase() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Plan plan = (Plan) modelObject;
        List<Segment> segments = queryService.list( Segment.class );
        for ( final Phase phase : plan.getPhases() ) {
            for ( final Event event : queryService.list( Event.class ) ) {
                if ( !event.isUnknown() ) {
                    boolean exists = CollectionUtils.exists(
                            segments,
                            new Predicate() {
                                public boolean evaluate( Object obj ) {
                                    Segment segment = (Segment) obj;
                                    return segment.getPhase().equals( phase )
                                            && segment.getEvent().equals( event );
                                }
                            }
                    );
                    if ( !exists ) {
                        Issue issue = makeIssue( communityService, Issue.COMPLETENESS, plan );
                        issue.setDescription( "No segment for phase "
                                + phase.getName()
                                + " of event "
                                + event.getName() );
                        issue.setRemediation( "Add a segment for phase "
                                + phase.getName()
                                + " of event "
                                + event.getName()
                                + "\nor remove "
                                + event.getName()
                                + " from the template scope"
                                + "\nor remove "
                                + phase.getName()
                                + " from the template scope."
                        );
                        issue.setSeverity( Level.Low );
                        issues.add( issue );
                    }
                }
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
        return "No segment covers a scenario in scope";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
