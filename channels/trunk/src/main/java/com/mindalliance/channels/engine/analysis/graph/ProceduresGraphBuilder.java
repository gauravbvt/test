package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.engine.analysis.GraphBuilder;
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
import com.mindalliance.channels.engine.query.QueryService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/9/11
 * Time: 12:47 PM
 */
public class ProceduresGraphBuilder implements GraphBuilder<Assignment, Commitment> {

    private Segment segment;
    private final boolean summarizeByOrgType;
    private boolean summarizedByOrg;
    private boolean summarizedByRole;
    private ModelEntity focusEntity;
    private QueryService queryService;

    public ProceduresGraphBuilder(
            Segment segment,
            boolean summarizeByOrgType,
            boolean summarizedByOrg,
            boolean summarizedByRole,
            ModelEntity focusEntity ) {
        this.segment = segment;
        this.summarizeByOrgType = summarizeByOrgType;
        this.summarizedByOrg = summarizedByOrg;
        this.summarizedByRole = summarizedByRole;
        this.focusEntity = focusEntity;
    }

    @Override
    public DirectedGraph<Assignment, Commitment> buildDirectedGraph() {
        DirectedGraph<Assignment, Commitment> digraph = new DirectedMultigraph<Assignment, Commitment>(
                new EdgeFactory<Assignment, Commitment>() {

                    public Commitment createEdge( Assignment sourceAssignment, Assignment targetAssignment ) {
                        InternalFlow flow = new InternalFlow(
                                sourceAssignment.getPart(),
                                targetAssignment.getPart(),
                                "" );
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
        List<Flow> flows = findAllFlows();
        for ( Flow flow : flows ) {
            commitments.addAll( queryService.findAllCommitments( flow, true ) );
        }
        List<Commitment> results = new ArrayList<Commitment>();
        for ( Commitment commitment : commitments ) {
            if ( focusEntity != null ) {
                if ( isFocusedOn( commitment ) ) {
                    results.add( summarize( commitment ) );
                }
            } else {
                results.add( summarize( commitment ) );
            }
        }
        return results;
    }

    private boolean isFocusedOn( Commitment commitment ) {
        return focusEntity != null
                && (
                isFocusedOn( commitment.getCommitter() )
                        || isFocusedOn( commitment.getBeneficiary() )
        );
    }

    private boolean isFocusedOn( Assignment assignment ) {
        return isFocusedOnAgent( assignment ) || isFocusedOnOrganization( assignment );
    }

    private boolean isFocusedOnAgent( Assignment assignment ) {
        return focusEntity != null && assignment.getActor().equals( focusEntity );
    }

    private boolean isFocusedOnOrganization( Assignment assignment ) {
        return focusEntity != null
                && assignment.getOrganization().narrowsOrEquals(
                focusEntity,
                getQueryService().getPlan().getLocale() );
    }

    private Commitment summarize( Commitment commitment ) {
        Assignment committer = new Assignment( commitment.getCommitter() );
        Assignment beneficiary = new Assignment( commitment.getBeneficiary() );
        if ( summarizeByOrgType ) {
            if ( !isFocusedOnAgent( committer ) ) {
                Organization org;
                Role role;
                org = isFocusedOnOrganization( committer )
                        ? committer.getOrganization()
                        : getOrganizationType( committer.getPart() );
                role = summarizedByRole
                        ? committer.getRole()
                        : null;
                if ( role != null )
                    committer.setEmployment( new Employment( org, role ) );
                else
                    committer.setEmployment( new Employment( org ) );
            }
            if ( !isFocusedOnAgent( beneficiary ) ) {
                Organization org;
                Role role;
                org = isFocusedOnOrganization( beneficiary )
                        ? beneficiary.getOrganization()
                        : getOrganizationType( beneficiary.getPart() );
                role = summarizedByRole
                        ? beneficiary.getRole()
                        : null;
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
*/        } else if ( summarizedByOrg ) {
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
                // get last one for now -- todo: something samrter?
                return (Organization) types.get( types.size() - 1 );
            }
        }
    }


    private List<Flow> findAllFlows() {
        return getQueryService().findAllSharingFlows( segment );
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

}
