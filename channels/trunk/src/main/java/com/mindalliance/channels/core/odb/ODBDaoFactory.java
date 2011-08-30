/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.odb;

import com.mindalliance.channels.core.PersistentObjectDao;
import com.mindalliance.channels.core.PersistentObjectDaoFactory;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

/**
 * Neodatis transaction factory.
 */
public class ODBDaoFactory implements PersistentObjectDaoFactory {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ODBDaoFactory.class );

    private Resource odbDir;

    private String fileName = "db";

    private static final int MAX_ATTEMPTS = 30;

    private static final long WAIT_TIME = 100L;

    @Override
    public boolean check( String uri ) {
        ODB odb = null;
        try {
            odb = openDatabase( uri );
            return true;

        } catch ( IOException e ) {
            LOG.warn( "Unable to open db", e );
            return false;
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
    }

    /**
     * Open a connection to the database.
     *
     * @param planUri the plan uri
     * @return the ODB connection
     * @throws IOException on errors
     */
    ODB openDatabase( String planUri ) throws IOException {
        int attempts = 0;
        File planDir = new File( odbDir.getFile(), planUri );
        File path = new File( planDir, fileName );
        Exception last;
        do {
            try {
                return ODBFactory.open( path.getAbsolutePath() );
            } catch ( ODBRuntimeException e ) {
                LOG.info( "Database of {} is locked. Retrying", planUri );
                last = e;
                try {
                    attempts++;
                    Thread.sleep( WAIT_TIME );
                } catch ( InterruptedException e2 ) {
                    last = e2;
                }
            }
        } while ( attempts < MAX_ATTEMPTS );

        throw new IOException( "Failed to open database for " + planUri, last );
    }

    @Override
    public PersistentObjectDao getDao( String planUri ) {
        return new ODBDao( this, planUri );
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
