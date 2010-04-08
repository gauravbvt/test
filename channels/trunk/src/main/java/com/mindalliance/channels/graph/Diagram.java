package com.mindalliance.channels.graph;

import java.io.OutputStream;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 4:30:22 PM
 */
public interface  Diagram extends Serializable {
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
     * @param outputFormat the name of the format
     * @param outputStream the output stream
     * @throws DiagramException raised if diagram generation fails
     */
    void render( String outputFormat, OutputStream outputStream );

    /**
     * Produce image map
     *
     * @return a String
     * @throws DiagramException if fails
     */
    String makeImageMap();
}
