package com.mindalliance.channels.graph;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.DiagramFactory;

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

    public AbstractDiagram( double[] diagramSize, String orientation ) {
        this.diagramSize = diagramSize;
        this.orientation = orientation;
    }

    public Analyst getAnalyst() {
        return Channels.instance().getAnalyst();
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
    @SuppressWarnings( "unchecked" )
    public DiagramFactory<V, E> getDiagramFactory() {
        return Channels.instance().getDiagramFactory();
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
    public String makeImageMap() {
        if ( imageMap == null ) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            render( DiagramFactory.IMAGE_MAP, new BufferedOutputStream( baos ) );
            // System.out.println( "*** Image map generated at " + System.currentTimeMillis() );
            imageMap = baos.toString();
            // System.out.println( imageMap );
        }
        return imageMap;
    }

}
