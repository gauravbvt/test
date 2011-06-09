package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent without supervisor commitment commits to a supervisor.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/8/11
 * Time: 8:40 PM
 */
public class AgentWithoutSupervisorCommitsToOne extends AbstractIssueDetector {

    public AgentWithoutSupervisorCommitsToOne() {
    }

    @Override
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Flow flow = (Flow)modelObject;
        Flow.Restriction restriction = flow.getRestriction();
        QueryService queryService = getQueryService();
        if ( restriction != null && flow.isSharing() && restriction.equals( Flow.Restriction.Supervisor ) ) {
           Part source = (Part)flow.getSource();
            List<Assignment> assignments = queryService.findAllAssignments( source, false );
            for ( Assignment assignment : assignments ) {
                final Actor actor = assignment.getActor();
                Organization org = assignment.getOrganization();
                boolean hasSupervisor = CollectionUtils.exists(
                        org.getJobs(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                Job job = (Job)object;
                                Actor a = job.getActor();
                                return a != null && a.equals(  actor  ) && job.getSupervisor() != null;
                            }
                        }
                );
                if ( !hasSupervisor ) {
                    Issue issue = makeIssue( Issue.COMPLETENESS, flow );
                    String assignmentLabel = assignment.getResourceSpec().getLabel();
                    issue.setDescription( assignmentLabel + " has no supervisor and so can't possibly share with one." );
                    issue.setRemediation( "Identify the supervisor of " + assignmentLabel
                            + "\nor remove the restriction to share only with a supervisor." );
                    issue.setSeverity( getSharingFailureSeverity( flow ) );
                    issues.add(  issue );
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
