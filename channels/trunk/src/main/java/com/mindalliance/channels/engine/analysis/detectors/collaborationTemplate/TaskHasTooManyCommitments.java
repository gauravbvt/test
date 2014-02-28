/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Agents who are neither systems nor archetypes and who are assigned to a task have commitments with too many other
 * agents.
 */
public class TaskHasTooManyCommitments extends AbstractIssueDetector {

    private static final int TOO_MANY = 10;

    public TaskHasTooManyCommitments() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        int n;
        // Send commitments
        List<Assignment> assignments = queryService.findAllAssignments( part, false );
        if ( !assignments.isEmpty() ) {
            if ( !assignments.isEmpty() && !areAllSystemsOrArchetypes( assignments )
                 && ( n = countDifferentBeneficiaries( part, assignments, queryService ) ) > TOO_MANY )
            {
                Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, part );
                issue.setDescription( "Agents executing task \"" + part.getTitle()
                                      + "\" would have communication commitments with too many different agents (" + n
                                      + ")." );
                issue.setRemediation( "Remove \"send\" flows"
                                      + "\nor add intermediates to spread the outgoing communication load." );
                issue.setSeverity( computeTaskFailureSeverity( queryService, part ) );
                issues.add( issue );
            }
        }
        return issues;
    }

    // No assignee is neither a system nor an "archetype".
    private static boolean areAllSystemsOrArchetypes( List<Assignment> assignments ) {
        return !CollectionUtils.exists( assignments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                Assignment assignment = (Assignment) object;
                ModelEntity entity = assignment.getKnownAssignee();
                return entity instanceof Organization ||
                        entity instanceof Actor
                                && (!( (Actor) entity ).isSystem()
                                                         || ( (Actor) entity ).isSingularParticipation() );
            }
        } );
    }

    @SuppressWarnings( "unchecked" )
    private static int countDifferentBeneficiaries( Part part, List<Assignment> assignments, QueryService queryService ) {
        final List<ModelEntity> assignees =
                (List<ModelEntity>) CollectionUtils.collect( assignments, new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (Assignment) input ).getKnownAssignee();
                    }
                } );
        Set<ModelEntity> beneficiaries = new HashSet<ModelEntity>();
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
                Commitment commitment = (Commitment) CollectionUtils.find( commitments, new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !assignees.contains( ( (Commitment) object ).getBeneficiary().getKnownAssignee() );
                    }
                } );
                if ( commitment != null )
                    beneficiaries.add( commitment.getBeneficiary().getKnownAssignee() );
            }
        }
        return beneficiaries.size();
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
        return "Agents assigned to task have too many communication commitments";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
