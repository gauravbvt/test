package com.mindalliance.channels;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A communication medium.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 30, 2009
 * Time: 7:14:12 AM
 */

public enum Medium {
    /**
     * Telephone
     */
    Phone( "Work phone", "(\\d*\\D*\\d{3}\\D*\\d{3}\\D*\\d{4}(\\s*((ext\\.?)|(x\\.?))\\s*\\d+)?)|(\\d{3})" ),
    /**
     * Telephone
     */
    HomePhone( "Home phone", "\\d*\\D*\\d{3}\\D*\\d{3}\\D*\\d{4}" ),
    /**
     * Fax
     */
    Fax( "Fax", "\\d*\\D*\\d{3}\\D*\\d{3}\\D*\\d{4}" ),
    /**
     * Cell phone
     */
    Cell( "Cell", "\\d*\\D*\\d{3}\\D*\\d{3}\\D*\\d{4}" ),
    /**
     * Email
     */
    Email( "Email", "[^@\\s]+@[^@\\s]+\\.\\w+" ),
    /**
     * Instant messaging
     */
    IM( "IM", ".+" ),
    /**
     * Radio
     */
    Radio( "Radio", ".+" ),
    /**
     * Television
     */
    TV( "Television", ".+" ),
    /**
     * Courier, like Fedex or UPS
     */
    Courier( "Courier", ".+" ),
    /**
     * In person
     */
    F2F( "Face-to-face", ".+" ),
    /**
     * Miscellaneous
     */
    Other( "Other", ".+" );

    /**
     * The medium's name
     */
    private String name = "";
    /**
     * A pattern for validation
     */
    private String addressPattern = "";
    /**
     * The compiled pattern
     */
    private Pattern compiledPattern;

    Medium( String name, String addressPattern ) {
        this.name = name;
        this.addressPattern = addressPattern;
        compiledPattern = Pattern.compile( addressPattern );
    }

    /**
     * Get Medium by name.
     * Return Other if none found.
     *
     * @param name a String
     * @return a Medium
     */
    public static Medium named( String name ) {
        for ( Medium m : Medium.values() ) {
            if ( m.getName().equals( name ) )
                return m;
        }
        return Other;
    }

    public String getName() {
        return name;
    }

    public String getAddressPattern() {
        return addressPattern;
    }

    private Pattern getCompiledPattern() {
        return compiledPattern;
    }

    /**
     * Check if an address is valid.
     *
     * @param address the address
     * @return true if valid
     */
    public boolean isAddressValid( String address ) {
        if ( addressPattern.isEmpty() ) return true;
        Pattern p = getCompiledPattern();
        Matcher m = p.matcher( address );
        return m.matches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }
}
