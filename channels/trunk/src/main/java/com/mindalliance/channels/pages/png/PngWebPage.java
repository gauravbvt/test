/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Response;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract superclass for all PNG-generating pages.
 */
public abstract class PngWebPage extends AbstractChannelsWebPage {

    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PngWebPage.class );

    private PageParameters parameters;

    private String ticket;

    @SpringBean
    private Analyst analyst;

    @SpringBean
    private DiagramFactory diagramFactory;

    public PngWebPage( PageParameters parameters ) {
        super( parameters );
        this.parameters = parameters;
        ticket = parameters.getString( AbstractDiagramPanel.TICKET_PARM );
    }

    /**
     * Get a diagram factory.
     *
     * @return a diagram factory
     */
    protected DiagramFactory getDiagramFactory() {
        return getChannels().getDiagramFactory();
    }

    private Channels getChannels() {
        return (Channels) getApplication();
    }

    @Override
    public String getMarkupType() {
        return "image/png";
    }

    /**
     * COnvert size parameter.
     *
     * @param s size parameter value
     * @return an array of two double values
     */
    protected double[] convertSize( String s ) {
        String[] sizes = s.split( "," );
        assert sizes.length == 2;
        double[] size = new double[2];
        size[0] = Double.parseDouble( sizes[0] );
        size[1] = Double.parseDouble( sizes[1] );
        return size;
    }

    /**
     * Configure diagram for size and orientation.
     *
     * @param diagram a diagram
     */
    protected void configureDiagram( Diagram diagram ) {
        if ( parameters.containsKey( "size" ) ) {
            double[] size = convertSize( parameters.getString( "size" ) );
            diagram.setDiagramSize( size[0], size[1] );
        }
        if ( parameters.containsKey( "orientation" ) )
            diagram.setOrientation( parameters.getString( "orientation" ) );
    }

    /**
     * Directly render the bytes of this page.
     *
     * @param markupStream ignored
     */
    @Override
    protected void onRender( MarkupStream markupStream ) {
        double[] size = null;
        String orientation = null;
        if ( parameters.containsKey( "size" ) )
            size = convertSize( parameters.getString( "size" ) );

        if ( parameters.containsKey( "orientation" ) )
            orientation = parameters.getString( "orientation" );

        try {
            Diagram diagram = makeDiagram( size, orientation );
            configureDiagram( diagram );
            final Response resp = getWebRequestCycle().getResponse();
            if ( resp instanceof WebResponse )
                setHeaders( (WebResponse) resp );
            LOG.debug( "Rendering PNG" );
            diagram.render( ticket,
                            DiagramFactory.PNG,
                            getResponse().getOutputStream(),
                            analyst,
                            diagramFactory,
                            getQueryService() );
        } catch ( DiagramException e ) {
            LOG.error( "Error while generating diagram", e );
            writeErrorImage( getResponse().getOutputStream() );
        }
    }

    private void writeErrorImage( OutputStream output ) throws DiagramException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try {
             File file = new File( getImagingService().tooComplexImagePath() );
             LOG.debug( "Reading too complex from " + file.getAbsolutePath() );
             BufferedInputStream in = new BufferedInputStream( new FileInputStream( file ) );
             int available;
             while ( ( available = in.available() ) > 0 ) {
                 byte[] bytes = new byte[available];
                 int n = in.read( bytes );
                 assert n == available;
                 if ( n > 0 )
                     baos.write( bytes, 0, n );
             }
             baos.writeTo( output );
             baos.flush();
             baos.close();
         } catch ( IOException e ) {
             LOG.warn( "Failed to render 'too complex' warning" );
             throw new DiagramException( "Diagram generation failed", e );
         }
     }



    /**
     * Create the diagram.
     *
     * @param size width and height as double array. Can be null.
     * @param orientation string
     * @return a diagram
     * @throws DiagramException if diagram can be generated
     */
    protected abstract Diagram makeDiagram( double[] size, String orientation ) throws DiagramException;
}
