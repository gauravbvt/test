package com.mindalliance.channels.core.model;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/29/12
 * Time: 2:13 PM
 */
public class InfoFormat extends ModelEntity {

    /**
     * Unknown info format.
     */
    public static InfoFormat UNKNOWN;

    /**
     * Name of unknown info format.
     */
    public static String UnknownName = "(unknown)";

    public InfoFormat() {
    }

    public InfoFormat( String name ) {
        super( name );
    }

    @Override
    public String getTypeName() {
        return "format";
    }

    @Override
    public String getKindLabel() {
        return "format";
    }

    public static String classLabel() {
        return "formats";
    }
}
