package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.query.QueryService;
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
     * A query service.
     */
    private QueryService queryService;
    /**
     * A hierarchical object.
     */
    private Hierarchical hierarchical;

    public HierarchyGraphBuilder(
            Hierarchical hierarchical,
            QueryService queryService ) {
        this.hierarchical = hierarchical;
        this.queryService = queryService;
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
        for ( Hierarchical root : queryService.findRoots( hierarchical ) ) {
            populateGraph( digraph, root );
            List<Hierarchical> descendants = queryService.findAllDescendants( root );
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
            for ( Hierarchical superior : hierarchical.getSuperiors( queryService ) ) {
                populateGraph( digraph, superior );
                digraph.addEdge( superior, hierarchical, new HierarchyRelationship( superior, hierarchical ) );
            }
        }
    }

}
