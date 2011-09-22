/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent without supervisor commitment commits to a supervisor.
 */
public class AgentWithoutSupervisorCommitsToOne extends AbstractIssueDetector {

    public AgentWithoutSupervisorCommitsToOne() {
    }

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        Flow.Restriction restriction = flow.getRestriction();
        if ( restriction != null && flow.isSharing() && restriction.equals( Flow.Restriction.Supervisor ) ) {
            Part source = (Part) flow.getSource();
            List<Assignment> assignments = queryService.findAllAssignments( source, false );
            for ( Assignment assignment : assignments ) {
                final Actor actor = assignment.getActor();
                boolean hasSupervisor =
                        CollectionUtils.exists( assignment.getOrganization().getJobs(), new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                Job job = (Job) object;
                                Actor a = job.getActor();
                                return a != null && a.equals( actor ) && job.getSupervisor() != null;
                            }
                        } );
                if ( !hasSupervisor ) {
                    Issue issue = makeIssue( queryService, Issue.COMPLETENESS, flow );
                    String assignmentLabel = assignment.getResourceSpec().getLabel();
                    issue.setDescription(
                            assignmentLabel + " has no supervisor and so can not possibly share with one." );
                    issue.setRemediation( "Identify the supervisor of " + assignmentLabel
                                          + "\nor remove the restriction to share only with a supervisor." );
                    issue.setSeverity( computeSharingFailureSeverity( queryService, flow ) );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Agent commits to share with supervisor but has none";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
