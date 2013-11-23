package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Hierarchy graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 6, 2009
 * Time: 1:27:20 PM
 */
public class HierarchyGraphBuilder implements GraphBuilder<Hierarchical, HierarchyRelationship> {

    /**
     * A community service.
     */
    private CommunityService communityService;
    /**
     * A hierarchical object.
     */
    private Hierarchical hierarchical;

    public HierarchyGraphBuilder(
            Hierarchical hierarchical,
            CommunityService communityService ) {
        this.hierarchical = hierarchical;
        this.communityService = communityService;
    }

    /**
     * {@inheritDoc}
     */
    public DirectedGraph<Hierarchical, HierarchyRelationship> buildDirectedGraph() {
        DirectedGraph<Hierarchical, HierarchyRelationship> digraph =
                new DirectedMultigraph<Hierarchical, HierarchyRelationship>(
                        new EdgeFactory<Hierarchical, HierarchyRelationship>() {

                            public HierarchyRelationship createEdge(
                                    Hierarchical entity,
                                    Hierarchical otherEntity ) {
                                return new HierarchyRelationship( entity, otherEntity );
                            }

                        } );
        for ( Hierarchical root : communityService.getPlanService().findRoots( hierarchical ) ) {
            populateGraph( digraph, root );
            List<Hierarchical> descendants = communityService.getPlanService().findAllDescendants( root );
            for ( Hierarchical descendant : descendants ) {
                populateGraph( digraph, descendant );
            }
        }
        return digraph;
    }

    private void populateGraph( DirectedGraph<Hierarchical, HierarchyRelationship> digraph,
                                Hierarchical hierarchical ) {
        if ( !digraph.containsVertex( hierarchical ) ) {
            digraph.addVertex( hierarchical );
            for ( Hierarchical superior : hierarchical.getSuperiors( communityService.getPlanService() ) ) {
                populateGraph( digraph, superior );
                digraph.addEdge( superior, hierarchical, new HierarchyRelationship( superior, hierarchical ) );
            }
        }
    }

}
