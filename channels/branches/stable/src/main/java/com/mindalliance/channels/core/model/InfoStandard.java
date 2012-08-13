package com.mindalliance.channels.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Information standard tag.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/11
 * Time: 11:22 AM
 */
public class InfoStandard extends Tag {

    private Map<String, String> eois = new HashMap<String, String>();

    public InfoStandard( String s ) {
        super( s );
    }

    public boolean isInfoStandard() {
        return true;
    }

    public void addEoi( String name, String description ) {
        eois.put( name, description );
    }

    public List<String> getEoiNames() {
        return new ArrayList<String>( eois.keySet() );
    }

    public String getEoiDescription( String eoiName ) {
        return eois.get( eoiName );
    }

}
