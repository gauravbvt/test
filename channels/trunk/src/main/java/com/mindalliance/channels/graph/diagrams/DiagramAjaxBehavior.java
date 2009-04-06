package com.mindalliance.channels.graph.diagrams;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Modifies the URLs diagram's imagemap to become Ajax callbacks.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 2, 2009
 * Time: 1:12:51 PM
 */
public abstract class DiagramAjaxBehavior extends AbstractDefaultAjaxBehavior {

    private StringBuilder imageMapHolder;
    private static Pattern HREF = Pattern.compile( "href=\"\\?(.*?)\"" );
    private static Pattern GRAPH = Pattern.compile( "graph=(\\d+)" );
    private static Pattern VERTEX = Pattern.compile( "graph=(\\d+)&amp;vertex=(\\d+)" );
    private static Pattern EDGE = Pattern.compile( "graph=(\\d+)&amp;edge=([\\d,]+)" );

    public DiagramAjaxBehavior( StringBuilder imageMapHolder ) {
        this.imageMapHolder = imageMapHolder;
    }

    /**
     * {@inheritDoc}
     */
    protected void onComponentTag( ComponentTag tag ) {
        StringBuffer newImageMap = new StringBuffer();
        // Modify image map so that URLs are Ajax callbacks
        String imageMap = imageMapHolder.toString();
        Matcher hrefMatcher = HREF.matcher( imageMap );
        while ( hrefMatcher.find() ) {
            String query = hrefMatcher.group( 1 );
            String callback = getCallback( query );            
            hrefMatcher.appendReplacement( newImageMap, Matcher.quoteReplacement("href=\"" + callback + "\"") );
        }
        hrefMatcher.appendTail( newImageMap );
        imageMapHolder.replace( 0, imageMapHolder.length(), newImageMap.toString() );
    }

    private String getCallback( String query ) {
        String graphId = null;
        String vertexId = null;
        String edgeId = null;
        Matcher vertexMatcher = VERTEX.matcher( query );
        if ( vertexMatcher.matches() ) {
            graphId = vertexMatcher.group( 1 );
            vertexId = vertexMatcher.group( 2 );
        } else {
            Matcher edgeMatcher = EDGE.matcher( query );
            if ( edgeMatcher.matches() ) {
                graphId = edgeMatcher.group( 1 );
                edgeId = edgeMatcher.group( 2 );
            } else {
                Matcher graphMatcher = GRAPH.matcher( query );
                if ( graphMatcher.matches() ) {
                    graphId = graphMatcher.group( 1 );
                }
            }
        }
        return makeCallback( graphId, vertexId, edgeId);
    }

    private String makeCallback( String graphId, String vertexId, String edgeId ) {
        StringBuilder cb = new StringBuilder();
        cb.append("javascript:");
        //cb.append("{alert('boo');");
        String script = "wicketAjaxGet('"
                        + getCallbackUrl(true)
                        + (graphId != null ? "&graph=" + graphId : "")
                        + (vertexId != null ? "&vertex=" + vertexId : "")
                        + (edgeId != null ? "&edge=" + edgeId : "")
                       // + "')";
                        + "'";
        cb.append(generateCallbackScript(script));
        return cb.toString().replaceAll("&amp;","&");
    }

}
