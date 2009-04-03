package com.mindalliance.channels.graph;

import com.mindalliance.channels.pages.Project;

import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 3:19:11 PM
 */
public abstract class AbstractDiagram<V,E> implements Diagram {

    /**
     * Whether the direction is LR or top-bottom
     */
    private String orientation;

    /**
     * Diagram size constraint.
     * Diagram takes natural size if null.
     */
    private double[] diagramSize;

    public AbstractDiagram(  ) {
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

    public DiagramFactory<V,E> getDiagramFactory() {
        return Project.diagramFactory();
    }

    public String getOrientation() {
        return orientation;
    }

    public double[] getDiagramSize() {
        return diagramSize;
    }

    /**
     * {@inheritDoc}
     */
    public String makeImageMap() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        render( DiagramFactory.IMAGE_MAP, new BufferedOutputStream( baos ) );
        // System.out.println("*** Image map generated");
        return baos.toString();
    }
    
}
