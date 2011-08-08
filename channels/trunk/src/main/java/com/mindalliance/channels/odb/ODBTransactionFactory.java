/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL.
 */

package com.mindalliance.channels.odb;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/** Neodatis transaction factory. */
public class ODBTransactionFactory {

    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger( ODBTransactionFactory.class );

    private Resource odbDir;

    private String fileName = "db";

    private static final int MAX_ATTEMPTS = 30;

    private static final long WAIT_TIME = 100L;

    public ODB openDatabase( String planUri ) throws IOException {
        int attempts = 0;
        File planDir = new File( odbDir.getFile(), planUri );
        File path = new File( planDir, fileName );
        do {
            try {
                return ODBFactory.open( path.getAbsolutePath() );
            } catch ( ODBRuntimeException ignored ) {
                LOG.info( "Database of {} is locked. Retrying", planUri );
                try {
                    attempts++;
                    Thread.sleep( WAIT_TIME );
                } catch ( InterruptedException ignored2 ) {
                    // do nothing
                }
            }
        } while ( attempts < MAX_ATTEMPTS );

        throw new IOException( "Failed to open database for " + planUri );
    }

    public ODBAccessor getODBAccessor( String planUri ) {
        return new ODBAccessor( this, planUri );
    }

    @Required
    public void setOdbDir( Resource odbDir ) {
        this.odbDir = odbDir;
    }

    /**
     * Get the default file name of the database.
     *
     * @return the name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the default file name of the database.
     *
     * @param fileName the name
     */
    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }
}
