package com.mindalliance.channels.dao;

import junit.framework.TestCase;
import org.apache.wicket.util.file.File;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.List;

/**
 * ...
 */
public class TestFileUserDetailsService extends TestCase {

    private FileUserDetailsService service;

    public TestFileUserDetailsService() {
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = new FileUserDetailsService();
    }

    public void testNoData() {
        try {
            service.loadUserByUsername( "bob" );
            fail( "Found non existent user" );
        } catch ( IllegalStateException ignored ) {
            // ok
        }
    }

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
        assertEquals( 1, authorities.size() );
        assertEquals( "ROLE_ADMIN", authorities.get(0).getAuthority() );

        assertNull( details.getPlan() );
    }


    public void testUserData() {
        service.setBase( System.getProperty( "user.dir" ) );
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
        assertEquals( 1, authorities.size() );
        assertEquals( "ROLE_ADMIN", authorities.get(0).getAuthority() );

        assertNull( details.getPlan() );
    }

    public void testInitialCopy() throws IOException {
        service.setDefaultDefinitions( new FileSystemResource( "src/main/webapp/WEB-INF/users.properties" ) );
        service.setBase( System.getProperty( "user.dir" ) );
        service.setUserDefinitions( "target/users.properties" );

        assertNotNull( service.loadUserByUsername( "guest" ) );
        assertTrue( new File( service.getUserDefinitions() ).exists() );

        new File( service.getBase(), service.getUserDefinitions() ).delete();

    }

    public void testJf() {
        service.setDefaultDefinitions( new FileSystemResource( "src/main/webapp/WEB-INF/users.properties" ) );
        service.setBase( System.getProperty( "user.dir" ) );
        service.setUserDefinitions( "target/users.properties" );
        User jf = (User) service.loadUserByUsername( "jf" );
        assertTrue( jf.isAdmin() );
        assertTrue( jf.isPlanner( "mindalliance.com/channels/plans/acme" ) );

        User david = (User) service.loadUserByUsername( "david" );
        assertFalse( david.isAdmin() );
        assertTrue( david.isPlanner( "mindalliance.com/channels/plans/acme" ) );
    }

}
