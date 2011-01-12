package com.mindalliance.channels.dao;

import org.apache.wicket.util.file.File;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.List;

/**
 * ...
 */
public class TestFileUserDetailsService {

    private FileUserDetailsService service;

    public TestFileUserDetailsService() {
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    @Before
    public void setUp() throws Exception {
        service = new FileUserDetailsService();
    }

    @Test
    public void testNoData() {
        try {
            service.loadUserByUsername( "bob" );
            fail( "Found non existent user" );
        } catch ( IllegalStateException ignored ) {
            // ok
        }
    }

    @Test
    public void testDefaultData() {
        service.setDefaultDefinitions( new FileSystemResource( "src/main/webapp/WEB-INF/users.properties" ) );
        String user = "denis";
        User details = (User) service.loadUserByUsername( user );
        assertNotNull( details );

        assertEquals( user, details.getUsername() );
        assertTrue( details.isEnabled() );
        assertTrue( details.isAccountNonExpired() );
        assertTrue( details.isAccountNonLocked() );
        assertTrue( details.isCredentialsNonExpired() );

        List<GrantedAuthority> authorities = (List<GrantedAuthority>) details.getAuthorities();
        assertEquals( 3, authorities.size() );
        assertEquals( "ROLE_ADMIN", authorities.get(0).getAuthority() );

        assertNull( details.getPlan() );
    }

    @Test
    public void testUserData() {
        service.setBase( new FileSystemResource( System.getProperty( "user.dir" ) ) );
        service.setUserDefinitions( "src/main/webapp/WEB-INF/users.properties" );
        String user = "denis";
        User details = (User) service.loadUserByUsername( user );
        assertNotNull( details );

        assertEquals( user, details.getUsername() );
        assertTrue( details.isEnabled() );
        assertTrue( details.isAccountNonExpired() );
        assertTrue( details.isAccountNonLocked() );
        assertTrue( details.isCredentialsNonExpired() );

        List<GrantedAuthority> authorities = (List<GrantedAuthority>) details.getAuthorities();
        assertEquals( 3, authorities.size() );
        assertEquals( "ROLE_ADMIN", authorities.get(0).getAuthority() );

        assertNull( details.getPlan() );
    }

    @Test
    public void testInitialCopy() throws IOException {
        service.setDefaultDefinitions( new FileSystemResource( "src/main/webapp/WEB-INF/users.properties" ) );
        service.setBase( new FileSystemResource( System.getProperty( "user.dir" ) ) );
        service.setUserDefinitions( "target/users.properties" );

        assertNotNull( service.loadUserByUsername( "guest" ) );
        assertTrue( new File( service.getUserDefinitions() ).exists() );

        new File( service.getBase().getFile(), service.getUserDefinitions() ).delete();

    }

    @Test
    public void testJf() {
        service.setDefaultDefinitions( new FileSystemResource( "src/main/webapp/WEB-INF/users.properties" ) );
        service.setBase( new FileSystemResource( System.getProperty( "user.dir" ) ) );
        service.setUserDefinitions( "target/users.properties" );
        User jf = (User) service.loadUserByUsername( "jf" );
        assertTrue( jf.isAdmin() );
        assertTrue( jf.isPlanner( "mindalliance.com/channels/plans/acme" ) );

        User david = (User) service.loadUserByUsername( "david" );
        assertFalse( david.isAdmin() );
        assertTrue( david.isPlanner( "mindalliance.com/channels/plans/acme" ) );
    }

}
