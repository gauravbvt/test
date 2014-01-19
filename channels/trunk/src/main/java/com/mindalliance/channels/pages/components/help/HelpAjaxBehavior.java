package com.mindalliance.channels.pages.components.help;

import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.guide.TopicItem;
import info.bliki.wiki.model.WikiModel;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/15/14
 * Time: 11:29 AM
 */
public abstract class HelpAjaxBehavior extends AbstractDefaultAjaxBehavior {

    public static final String IMAGE_PARAM = "image";
    public static final String CAPTION_PARAM = "caption";
    public static final String THUMBNAIL_CLASS = "thumbnail";

    // Group 1 = path to image file, 2 = image file name, 3= caption
    private static final Pattern IIMAGE_PATTERN = Pattern.compile( "<div\\s.*>\\n*\\s*<img src=\"images/([^\"]+/)(\\w+.\\w+)\".*alt=\"([^\"]*)\".*>\\n*\\s*</div>" );
    private static final String GLOSSARY_TERM_PATTERN_FORMAT = "\\W({0}s?)\\W";
    private TopicItem topicItem;
    private StringBuilder htmlBuilder;
    private Map<String, TopicItem> glossary;
    private ImagingService imagingService;

    public HelpAjaxBehavior( StringBuilder htmlBuilder,
                             TopicItem topicItem,
                             Map<String, TopicItem> glossary,
                             ImagingService imagingService ) {
        this.topicItem = topicItem;
        this.htmlBuilder = htmlBuilder;
        this.glossary = glossary;
        this.imagingService = imagingService;
    }

    @Override
    protected void onComponentTag( ComponentTag tag ) {
        super.onComponentTag( tag );
        htmlBuilder.append( wikimediaToHtml( topicItem.getDescription() ) );
    }

    private String wikimediaToHtml( String string ) {
        String content = trimAllLines( string );
        WikiModel wikiModel = new WikiModel( "images/doc/${image}", "" );
        String html = wikiModel.render( content );
        return processGlossary( processImages( html ) ); // todo - don't process a term in its own definition
    }

    private String processGlossary( String html ) {
        Set<String> terms = glossary.keySet();
        for ( String term : terms ) {
            if ( term.matches( "[\\w\\s-]*" ) ) // make sure it is a valid glossary term
                html = processGlossaryTerm( html, term );
        }
        return html;
    }

    private String processGlossaryTerm( String html, String term ) {
        StringBuilder sb = new StringBuilder();
        int cursor = 0;
        String patternString = MessageFormat.format( GLOSSARY_TERM_PATTERN_FORMAT, term );
        Pattern pattern = Pattern.compile( patternString, Pattern.CASE_INSENSITIVE );
        Matcher matcher = pattern.matcher( html );
        while ( matcher.find() ) {
            int begin = matcher.start();
            int end = matcher.end();
            if ( insideMarker( html, end ) ) {
                sb.append( html.substring( cursor, end ) );
            } else {
                String replacement = " <span class=\"definition\" title=\""
                        + firstParagraphOf( glossary.get( term ).getDescription() )
                        + "\">"
                        + matcher.group( 1 )
                        + "</span> ";
                sb.append( html.substring( cursor, begin ) )
                        .append( replacement );
            }
            cursor = end;
        }
        sb.append( html.substring( cursor ) );
        return sb.toString();
    }

    private boolean insideMarker( String html, int end ) {
        int closeIndex = html.indexOf( ">", end );
        int openIndex = html.indexOf( "<", end );
        return closeIndex >= 0
                && closeIndex < openIndex;
    }

    private String firstParagraphOf( String string ) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader( new StringReader( string ) );
        int emptyLineCount = 0;
        try {
            String line;
            while ( emptyLineCount < 2 && ( line = reader.readLine() ) != null ) {
                String trimmed = line.trim();
                if ( trimmed.isEmpty() ) {
                    emptyLineCount++;
                    sb.append( " " );
                } else {
                    sb.append( trimmed );
                }
            }
        } catch ( IOException e ) {
            // do nothing
        } finally {
            try {
                reader.close();
            } catch ( IOException e ) {
                // do nothing
            }
        }
        return sb.toString().trim();
    }

    private String processImages( String html ) {
        StringBuilder sb = new StringBuilder();
        // replace images elements by callback links surrounding thumbnails
        Matcher matcher = IIMAGE_PATTERN.matcher( html );
        int cursor = 0;
        while ( matcher.find() ) {
            int begin = matcher.start();
            int end = matcher.end();
            String imagePath = matcher.group( 1 ); // path under images/ e.g. images/doc/bla.png => doc/
            String imageName = matcher.group( 2 ); // file name with extension
            String caption = matcher.group( 3 );
            String imageLink = makeImageLink( imagePath, imageName, caption );
            sb.append( html.substring( cursor, begin ) )
                    .append( imageLink );
            cursor = end;
        }
        sb.append( html.substring( cursor ) );
        return sb.toString();
    }

    private String makeImageLink( String imagePath, String imageName, String caption ) {
        StringBuilder sb = new StringBuilder();
        int[] size = imagingService.getImageSize( imagePath, imageName );
        int width = size[0];
        int height = size[1];
        if ( width != 0 && height != 0 ) {
            if ( height <= ImagingService.THUMBNAIL_HEIGHT ) {
                // Use image as is, no link to full image
                sb.append( "<span class=\"" )
                        .append( THUMBNAIL_CLASS )
                        .append( "\">" )
                        .append( "<img src=\"images/" )
                        .append( imagePath )
                        .append( "/" )
                        .append( imageName )
                        .append( "\" alt=\"" )
                        .append( caption )
                        .append( "\" title=\"" )
                        .append( caption )
                        .append( "\"/>" )
                        .append( "</span>" );
            } else {
                // Use thumbnail linked to full image
                sb.append( "<span class=\"" )
                        .append( THUMBNAIL_CLASS )
                        .append( "\">" )
                        .append( "<a href=\"" )
                        .append( makeCallback( imagePath, imageName, caption ) )
                        .append( "\">" )
                        .append( "<img src=\"" )
                        .append( getThumbNail( imagePath, imageName ) )
                        .append( "\" alt=\"" )
                        .append( caption )
                        .append( "\" title=\"" )
                        .append( caption )
                        .append( "\"/>" )
                        .append( "</a>" )
                        .append( "</span>" );
            }
        }
        return sb.toString();
    }

    private String makeCallback( String imagePath, String imageName, String caption ) {
        StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append( "wicketAjaxGet('" )
                .append( getCallbackUrl() )
                .append( "&" ).append( IMAGE_PARAM ).append( "=" ).append( "images/" ).append( imagePath ).append( imageName ).append( "" )
                .append( "&" ).append( CAPTION_PARAM ).append( "=" ).append( ChannelsUtils.sanitize( caption ) ).append( "" );
        scriptBuilder.append( "'" );
        String script = scriptBuilder.toString();
        CharSequence callbackScript = generateCallbackScript( script );
        StringBuilder callbackBuilder = new StringBuilder();
        callbackBuilder.append( "javascript:" )
                .append( callbackScript );
        return callbackBuilder.toString().replaceAll( "&amp;", "&" );
    }

    private String getThumbNail( String imagePath, String imageName ) {
        return "images/" + imagingService.getThumbnailPath( imagePath, imageName );
    }

    private String trimAllLines( String string ) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader( new StringReader( string ) );
        try {
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                sb.append( line.trim() );
                sb.append( '\n' );
            }
        } catch ( IOException e ) {
            // do nothing
        } finally {
            try {
                reader.close();
            } catch ( IOException e ) {
                // do nothing
            }
        }
        return sb.toString();
    }

}
