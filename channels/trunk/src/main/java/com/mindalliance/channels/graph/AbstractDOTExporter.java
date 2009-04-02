package com.mindalliance.channels.graph;

import java.util.Set;
import java.util.List;
import java.io.Writer;
import java.io.PrintWriter;

import org.jgrapht.Graph;
import org.jgrapht.DirectedGraph;

/**
 * Abstract DOT exporter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 9:38:09 PM
 */
public abstract class AbstractDOTExporter<V,E> implements StyledDOTExporter<V,E> {
    /**
     * Indentation
     */
    protected static String INDENT = "    ";
    /**
     * Vertices to highlight
     */
    private Set<V> highlightedVertices;
    /**
     * Edges to highlight
     */
    private Set<E> highlightedEdges;
    /**
     * A provider of providers
     */
    private MetaProvider<V, E> metaProvider;


    public AbstractDOTExporter( MetaProvider<V, E> metaProvider ) {
        this.metaProvider = metaProvider;
    }

    public void setHighlightedVertices( Set<V> highlightedVertices ) {
        this.highlightedVertices = highlightedVertices;
    }

    public void setHighlightedEdges( Set<E> highlightedEdges ) {
        this.highlightedEdges = highlightedEdges;
    }

    public Set<V> getHighlightedVertices() {
        return highlightedVertices;
    }

    public Set<E> getHighlightedEdges() {
        return highlightedEdges;
    }

    public MetaProvider<V, E> getMetaProvider() {
        return metaProvider;
    }

    /**
     * Writes a Graph in DOT format
     *
     * @param writer -- where to export
     * @param g      -- the graph being exported
     */
    public void export( Writer writer, Graph<V, E> g ) {
        PrintWriter out = new PrintWriter( writer );
        String arrow;
        // Graph declaration
        if ( g instanceof DirectedGraph ) {
            arrow = " -> ";
            out.println( "digraph G {" );
        } else {
            arrow = " -- ";
            out.println( "graph G {" );
        }
        if ( metaProvider.getDOTAttributeProvider() != null ) {
            out.print( asGraphAttributes(
                    metaProvider.getDOTAttributeProvider().getGraphAttributes() ) );
        }
        out.println();
        exportVertices( out, g );

        // Edges
        exportEdges( out, g, arrow );
        // Close graph
        out.println( "}" );
        out.flush();
    }

    /**
     * Export vertices to DOT.
     * @param out a print writer
     * @param g a graph
     */
    protected void exportVertices( PrintWriter out, Graph<V, E> g ) {
        printoutVertices( out, g.vertexSet() );
    }

    /**
     * Export edges to DOT.
     * @param out a print writer
     * @param g a graph
     * @param arrow the arrow string
     */
    protected void exportEdges( PrintWriter out, Graph<V, E> g, String arrow ) {
        MetaProvider<V,E> metaProvider = getMetaProvider();
        for ( E e : g.edgeSet() ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            if ( metaProvider.getEdgeLabelProvider() != null ) {
                String label = metaProvider.getEdgeLabelProvider().getEdgeName( e );
                attributes.add( new DOTAttribute( "label", label ) );
            }
            if ( metaProvider.getDOTAttributeProvider() != null ) {
                attributes.addAll( metaProvider.getDOTAttributeProvider().getEdgeAttributes( e,
                        getHighlightedEdges().contains( e ) ) );
            }
            if ( metaProvider.getURLProvider() != null ) {
                String url = metaProvider.getURLProvider().getEdgeURL( e );
                if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
            }
            String source = getVertexID( g.getEdgeSource( e ) );
            String target = getVertexID( g.getEdgeTarget( e ) );
            out.print( INDENT + source + arrow + target );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    /**
     * Printout vertices.
     * @param out a print writer
     * @param vertices a set of vertices
     */
    protected void printoutVertices( PrintWriter out, Set<V> vertices ) {
        MetaProvider<V, E> metaProvider = getMetaProvider();
        // Vertices
        for ( V v : vertices ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            if ( metaProvider.getVertexLabelProvider() != null ) {
                String label = metaProvider.getVertexLabelProvider().getVertexName( v );
                attributes.add( new DOTAttribute( "label", label ) );
            }
            if ( metaProvider.getDOTAttributeProvider() != null ) {
                attributes.addAll( metaProvider.getDOTAttributeProvider().getVertexAttributes( v,
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
    }

    /**
     * Convert graph attributes to string.
     * @param attributes DOT attributes
     * @return a string
     */
    protected String asGraphAttributes( List<DOTAttribute> attributes ) {
        StringBuilder sb = new StringBuilder();
        for ( DOTAttribute attribute : attributes ) {
            sb.append( attribute.toString() );
            sb.append( ";\n" );
        }
        return sb.toString();
    }

    /**
     * Convert element attributes to a string.
     * @param attributes dot attributes
     * @return a string
     */
    protected String asElementAttributes( List<DOTAttribute> attributes ) {
        StringBuilder sb = new StringBuilder();
        for ( DOTAttribute attribute : attributes ) {
            sb.append( attribute.toString() );
            sb.append( "," );
        }
        return sb.toString();
    }

    // Assumes vertex name is DOT-compliant
    protected String getVertexID( V v ) {
        return getMetaProvider().getVertexIDProvider().getVertexName( v );
    }



}
