package com.mindalliance.channels.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.engine.analysis.Analyst;

import java.io.OutputStream;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 4:30:22 PM
 */
public interface  Diagram<V,E> extends Serializable {
    /**
     * Constrain size of diagram.
     *
     * @param width  in inches
     * @param height in inches
     */
    void setDiagramSize( double width, double height );

    /**
     * Set orientation of the diagram
     *
     * @param orientation a String
     */
    void setOrientation( String orientation );

    /**
     * Render the diagram on output stream in given format.
     *
     *
     * @param ticket a per-format use once ticket
     * @param outputFormat the name of the format
     * @param outputStream the output stream
     * @param analyst an analyst
     * @param diagramFactory   a diagram factory
     * @param communityService a plan community service
     * @throws DiagramException raised if diagram generation fails
     */
    void render( String ticket, String outputFormat, OutputStream outputStream, Analyst analyst,
                 DiagramFactory<V, E> diagramFactory, CommunityService communityService ) throws DiagramException ;

    /**
     * Produce image map
     *
     *
     * @param ticket  a per-format use-once ticket
     * @param analyst an analyst
     * @param diagramFactory  a diagram factory
     * @param communityService a plan community service
     * @return a String
     * @throws DiagramException if fails
     */
    String makeImageMap( String ticket, Analyst analyst, DiagramFactory<V, E> diagramFactory,
                         CommunityService communityService );
}
