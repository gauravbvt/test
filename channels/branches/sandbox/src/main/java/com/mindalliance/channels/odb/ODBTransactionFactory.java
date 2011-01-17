package com.mindalliance.channels.odb;

import org.neodatis.odb.ODB;

import java.io.IOException;

/**
 *  Neodatis database transaction factory.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2010
 * Time: 10:36:31 PM
 */
public interface ODBTransactionFactory {

    ODB openDatabase() throws IOException;

    ODBAccessor getODBAccessor();

}
