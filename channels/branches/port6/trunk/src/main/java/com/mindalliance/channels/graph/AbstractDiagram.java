/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

public abstract class AbstractDiagram<V, E> implements Diagram {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractDiagram.class );

    /**
     * Whether the direction is LR or top-bottom.
     */
    private String orientation;

    /**
     * Diagram size constraint. Diagram takes natural size if null.
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

    @Override
    public void setDiagramSize( double width, double height ) {
        diagramSize = new double[2];
        diagramSize[0] = width;
        diagramSize[1] = height;
    }

    @Override
    public void setOrientation( String orientation ) {
        this.orientation = orientation;
    }

    public String getOrientation() {
        return orientation;
    }

    public double[] getDiagramSize() {
        return diagramSize;
    }

    @Override
    public String makeImageMap( String ticket, Analyst analyst, DiagramFactory diagramFactory,
                                CommunityService communityService ) {
        if ( imageMap == null || imageMap.isEmpty() ) {
            LOG.debug( "Making image map for " + this.getClass().getSimpleName() );
            try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            render( ticket, DiagramFactory.IMAGE_MAP, baos, analyst, diagramFactory, communityService );
            // System.out.println( "*** Image map generated at " + System.currentTimeMillis() );
            imageMap = baos.toString();
            // System.out.println( imageMap );
            } catch ( DiagramException e ) {
                LOG.warn( "Failed to generate image map" );
                imageMap = "";
            }
        }
        return imageMap;
    }
}
