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
import java.util.List;
import java.util.Map;
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
    private static final Pattern Image_Pattern = Pattern.compile( "<div\\s.*>\\n*\\s*<img src=\"images/([^\"]+/)(\\w+.\\w+)\".*alt=\"([^\"]*)\".*>\\n*\\s*</div>" );

    private final Map<TopicItem, String> topicItemsContents;
    private final List<TopicItem> topicItems;
    private ImagingService imagingService;

    public HelpAjaxBehavior( Map<TopicItem, String> topicItemsContents, List<TopicItem> topicItems, ImagingService imagingService ) {
        this.topicItemsContents = topicItemsContents;
        this.topicItems = topicItems;
        this.imagingService = imagingService;
    }

    @Override
    protected void onComponentTag( ComponentTag tag ) {
        super.onComponentTag( tag );
        if ( topicItemsContents.isEmpty() )
            for ( TopicItem topicItem : topicItems ) {
                topicItemsContents.put( topicItem, wikimediaToHtml( topicItem.getDescription() ) );
            }
    }

    private String wikimediaToHtml( String string ) {
        String content = trimAllLines( string );
        WikiModel wikiModel = new WikiModel( "images/doc/${image}", "" );
        String html = wikiModel.render( content );
        return ajaxify( html );
    }

    private String ajaxify( String html ) {
        StringBuilder sb = new StringBuilder();
        // replace images elements by callback links surrounding thumbnails
        Matcher matcher = Image_Pattern.matcher( html );
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
            cursor = end + 1;
        }
        sb.append( html.substring( cursor ) );
        return sb.toString();
    }

    private String makeImageLink( String imagePath, String imageName, String caption ) {
        StringBuilder sb = new StringBuilder();
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
        return sb.toString();
    }

    private String makeCallback( String imagePath, String imageName, String caption ) {
        StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append( "wicketAjaxGet('" )
                .append( getCallbackUrl() )
                .append( "&" ).append( IMAGE_PARAM ).append( "=" ).append("images/").append( imagePath ).append( imageName ).append( "" )
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
        // return imagePath + imageName;  //Todo generate thumbnail if needed and return path to it
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
