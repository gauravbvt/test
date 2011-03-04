package com.mindalliance.channels.odb;

import com.mindalliance.channels.dao.PlanDefinition;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
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

    private Resource odbDir;

    private String fileName = "db";

    public DefaultODBTransactionFactory() {
    }

    @Override
    public ODB openDatabase( String planUri ) throws IOException {
        File planDir = new File( odbDir.getFile(), PlanDefinition.sanitize( planUri ) );
        File path = new File( planDir, fileName );
        return  ODBFactory.open( path.getAbsolutePath() );
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
     * @return the name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the default file name of the database.
     * @param fileName the name
     */
    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }
}
