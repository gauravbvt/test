package com.mindalliance.channels.graph;

import org.jgrapht.Graph;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.BufferedInputStream;
import java.io.StringWriter;
import java.io.IOException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 9:56:53 AM
 * <p/>
 * Renders a dot-formatted diagram specification using graphviz.
 *
 * @param <E> A class for edges
 * @param <V> A Class for vertices
 */
public class GraphvizRenderer<V, E> implements GraphRenderer<V, E> {
    /**
     * The path to the dot executable
     */
    private String dotPath = "/usr/bin/";

    /**
     * Drawing algorithm (neato, dot...)
     */
    private String algo = "neato";

   /**
     * Maximum time allocated to graphviz process before it is interrupted and forcibly terminated.
     */
    private long timeout = Long.MAX_VALUE;
    /**
     * The vertices to highlight
     */
    private Set<V> highlightedVertices;
    /**
     * The edges to highlight
     */
    private Set<E> highlightedEdges;

    public GraphvizRenderer() {
        resetHighlight();
    }

    public String getDotPath() {
        return dotPath;
    }

    public void setDotPath( String path ) {
        dotPath = path;
    }

    public void setAlgo( String algo ) {
        this.algo = algo;
    }

    public void setTimeout( long timeout ) {
        this.timeout = timeout;
    }

     /**
     * Highlights a vertex
     *
     * @param vertex -- the vertex to highlight
     */
    public void highlightVertex( V vertex ) {
        highlightedVertices.add( vertex );
    }

    /**
     * Highlights an edge
     *
     * @param edge -- the edge to highlight
     */
    public void highlightEdge( E edge ) {
        highlightedEdges.add( edge );
    }

    /**
     * Removes all highlights
     */
    public void resetHighlight() {
        highlightedVertices = new HashSet<V>();
        highlightedEdges = new HashSet<E>();
    }

    /**
     * @param graph                -- a Graph
     * @param vertexIDProvider     -- a vertex ID provider
     * @param vertexLabelProvider  -- a vertex label provider
     * @param edgeLabelProvider    -- an edge lable provider
     * @param dotAttributeProvider -- a DOT attribute provider
     * @param urlProvider          -- a URL provider
     * @param format               -- an output format (png, svg, imap etc.)
     * @return an InputStream on the rendered graph
     * @throws DiagramException -- if generation fails
     */
    public InputStream render( Graph<V, E> graph,
                               VertexNameProvider<V> vertexIDProvider,
                               VertexNameProvider<V> vertexLabelProvider,
                               EdgeNameProvider<E> edgeLabelProvider,
                               DOTAttributeProvider<V, E> dotAttributeProvider,
                               URLProvider<V, E> urlProvider,
                               String format ) throws DiagramException {
        String dot = getDOT( graph,
                vertexIDProvider,
                vertexLabelProvider,
                edgeLabelProvider,
                dotAttributeProvider,
                urlProvider );
        System.out.println( dot );
        return doRender( dot, format );
    }

    /**
     * Renders a graph specified in DOT in a given format.
     *
     * @param dot    Graph description in DOT language
     * @param format a Grpahviz output format ("png", "svg", "imap" etc.)
     * @return an InputStream with the generated output
     * @throws DiagramException if generation fails
     */
    private InputStream doRender( String dot, String format ) throws DiagramException {
        String command = getDotPath() + "/" + algo + " -T" + format;
        Process p = null;
        int exitValue;
        Timer timer = new Timer();
        try {
            // start process
            p = Runtime.getRuntime().exec( command );
            // send dot string
            OutputStream input = p.getOutputStream();
            StringReader reader = new StringReader( dot );
            int c;
            while ( ( c = reader.read() ) > 0 ) {
                input.write( c );
            }
            input.flush();
            input.close();
            timer.schedule( new InterruptScheduler( Thread.currentThread() ), this.timeout );
            // wait for process to complete (assumes the dot always terminates)
            exitValue = p.waitFor();
            if ( exitValue != 0 ) {
                // grab error if any
                BufferedInputStream error = new BufferedInputStream( p.getErrorStream() );
                StringBuilder buffer = new StringBuilder();
                while ( error.available() != 0 ) {
                    buffer.append( (char) error.read() );
                }
                String errorMessage = buffer.toString().trim();
                throw new DiagramException( errorMessage );
            }
            // return process output stream
            return p.getInputStream();
        } catch ( IOException e ) {
            throw new DiagramException( "Diagram generation failed", e );
        } catch ( InterruptedException e ) {
            p.destroy();
            throw new DiagramException( "Diagram generation failed", e );
        } catch ( DiagramException e ) {
            // TODO -- replace with logging
            System.err.println( e );
            throw e;
        } finally {
            // Stop the timer
            timer.cancel();
        }
    }

    /**
     * Produces a description of a graph in DOT format
     *
     * @param graph                -- the graph to be converted to DOT format
     * @param vertexIDProvider     -- a name provider
     * @param vertexLabelProvider  -- a label provider
     * @param edgeLabelProvider    -- a label provider
     * @param dotAttributeProvider -- a "style" attribute provider
     * @param urlProvider          -- a URL provider
     * @return a String in DOT format
     */
    public String getDOT( Graph<V, E> graph,
                          VertexNameProvider<V> vertexIDProvider,
                          VertexNameProvider<V> vertexLabelProvider,
                          EdgeNameProvider<E> edgeLabelProvider,
                          DOTAttributeProvider<V, E> dotAttributeProvider,
                          URLProvider<V, E> urlProvider ) {
        StyledDOTExporter<V, E> styledDotExporter = new StyledDOTExporter<V, E>( vertexIDProvider,
                vertexLabelProvider,
                edgeLabelProvider,
                dotAttributeProvider,
                urlProvider );
        styledDotExporter.setHighlightedVertices( highlightedVertices );
        styledDotExporter.setHighlightedEdges( highlightedEdges );
        StringWriter writer = new StringWriter();
        styledDotExporter.export( writer, graph );
        return writer.toString();
    }

    /**
     * Task that interrupts a threads when run
     */
    private class InterruptScheduler extends TimerTask {
        /**
         * Thread to interrupt
         */
        private Thread target;

        public InterruptScheduler( Thread target ) {
            this.target = target;
        }

        @Override
        public void run() {
            target.interrupt();
        }

    }

}
