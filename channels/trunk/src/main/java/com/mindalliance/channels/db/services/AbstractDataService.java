package com.mindalliance.channels.db.services;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.db.data.ChannelsDocument;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 2:55 PM
 */
public abstract class AbstractDataService<T extends ChannelsDocument>  implements DataService<T>{

    @Autowired
    private MongoTemplate mongoTemplate;

    public MongoOperations getDb() {
        return mongoTemplate;
    }

    @Override
    public T refresh( T channelsDocument ) {
        return channelsDocument == null ? null : load( channelsDocument.getUid() );
    }

    protected int toInteger( long value ) {
        return Integer.parseInt( Long.toString( value ) );
    }

    @SuppressWarnings( "unchecked" )
    protected List<T> toList( Iterable<T> iterable ) {
        return IteratorUtils.toList( iterable.iterator() );
    }


}
