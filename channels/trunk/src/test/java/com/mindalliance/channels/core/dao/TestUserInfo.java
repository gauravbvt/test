// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.model.Plan;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
public class TestUserInfo {

    @Test
    public void test1() {
        UserInfo info = new UserInfo( "bob", "2222,Bob Epine,bob@example.com,ROLE_USER,ignored?" );
        assertEquals( "bob", info.getUsername() );
        assertTrue( info.isUser() );
        assertFalse( info.isPlanner() );
        assertTrue( info.isUser( "bla" ) );
        assertTrue( info.isUser( null ) );
        assertFalse( info.isPlanner( "bla" ) );
        assertFalse( info.isPlanner( null ) );
        assertFalse( info.isAdmin() );
    }

    @Test
    public void test2() {
        UserInfo info = new UserInfo( "bob",
            "2222,Bob Epine,bob@example.com,[bla|ROLE_USER],ROLE_PLANNER" );

        assertTrue( info.isUser() );
        assertTrue( info.isPlanner() );
        assertTrue( info.isUser( "bla" ) );
        assertTrue( info.isPlanner( "bla" ) );
        assertFalse( info.isAdmin() );

        assertEquals( "2222,Bob Epine,bob@example.com,ROLE_PLANNER", info.toString() );
    }

    @Test
    public void test3() {
        UserInfo info = new UserInfo( "bob",
                "2222,Bob Epine,bob@example.com,[bla|ROLE_USER],[foo|ROLE_PLANNER],ROLE_USER" );

        assertTrue( info.isUser() );
        assertFalse( info.isPlanner() );
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
    public void test5() {
        UserInfo info = new UserInfo( "bob", "2222,Bob Epine,bob@example.com" );

        assertFalse( info.isUser( "bla" ) );
        assertFalse( info.isPlanner( "bla" ) );
        assertFalse( info.isAdmin() );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );

        assertEquals( "2222,Bob Epine,bob@example.com", info.toString() );
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

    @Test
    public void testSetter1() {
        List<Plan> planList = new ArrayList<Plan>();

        UserInfo  info = new UserInfo( "bob", "4444,Joe Shmoe,joe@example.com" );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertFalse( info.isUser( "foo" ) );
        assertFalse( info.isEnabled() );

        info.setAuthorities( UserInfo.ROLE_USER, "foo", planList );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertTrue( info.isUser( "foo" ) );
        assertTrue( info.isEnabled() );

        info.setAuthorities( null, "foo", planList );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertFalse( info.isUser( "foo" ) );
        assertFalse( info.isEnabled() );
    }

    @Test
    public void testSetter2() {
        List<Plan> planList = new ArrayList<Plan>();
        Plan plan = new Plan();
        plan.setUri( "bar" );
        planList.add( plan );

        UserInfo  info = new UserInfo( "bob", "4444,Joe Shmoe,joe@example.com" );

        info.setAuthorities( UserInfo.ROLE_ADMIN, "foo", planList );
        assertTrue( info.isUser() );
        assertTrue( info.isPlanner() );
        assertTrue( info.isAdmin() );
        assertTrue( info.isUser( "foo" ) );
        assertTrue( info.isUser( "bar" ) );
        assertTrue( info.isEnabled() );

        info.setAuthorities( null, "foo", planList );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertFalse( info.isUser( "foo" ) );
        assertTrue( info.isPlanner( "bar" ) );
        assertTrue( info.isEnabled() );
    }

    @Test
    public void testSetter3() {
        List<Plan> planList = new ArrayList<Plan>();
        Plan plan = new Plan();
        plan.setUri( "bar" );
        planList.add( plan );

        UserInfo  info = new UserInfo( "bob", "4444,Joe Shmoe,joe@example.com" );

        info.setAuthorities( UserInfo.ROLE_PLANNER, "foo", planList );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertTrue( info.isUser( "foo" ) );
        assertTrue( info.isPlanner( "foo" ) );
        assertFalse( info.isUser( "bar" ) );
        assertTrue( info.isEnabled() );

        info.setAuthorities( null, "foo", planList );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertFalse( info.isUser( "foo" ) );
        assertFalse( info.isPlanner( "bar" ) );
        assertFalse( info.isEnabled() );
    }

    @Test
    public void testSetter4() {
        List<Plan> planList = new ArrayList<Plan>();
        Plan plan = new Plan();
        plan.setUri( "bar" );
        planList.add( plan );

        UserInfo  info = new UserInfo( "bob", "4444,Joe Shmoe,joe@example.com" );

        info.setAuthorities( UserInfo.ROLE_USER, "foo", planList );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertTrue( info.isUser( "foo" ) );
        assertFalse( info.isPlanner( "foo" ) );
        assertFalse( info.isUser( "bar" ) );
        assertTrue( info.isEnabled() );

        info.setAuthorities( null, "foo", planList );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertFalse( info.isUser( "foo" ) );
        assertFalse( info.isPlanner( "bar" ) );
        assertFalse( info.isEnabled() );
    }

    @Test
    public void testSetter5() {
        List<Plan> planList = new ArrayList<Plan>();
        Plan plan = new Plan();
        plan.setUri( "bar" );
        planList.add( plan );

        UserInfo  info = new UserInfo( "bob", "4444,Joe Shmoe,joe@example.com" );

        info.setAuthorities( UserInfo.ROLE_USER, null, planList );
        assertTrue( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertTrue( info.isUser( "foo" ) );
        assertFalse( info.isPlanner( "foo" ) );
        assertTrue( info.isUser( "bar" ) );
        assertTrue( info.isEnabled() );

        info.setAuthorities( UserInfo.ROLE_PLANNER, "bar", planList );
        assertTrue( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertTrue( info.isUser( "foo" ) );
        assertTrue( info.isPlanner( "bar" ) );
        assertTrue( info.isUser( "bar" ) );
        assertTrue( info.isEnabled() );

        info.setAuthorities( null, "foo", planList );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertFalse( info.isUser( "foo" ) );
        assertTrue( info.isUser( "bar" ) );
        assertTrue( info.isPlanner( "bar" ) );
        assertTrue( info.isEnabled() );

        info.setAuthorities( null, "bar", planList );
        assertFalse( info.isUser( "bar" ) );
        assertFalse( info.isEnabled() );
    }

    @Test
    public void testSetter6() {
        List<Plan> planList = new ArrayList<Plan>();
        Plan plan = new Plan();
        plan.setUri( "foo" );
        Plan planB = new Plan();
        planB.setUri( "bar" );
        planList.add( plan );
        planList.add( planB );

        UserInfo  info = new UserInfo( "bob", "4444,Joe Shmoe,joe@example.com" );

        info.setAuthorities( UserInfo.ROLE_PLANNER, null, planList );
        assertTrue( info.isUser() );
        assertTrue( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertTrue( info.isPlanner( "foo" ) );
        assertTrue( info.isPlanner( "bar" ) );
        assertTrue( info.isPlanner( "baz" ) );
        assertTrue( info.isEnabled() );

        info.setAuthorities( UserInfo.ROLE_PLANNER, "foo", planList );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertTrue( info.isPlanner( "foo" ) );
        assertTrue( info.isPlanner( "bar" ) );
        assertFalse( info.isPlanner( "baz" ) );
        assertTrue( info.isEnabled() );
    }

    @Test
    public void testSetter7() {
        List<Plan> planList = new ArrayList<Plan>();
        Plan plan = new Plan();
        plan.setUri( "foo" );
        Plan planB = new Plan();
        planB.setUri( "bar" );
        planList.add( plan );
        planList.add( planB );

        UserInfo  info = new UserInfo( "bob", "4444,Joe Shmoe,joe@example.com" );

        info.setAuthorities( UserInfo.ROLE_USER, null, planList );
        assertTrue( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertTrue( info.isUser( "foo" ) );
        assertTrue( info.isUser( "bar" ) );
        assertTrue( info.isUser( "baz" ) );
        assertTrue( info.isEnabled() );

        info.setAuthorities( UserInfo.ROLE_USER, "foo", planList );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertTrue( info.isUser( "foo" ) );
        assertTrue( info.isUser( "bar" ) );
        assertFalse( info.isUser( "baz" ) );
        assertTrue( info.isEnabled() );

        info.setAuthorities( UserInfo.ROLE_PLANNER, "foo", planList );
        info.setAuthorities( UserInfo.ROLE_USER, null, planList );
        assertTrue( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertTrue( info.isPlanner( "foo" ) );
        assertTrue( info.isUser( "bar" ) );
        assertTrue( info.isUser( "baz" ) );
        assertTrue( info.isEnabled() );

        info.setAuthorities( null, null, planList );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isAdmin() );
        assertFalse( info.isUser() );
        assertFalse( info.isPlanner() );
        assertFalse( info.isEnabled() );

    }
}
