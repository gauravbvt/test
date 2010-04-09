// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.dao;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * ...
 */
public class TestUserInfo {

    @Test
    public void test1() {
        UserInfo info = new UserInfo( "bob", "2222,Bob Epine,bob@example.com,ROLE_USER,ignored?" );

        assertTrue( info.isUser( "bla" ) );
        assertFalse( info.isUser( null ) );
        assertFalse( info.isPlanner( "bla" ) );
        assertFalse( info.isPlanner( null ) );
        assertFalse( info.isAdmin() );
    }

    @Test
    public void test2() {
        UserInfo info = new UserInfo( "bob",
            "2222,Bob Epine,bob@example.com,[bla|ROLE_USER],ROLE_PLANNER" );

        assertTrue( info.isUser( "bla" ) );
        assertTrue( info.isPlanner( "bla" ) );
        assertFalse( info.isAdmin() );

        assertEquals( "2222,Bob Epine,bob@example.com,ROLE_PLANNER", info.toString() );
    }

    @Test
    public void test3() {
        UserInfo info = new UserInfo( "bob",
                "2222,Bob Epine,bob@example.com,[bla|ROLE_USER],[foo|ROLE_PLANNER],ROLE_USER" );

        assertTrue( info.isUser( "bla" ) );
        assertTrue( info.isUser( "foo" ) );
        assertTrue( info.isUser( "bar" ) );
        assertFalse( info.isPlanner( "bla" ) );
        assertFalse( info.isPlanner( "bar" ) );
        assertTrue( info.isPlanner( "foo" ) );
        assertFalse( info.isAdmin() );

        assertEquals( "2222,Bob Epine,bob@example.com,[foo|ROLE_PLANNER],ROLE_USER", info.toString() );
    }

    @Test
    public void test4() {
        UserInfo info = new UserInfo( "bob",
                "2222,Bob Epine,bob@example.com,[bla],[foo|ROLE_PLANNER],ROLE_USER" );

        assertTrue( info.isUser( "bla" ) );
        assertTrue( info.isUser( "foo" ) );
        assertFalse( info.isPlanner( "bla" ) );
        assertTrue( info.isPlanner( "foo" ) );
        assertFalse( info.isAdmin() );

        assertEquals( "2222,Bob Epine,bob@example.com,[foo|ROLE_PLANNER],ROLE_USER", info.toString() );
    }

    @Test
    public void testEnabled() {
        assertTrue( new UserInfo( "bob",
             "2222,Bob Epine,bob@example.com,[bla],[foo|ROLE_PLANNER],ROLE_USER" ).isEnabled() );
        assertTrue( new UserInfo( "bob",
             "2222,Bob Epine,bob@example.com,[bla]" ).isEnabled() );
        assertTrue( new UserInfo( "bob",
             "2222,Bob Epine,bob@example.com,ROLE_USER" ).isEnabled() );
        assertFalse( new UserInfo( "bob",
             "2222,Bob Epine,bob@example.com" ).isEnabled() );
    }

    @Test
    public void testAccessors() {
        UserInfo info = new UserInfo( "bob",
                "2222,Bob Epine,bob@example.com,[bla],[foo|ROLE_PLANNER],ROLE_USER" );

        assertEquals( "2222", info.getPassword() );
        info.setPassword( "bla" );
        assertEquals( "128ecf542a35ac5270a87dc740918404", info.getPassword() );

        assertEquals( "Bob Epine", info.getFullName() );
        info.setFullName( "Joe Blow" );
        assertEquals( "Joe Blow", info.getFullName() );

        assertEquals( "bob@example.com", info.getEmail() );
        info.setEmail( "joe@example.com" );
        assertEquals( "joe@example.com", info.getEmail() );
    }

}
