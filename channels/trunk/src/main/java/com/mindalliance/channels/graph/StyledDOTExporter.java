package com.mindalliance.channels.graph;

import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.Graph;
import org.jgrapht.DirectedGraph;

import java.io.Writer;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 4:15:11 PM
 *
 * @param <V> a vertx class
 * @param <E> an edge class
 * Exports a Graph in DOT format
 */
public class StyledDOTExporter<V, E> {
    /**
     * Indentation
     */
    private static String INDENT = "    ";
    /**
     * Vertices to highlight
     */
    private Set<V> highlightedVertices;
    /**
     * Edges to highlight
     */
    private Set<E> highlightedEdges;
    /**
     * A vertex ID provider
     */
    private VertexNameProvider<V> vertexIDProvider;
    /**
     * A vertex label provider
     */
    private VertexNameProvider<V> vertexLabelProvider;
    /**
     * An edge label provider
     */
    private EdgeNameProvider<E> edgeLabelProvider;
    /**
     * A DOT attribute provider
     */
    private DOTAttributeProvider<V, E> attributeProvider;
    /**
     * A URL provider
     */
    private URLProvider<V, E> urlProvider;

    public StyledDOTExporter() {
        this( new IntegerNameProvider<V>(), null, null, null, null );
    }


    public StyledDOTExporter( VertexNameProvider<V> vertexIDProvider,
                              VertexNameProvider<V> vertexLabelProvider,
                              EdgeNameProvider<E> edgeLabelProvider,
                              DOTAttributeProvider<V, E> attributeProvider,
                              URLProvider<V, E> urlProvider ) {
        this.vertexIDProvider = vertexIDProvider;
        this.vertexLabelProvider = vertexLabelProvider;
        this.edgeLabelProvider = edgeLabelProvider;
        this.attributeProvider = attributeProvider;
        this.urlProvider = urlProvider;
    }

    public void setHighlightedVertices( Set<V> highlightedVertices ) {
        this.highlightedVertices = highlightedVertices;
    }

    public void setHighlightedEdges( Set<E> highlightedEdges ) {
        this.highlightedEdges = highlightedEdges;
    }

    /**
     * Writes a Graph in DOT format
     *
     * @param writer -- where to export
     * @param g      -- the graph being exported
     */
    public void export( Writer writer, Graph<V, E> g ) {
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
        if ( attributeProvider != null ) {
            out.print( asGraphAttributes( attributeProvider.getGraphAttributes() ) );
        }
        out.println();
        exportVertices( out, g );

        // Edges
        exportEdges( out, g, connector );
        // Close graph
        out.println( "}" );
        out.flush();
    }

    private void exportEdges( PrintWriter out, Graph<V, E> g, String connector ) {
        for ( E e : g.edgeSet() ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            if ( edgeLabelProvider != null ) {
                String label = edgeLabelProvider.getEdgeName( e );
                attributes.add( new DOTAttribute( "label", label ) );
            }
            if ( attributeProvider != null ) {
                attributes.addAll( attributeProvider.getEdgeAttributes( e,
                        highlightedEdges.contains( e ) ) );
            }
            if ( urlProvider != null ) {
                String url = urlProvider.getEdgeURL( e );
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

    private void exportVertices( PrintWriter out, Graph<V, E> g ) {
        // Vertices
        for ( V v : g.vertexSet() ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            if ( vertexLabelProvider != null ) {
                String label = vertexLabelProvider.getVertexName( v );
                attributes.add( new DOTAttribute( "label", label ) );
            }
            if ( attributeProvider != null ) {
                attributes.addAll( attributeProvider.getVertexAttributes( v,
                        highlightedVertices.contains( v ) ) );
            }
            if ( urlProvider != null ) {
                String url = urlProvider.getVertexURL( v );
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
    private String getVertexID( V v ) {
        return vertexIDProvider.getVertexName( v );
    }


}
