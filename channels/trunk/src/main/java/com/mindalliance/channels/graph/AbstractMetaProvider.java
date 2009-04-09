package com.mindalliance.channels.graph;

import com.mindalliance.channels.analysis.Analyst;

/**
 * Abstract meta provider.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 8:05:43 PM
 */
public abstract class AbstractMetaProvider<V,E> implements MetaProvider<V,E> {
    /**
     * Message format as URL template with {1} = graph id.
     */
    protected static String GRAPH_URL_FORMAT = "?graph={0,number,0}";
    /**
     * Message format as URL template with {1} = graph id and {2} = vertex id.
     */
    protected static String VERTEX_URL_FORMAT = "?graph={0,number,0}&vertex={1,number,0}";
    /**
     * Message format as URL template with {1} = graph id and {2} = edge id.
     */
    protected static String EDGE_URL_FORMAT = "?graph={0,number,0}&edge={1,number,0}";
    /**
     * Color used to indicate issues.
     */
    protected static final String COLOR_ERROR = "red3";
    /**
     * Font for edge labels.
     */
    public static final String EDGE_FONT = "Helvetica-Oblique";
    /**
     * Font size for edge labels.
     */
    public static final String EDGE_FONT_SIZE = "8";

    /**
     * Number of characters after which a long line is wrapped on separator.
     */
    protected static final int LINE_WRAP_SIZE = 15;
    /**
     * PNG, SVG, IMAP etc.
     */
    private String outputFormat;
    /**
     * Relative path to icon directory
     */
    private String imageDirectory;
    /**
     * Diagram size constraint.
     * Diagram takes natural size if null.
     */
    private double[] graphSize;
    /**
     * Whether the direction is LR or top-bottom
     */
    private String graphOrientation = "LR";
    /**
     * Scenario analyst in context
     */
    private Analyst analyst;

    public AbstractMetaProvider( String outputFormat,
                                 String imageDirectory,
                                 Analyst analyst ) {
        this.outputFormat = outputFormat;
        this.imageDirectory = imageDirectory;
        this.analyst = analyst;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public String getImageDirectory() {
        return imageDirectory;
    }

    public double[] getGraphSize() {
        return graphSize;
    }

    public void setGraphSize( double[] graphSize ) {
        this.graphSize = graphSize;
    }

    public String getGraphOrientation() {
        return graphOrientation;
    }

    /**
     * Set graph orientation
     *
     * @param graphOrientation a String ("TB" or "LR")
     */
    public void setGraphOrientation( String graphOrientation ) {
        if ( graphOrientation != null ) this.graphOrientation = graphOrientation;
    }

    public Analyst getAnalyst() {
        return analyst;
    }

    /**
     * Make label safe.
     * @param label  a string
     * @return a sanitized string
     */
    protected String sanitize( String label ) {
        return label.replaceAll( "\"", "\\\\\"" );
    }

    /**
     * Insert '|' at a space or after other separator at intervals of minimum size in a string.
     *
     * @param s a String
     * @param lineWrapSize an integer
     * @return modified string
     */
    protected String separate( String s, int lineWrapSize ) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        String separators = "  ,.-_?\"";
        for ( char c : s.toCharArray() ) {
            if ( count >= lineWrapSize && separators.indexOf( c ) >= 0 ) {
                if ( c != ' ' ) sb.append( c );
                sb.append( '|' );
                count = 0;
            } else {
                sb.append( c );
                count++;
            }
        }
        return sb.toString();
    }

    /**
     * Get graph size as a string.
     * @return a string
     */
    protected String getGraphSizeString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getGraphSize()[0] );
        sb.append( ',' );
        sb.append( getGraphSize()[1] );
        return sb.toString();
    }





}
