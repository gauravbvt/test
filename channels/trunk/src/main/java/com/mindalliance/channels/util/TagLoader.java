package com.mindalliance.channels.util;

import com.mindalliance.channels.model.Taggable;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Tag loader.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/11
 * Time: 10:52 AM
 */
public class TagLoader implements Loader {

    private Taggable taggable;

    public TagLoader( Taggable taggable ) {
        this.taggable = taggable;
    }

    public void load( BufferedReader reader ) throws IOException {
        String inputLine;
        while ( ( inputLine = reader.readLine() ) != null ) {
            taggable.addTags( inputLine.trim() );
        }
    }

}
