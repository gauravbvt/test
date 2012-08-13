package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
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
public class RequiredNetworkingGraphBuilder implements GraphBuilder<Organization, RequirementRelationship> {

    private final Phase.Timing timing;
    private final Event event;
    private final Organization selectedOrganization;
    private final RequirementRelationship selectedRequirementRel;
    private QueryService queryService;
    private Analyst analyst;

    public RequiredNetworkingGraphBuilder(
            Phase.Timing timing,
            Event event,
            Organization selectedOrganization,
            RequirementRelationship selectedRequirementRel ) {
        this.timing = timing;
        this.event = event;
        this.selectedOrganization = selectedOrganization;
        this.selectedRequirementRel = selectedRequirementRel;
    }

    @Override
    public DirectedGraph<Organization, RequirementRelationship> buildDirectedGraph() {
        DirectedGraph<Organization, RequirementRelationship> digraph =
                new DirectedMultigraph<Organization, RequirementRelationship>(
                        new EdgeFactory<Organization, RequirementRelationship>() {

                            @Override
                            public RequirementRelationship createEdge( Organization fromOrg, Organization toOrg ) {
                                return new RequirementRelationship( fromOrg, toOrg, timing, event );
                            }
                        } );
        populateRequiredNetworkingGraph( digraph );
        return digraph;
    }

    @SuppressWarnings( "unchecked" )
    private void populateRequiredNetworkingGraph( DirectedGraph<Organization, RequirementRelationship> graph ) {
        if ( selectedOrganization == null && selectedRequirementRel == null ) {
            for ( Organization org : queryService.listActualEntities( Organization.class ) ) {
                if ( !org.isUnknown() && queryService.isReferenced( org ) )
                    graph.addVertex( org );
            }
        }
        List<RequirementRelationship> reqRels = new ArrayList<RequirementRelationship>();
        if ( selectedRequirementRel != null ) {
            reqRels.add( selectedRequirementRel );
        } else {
            reqRels.addAll( (List<RequirementRelationship>) CollectionUtils.select(
                    analyst.findRequirementRelationships( timing, event, queryService ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            RequirementRelationship reqRel = (RequirementRelationship) object;
                            return selectedOrganization == null
                                || reqRel.getToIdentifiable() == selectedOrganization.getId()
                                || reqRel.getFromIdentifiable() == selectedOrganization.getId();
                        }
                    } ) );
        }
        for ( RequirementRelationship reqRel : reqRels ) {
            Organization fromOrg = reqRel.getFromIdentifiable( queryService );
            Organization toOrg = reqRel.getToIdentifiable( queryService );
            graph.addVertex( fromOrg );
            graph.addVertex( toOrg );
            graph.addEdge(
                    reqRel.getFromIdentifiable( queryService ),
                    reqRel.getToIdentifiable( queryService ),
                    reqRel );
        }
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }
}
