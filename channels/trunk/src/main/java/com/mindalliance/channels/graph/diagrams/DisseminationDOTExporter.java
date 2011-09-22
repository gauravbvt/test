/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.model.Dissemination;
import com.mindalliance.channels.engine.query.QueryService;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Segment;
import org.jgrapht.Graph;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DisseminationDOTExporter extends AbstractDOTExporter<Node, Dissemination> {

    public DisseminationDOTExporter( MetaProvider<Node, Dissemination> metaProvider ) {
        super( metaProvider );
    }

    @Override
    protected void exportVertices( QueryService queryService, PrintWriter out, Graph<Node, Dissemination> g ) {
        DisseminationMetaProvider metaProvider = (DisseminationMetaProvider) getMetaProvider();
        Map<Segment, Set<Node>> segmentNodes = new HashMap<Segment, Set<Node>>();
        for ( Node node : g.vertexSet() ) {
            Segment segment = node.getSegment();
            Set<Node> nodesInSegment = segmentNodes.get( segment );
            if ( nodesInSegment == null ) {
                nodesInSegment = new HashSet<Node>();
                segmentNodes.put( segment, nodesInSegment );
            }
            nodesInSegment.add( node );
        }
        for ( Segment segment : segmentNodes.keySet() ) {
            if ( segment.equals( getSegment() ) )
                printoutVertices( queryService, out, segmentNodes.get( segment ) );
            else {
                out.println( "subgraph cluster_" + segment.getName().replaceAll( "[^a-zA-Z0-9_]", "_" ) + " {" );
                List<DOTAttribute> attributes = new DOTAttribute( "label", "Segment: " + segment.getName() ).asList();
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll( metaProvider.getDOTAttributeProvider().getSubgraphAttributes( false ) );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().
                            getGraphURL( segmentNodes.get( segment ).iterator().next() );
                    if ( url != null )
                        attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutVertices( queryService, out, segmentNodes.get( segment ) );
                out.println( "}" );
            }
        }
    }

    private Segment getSegment() {
        return (Segment) getMetaProvider().getContext();
    }
}
