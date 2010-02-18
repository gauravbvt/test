// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer;

import com.mindalliance.mindpeer.model.User;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.NotReadablePropertyException;
import static org.springframework.security.access.AccessDecisionVoter.*;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * ...
 */
public class TestUserVoter {

    private UserVoter userVoter;

    private User user1;

    /**
     * Create a new TestUserVoter instance.
     */
    public TestUserVoter() {
    }

    /**
     * ...
     */
    @Before
    public void init() {
        userVoter = new UserVoter();
        user1 = new User();
        user1.setId( 1L );
    }

    @Test
    public void testSupportClass() {
        assertTrue( userVoter.supports( Object.class ) );
    }

    /**
     * ...
     */
    @Test
    public void testSupports() {
        assertFalse( userVoter.supports( new ConfigAttribute() {
            public String getAttribute() {
                return null;
            }
        }  ) );
        assertTrue( userVoter.supports( new SecurityConfig( "USER" ) ) );
        assertTrue( userVoter.supports( new SecurityConfig( "USER==path" ) ) );
        assertFalse( userVoter.supports( new SecurityConfig( "USERID" ) ) );
        assertFalse( userVoter.supports( new SecurityConfig( "USER==" ) ) );
        assertFalse( userVoter.supports( new SecurityConfig( "User" ) ) );
    }

    /**
     * ...
     */
    @Test
    public void testVote() {
        User user2 = new User();
        user2.setId( 2L );

        UsernamePasswordAuthenticationToken auth = auth( user1 );
        assertSame( ACCESS_GRANTED, userVoter.vote( auth, user1, perms( "ROLE_ADMIN", "USER" ) ) );
        assertSame( ACCESS_GRANTED, userVoter.vote( auth, this, perms( "USER==user" ) ) );
        assertSame( ACCESS_DENIED, userVoter.vote( auth( user2 ), this, perms( "USER==user" ) ) );

        assertSame( ACCESS_DENIED, userVoter.vote( auth, user2, perms( "USER" ) ) );

        try {
            userVoter.vote( auth( user2 ), this, perms( "USER==oops" ) );
            fail();
        } catch ( NotReadablePropertyException ignored ) {
        }

        assertSame( ACCESS_ABSTAIN, userVoter.vote( auth, user1, perms( "ROLE_ADMIN", "BLA" ) ) );
        assertSame( ACCESS_ABSTAIN, userVoter.vote( auth, user1, perms( "USERS" ) ) );
        assertSame( ACCESS_ABSTAIN, userVoter.vote( auth, user1, perms( "USERS==" ) ) );
    }

    /**
     * ...
     *
     * @param user of type User
     * @return UsernamePasswordAuthenticationToken
     */
    private static UsernamePasswordAuthenticationToken auth( User user ) {
        return new UsernamePasswordAuthenticationToken( user, "bla" );
    }

    /**
     * ...
     *
     * @param attributeTokens of type String...
     * @return ConfigAttributeDefinition
     */
    private static Collection<ConfigAttribute> perms( String... attributeTokens ) {
        List<ConfigAttribute> result = new ArrayList<ConfigAttribute>();
        for ( String s : attributeTokens )
            result.add( new SecurityConfig( s ) );
        return result;
    }

    public User getUser() {
        return user1;
    }
}
