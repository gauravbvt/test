package com.mindalliance.channels.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Scenario;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 4:15:11 PM
 * <p/>
 * Exports a Graph in DOT format
 */
public class ScenarioDOTExporter implements StyledDOTExporter<Node, Flow> {
    /**
     * Indentation
     */
    private static String INDENT = "    ";
    /**
     * Vertices to highlight
     */
    private Set<Node> highlightedVertices;
    /**
     * Edges to highlight
     */
    private Set<Flow> highlightedEdges;
    /**
     * A provider of providers
     */
    private MetaProvider<Node, Flow> metaProvider;


    public ScenarioDOTExporter( MetaProvider<Node, Flow> metaProvider ) {
        this.metaProvider = metaProvider;
    }

    public void setHighlightedVertices( Set<Node> highlightedVertices ) {
        this.highlightedVertices = highlightedVertices;
    }

    public void setHighlightedEdges( Set<Flow> highlightedEdges ) {
        this.highlightedEdges = highlightedEdges;
    }

    /**
     * Writes a Graph in DOT format
     *
     * @param writer -- where to export
     * @param g      -- the graph being exported
     */
    public void export( Writer writer, Graph<Node, Flow> g ) {
        PrintWriter out = new PrintWriter( writer );
        String connector;
        // Graph declaration
        if ( g instanceof DirectedGraph ) {
            connector = " -> ";
            out.println( "digraph G {" );
        } else {
            connector = " -- ";
            out.println( "graph G {" );
        }
        if ( metaProvider.getDOTAttributeProvider() != null ) {
            out.print( asGraphAttributes( metaProvider.getDOTAttributeProvider().getGraphAttributes() ) );
        }
        out.println();
        exportVertices( out, g );

        // Edges
        exportEdges( out, g, connector );
        // Close graph
        out.println( "}" );
        out.flush();
    }

    private void exportVertices( PrintWriter out, Graph<Node, Flow> g ) {
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
                out.println( "subgraph cluster_" + scenario.getName().replaceAll( "\\s+", "_" ) + " {" );
                List<DOTAttribute> attributes = new DOTAttribute( "label", scenario.getName() ).asList();
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll( metaProvider.getDOTAttributeProvider().getSubgraphAttributes() );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().
                            getGraphURL( scenarioNodes.get( scenario ).iterator().next() );
                    if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutNodes( out, scenarioNodes.get( scenario ) );
                out.println( "}" );
            } else {
                printoutNodes( out, scenarioNodes.get( scenario ) );
            }
        }
    }

    private void printoutNodes( PrintWriter out, Set<Node> nodes ) {

        // Vertices
        for ( Node v : nodes ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            if ( metaProvider.getVertexLabelProvider() != null ) {
                String label = metaProvider.getVertexLabelProvider().getVertexName( v );
                attributes.add( new DOTAttribute( "label", label ) );
            }
            if ( metaProvider.getDOTAttributeProvider() != null ) {
                attributes.addAll( metaProvider.getDOTAttributeProvider().getVertexAttributes( v,
                        highlightedVertices.contains( v ) ) );
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
    }

    private void exportEdges( PrintWriter out, Graph<Node, Flow> g, String connector ) {
        for ( Flow e : g.edgeSet() ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            if ( metaProvider.getEdgeLabelProvider() != null ) {
                String label = metaProvider.getEdgeLabelProvider().getEdgeName( e );
                attributes.add( new DOTAttribute( "label", label ) );
            }
            if ( metaProvider.getDOTAttributeProvider() != null ) {
                attributes.addAll( metaProvider.getDOTAttributeProvider().getEdgeAttributes( e,
                        highlightedEdges.contains( e ) ) );
            }
            if ( metaProvider.getURLProvider() != null ) {
                String url = metaProvider.getURLProvider().getEdgeURL( e );
                if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
            }
            String source = getVertexID( g.getEdgeSource( e ) );
            String target = getVertexID( g.getEdgeTarget( e ) );
            out.print( INDENT + source + connector + target );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    private String asGraphAttributes( List<DOTAttribute> attributes ) {
        StringBuilder sb = new StringBuilder();
        for ( DOTAttribute attribute : attributes ) {
            sb.append( attribute.toString() );
            sb.append( ";\n" );
        }
        return sb.toString();
    }

    private String asElementAttributes( List<DOTAttribute> attributes ) {
        StringBuilder sb = new StringBuilder();
        for ( DOTAttribute attribute : attributes ) {
            sb.append( attribute.toString() );
            sb.append( "," );
        }
        return sb.toString();
    }

    // Assumes vertex name is DOT-compliant
    private String getVertexID( Node v ) {
        return metaProvider.getVertexIDProvider().getVertexName( v );
    }


}
