/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.attachments;

import com.mindalliance.channels.core.model.Taggable;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Tag loader.
 */
public class TagLoader implements Loader {

    private Taggable taggable;

    public TagLoader( Taggable taggable ) {
        this.taggable = taggable;
    }

    @Override
    public void load( BufferedReader reader ) throws IOException {
        String inputLine;
        while ( ( inputLine = reader.readLine() ) != null ) {
            taggable.addTags( inputLine.trim() );
        }
    }

}
