package com.mindalliance.channels.social;

import org.neodatis.odb.ODB;

/**
 *  Database factory.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2010
 * Time: 10:36:31 PM
 */
public interface DatabaseFactory {

    ODB getDatabase();

}
