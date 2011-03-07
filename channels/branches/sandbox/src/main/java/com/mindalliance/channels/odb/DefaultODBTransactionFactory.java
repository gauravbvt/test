package com.mindalliance.channels.odb;

import com.mindalliance.channels.dao.PlanDefinition;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2010
 * Time: 10:38:04 PM
 */
public class DefaultODBTransactionFactory implements ODBTransactionFactory {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultODBTransactionFactory.class );

    private Resource odbDir;

    private String fileName = "db";
    private static final int MAX_ATTEMPTS = 30;
    private static final long WAIT_TIME = 100;

    public DefaultODBTransactionFactory() {
    }

    @Override
    public ODB openDatabase( String planUri ) throws IOException {
        int attempts = 0;
        File planDir = new File( odbDir.getFile(), PlanDefinition.sanitize( planUri ) );
        File path = new File( planDir, fileName );
        do {
            try {
                return ODBFactory.open( path.getAbsolutePath() );
            } catch ( ODBRuntimeException e ) {
                LOG.info( "Database of " + planUri + " is locked. Retrying" );
                try {
                    attempts++;
                    Thread.sleep( WAIT_TIME );
                } catch ( InterruptedException e1 ) {
                    // do nothing
                }
            }
        } while ( attempts < MAX_ATTEMPTS );
        throw new IOException( "Failed to open database for " + planUri );
    }

    @Override
    public ODBAccessor getODBAccessor( String planUri ) {
        return new ODBAccessor( this, planUri );
    }

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
