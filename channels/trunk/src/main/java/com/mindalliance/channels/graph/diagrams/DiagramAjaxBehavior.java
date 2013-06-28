package com.mindalliance.channels.graph.diagrams;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Modifies the URLs diagram's imagemap to become Ajax callbacks.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 2, 2009
 * Time: 1:12:51 PM
 */
public abstract class DiagramAjaxBehavior extends AbstractDefaultAjaxBehavior {
    /**
     * A string builder on an image map.
     */
    private StringBuilder imageMapHolder;
    /**
     * CSS dom element identifier for diagram container.
     */
    private String domIdentifier;
    /**
     * Pattern for url in image map.
     */
    private static Pattern HREF = Pattern.compile( "href=\"\\?(.*?)\"" );
    /**
     * Pattern for graph parameter in url.
     */
    private static Pattern GRAPH = Pattern.compile( "graph=(\\d+)" );
    /**
     * Pattern for vertex parameter in url.
     */
    private static Pattern VERTEX = Pattern.compile( "graph=(\\d+)&amp;vertex=([a-zA-Z0-9_]+).*" );
    /**
     * Pattern for edge parameter in url.
     */
    private static Pattern EDGE = Pattern.compile( "graph=(\\d+)&amp;edge=([a-zA-Z0-9_,]+).*" );

    private static Pattern EXTRA = Pattern.compile( "&amp;(_\\w+)=(\\w+)");

    public DiagramAjaxBehavior( StringBuilder imageMapHolder, String domIdentifier ) {
        this.imageMapHolder = imageMapHolder;
        this.domIdentifier = domIdentifier;
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
        Matcher extraMatcher = EXTRA.matcher( query );
        Map<String,String> extras = new HashMap<String,String>();
        while( extraMatcher.find() ) {
            String name = extraMatcher.group( 1 );
            String value = extraMatcher.group( 2 );
            extras.put( name, value );
        }
        return makeCallback( graphId, vertexId, edgeId, extras );
    }

    private String makeCallback( String graphId, String vertexId, String edgeId, Map<String, String> extras ) {
        StringBuilder cb = new StringBuilder();
        cb.append("javascript:");
        //cb.append("{alert('boo');");
        String script = "wicketAjaxGet('"
                        + getCallbackUrl( )
                        + (graphId != null ? "&graph=" + graphId : "")
                        + (vertexId != null ? "&vertex=" + vertexId : "")
                        + (edgeId != null ? "&edge=" + edgeId : "");
        for ( String extra : extras.keySet() ) {
            script += "&" + extra + "=" + extras.get( extra );
        }
        script = script
                        + (domIdentifier != null
                                ? "&width='+$('"+domIdentifier+"').width()+'"
                                : "")
                        + (domIdentifier != null
                                ? "&height='+$('"+domIdentifier+"').height()"
                                : "")
                                                                                    // + "')";
                        + (domIdentifier == null ? "'" : "");
        CharSequence callbackScript = generateCallbackScript(script);
        cb.append(callbackScript);
        return cb.toString().replaceAll("&amp;","&");
    }

}
