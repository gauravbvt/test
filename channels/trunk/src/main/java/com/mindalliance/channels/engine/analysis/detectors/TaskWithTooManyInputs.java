package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.query.Assignments;
import com.mindalliance.channels.engine.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Assignees of task would receive information from too many different sources.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 1, 2010
 * Time: 8:50:28 PM
 */
public class TaskWithTooManyInputs extends AbstractIssueDetector {

    public static int TOO_MANY = 10;

    public TaskWithTooManyInputs() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        // Send commitments
        int n;
        List<Assignment> assignments = getQueryService().findAllAssignments( part, false );
        if ( !assignments.isEmpty() && !areAllSystemsOrArchetypes( assignments )
                && ( n = countDifferentCommitters( part, assignments ) ) > TOO_MANY ) {
            Issue issue = makeIssue( Issue.ROBUSTNESS, part );
            issue.setDescription( "Agents executing task \""
                    + part.getTitle()
                    + "\" could receive information from too many different agents ("
                    + n
                    + ")." );
            issue.setRemediation( "Remove \"receive\" sharing flows"
                    + "\nor add intermediates to spread the incoming communication load." );
            issue.setSeverity( this.computeTaskFailureSeverity( part ) );
            issues.add( issue );
        }
        return issues;
    }

    // No assignee is neither a system nor an archetype.
    private boolean areAllSystemsOrArchetypes( List<Assignment> assignments ) {
        return !CollectionUtils.exists(
                assignments,
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Assignment assignment = (Assignment) object;
                        ModelEntity entity = assignment.getKnownAssignee();
                        return entity instanceof Organization
                                || ( entity instanceof Actor
                                && !( ( (Actor) entity ).isSystem() || ( (Actor) entity ).isArchetype() ) );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private int countDifferentCommitters( Part part, List<Assignment> assignments ) {
        List<ModelEntity> assignees = (List<ModelEntity>) CollectionUtils.collect(
                assignments,
                new Transformer() {
                    public Object transform( Object input ) {
                        return ( (Assignment) input ).getKnownAssignee();
                    }
                }
        );
        Set<ModelEntity> committers = new HashSet<ModelEntity>();
        QueryService queryService = getQueryService();
        Assignments a = queryService.getAssignments( false );
        for ( Flow sharingReceive : part.getAllSharingReceives() ) {
            for ( Commitment commitment : queryService.findAllCommitments( sharingReceive,
                                                                            false, a ) ) {
                Assignment assignment = commitment.getCommitter();
                ModelEntity assignee = assignment.getKnownAssignee();
                if ( !assignees.contains( assignee ) )
                    committers.add( assignee );
            }
        }
        return committers.size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
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
        return "Agents assigned to task receive information from too many sources";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
