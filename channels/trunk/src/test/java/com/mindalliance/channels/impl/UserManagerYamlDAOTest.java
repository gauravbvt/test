// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.mindalliance.channels.model.support.SuggestionTest;

import junit.framework.JUnit4TestAdapter;

/**
 * Simplistic test for UserManagerYamlDAO.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class UserManagerYamlDAOTest {

    private UserManagerYamlDAO dao;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp() {
        this.dao = new UserManagerYamlDAO();
    }

    private UserManager createUserManager() {
        UserManager result = new UserManager();
        UserImpl admin = new UserImpl( "admin", "admin",
                new GrantedAuthority[]{
                    new GrantedAuthorityImpl( "ROLE_ADMIN" ),
                    new GrantedAuthorityImpl( "ROLE_SUPERVISOR" ),
                    new GrantedAuthorityImpl( "ROLE_USER" ),
                } );
        admin.setName( "Administrator" );
        admin.setEmail( "test@example.com" );
        result.addUser( admin );

        UserImpl user = new UserImpl( "user", "user",
                new GrantedAuthority[]{
                    new GrantedAuthorityImpl( "ROLE_USER" ),
                } );
        admin.setName( "Joe User" );
        admin.setEmail( "user@example.com" );
        result.addUser( user );

        return result;
    }

    /**
     * Test method for
     * {@link UserManagerYamlDAO#save(UserManager, OutputStream)}.
     * @throws IOException on errors
     */
    @Test
    public final void testSaveLoad() throws IOException {
        File test = new File( "test.yml" );
        UserManager mgr = createUserManager();

        OutputStream out = new FileOutputStream( test );
        this.dao.save( mgr, out );
        out.close();

        InputStream in = new FileInputStream( test );
        UserManager mgr2 = this.dao.load( in );
        in.close();

        for ( String u : new String[]{ "admin", "user" } )
            assertEquals(
                mgr.loadUserByUsername( u ),
                mgr2.loadUserByUsername( u ) );

        test.delete();
    }

    /**
     * Needed for compatibility when running inside eclipse...
     * @return a compatibility adapter
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter( SuggestionTest.class );
    }
}
