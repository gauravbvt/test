package com.mindalliance.channels.db.data;

import org.springframework.data.domain.Sort;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/26/13
 * Time: 2:09 PM
 */
public interface ChannelsDocument extends PersistentPlanObject {

    static final Sort SORT_CREATED_DESC = new Sort(Sort.Direction.DESC, "created" );

    String getUid();

    void setUid( String uid );
}
