package com.mindalliance.channels.core.export.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for CommandConverter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2009
 * Time: 4:00:00 PM
 */
public class Arguments {

    private Map<String,Object> value = new HashMap<String,Object>();

    public Arguments() {}

    public Arguments( Map<String, Object> value ) {
        this.value = value;
    }

    public Map<String, Object> getValue() {
        return value;
    }

    public void setValue( HashMap<String, Object> value ) {
        this.value = value;
    }

}
