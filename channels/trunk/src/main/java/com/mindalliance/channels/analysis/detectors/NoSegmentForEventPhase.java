package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Plan plan = (Plan) modelObject;
        List<Segment> segments = getQueryService().list( Segment.class );
        for ( final Phase phase : plan.getPhases() ) {
            for ( final Event event : getQueryService().list( Event.class ) ) {
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
                        Issue issue = makeIssue( Issue.COMPLETENESS, plan );
                        issue.setDescription( "No plan segment for phase "
                                + phase.getName()
                                + " of event "
                                + event.getName() );
                        issue.setRemediation( "Add a plan segment for phase "
                                + phase.getName()
                                + " of event "
                                + event.getName()
                                + "\nor remove "
                                + event.getName()
                                + " from the plan scope"
                                + "\nor remove "
                                + phase.getName()
                                + " from the plan scope."
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
    public boolean appliesTo( ModelObject modelObject ) {
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
    protected String getLabel() {
        return "No plan segment for event phase";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
