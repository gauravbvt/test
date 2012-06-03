package com.mindalliance.channels.guide;

import java.io.Serializable;

/**
 * Change qualifier in an activity script.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/3/12
 * Time: 3:36 PM
 */
public class ChangeQualifier implements Serializable {

    private String name;
    private String value;

    public ChangeQualifier() {
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
}
