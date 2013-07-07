package com.mindalliance.channels.db.data;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/26/13
 * Time: 2:09 PM
 */
public interface ChannelsDocument extends PersistentPlanObject {

    String getUid();

    void setUid( String uid );
}
