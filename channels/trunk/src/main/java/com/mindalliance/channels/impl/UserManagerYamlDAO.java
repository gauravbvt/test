// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.ho.yaml.Yaml;

/**
 * A simplistic persister for the user manager.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class UserManagerYamlDAO implements UserManagerDAO {

    /**
     * Default constructor.
     */
    public UserManagerYamlDAO() {
    }

    /**
     * Load a user manager from an external stream.
     * @param input a stream to decypher.
     */
    public UserManager load( InputStream input ) {
        return (UserManager) Yaml.load( input );
    }

    /**
     * Save a user manager (unsupported for now).
     * @param manager the manager to save
     * @param output where to save
     * @throws IOException on write errors
     */
    public void save( UserManager manager, OutputStream output )
        throws IOException {

        Writer writer = new BufferedWriter(
                            new OutputStreamWriter( output, "UTF8" ) );
        writer.write( Yaml.dump( manager ) );
        writer.flush();
    }
}
