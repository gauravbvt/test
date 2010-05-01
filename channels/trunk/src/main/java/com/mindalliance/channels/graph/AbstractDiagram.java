package com.mindalliance.channels.graph;

import com.mindalliance.channels.analysis.Analyst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 3:19:11 PM
 */
public abstract class AbstractDiagram<V, E> implements Diagram {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractDiagram.class );
    /**
     * Whether the direction is LR or top-bottom
     */
    private String orientation;
    /**
     * Diagram size constraint.
     * Diagram takes natural size if null.
     */
    private double[] diagramSize;
    /**
     * Diagram's image map.
     */
    private String imageMap = null;

    // private DiagramFactory<V,E> diagramFactory;

    public AbstractDiagram( double[] diagramSize, String orientation ) {
        this.diagramSize = diagramSize;
        this.orientation = orientation;
    }

    /**
     * {@inheritDoc}
     */
    public void setDiagramSize( double width, double height ) {
        diagramSize = new double[2];
        diagramSize[0] = width;
        diagramSize[1] = height;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrientation( String orientation ) {
        this.orientation = orientation;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * {@inheritDoc}
     */
    public double[] getDiagramSize() {
        return diagramSize;
    }

    /**
     * {@inheritDoc}
     */
    public String makeImageMap( Analyst analyst, DiagramFactory diagramFactory ) {
        if ( imageMap == null ) {
            LOG.debug( "Making image map for " + this.getClass().getSimpleName() );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            render( DiagramFactory.IMAGE_MAP, new BufferedOutputStream( baos ), analyst, diagramFactory );
            // System.out.println( "*** Image map generated at " + System.currentTimeMillis() );
            imageMap = baos.toString();
            // System.out.println( imageMap );
        }
        return imageMap;
    }

}
