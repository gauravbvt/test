package com.mindalliance.channels.odb;

import com.mindalliance.channels.dao.PlanDefinition;
import com.mindalliance.channels.dao.User;
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
    public ODB openDatabase() throws IOException {
        File planDir = new File( odbDir.getFile(), PlanDefinition.sanitize( getPlanUri() ) );
        File path = new File( planDir, fileName );
        return  ODBFactory.open( path.getAbsolutePath() );
    }

    @Override
    public ODBAccessor getODBAccessor() {
        return new ODBAccessor( this );
    }

    private String getPlanUri() {
        return User.current().getPlan().getUri();
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
