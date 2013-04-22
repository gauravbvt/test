package com.mindalliance.channels.core.command;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/28/13
 * Time: 9:14 AM
 */

import com.mindalliance.channels.core.model.Mappable;

import java.util.ArrayList;
import java.util.List;

public class MappedList<T extends Mappable> {

    private List<MappedObject> mappedList;

    public MappedList(List<T> mappables) {
         mapList( mappables );
    }

    private void mapList( List<T> mappables ) {
        mappedList = new ArrayList<MappedObject>(  );
        for ( Mappable mappable : mappables ) {
            mappedList.add(  new MappedObject( mappable ) );
        }
    }

    public List<MappedObject> getMappedObjects() {
        return mappedList;
    }
}
