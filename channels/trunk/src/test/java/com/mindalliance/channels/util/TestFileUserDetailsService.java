package com.mindalliance.channels.util;

import junit.framework.TestCase;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.wicket.util.file.File;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;

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
            fail( "Found non existant user" );
        } catch ( UsernameNotFoundException e ) {
            // ok
        }
    }

    public void testDefaultData() {
        service.setDefaultDefinitions( new FileSystemResource( "src/main/webapp/WEB-INF/users.properties" ) );
        String user = "denis";
        FileUserDetailsService.Details details = (FileUserDetailsService.Details)
                service.loadUserByUsername( user );
        assertNotNull( details );

        assertEquals( user, details.getUsername() );
        assertTrue( details.isEnabled() );
        assertTrue( details.isAccountNonExpired() );
        assertTrue( details.isAccountNonLocked() );
        assertTrue( details.isCredentialsNonExpired() );

        GrantedAuthority[] authorities = details.getAuthorities();
        assertEquals( "ROLE_ADMIN", authorities[0].getAuthority() );
        assertEquals( "ROLE_PLANNER", authorities[1].getAuthority() );
        assertEquals( "ROLE_USER", authorities[2].getAuthority() );

        assertEquals( 0L, details.getDefaultPlan() );
    }


    public void testUserData() {
        service.setBase( System.getProperty( "user.dir" ) );
        service.setUserDefinitions( "src/main/webapp/WEB-INF/users.properties" );
        String user = "denis";
        FileUserDetailsService.Details details = (FileUserDetailsService.Details)
                service.loadUserByUsername( user );
        assertNotNull( details );

        assertEquals( user, details.getUsername() );
        assertTrue( details.isEnabled() );
        assertTrue( details.isAccountNonExpired() );
        assertTrue( details.isAccountNonLocked() );
        assertTrue( details.isCredentialsNonExpired() );

        GrantedAuthority[] authorities = details.getAuthorities();
        assertEquals( "ROLE_ADMIN", authorities[0].getAuthority() );
        assertEquals( "ROLE_PLANNER", authorities[1].getAuthority() );
        assertEquals( "ROLE_USER", authorities[2].getAuthority() );

        assertEquals( 0L, details.getDefaultPlan() );
    }

    public void testInitialCopy() throws IOException {
        service.setDefaultDefinitions( new FileSystemResource( "src/main/webapp/WEB-INF/users.properties" ) );
        service.setBase( System.getProperty( "user.dir" ) );
        service.setUserDefinitions( "target/users.properties" );

        assertNotNull( service.loadUserByUsername( "guest" ) );
        assertTrue( new File( service.getUserDefinitions() ).exists() );

//        service.getUserDefinitions().getFile().delete();

    }

}
