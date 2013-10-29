/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;

/**
 * Abstract meta provider.
 */
public abstract class AbstractMetaProvider<V,E> implements MetaProvider<V,E> {
    /**
     * Default font color.
     */
    public static final String FONTCOLOR = "#333333";
    /**
     * Message format as URL template with {1} = graph id.
     */
    protected static final String GRAPH_URL_FORMAT = "?graph={0,number,0}";
    /**
     * Message format as URL template with {1} = graph id and {2} = vertex id.
     */
    protected static final String VERTEX_URL_FORMAT = "?graph={0,number,0}&vertex={1,number,0}";
    /**
     * Message format as URL template with {1} = graph id and {2} = vertex id.
     */
    protected static final String AGENCY_VERTEX_URL_FORMAT = "?graph={0,number,0}&vertex={1}";
    /**
     * Message format as URL template with {1} = graph id and {2} = edge id.
     */
    protected static final String EDGE_URL_FORMAT = "?graph={0,number,0}&edge={1,number,0}";
    /**
     * Color used to indicate issues.
     */
    protected static final String COLOR_ERROR = "#C25036";
    /**
     * Invisible pen color.
     */
    public static final String INVISIBLE_COLOR = "none";
    /**
     * Font for edge labels.
     */
    public static final String EDGE_FONT = "Helvetica-Oblique";
    /**
     * Font for edge labels.
     */
    public static final String EDGE_FONT_BOLD = "Helvetica-Bold";
    /**
     * Font size for edge labels.
     */
    public static final String EDGE_FONT_SIZE = "8";

    /**
     * Number of characters after which a long line is wrapped on separator.
     */
    public static final int LINE_WRAP_SIZE = 5;
    /**
     * PNG, SVG, IMAP etc.
     */
    private String outputFormat;
    /**
     * Relative path to icon directory.
     */
    private Resource imageDirectory;
    /**
     * Diagram size constraint.
     * Diagram takes natural size if null.
     */
    private double[] graphSize;
    /**
     * Whether the direction is LR or top-bottom.
     */
    private String graphOrientation = "TB";
    /**
     * Segment analyst in context.
     */
    private Analyst analyst;

    private QueryService queryService;

    public AbstractMetaProvider( String outputFormat, Resource imageDirectory, Analyst analyst,
                                 QueryService queryService ) {
        this.outputFormat = outputFormat;
        this.imageDirectory = imageDirectory;
        this.analyst = analyst;
        this.queryService = queryService;
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public Resource getImageDirectory() {
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
     * Set graph orientation.
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
     * Get current plan.
     * @return a plan
     */
    protected Plan getPlan() {
        // TODO -- get from a parameter instead
        return ChannelsUser.plan();
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
    public static String separate( String s, int lineWrapSize ) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        String separators = "  ,.-_?";
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

    /**
     * Get label for identifiable.
     * @param identifiable an identifiable
     * @return a string
     */
    protected String getIdentifiableLabel( Identifiable identifiable ) {
         String label = AbstractMetaProvider.separate( identifiable.getName(), LINE_WRAP_SIZE );
         while ( label.split( "\\|" ).length > 3 ) {
             label = StringUtils.reverse( label );
             label = label.replaceFirst( "\\|", " " );
             label = StringUtils.reverse( label );
         }
         return label;
     }

    protected String sanitizeToId( String s ) {
        return s.replaceAll("\\W", "");
    }

}
