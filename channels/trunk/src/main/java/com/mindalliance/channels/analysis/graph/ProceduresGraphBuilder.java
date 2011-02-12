package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.analysis.GraphBuilder;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private List<Commitment> findCommitments() {
        List<Commitment> commitments = new ArrayList<Commitment>();
        Set<Flow> flows = findAllFlows();
        for ( Flow flow : flows ) {
            List<Commitment> flowCommitments = new ArrayList<Commitment>( queryService.findAllCommitments( flow, true ) );
            for ( Commitment commitment : flowCommitments ) {
                if ( focusEntity != null ) {
                    if ( isFocusedOn( commitment ) ) {
                        commitments.add( summarize( commitment ) );
                    }
                } else {
                    commitments.add( summarize( commitment ) );
                }
            }
        }
        return commitments;
    }

    private boolean isFocusedOn( Commitment commitment ) {
        return focusEntity != null
                && (
                isFocusedOn( commitment.getCommitter())
                        || isFocusedOn( commitment.getBeneficiary() )
        );
    }

    private boolean isFocusedOn( Assignment assignment ) {
        return focusEntity != null
                && ( assignment.getActor().equals( focusEntity )
                        || assignment.getOrganization().equals( focusEntity )
                );
    }

    private boolean isFocusedOnAgent( Assignment assignment ) {
         return focusEntity != null &&  assignment.getActor().equals( focusEntity );
    }

    private Commitment summarize( Commitment commitment ) {
        Assignment committer = new Assignment( commitment.getCommitter() );
        Assignment beneficiary = new Assignment( commitment.getBeneficiary() );
        if ( summarizedByOrg ) {
            if (!isFocusedOnAgent( committer ))
                committer.setEmployment( new Employment( committer.getOrganization() ) );
            if (!isFocusedOnAgent( beneficiary ))
                beneficiary.setEmployment( new Employment( beneficiary.getOrganization() ) );
        } else if ( summarizedByRole ) {
            if (!isFocusedOnAgent( committer ))
                committer.setEmployment( new Employment( committer.getOrganization(), committer.getRole() ) );
            if (!isFocusedOnAgent( beneficiary ))
                beneficiary.setEmployment( new Employment( beneficiary.getOrganization(), beneficiary.getRole() ) );
        }
        return new Commitment( committer, beneficiary, commitment.getSharing() );
    }


    private Set<Flow> findAllFlows() {
        Set<Flow> flows = new HashSet<Flow>();
        List<Segment> segments = new ArrayList<Segment>();
        if ( segment == null ) {
            segments.addAll( getQueryService().getPlan().getSegments() );
        } else {
            segments.add( segment );
        }
        for ( Segment seg : segments ) {
            flows.addAll( seg.getAllSharingFlows() );
        }
        return flows;
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

}
