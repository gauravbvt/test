package com.mindalliance.channels.graph;

import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( GraphvizRenderer.class );

    private static int MAXBYTES = 1024;

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
    private long timeout = 30000L;
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
     * @param graph       -- a Graph
     * @param dotExporter - a StyledDOTExporter
     * @param format      -- an output format (png, svg, imap etc.)
     * @param output      the rendered graph
     * @throws DiagramException -- if generation fails
     */
    public void render( Graph<V, E> graph,
                        StyledDOTExporter<V, E> dotExporter,
                        String format,
                        OutputStream output ) throws DiagramException {
        dotExporter.setHighlightedVertices( highlightedVertices );
        dotExporter.setHighlightedEdges( highlightedEdges );
        String dot = getDOT( graph, dotExporter );
        // System.out.println( dot );
        doRender( dot, format, output );
    }

    /**
     * Renders a graph specified in DOT in a given format.
     *
     * @param dot    Graph description in DOT language
     * @param format a Grpahviz output format ("png", "svg", "imap" etc.)
     * @param output the rendered graph
     * @throws DiagramException if generation fails
     */
    private void doRender( String dot, String format,
                           OutputStream output ) throws DiagramException {
        String command = getDotPath()
                + System.getProperty( "file.separator" )
                + algo
                + " -Gcharset=latin1"  
                + " -T" + format;
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
            // will interrupt this thread if external process does not terminate before timeout
            timer.schedule( new InterruptScheduler( Thread.currentThread() ), this.timeout );
            // wait for process to complete
            // transfer from process input stream to output stream
            BufferedInputStream in = new BufferedInputStream( p.getInputStream(), MAXBYTES );
            int count;
            do {
                byte[] bytes = new byte[MAXBYTES];
                count = in.read( bytes );
                if ( count > 0 )
                    output.write( bytes, 0, count );
            } while ( count >= 0 );
            exitValue = p.waitFor();
            // Stop the timer
            timer.cancel();
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

            output.flush();
        } catch ( IOException e ) {
            throw new DiagramException( "Diagram generation failed on IO", e );
        } catch ( InterruptedException e ) {
            p.destroy();
            throw new DiagramException( "Diagram generation interrupted", e );
        } catch ( DiagramException e ) {
            LOG.error( "Diagram generation failed", e );
            throw e;
        } finally {
            // Stop the timer
            timer.cancel();
        }
    }

    /**
     * Produces a description of a graph in DOT format
     *
     * @param graph       -- the graph to be converted to DOT format
     * @param dotExporter -- a DOT generator
     * @return a String in DOT format
     */
    public String getDOT( Graph<V, E> graph,
                          StyledDOTExporter<V, E> dotExporter ) {
        StringWriter writer = new StringWriter();
        dotExporter.export( writer, graph );
        // System.out.println( writer.toString() );
        return writer.toString();
    }

    /** {@inheritDoc} */
    public void highlight( V vertex, E edge ) {
        resetHighlight();

        if ( vertex != null )
            highlightVertex( vertex );
        if ( edge != null )
            highlightEdge( edge );
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
