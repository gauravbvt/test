package com.mindalliance.channels.util;

import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.acegisecurity.userdetails.memory.InMemoryDaoImpl;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

import java.io.IOException;
import java.util.Properties;

/**
 * A user details service that keeps in sync with changes to the underlying user definition file.
 */
public class FileUserDetailsService extends InMemoryDaoImpl {

    private Resource userDefinitions;

    private long lastModified;


    public FileUserDetailsService() {
    }

    public Resource getUserDefinitions() {
        return userDefinitions;
    }

    public void setUserDefinitions( Resource userDefinitions ) {
        this.userDefinitions = userDefinitions;
        try {
            LoggerFactory.getLogger( getClass() ).debug( "Loading user definitions" );
            lastModified = userDefinitions.lastModified();
            Properties properties = new Properties();
            properties.load( userDefinitions.getInputStream() );
            setUserProperties( properties );

        } catch ( IOException e ) {
            LoggerFactory.getLogger( getClass() ).error( "Unable to load user definitions", e );
        }
    }

    @Override
    public UserDetails loadUserByUsername( String username )
            throws UsernameNotFoundException, DataAccessException {
        try {
            if ( userDefinitions.lastModified() > lastModified )
                setUserDefinitions( userDefinitions );
        } catch ( IOException e ) {
            String msg = "Unable to get modification date";
            LoggerFactory.getLogger( getClass() ).error( msg, e );
            throw new DataRetrievalFailureException( msg, e );
        }
        return super.loadUserByUsername( username );
    }
}
