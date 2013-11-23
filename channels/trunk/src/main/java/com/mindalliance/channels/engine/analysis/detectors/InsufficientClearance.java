package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;


/**
 * An actor assigned to a task has insufficent clearances to receive shared, classified information.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 11, 2009
 * Time: 8:57:24 AM
 */
public class InsufficientClearance extends AbstractIssueDetector {

    public InsufficientClearance() {
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        if ( flow.isSharing() && flow.isClassified() ) {
            List<Assignment> assignments = queryService.findAllAssignments( (Part) flow.getTarget(), false );
            for ( Assignment assignment : assignments ) {
                Actor actor = assignment.getActor();
                if ( actor.isActual() && !actor.isClearedFor( flow, queryService.getPlan() ) ) {
                    Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
                    issue.setDescription( "Assigned recipient " + actor.getName()
                            + " of \"" + flow.getName()
                            + "\" does not have sufficient clearance." );
                    issue.setRemediation( "Declassify the information" +
                            "\nor increase the clearance of " + actor.getName() );
                    if ( flow.isCritical() ) {
                        issue.setSeverity( queryService.computePartPriority( (Part) flow.getTarget() ) );
                    } else {
                        issue.setSeverity( Level.Medium );
                    }
                    issues.add( issue );
                } else if ( actor.isUnknown() ) {
                    Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
                    issue.setDescription( "Assigned recipient "
                            + " of classified \"" + flow.getName()
                            + "\" is unknown." );
                    issue.setRemediation( "Declassify the information" +
                            "\nor change the definition of the task to assign only cleared recipients"
                    + "\nor increase the classification of the intended recipients");
                    if ( flow.isCritical() ) {
                        issue.setSeverity( queryService.computePartPriority( (Part) flow.getTarget() ) );
                    } else {
                        issue.setSeverity( Level.Low );
                    }
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Recipient of classified information has insufficient clearance";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
