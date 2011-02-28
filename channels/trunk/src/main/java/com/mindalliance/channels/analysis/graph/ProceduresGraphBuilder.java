package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.analysis.GraphBuilder;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
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
    private boolean summarizedByOrg;
    private boolean summarizedByRole;
    private ModelEntity focusEntity;
    private QueryService queryService;

    public ProceduresGraphBuilder(
            Segment segment,
            boolean summarizedByOrg,
            boolean summarizedByRole,
            ModelEntity focusEntity ) {
        this.segment = segment;
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
        for ( Commitment commitment :  commitments ) {
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
        if ( summarizedByOrg ) {
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
