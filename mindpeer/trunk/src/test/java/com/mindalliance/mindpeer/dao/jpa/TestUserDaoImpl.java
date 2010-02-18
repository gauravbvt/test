// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.dao.jpa;

import com.mindalliance.mindpeer.SecuredAspects;
import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.ModelObject;
import com.mindalliance.mindpeer.model.User;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/applicationContext.xml", "/integratedTestContext.xml" } )
public class TestUserDaoImpl {

    @Autowired
    private UserDao userDao;

    private User user;

    public TestUserDaoImpl() {
    }

    @Before
    public void init() {
        SecuredAspects.bypass( true );
        user = new User();
        user.setUsername( "joe" );
        user.setPassword( "da39a3ee5e6b4b0d3255bfef95601890afd80709" );
        user.setEmail( "joe@example.com" );
    }

    @After
    public void cleanUp() {
        SecuredAspects.bypass( false );
    }

    @Test
    @Transactional
    @Rollback
    public void testFindAll() {
        assertEquals( 2, userDao.findAll().size() );
    }

    @Test
    @Transactional
    @Rollback
    public void testLoadUserByUsername() {
        UserDetailsService uds = (UserDetailsService) userDao;
        User u = userDao.findByName( "Guest" );
        UserDetails ud = uds.loadUserByUsername( "guest" );
        assertEquals( u, ud );

        try {
            uds.loadUserByUsername( "bob" );
            fail();
        } catch ( UsernameNotFoundException ignored ) {
            // yay
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByName() {
        assertNull( userDao.findByName( null ) );

        userDao.save( user );
        assertNotNull( userDao.findByName( "joe" ) );
        assertNull( userDao.findByName( "bob" ) );
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByEmail() {
        assertNull( userDao.findByEmail( null ) );

        User u = userDao.findByEmail( "guest@example.com" );
        assertEquals( "guest", u.getUsername() );

        assertNull( userDao.findByEmail( "bob@example.com" ) );
    }

    @Test
    @Transactional
    @Rollback
    public void testCountAll() {
        userDao.save( user );
        assertEquals( 3, userDao.countAll() );
    }

    @Test
    @Transactional
    @Rollback
    public void testDelete() {
        assertEquals( 2, userDao.countAll() );

        User u = userDao.save( user );
        userDao.delete( u );
        assertEquals( 2, userDao.countAll() );

        userDao.delete( userDao.findByName( "guest" ) );
        assertEquals( 1, userDao.countAll() );
    }

    @Test
    @Transactional
    @Rollback
    public void testLoad() {
        User user1 = userDao.save( user );
        User user2 = userDao.load( user1.getId() );
        assertEquals( user1, user2 );

        User user3 = userDao.load( Long.valueOf( -1L ) );
        assertNull( user3 );
    }

    /**
     * Test method for {@link AbstractDaoImpl#save(ModelObject)}.
     */
    @Test
    @Transactional
    @Rollback
    public void testSave() {
        User u1 = userDao.save( user );

        User u2 = new User();
        u2.setUsername( "joe" );
        u2.setEmail( "bob@example.com" );

        try {
            userDao.save( u2 );
            fail();
        } catch ( JpaSystemException e ) {
            String message = e.getMostSpecificCause().getMessage();
            assertTrue( message.startsWith( "Unique index or primary key violation" ) );
        }

        u2.setUsername( "bob" );
        u2.setEmail( "joe@example.com" );
        try {
            userDao.save( u2 );
            fail();
        } catch ( JpaSystemException e ) {
            String message = e.getMostSpecificCause().getMessage();
            assertTrue( message.startsWith( "Unique index or primary key violation" ) );
        }

    }

    @Test
    public void testCurrentUser() {
        //assertNull( userDao.currentUser().getUsername() );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(
            new AnonymousAuthenticationToken( "bla", "something", authList( "ROLE_ANONYMOUS" ) ) );
        assertNull( userDao.currentUser() );

        User bob = new User();
        context.setAuthentication(
            new AnonymousAuthenticationToken( "bla", bob, authList( "ROLE_USER" ) ) );
         assertEquals( bob, userDao.currentUser() );
    }

    private static List<GrantedAuthority> authList( String... authority ) {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        for ( String a : authority )
            list.add( new GrantedAuthorityImpl( a ) );

        return list;
    }
}
