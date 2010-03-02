package com.mindalliance.channels.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 10:45:02 AM
 */
public class DOTAttribute {
    /**
     * The attribute's name
     */
    private String name;
    /**
     * The attribute's value
     */
    private String value;

    public DOTAttribute( String name, String value ) {
        this.name = name;
        this.value = value;
    }

    /**
     * DOTAttribute as string
     *
     * @return string representation
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( name );
        sb.append( '=' );
        sb.append( "\"" );
        sb.append( value );
        sb.append( "\"" );
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    /**
     * Create a list with the DOTAttribute as single element
     *
     * @return a list of one DOTAttribute
     */
    public List<DOTAttribute> asList() {
        List<DOTAttribute> list = new ArrayList<DOTAttribute>();
        list.add( this );
        return list;
    }

    /**
     * Produces an empty list
     *
     * @return an empty list of DOTAttributes
     */
    public static List<DOTAttribute> emptyList() {
        return new ArrayList<DOTAttribute>();
    }

}
