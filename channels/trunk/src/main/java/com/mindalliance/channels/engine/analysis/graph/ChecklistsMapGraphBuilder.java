/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InternalFlow;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.List;

public class ChecklistsMapGraphBuilder implements GraphBuilder<Assignment, Commitment> {

    private final Segment segment;

    private final boolean summarizeByOrgType;

    private final boolean summarizedByOrg;

    private final boolean summarizedByRole;

    private final ModelEntity focusEntity;

    private CommunityService communityService;

    public ChecklistsMapGraphBuilder( Segment segment, boolean summarizeByOrgType, boolean summarizedByOrg,
                                      boolean summarizedByRole, ModelEntity focusEntity ) {
        this.segment = segment;
        this.summarizeByOrgType = summarizeByOrgType;
        this.summarizedByOrg = summarizedByOrg;
        this.summarizedByRole = summarizedByRole;
        this.focusEntity = focusEntity;
    }

    @Override
    public DirectedGraph<Assignment, Commitment> buildDirectedGraph() {
        DirectedGraph<Assignment, Commitment> digraph =
                new DirectedMultigraph<Assignment, Commitment>( new EdgeFactory<Assignment, Commitment>() {

                    @Override
                    public Commitment createEdge( Assignment sourceAssignment, Assignment targetAssignment ) {
                        InternalFlow flow =
                                new InternalFlow( sourceAssignment.getPart(), targetAssignment.getPart(), "" );
                        return new Commitment( sourceAssignment, targetAssignment, flow );
                    }
                } );
        populateProceduresGraph( digraph );
        return digraph;
    }

    private void populateProceduresGraph( DirectedGraph<Assignment, Commitment> graph ) {
        List<Commitment> commitments = findCommitments();
        for ( Commitment commitment : commitments ) {
            graph.addVertex( commitment.getCommitter() );
            graph.addVertex( commitment.getBeneficiary() );
            graph.addEdge( commitment.getCommitter(), commitment.getBeneficiary(), commitment );
        }
    }

    public boolean hasCommitments() {
        return !findCommitments().isEmpty();
    }

    private List<Commitment> findCommitments() {
        List<Commitment> commitments = new ArrayList<Commitment>();
        ModelService modelService = communityService.getModelService();
        Assignments allAssignments = modelService.getAssignments();
        for ( Flow flow : findAllFlows() )
            commitments.addAll( communityService.getModelService().findAllCommitments( flow, true, allAssignments ) );

        List<Commitment> results = new ArrayList<Commitment>();
        for ( Commitment commitment : commitments )
            if ( focusEntity == null || isFocusedOn( commitment ) )
                 results.add( summarize( commitment ) );

        return results;
    }

    private boolean isFocusedOn( Commitment commitment ) {
        return focusEntity != null && ( isFocusedOn( commitment.getCommitter() )
                                        || isFocusedOn( commitment.getBeneficiary() ) );
    }

    private boolean isFocusedOn( Assignment assignment ) {
        return isFocusedOnAgent( assignment ) || isFocusedOnOrganization( assignment );
    }

    private boolean isFocusedOnAgent( Assignment assignment ) {
        return focusEntity != null && focusEntity.equals( assignment.getActor() );
    }

    private boolean isFocusedOnOrganization( Assignment assignment ) {
        return focusEntity != null && assignment.getOrganization().narrowsOrEquals( focusEntity, communityService.getModelService().getPlanLocale() );
    }

    private Commitment summarize( Commitment commitment ) {
        Assignment committer = new Assignment( commitment.getCommitter() );
        Assignment beneficiary = new Assignment( commitment.getBeneficiary() );
        if ( summarizeByOrgType ) {
            if ( !isFocusedOnAgent( committer ) ) {
                Organization org = isFocusedOnOrganization( committer ) ?
                                   committer.getOrganization() :
                                   getOrganizationType( committer.getPart() );
                Role role = summarizedByRole ? committer.getRole() : null;
                if ( role != null )
                    committer.setEmployment( new Employment( org, role ) );
                else
                    committer.setEmployment( new Employment( org ) );
            }
            if ( !isFocusedOnAgent( beneficiary ) ) {
                Organization org = isFocusedOnOrganization( beneficiary ) ?
                                   beneficiary.getOrganization() :
                                   getOrganizationType( beneficiary.getPart() );
                Role role = summarizedByRole ? beneficiary.getRole() : null;
                if ( role != null )
                    beneficiary.setEmployment( new Employment( org, role ) );
                else
                    beneficiary.setEmployment( new Employment( org ) );
            }

            /*           if ( !isFocusedOnAgent( beneficiary ) )
                            if ( isFocusedOnOrganization( beneficiary ) )
                                beneficiary.setEmployment( new Employment( beneficiary.getOrganization() ) );
                            else
                                beneficiary.setEmployment( new Employment( beneficiaryOrg ) );
            */
        } else if ( summarizedByOrg ) {
            Organization committerOrg = committer.getOrganization();
            Organization beneficiaryOrg = beneficiary.getOrganization();
            if ( !isFocusedOnAgent( committer ) )
                committer.setEmployment( new Employment( committerOrg ) );
            if ( !isFocusedOnAgent( beneficiary ) )
                beneficiary.setEmployment( new Employment( beneficiaryOrg ) );
        } else if ( summarizedByRole ) {
            if ( !isFocusedOnAgent( committer ) )
                committer.setEmployment( new Employment( committer.getOrganization(), committer.getRole() ) );
            if ( !isFocusedOnAgent( beneficiary ) )
                beneficiary.setEmployment( new Employment( beneficiary.getOrganization(), beneficiary.getRole() ) );
        }
        return new Commitment( committer, beneficiary, commitment.getSharing() );
    }

    private Organization getOrganizationType( Part part ) {
        Organization org = part.getOrganization();
        if ( org == null || org.isUnknown() )
            return Organization.getUniversalTypeFor( Organization.class );
        else if ( org.isType() )
            return org;
        else {
            // choose one
            List<ModelEntity> types = org.getAllTypes();
            if ( types.isEmpty() ) {
                return Organization.getUniversalTypeFor( Organization.class );
            } else {
                // get last one for now -- todo: something smarter?
                return (Organization) types.get( types.size() - 1 );
            }
        }
    }

    private List<Flow> findAllFlows() {
        return communityService.getModelService().findAllSharingFlows( segment );
    }

    public void setCommunityService( CommunityService communityService ) {
        this.communityService = communityService;
    }
}
