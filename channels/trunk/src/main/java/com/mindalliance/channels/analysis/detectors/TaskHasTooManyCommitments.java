package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Agents who are neither systems nor archetypes and who are assigned to a task
 * have commitments with too many other agents.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 1, 2010
 * Time: 3:04:50 PM
 */
public class TaskHasTooManyCommitments extends AbstractIssueDetector {

    public static int TOO_MANY = 10;

    public TaskHasTooManyCommitments() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        int n;
        // Send commitments
        List<Assignment> assignments = getQueryService().findAllAssignments( part, false );
        if ( !assignments.isEmpty() ) {
            if ( !assignments.isEmpty() && !areAllSystemsOrArchetypes( assignments )
                    && ( n = countDifferentBeneficiaries( part, assignments ) ) > TOO_MANY ) {
                Issue issue = makeIssue( Issue.ROBUSTNESS, part );
                issue.setDescription( "Agents executing task \""
                        + part.getTitle()
                        + "\" would have sharing commitments with too many different agents ("
                        + n
                        + ")." );
                issue.setRemediation( "Remove \"send\" sharing flows"
                        + "\nor add intermediates to spread the outgoing communication load." );
                issue.setSeverity( this.getTaskFailureSeverity( part ) );
                issues.add( issue );
            }
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
    private int countDifferentBeneficiaries( Part part, List<Assignment> assignments ) {
        final List<ModelEntity> assignees = (List<ModelEntity>) CollectionUtils.collect(
                assignments,
                new Transformer() {
                    public Object transform( Object input ) {
                        return ( (Assignment) input ).getKnownAssignee();
                    }
                }
        );
        Set<ModelEntity> beneficiaries = new HashSet<ModelEntity>();
        QueryService queryService = getQueryService();
        Assignments a = queryService.getAssignments( false );
        for ( Flow sharingSend : part.getAllSharingSends() ) {
            List<Commitment> commitments = queryService.findAllCommitments( sharingSend, false, a );
            if ( sharingSend.isAll() ) {
                for ( Commitment commitment : commitments ) {
                    Assignment assignment = commitment.getBeneficiary();
                    ModelEntity assignee = assignment.getKnownAssignee();
                    if ( !assignees.contains( assignee ) )
                        beneficiaries.add( assignee );
                }
            } else {
                Commitment commitment = (Commitment) CollectionUtils.find(
                        commitments,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return !assignees.contains( ( (Commitment) object ).getBeneficiary().getKnownAssignee() );
                            }
                        }
                );
                if ( commitment != null )
                    beneficiaries.add( commitment.getBeneficiary().getKnownAssignee() );
            }
        }
        return beneficiaries.size();
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
        return "Agents assigned to task have too many sharing commitments";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
