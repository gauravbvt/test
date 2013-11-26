/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A defined task is triggered but the triggering task is never started.
 */
public class TriggeredButNeverStartedDefinedTask extends AbstractIssueDetector {

    public TriggeredButNeverStartedDefinedTask() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( !part.hasDefaultTask() ) {
            if ( !part.isAutoStarted() ) {
                if ( part.isTriggered() ) {
                    boolean started = queryService.findIfPartStarted( part );
                    if ( !started ) {
                        Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                        issue.setDescription( "This task is triggered but any flow that triggers it"
                                + "  comes from a task that is itself never started." );
                        issue.setRemediation( "Make sure a triggering flow comes from a task that is started." );
                        issue.setSeverity( queryService.computePartPriority( part ) );
                        issues.add( issue );
                    }
                }
            } else {
                boolean started = queryService.findIfSegmentStarted( part.getSegment() );
                if ( part.isStartsWithSegment() && !started ) {
                    Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                    issue.setDescription( "This task starts with the segment but no task"
                            + " is ever started that causes the segment to happen.");
                    issue.setRemediation( "Make sure the event phase of the segment can start " +
                            "(it is a co-event phase of an incident " +
                            "or the phase is started by a task that itself can start)" );
                    issue.setSeverity( queryService.computePartPriority( part ) );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Task is triggered but what triggers it is never started";
    }
}
