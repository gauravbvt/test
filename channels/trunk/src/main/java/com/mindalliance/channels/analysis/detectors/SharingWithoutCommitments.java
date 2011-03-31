package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Sharing flow without commitment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 10, 2009
 * Time: 1:15:20 PM
 */
public class SharingWithoutCommitments extends AbstractIssueDetector {

    public SharingWithoutCommitments() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        QueryService queryService = getQueryService();
        Assignments assignments = queryService.getAssignments( false );
        List<Commitment> commitments = queryService.findAllCommitments( flow,
                                                                        false, assignments );
        if ( flow.getRestriction() != Flow.Restriction.Self &&  commitments.isEmpty() ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, flow );
            Part source = (Part)flow.getSource();
            Part target = (Part)flow.getTarget();
            String description = "No sharing commitment is implied by this information flow";
            if ( flow.isProhibited() ) {
                description += " because sharing is prohibited";
                issue.setRemediation( "Remove all prohibiting policies\nor remove the flow" );
            } else {
                description += " because";
                boolean noSourceAssignment = isSourceUnassigned( flow );
                if ( noSourceAssignment ) {
                    description += " no one is assigned to task \"" + source.getTask() + "\"";
                }
                boolean noTargetAssignment = isTargetUnassigned( flow );
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
                    if ( noSourceAssignment ) sb.append( " and for" );
                    sb.append( " task \"" );
                    sb.append( target.getTask() );
                    sb.append( "\"" );
                }
                sb.append( "\nor add or redefine one or more participating agents so that at least one matches the specifications of" );
                if ( noSourceAssignment ) {
                    sb.append( " task \"" );
                    sb.append( source.getTask() );
                    sb.append( "\"" );
                }
                if ( noTargetAssignment ) {
                    if ( noSourceAssignment ) sb.append( " and the specifications of" );
                    sb.append( " task \"" );
                    sb.append( target.getTask() );
                    sb.append( "\"" );
                }
                issue.setRemediation( sb.toString() );
            }
            description += ".";
            issue.setDescription( description );
            issue.setSeverity( this.getSharingFailureSeverity( flow ) );
            issues.add( issue );
        }
        return issues;
    }

    private boolean isTargetUnassigned( Flow flow ) {
        return getQueryService().findAllAssignments( (Part) flow.getTarget(), false ).isEmpty();
    }

    private boolean isSourceUnassigned( Flow flow ) {
        return getQueryService().findAllAssignments( (Part) flow.getSource(), false ).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow && ( (Flow) modelObject ).isSharing();
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
        return "Sharing flow implies no commitments";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

}
