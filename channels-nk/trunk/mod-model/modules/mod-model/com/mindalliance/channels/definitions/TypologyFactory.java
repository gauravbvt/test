// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.definitions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import org.ho.yaml.Yaml;

/**
 * Utility typology loader from a file.
 * This class serves no other purpose than to initialize
 * the spring contexts...
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 * @composed - - 1 Typology
 */
public class TypologyFactory {

    private Typology typology;

    /**
     * Default constructor.
     */
    public TypologyFactory() {
    }

    /**
     * Default constructor.
     * @param stream the input stream
     */
    public TypologyFactory( InputStream stream ) {
        this();
        try {
            setTypology( load( stream ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Load a typology from a stream.
     * @param stream the stream
     * @throws IOException on errors
     */
    public Typology load( InputStream stream ) throws IOException {
        return Yaml.loadType( stream, Typology.class );
    }

    /**
     * Save a typology in yaml format.
     * @param typology the typology
     * @param stream the stream to write to
     * @throws IOException on errors
     */
    public void save( Typology typology, OutputStream stream )
        throws IOException {

        try {
            OutputStreamWriter w = new OutputStreamWriter( stream, "UTF8" );
            w.write( Yaml.dump( typology, true ) );
            w.flush();

        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Return the typology.
     */
    public Typology getTypology() {
        return this.typology;
    }

    /**
     * Set the typology.
     * @param typology the typology
     */
    public void setTypology( Typology typology ) {
        this.typology = typology;
    }

    /**
     * Get a category set initialized from a category in
     * a discipline.
     * @param disciplineName the discipline name
     * @param categoryName the category name
     */
    public CategorySet getCategorySet(
            String disciplineName, String categoryName ) {

        Discipline discipline = typology.getDiscipline( disciplineName );
        if ( discipline == null )
            throw new IllegalArgumentException(
                MessageFormat.format(
                    "Unknown discipline {0}", disciplineName ) );

        Category category = discipline.getCategory( categoryName );
        if ( category == null )
            throw new IllegalArgumentException(
                MessageFormat.format(
                    "No {0} category in discipline {1}",
                    categoryName, disciplineName ) );

        return new CategorySet( category.getTaxonomy(), category );
    }
}
