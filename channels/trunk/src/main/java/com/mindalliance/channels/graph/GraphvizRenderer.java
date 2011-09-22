/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph;

import com.mindalliance.channels.core.query.QueryService;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
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

    /**
     * The formats to generate.
     */
    private static final List<String> FORMATS = Arrays.asList( DiagramFactory.IMAGE_MAP, DiagramFactory.PNG );

    /**
     * Where temporary files are created.
     */
    private Resource tempDir = new FileSystemResource( "/tmp/channels-graphs" );

    /**
     * The path to the dot executable.
     */
    private String dotPath = "/usr/bin/";

    /**
     * Drawing algorithm (neato, dot...).
     */
    private String algo = "dot";

    /**
     * Maximum time allocated to graphviz process before it is interrupted and forcibly terminated.
     */
    private long timeout = 60000L;

    /**
     * The vertices to highlight.
     */
    private Set<V> highlightedVertices;

    /**
     * The edges to highlight.
     */
    private Set<E> highlightedEdges;

    /**
     * Max attempts at rendering.
     */
    private static final int MAX_ATTEMPTS = 5;

    public GraphvizRenderer() {
        resetHighlight();
    }

    public String getDotPath() {
        return dotPath;
    }

    public void setDotPath( String path ) {
        dotPath = path;
    }

    @Override
    public void setAlgo( String algo ) {
        this.algo = algo;
    }

    public void setTimeout( long timeout ) {
        this.timeout = timeout;
    }

    /**
     * Highlights a vertex.
     *
     * @param vertex -- the vertex to highlight
     */
    @Override
    public void highlightVertex( V vertex ) {
        highlightedVertices.add( vertex );
    }

    /**
     * Highlights an edge.
     *
     * @param edge -- the edge to highlight
     */
    @Override
    public void highlightEdge( E edge ) {
        highlightedEdges.add( edge );
    }

    /**
     * Removes all highlights.
     */
    @Override
    public void resetHighlight() {
        highlightedVertices = new HashSet<V>();
        highlightedEdges = new HashSet<E>();
    }

    @Override
    public GraphRenderer<V, E> cloneSelf() {
        GraphvizRenderer<V, E> gr = new GraphvizRenderer<V, E>();
        gr.setAlgo( algo );
        gr.setDotPath( dotPath );
        gr.setTimeout( timeout );
        return gr;
    }

    @Override
    public void render( QueryService queryService, Graph<V, E> graph, StyledDOTExporter<V, E> dotExporter, String format, String ticket,
                        OutputStream output ) throws DiagramException {
        assert ticket != null;
        dotExporter.setHighlightedVertices( highlightedVertices );
        dotExporter.setHighlightedEdges( highlightedEdges );
        // System.out.println( dot );
        boolean success = false;
        int attempts = 0;
        String name = sanitize( ticket );
        while ( !success && attempts < MAX_ATTEMPTS ) {
            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                // try to get it from file
                success = loadFromFile( name, format, baos );
                // if not there, generate it to file and load from it
                if ( !success ) {
                    String dot = getDOT( queryService, graph, dotExporter );
                    baos = new ByteArrayOutputStream();
                    doRender( dot, name, format, baos );
                    success = loadFromFile( name, format, baos );
                }
                if ( success )
                    baos.writeTo( output );
            } catch ( IOException e ) {
                attempts++;
            } catch ( InterruptedException e ) {
                attempts++;
            } finally {
                if ( baos != null ) {
                    try {
                        baos.flush();
                        baos.close();
                    } catch ( IOException e ) {
                        LOG.warn( "Error closing ", e );
                    }
                }
            }
        }
        if ( !success )
            throw new DiagramException( "Diagram generation failed" );
    }

    private boolean loadFromFile( String name, String format, ByteArrayOutputStream baos ) {
        BufferedInputStream in = null;
        File file = null;
        try {
            file = makeFile( name, format );
            if ( file.exists() ) {
                if ( file.length() > Integer.MAX_VALUE ) {
                    throw new DiagramException( "Diagram is too large" );
                }
                LOG.debug( "Reading " + format + " from " + file.getAbsolutePath() );
                in = new BufferedInputStream( new FileInputStream( file ) );
                int available;
                while ( ( available = in.available() ) > 0 ) {
                    byte[] bytes = new byte[available];
                    int n = in.read( bytes );
                    assert n == available;
                    if ( n > 0 )
                        baos.write( bytes, 0, n );
                }
                return true;
            } else {
                return false;
            }
        } catch ( IOException e ) {
            return false;
        } finally {
            try {
                baos.flush();
                baos.close();
                if ( in != null )
                    in.close();
                if ( file != null && file.exists() ) {
                    boolean deleted = file.delete();
                    if ( deleted )
                        LOG.debug( "Deleted diagram file" + file.getAbsolutePath() );
                    else
                        LOG.warn( "Failed to delete diagram file" + file.getAbsolutePath() );
                }
            } catch ( IOException e ) {
                LOG.error( "Failed to finalize loading diagram from file", e );
            }
        }
    }

    private File makeFile( String name, String format ) throws IOException {
        File file = tempDir.getFile();
        if ( !file.isDirectory() ) {
            boolean success = file.mkdir();
            if ( !success )
                throw new DiagramException( "Failed to create temp directory " + file.getAbsolutePath() );
        }
        return new File( file, name + '.' + format );
    }

    /**
     * Renders a graph specified in DOT in a given format.
     *
     * @param dot Graph description in DOT language
     * @param name a file name without extension
     * @param format a Grpahviz output format ("png", "svg", "imap" etc.)
     * @param output the rendered graph
     * @throws IOException if generation fails
     * @throws InterruptedException if generation fails
     */
    private void doRender( String dot, String name, String format, OutputStream output )
            throws IOException, InterruptedException {
        assert FORMATS.contains( format );
        String command = dotPath + System.getProperty( "file.separator" ) + algo + " -Gcharset=latin1"
                         + getFormatAndOutputParameters( name );
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
            int maxbytes = 1024;
            BufferedInputStream in = new BufferedInputStream( p.getInputStream(), maxbytes );
            int count;
            do {
                byte[] bytes = new byte[maxbytes];
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
            LOG.error( "Diagram generation failed on IO", e );
            throw e;
        } catch ( InterruptedException e ) {
            p.destroy();
            LOG.error( "Diagram generation interrupted" );
            throw e;
        } finally {
            // Stop the timer
            timer.cancel();
        }
    }

    private String getFormatAndOutputParameters( String name ) throws IOException {
        String fileSep = System.getProperty( "file.separator" );
        StringBuilder sb = new StringBuilder();
        for ( String format : FORMATS ) {
            sb.append( " -T" );
            sb.append( format );
            sb.append( " -o" );
            sb.append( tempDir.getFile().getAbsolutePath() );
            sb.append( fileSep );
            sb.append( name );
            sb.append( '.' );
            sb.append( format );
        }
        return sb.toString();
    }

    private static String sanitize( String ticket ) {
        return ticket.replaceAll( "\\W", "_" );
    }

    /**
     * Produces a description of a graph in DOT format.
     *
     * @param queryService a query service
     * @param graph -- the graph to be converted to DOT format
     * @param dotExporter -- a DOT generator
     * @return a String in DOT format
     */
    public String getDOT( QueryService queryService, Graph<V, E> graph, StyledDOTExporter<V, E> dotExporter ) {
        StringWriter writer = new StringWriter();
        dotExporter.export( queryService, writer, graph );
        // System.out.println( writer.toString() );
        return writer.toString();
    }

    @Override
    public void highlight( V vertex, E edge ) {
        resetHighlight();

        if ( vertex != null )
            highlightVertex( vertex );
        if ( edge != null )
            highlightEdge( edge );
    }

    public void setTempDir( Resource tempDir ) {
        LOG.debug( "Setting temp dir to: {}", tempDir );
        this.tempDir = tempDir;
    }

    /**
     * Task that interrupts a threads when run.
     */
    private class InterruptScheduler extends TimerTask {

        /**
         * Thread to interrupt.
         */
        private Thread target;

        private InterruptScheduler( Thread target ) {
            this.target = target;
        }

        @Override
        public void run() {
            target.interrupt();
        }
    }
}
