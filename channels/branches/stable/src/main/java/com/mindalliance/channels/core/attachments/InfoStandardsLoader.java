/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.attachments;

import com.mindalliance.channels.core.model.InfoStandard;
import com.mindalliance.channels.core.model.Tag;
import com.mindalliance.channels.core.model.Taggable;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Info standard loader.
 * <p/>
 * File format:
 * <p/>
 * # Comment line tagName eoiName - description eoiName - description ... <empty line> tagName eoiName - description
 * ...
 */
public class InfoStandardsLoader implements Loader {

    private Taggable taggable;

    private static final String COMMENT_MARKER = "#";

    private static final String DESCRIPTION_SEPARATOR = "-";

    public InfoStandardsLoader( Taggable taggable ) {
        this.taggable = taggable;
    }

    @Override
    public void load( BufferedReader reader ) throws IOException {
        String inputLine;
        boolean tagNameNext = true;
        InfoStandard infoStandard = null;
        while ( ( inputLine = reader.readLine() ) != null ) {
            inputLine = inputLine.trim();
            if ( inputLine.isEmpty() ) {
                if ( infoStandard != null ) {
                    taggable.addTag( infoStandard );
                    tagNameNext = true;
                    infoStandard = null;
                }

            } else if ( !inputLine.startsWith( COMMENT_MARKER ) ) {
                if ( tagNameNext ) {
                    infoStandard = new InfoStandard( inputLine.replaceAll( Tag.SEPARATOR, "" ) );
                    tagNameNext = false;
                } else {
                    int index = inputLine.indexOf( DESCRIPTION_SEPARATOR );
                    String eoiName;
                    String description = "";
                    if ( index > 0 ) {
                        eoiName = inputLine.substring( 0, index ).trim();
                        if ( index < inputLine.length() - 1 )
                            description = inputLine.substring( index + 1 ).trim();
                    } else
                        eoiName = inputLine;

                    if ( !eoiName.isEmpty() )
                        infoStandard.addEoi( eoiName, description );
                }
            }
        }
        if ( infoStandard != null )
            taggable.addTag( infoStandard );
    }
}
