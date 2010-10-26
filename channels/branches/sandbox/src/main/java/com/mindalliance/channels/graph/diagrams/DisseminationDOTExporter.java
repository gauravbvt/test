package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.data.Dissemination;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Segment;
import org.jgrapht.Graph;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 22, 2010
 * Time: 8:50:35 PM
 */
public class DisseminationDOTExporter extends AbstractDOTExporter<Node, Dissemination> {

    public DisseminationDOTExporter( MetaProvider<Node, Dissemination> metaProvider ) {
        super( metaProvider );
    }

    /**
     * {@inheritDoc}
     */
    protected void exportVertices( PrintWriter out, Graph<Node, Dissemination> g ) {
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
            if ( !segment.equals( getSegment() ) ) {
                out.println( "subgraph cluster_"
                        + segment.getName().replaceAll( "[^a-zA-Z0-9_]", "_" )
                        + " {" );
                List<DOTAttribute> attributes = new DOTAttribute( "label",
                        "Segment: " + segment.getName() ).asList();
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll(
                            metaProvider.getDOTAttributeProvider().getSubgraphAttributes( false ) );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().
                            getGraphURL( segmentNodes.get( segment ).iterator().next() );
                    if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutVertices( out, segmentNodes.get( segment ) );
                out.println( "}" );
            } else {
                printoutVertices( out, segmentNodes.get( segment ) );
            }
        }
    }


    private Segment getSegment() {
        return (Segment) getMetaProvider().getContext();
    }



}
