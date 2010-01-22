// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.dao.jpa;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import com.mindalliance.mindpeer.model.ModelObject;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
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
@ContextConfiguration
public class TestUserDaoImpl {

    @Autowired
    private UserDao userDao;

    private User user;

    public TestUserDaoImpl() {
    }

    @Before
    public void startTransaction() {
        user = new User();
        user.setUsername( "joe" );
        user.setPassword( "da39a3ee5e6b4b0d3255bfef95601890afd80709" );
        user = userDao.save( user );
    }

    @Test
    @Transactional
    @Rollback
    public void testFindAll() {
        List<User> users = new ArrayList<User>();
        users.add( user );
        assertEquals( users, userDao.findAll() );
    }

    @Test
    @Transactional
    @Rollback
    public void testLoadUserByUsername() {
        UserDetailsService uds = (UserDetailsService) userDao;
        User u = userDao.findByName( "joe" );
        UserDetails ud = uds.loadUserByUsername( "joe" );
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
        User u = userDao.findByName( "joe" );
        assertEquals( user, u );

        assertNull( userDao.findByName( "bob" ) );
    }

    @Test
    @Transactional
    @Rollback
    public void testCountAll() {
        assertEquals( 1, userDao.countAll() );
    }

    @Test
    @Transactional
    @Rollback
    public void testDelete() {
        userDao.delete( user );
        assertEquals( 0, userDao.countAll() );
    }

    @Test
    @Transactional
    @Rollback
    public void testLoad() {
        User user2 = userDao.load( user.getId() );
        assertEquals( user, user2 );

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
        //if we have got this far then save works
    }

    @Test
    public void testCurrentUser() {
        assertNull( userDao.currentUser() );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(
                new AnonymousAuthenticationToken(
                        "bla", "something",
                         new GrantedAuthority[]{ new GrantedAuthorityImpl( "ROLE_ANONYMOUS" ) } ) );
        assertNull( userDao.currentUser() );


        User bob = new User();
        context.setAuthentication(
                 new AnonymousAuthenticationToken(
                         "bla", bob,
                          new GrantedAuthority[]{ new GrantedAuthorityImpl( "ROLE_USER" ) } ) );
         assertEquals( bob, userDao.currentUser() );

    }


}
