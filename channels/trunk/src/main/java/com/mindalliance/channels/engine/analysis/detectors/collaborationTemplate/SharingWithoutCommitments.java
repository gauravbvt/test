/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Flow.Restriction;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Sharing flow without commitment.
 */
public class SharingWithoutCommitments extends AbstractIssueDetector {

    public SharingWithoutCommitments() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        Assignments assignments = queryService.getAssignments( false );
        List<Commitment> commitments = queryService.findAllCommitments( flow, false, assignments );
        if ( !flow.getRestrictions().contains( Restriction.Self ) && commitments.isEmpty() ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, flow );
            Part source = (Part) flow.getSource();
            Part target = (Part) flow.getTarget();
            String description = "No communication commitment is implied by this information flow";
            if ( flow.isProhibited() ) {
                description += " because communication is prohibited";
                issue.setRemediation( "Remove all prohibiting policies\nor remove the flow" );
            } else {
                description += " because";
                boolean noSourceAssignment = isSourceUnassigned( queryService, flow );
                if ( noSourceAssignment )
                    description += " no one is assigned to task \"" + source.getTask() + "\"";
                boolean noTargetAssignment = isTargetUnassigned( queryService, flow );
                if ( noTargetAssignment ) {
                    description += noSourceAssignment ? " and" : "";
                    description += " no one is assigned to task \"" + target.getTask() + "\"";
                }
                StringBuilder sb = new StringBuilder();
                sb.append( "Modify the specifications for" );
                if ( noSourceAssignment ) {
                    sb.append( " task \"" );
                    sb.append( source.getTask() );
                    sb.append( "\"" );
                }
                if ( noTargetAssignment ) {
                    if ( noSourceAssignment )
                        sb.append( " and for" );
                    sb.append( " task \"" );
                    sb.append( target.getTask() );
                    sb.append( "\"" );
                }
                sb.append(
                        "\nor add or redefine one or more participating agents so that at least one matches the specifications of" );
                if ( noSourceAssignment ) {
                    sb.append( " task \"" );
                    sb.append( source.getTask() );
                    sb.append( "\"" );
                }
                if ( noTargetAssignment ) {
                    if ( noSourceAssignment )
                        sb.append( " and the specifications of" );
                    sb.append( " task \"" );
                    sb.append( target.getTask() );
                    sb.append( "\"" );
                }
                issue.setRemediation( sb.toString() );
            }
            description += ".";
            issue.setDescription( description );
            issue.setSeverity( computeSharingFailureSeverity( queryService, flow ) );
            issues.add( issue );
        }
        return issues;
    }

    private static boolean isTargetUnassigned( QueryService queryService, Flow flow ) {
        return queryService.findAllAssignments( (Part) flow.getTarget(), false ).isEmpty();
    }

    private static boolean isSourceUnassigned( QueryService queryService, Flow flow ) {
        return queryService.findAllAssignments( (Part) flow.getSource(), false ).isEmpty();
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow && ( (Flow) modelObject ).isSharing();
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Information flow specifies no communication commitments between agents";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
