/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.engine.analysis.graph.HierarchyRelationship;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import com.mindalliance.channels.core.model.Hierarchical;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Hierarchy DOT exporter.
 */
public class HierarchyDOTExporter extends AbstractDOTExporter<Hierarchical, HierarchyRelationship> {
    public HierarchyDOTExporter( MetaProvider<Hierarchical, HierarchyRelationship> metaProvider ) {
        super( metaProvider );
    }

    @Override
    protected void exportVertices( QueryService queryService, PrintWriter out, Graph<Hierarchical, HierarchyRelationship> g ) {
        DirectedGraph<Hierarchical, HierarchyRelationship> digraph =
                (DirectedGraph<Hierarchical, HierarchyRelationship>) g;
        List<Hierarchical> roots = findRoots( digraph );
        printoutRankedVertices( queryService, out, roots );
        List<Hierarchical> ranked = roots;
        List<Hierarchical> placed = new ArrayList<Hierarchical>( ranked );
        while ( !ranked.isEmpty() ) {
            ranked = findAllChildrenOf( ranked, digraph, placed );
            placed.addAll(  ranked );
            printoutRankedVertices( queryService, out, ranked );
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<Hierarchical> findRoots(
            final DirectedGraph<Hierarchical, HierarchyRelationship> digraph ) {
        return (List<Hierarchical>) CollectionUtils.select(
                digraph.vertexSet(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return digraph.inDegreeOf( (Hierarchical) object ) == 0;
                    }
                } );
    }

    @SuppressWarnings( "unchecked" )
    private List<Hierarchical> findAllChildrenOf(
            final List<Hierarchical> ranked,
            DirectedGraph<Hierarchical, HierarchyRelationship> digraph,
            final List<Hierarchical> placed ) {
        return (List<Hierarchical>) CollectionUtils.select(
                digraph.vertexSet(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !placed.contains( object )
                        && !CollectionUtils.intersection( ranked, ( (Hierarchical) object ).getSuperiors() ).isEmpty();
                    }
                } );
    }


    private void printoutRankedVertices( QueryService queryService, PrintWriter out, List<Hierarchical> vertices ) {
        if ( !vertices.isEmpty() ) {
            MetaProvider<Hierarchical, HierarchyRelationship> metaProvider = getMetaProvider();
            out.print( "{ rank=same; " );
            // Vertices
            for ( Hierarchical v : vertices ) {
                List<DOTAttribute> attributes = DOTAttribute.emptyList();
                if ( metaProvider.getVertexLabelProvider() != null ) {
                    String label = metaProvider.getVertexLabelProvider().getVertexName( v );
                    attributes.add( new DOTAttribute( "label", label ) );
                }
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll( metaProvider.getDOTAttributeProvider().getVertexAttributes( queryService,
                                                                                                   v,
                            getHighlightedVertices().contains( v ) ) );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().getVertexURL( v );
                    if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( INDENT + getVertexID( v ) );
                out.print( "[" );
                if ( !attributes.isEmpty() ) {
                    out.print( asElementAttributes( attributes ) );
                }
                out.println( "];" );
            }
            out.print( "}" );
        }
    }

}
