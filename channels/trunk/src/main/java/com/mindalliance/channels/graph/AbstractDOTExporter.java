/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph;

import com.mindalliance.channels.core.community.CommunityService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Set;

/**
 * Abstract DOT exporter.
 */
public abstract class AbstractDOTExporter<V, E> implements StyledDOTExporter<V, E> {

    /**
     * Indentation.
     */
    protected static final String INDENT = "    ";

    /**
     * Edges to highlight.
     */
    private Set<E> highlightedEdges;

    /**
     * Vertices to highlight.
     */
    private Set<V> highlightedVertices;

    /**
     * A provider of providers.
     */
    private MetaProvider<V, E> metaProvider;

    //-------------------------------
    public AbstractDOTExporter( MetaProvider<V, E> metaProvider ) {
        this.metaProvider = metaProvider;
    }

    //-------------------------------

    /**
     * Convert element attributes to a string.
     *
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

    /**
     * Convert graph attributes to string.
     *
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
     * Pre-processing before exporting.
     *
     * @param communityService a plan community service
     * @param g a graph
     */
    protected void beforeExport( CommunityService communityService, Graph<V, E> g ) {
        // Default is do nothing.
    }

    /**
     * Writes a Graph in DOT format.
     *
     * @param communityService plan community service
     * @param writer -- where to export
     * @param g -- the graph being exported
     */
    public void export( CommunityService communityService, Writer writer, Graph<V, E> g ) throws InterruptedException {
        beforeExport( communityService, g );
        PrintWriter out = new PrintWriter( writer );
        // Graph declaration
        if ( g instanceof DirectedGraph )
            out.println( "digraph G {" );
        else
            out.println( "graph G {" );
        if ( metaProvider.getDOTAttributeProvider() != null )
            out.print( asGraphAttributes( metaProvider.getDOTAttributeProvider().getGraphAttributes() ) );
        out.println();
        exportVertices( communityService, out, g );

        // Edges
        exportEdges( communityService, out, g );
        // Close graph
        out.println( "}" );
        out.flush();
    }

    /**
     * Export edges to DOT.
     *
     * @param communityService a plan community service
     * @param out a print writer
     * @param g a graph
     * @throws InterruptedException an interrupted exception
     */
    protected void exportEdges( CommunityService communityService, PrintWriter out, Graph<V, E> g ) throws InterruptedException {
        MetaProvider<V, E> metaProvider = this.metaProvider;
        String arrow = getArrow( g );
        for ( E e : g.edgeSet() ) {
            Thread.sleep(0);
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
/*
            if ( metaProvider.getEdgeLabelProvider() != null ) {
                String label = metaProvider.getEdgeLabelProvider().getEdgeName( e );
                attributes.add( new DOTAttribute( "label", label ) );
            }
*/
            if ( metaProvider.getDOTAttributeProvider() != null ) {
                attributes.addAll( metaProvider.getDOTAttributeProvider().getEdgeAttributes( communityService,
                                                                                             e,
                                                                                             highlightedEdges.contains(
                                                                                                     e ) ) );
            }
            if ( metaProvider.getURLProvider() != null ) {
                String url = metaProvider.getURLProvider().getEdgeURL( e );
                if ( url != null )
                    attributes.add( new DOTAttribute( "URL", url ) );
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
     * Export vertices to DOT.
     *
     * @param communityService a query service  plan community service
     * @param out a print writer
     * @param g a graph
     */
    protected void exportVertices( CommunityService communityService, PrintWriter out, Graph<V, E> g ) {
        printoutVertices( communityService, out, g.vertexSet() );
    }

    protected String getArrow( Graph<V, E> g ) {
        return g instanceof DirectedGraph ? " -> " : " -- ";
    }

    /**
     * Get indentation.
     *
     * @return a string of spaces
     */
    protected String getIndent() {
        return INDENT;
    }

    // Assumes vertex name is DOT-compliant
    protected String getVertexID( V v ) {
        return metaProvider.getVertexIDProvider().getVertexName( v );
    }

    protected String makeLabel( String s ) {
        return sanitize( AbstractMetaProvider.separate( s, AbstractMetaProvider.LINE_WRAP_SIZE ).replaceAll( "\\|",
                                                                                                             "\\\\n" ) );
    }

    /**
     * Printout vertices.
     *
     * @param communityService a plan community service
     * @param out a print writer
     * @param vertices a set of vertices
     */
    protected void printoutVertices( CommunityService communityService, PrintWriter out, Set<V> vertices ) {
        MetaProvider<V, E> metaProvider = this.metaProvider;
        // Vertices
        for ( V v : vertices ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            if ( metaProvider.getVertexLabelProvider() != null ) {
                String label = metaProvider.getVertexLabelProvider().getVertexName( v );
                attributes.add( new DOTAttribute( "label", label ) );
            }
            if ( metaProvider.getDOTAttributeProvider() != null ) {
                attributes.addAll( metaProvider.getDOTAttributeProvider().getVertexAttributes( communityService,
                                                                                               v,
                                                                                               highlightedVertices.contains(
                                                                                                       v ) ) );
            }
            if ( metaProvider.getURLProvider() != null ) {
                String url = metaProvider.getURLProvider().getVertexURL( v );
                if ( url != null )
                    attributes.add( new DOTAttribute( "URL", url ) );
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
     * Make label safe.
     *
     * @param label a string
     * @return a sanitized string
     */
    public String sanitize( String label ) {
        return label.replaceAll( "\"", "\\\\\"" );
    }

    //-------------------------------
    public Set<E> getHighlightedEdges() {
        return highlightedEdges;
    }

    public void setHighlightedEdges( Set<E> highlightedEdges ) {
        this.highlightedEdges = highlightedEdges;
    }

    public Set<V> getHighlightedVertices() {
        return highlightedVertices;
    }

    public void setHighlightedVertices( Set<V> highlightedVertices ) {
        this.highlightedVertices = highlightedVertices;
    }

    public MetaProvider<V, E> getMetaProvider() {
        return metaProvider;
    }
}
