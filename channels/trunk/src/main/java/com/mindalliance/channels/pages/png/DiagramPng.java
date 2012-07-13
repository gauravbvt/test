/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
public abstract class DiagramPng extends ChannelsDynamicImageResource {


    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DiagramPng.class );


    protected DiagramPng() {
        super( "png" );
    }

    @Override
    protected byte[] getImageData( Attributes attributes ) {
        PageParameters parameters = attributes.getParameters();
        return generatePng( parameters );
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof DiagramPng;
    }


    /**
     * Convert size parameter.
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
     * @param parameters page parameters
     * @param diagram a diagram
     */
    protected void configureDiagram( PageParameters parameters, Diagram diagram ) {
        if ( parameters.getNamedKeys().contains( "size" ) ) {
            double[] size = convertSize( parameters.get( "size" ).toString() );
            diagram.setDiagramSize( size[0], size[1] );
        }
        if ( parameters.getNamedKeys().contains( "orientation" ) )
            diagram.setOrientation( parameters.get( "orientation").toString());
    }

    /**
     * Generates the bytes of the PNG.
     *
     * @param parameters page parameters
     * @return a byte array
     */
    private byte[] generatePng(
            PageParameters parameters ) {
        Channels channels = (Channels)Channels.get();
        PlanService planService = channels.getPlanServiceFactory().getService( ChannelsUser.plan() );
        double[] size = null;
        String orientation = null;
        if ( parameters.getNamedKeys().contains( "size" ) )
            size = convertSize( parameters.get( "size" ).toString() );

        if ( parameters.getNamedKeys().contains( "orientation" ) )
            orientation = parameters.get( "orientation" ).toString();
        String ticket = parameters.get( AbstractDiagramPanel.TICKET_PARM ).toString();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(  );
        try {
            Diagram diagram = makeDiagram(
                    size,
                    orientation,
                    parameters,
                    planService,
                    channels.getDiagramFactory(),
                    channels.getAnalyst() );
            configureDiagram( parameters, diagram );
            LOG.debug( "Rendering PNG" );
            diagram.render( ticket,
                            DiagramFactory.PNG,
                            bos,
                            channels.getAnalyst(),
                            channels.getDiagramFactory(),
                            planService
                             );
        } catch ( DiagramException e ) {
            LOG.error( "Error while generating diagram", e );
            writeErrorImage( bos );
        }
        return bos.toByteArray();
    }

    private void writeErrorImage( OutputStream output ) throws DiagramException {
        Channels channels = (Channels)Channels.get();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try {
             File file = new File( channels.getImagingService().tooComplexImagePath() );
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
     * @param diagramSize width and height as double array. Can be null.
     * @param orientation string
     * @param parameters page parameters
     * @param planService plan service
     * @param diagramFactory a diagram factory
     * @param analyst an analyst
     * @return a diagram
     * @throws DiagramException if diagram can be generated
     */
    protected abstract Diagram makeDiagram(
            double[] diagramSize,
            String orientation,
            PageParameters parameters,
            PlanService planService,
            DiagramFactory diagramFactory,
            Analyst analyst ) throws DiagramException;

}
