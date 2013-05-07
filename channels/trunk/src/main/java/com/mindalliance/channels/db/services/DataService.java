package com.mindalliance.channels.db.services;

import com.mindalliance.channels.db.data.ChannelsDocument;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 3:40 PM
 */
public interface DataService<T extends ChannelsDocument> {

    MongoOperations getDb();

    void save(T object);

    T load( String uid );

    T refresh( T channelsDocument );

}
