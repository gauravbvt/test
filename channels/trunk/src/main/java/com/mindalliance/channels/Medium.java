package com.mindalliance.channels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.Serializable;

/**
 * A communication medium
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 30, 2009
 * Time: 7:14:12 AM
 */
public class Medium implements Serializable {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( Medium.class );

    private String name = "";

    private String addressPattern = "";

    transient Pattern compiledPattern;

    public Medium() {
    }

    public Medium( String name ) {
        this(name, ".*");
    }

    public Medium( String name, String addressPattern ) {
        this.name = name;
        this.addressPattern = addressPattern;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getAddressPattern() {
        return addressPattern;
    }

    public void setAddressPattern( String addressPattern ) {
        this.addressPattern = addressPattern;
        compiledPattern = Pattern.compile( addressPattern );
    }

    private Pattern getCompiledPattern() {
        if ( compiledPattern == null ) {
            try {
                compiledPattern = Pattern.compile( addressPattern );
            } catch ( Exception e ) {
                LOG.warn( "Invalid addressPattern " + addressPattern, e );
            }
        }
        return compiledPattern;
    }

    public boolean isPatternValid() {
        return getCompiledPattern() != null;
    }

    public boolean isAddressValid( String address ) {
        if ( addressPattern.isEmpty() ) return true;
        Pattern p = getCompiledPattern();
        Matcher m = p.matcher( address );
        return m.matches();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return name;
    }
}
