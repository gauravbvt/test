package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Required networking graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/6/11
 * Time: 3:53 PM
 */
public class RequiredNetworkingGraphBuilder implements GraphBuilder<Agency, RequirementRelationship> {

    private final Phase.Timing timing;
    private final Event event;
    private final Agency selectedAgency;
    private final RequirementRelationship selectedRequirementRel;
    private PlanCommunity planCommunity;

    public RequiredNetworkingGraphBuilder(
            Phase.Timing timing,
            Event event,
            Agency selectedAgency,
            RequirementRelationship selectedRequirementRel ) {
        this.timing = timing;
        this.event = event;
        this.selectedAgency = selectedAgency;
        this.selectedRequirementRel = selectedRequirementRel;
    }

    @Override
    public DirectedGraph<Agency, RequirementRelationship> buildDirectedGraph() {
        DirectedGraph<Agency, RequirementRelationship> digraph =
                new DirectedMultigraph<Agency, RequirementRelationship>(
                        new EdgeFactory<Agency, RequirementRelationship>() {

                            @Override
                            public RequirementRelationship createEdge( Agency fromAgency, Agency toAgency ) {
                                return new RequirementRelationship( fromAgency, toAgency, timing, event );
                            }
                        } );
        populateRequiredNetworkingGraph( digraph );
        return digraph;
    }

    @SuppressWarnings( "unchecked" )
    private void populateRequiredNetworkingGraph( DirectedGraph<Agency, RequirementRelationship> graph ) {
 /*       if ( selectedAgency == null && selectedRequirementRel == null ) {
            for ( Agency agency : planCommunity.getParticipationManager().getAllKnownAgencies( planCommunity ) ) {
                graph.addVertex( agency );
            }
        }
*/
        List<RequirementRelationship> reqRels = new ArrayList<RequirementRelationship>();
        if ( selectedRequirementRel != null ) {
            reqRels.add( selectedRequirementRel );
        } else {
            reqRels.addAll( (List<RequirementRelationship>) CollectionUtils.select(
                    planCommunity.getParticipationAnalyst().findRequirementRelationships( timing, event, planCommunity ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            RequirementRelationship reqRel = (RequirementRelationship) object;
                            return selectedAgency == null
                                || reqRel.getToAgency( planCommunity ).equals( selectedAgency )
                                || reqRel.getToAgency( planCommunity ).equals( selectedAgency );
                        }
                    } ) );
        }
        for ( RequirementRelationship reqRel : reqRels ) {
            Agency fromAgency = reqRel.getFromAgency( planCommunity );
            Agency toAgency = reqRel.getToAgency( planCommunity );
            graph.addVertex( fromAgency );
            graph.addVertex( toAgency );
            graph.addEdge(
                    fromAgency,
                    toAgency,
                    reqRel );
        }
    }

    public void setPlanCommunity( PlanCommunity planCommunity ) {
        this.planCommunity = planCommunity;
    }

}
