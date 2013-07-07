package com.mindalliance.channels.db.data;

/**
 * Object that has a last-modified timestamp property and accessors for it.
 * This is used in conjunction with Hibernate's entity listener for automatically adjusting it.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/1/12
 * Time: 3:03 PM
 */

import java.util.Date;

public interface Timestamped {

    Date getLastModified();

    void setLastModified( Date date );

}

