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

public enum Medium  {

    Phone      ( "Phone",        "\\d{3}-\\d{3}-\\d{4}" ),
    Fax        ( "Fax",          "\\d{3}-\\d{3}-\\d{4}" ),
    Cell       ( "Cell",         "\\d{3}-\\d{3}-\\d{4}" ),
    Email      ( "Email",        "[^@\\s]+@[^@\\s]+\\.\\w+" ),
    IM         ( "IM",           ".+" ),
    Radio      ( "Radio",        ".+" ),
    TV         ( "Television",   ".+" ),
    Courier    ( "Courier",      ".+" ),
    F2F        ( "Face-to-face", ".*" ),
    SendWordNow( "SendWordNow",  ".+" ),
    Other      ( "Other",        ".+" );


    private String name = "";

    private String addressPattern = "";

    private Pattern compiledPattern;

    Medium( String name, String addressPattern ) {
        this.name = name;
        this.addressPattern = addressPattern;
        compiledPattern = Pattern.compile( addressPattern );
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
     * @param address the address
     * @return true if valid
     */
    public boolean isAddressValid( String address ) {
        if ( addressPattern.isEmpty() ) return true;
        Pattern p = getCompiledPattern();
        Matcher m = p.matcher( address );
        return m.matches();
    }

    @Override
    public String toString() {
        return name;
    }
}
