package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import org.jgrapht.Graph;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Scenario DOT exporter.
 * Exports a Graph in DOT format.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 4:15:11 PM
 */
public class FlowMapDOTExporter extends AbstractDOTExporter<Node, Flow> {

    public FlowMapDOTExporter( MetaProvider<Node, Flow> metaProvider ) {
        super(metaProvider);
    }
    /** {@inheritDoc} */
    protected void exportVertices( PrintWriter out, Graph<Node, Flow> g ) {
        MetaProvider<Node,Flow> metaProvider = getMetaProvider();
        Map<Scenario, Set<Node>> scenarioNodes = new HashMap<Scenario, Set<Node>>();
        for ( Node node : g.vertexSet() ) {
            Scenario scenario = node.getScenario();
            Set<Node> nodesInScenario = scenarioNodes.get( scenario );
            if ( nodesInScenario == null ) {
                nodesInScenario = new HashSet<Node>();
                scenarioNodes.put( scenario, nodesInScenario );
            }
            nodesInScenario.add( node );
        }
        for ( Scenario scenario : scenarioNodes.keySet() ) {
            if ( scenario != metaProvider.getContext() ) {
                out.println( "subgraph cluster_"
                        + scenario.getName().replaceAll( "[^a-zA-Z0-9_]", "_" )
                        + " {" );
                List<DOTAttribute> attributes = new DOTAttribute( "label",
                        "Scenario: " + scenario.getName() ).asList();
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll(
                            metaProvider.getDOTAttributeProvider().getSubgraphAttributes() );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().
                            getGraphURL( scenarioNodes.get( scenario ).iterator().next() );
                    if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutVertices( out, scenarioNodes.get( scenario ) );
                out.println( "}" );
            } else {
                printoutVertices( out, scenarioNodes.get( scenario ) );
            }
        }
    }

}
