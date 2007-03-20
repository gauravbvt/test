// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.system;

import static org.junit.Assert.*;

import java.beans.PropertyVetoException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.junit.Before;
import org.junit.Test;

import com.mindalliance.channels.Project;
import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;


/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class SystemImplTest {
    
    private SystemImpl system;
    private UserImpl user;
    private UserImpl admin;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        system = new SystemImpl();
        user = new UserImpl( "User", "user", "pass1", new String[]{ "ROLE_USER" } );
        admin = new UserImpl( "Admin", "admin", "pass2", new String[]{ "ROLE_USER", "ROLE_ADMIN" } );
    }

    /**
     * Test method for {@link SystemImpl#addProject(com.mindalliance.channels.Project)}.
     * @throws PropertyVetoException 
     */
    @Test
    public final void testAddRemoveProject() throws PropertyVetoException {
        assertEquals( 0, system.getProjects().size() );
        ProjectImpl project = new ProjectImpl( "test" );
        
        system.addProject( project );
        assertEquals( 1, system.getProjects().size() );
        assertTrue( system.getProjects().contains( project ) );
        assertEquals( 1, project.getVetoableChangeListeners( "name" ).length );

        system.addProject( project );
        assertEquals( 1, project.getVetoableChangeListeners( "name" ).length );
        assertEquals( 1, system.getProjects().size() );
        assertTrue( system.getProjects().contains( project ) );

        ProjectImpl project2 = new ProjectImpl( "test2" );
        
        system.addProject( project2 );
        assertEquals( 2, system.getProjects().size() );
        assertTrue( system.getProjects().contains( project ) );
        assertTrue( system.getProjects().contains( project2 ) );
        
        system.removeProject( project );
        assertEquals( 0, project.getVetoableChangeListeners( "name" ).length );
        assertEquals( 1, system.getProjects().size() );
        assertTrue( system.getProjects().contains( project2 ) );
    }

    /**
     * Test method for {@link SystemImpl#setProjects(java.util.SortedSet)}.
     */
    @Test( expected = NullPointerException.class )
    public final void testSetProjects() {
        system.setProjects( null );
        fail();
    }

    /**
     * Test method for {@link SystemImpl#setProjects(java.util.SortedSet)}.
     * @throws PropertyVetoException 
     */
    @Test
    public final void testSetProjects_1() throws PropertyVetoException {
        SortedSet<Project> projects = new TreeSet<Project>();
        
        system.setProjects( projects );
        assertEquals( 0, system.getProjects().size() );
        
        ProjectImpl p = new ProjectImpl( "test" );
        projects.add( p );
        assertEquals( 0, system.getProjects().size() );
        assertEquals( 0, p.getVetoableChangeListeners( "name" ).length );

        system.setProjects( projects );
        assertEquals( 1, system.getProjects().size() );
        assertTrue( system.getProjects().contains( p ) );
        assertEquals( 1, p.getVetoableChangeListeners( "name" ).length );
        
        system.setProjects( projects );
        assertEquals( 1, system.getProjects().size() );
        assertTrue( system.getProjects().contains( p ) );
        assertEquals( 1, p.getVetoableChangeListeners( "name" ).length );
    }

    /**
     * Test method for {@link SystemImpl#addAdministrator(com.mindalliance.channels.User)}.
     * @throws UserExistsException never
     */
    @Test
    public final void testAddAdministrator() throws UserExistsException {
        assertFalse( system.isAdministrator( admin ) );
        assertFalse( system.getUsers().contains( admin ) );
        
        system.addAdministrator( admin );
        
        assertTrue( system.isAdministrator( admin ) );
        assertTrue( system.getUsers().contains( admin ) );
        assertEquals( 1, system.getAdministrators().size() );
        assertTrue( system.getAdministrators().contains( admin ) );
        assertEquals( 1, admin.getVetoableChangeListeners( "username" ).length );
    }

    /**
     * Test method for {@link SystemImpl#getAdministrators()}.
     * @throws UserExistsException never
     */
    @Test
    public final void testGetAdministrators() throws UserExistsException {
        assertEquals( 0, system.getAdministrators().size() );
        system.addAdministrator( user );
        assertEquals( 1, system.getAdministrators().size() );
        assertTrue( system.getAdministrators().contains( user ) );
        assertEquals( 1, user.getVetoableChangeListeners( "username" ).length );
        
        system.addAdministrator( admin );
        assertEquals( 2, system.getAdministrators().size() );
        assertTrue( system.getAdministrators().contains( user ) );
        assertTrue( system.getAdministrators().contains( admin ) );
        assertEquals( 1, admin.getVetoableChangeListeners( "username" ).length );
        
        system.removeAdministrator( user );
        assertEquals( 1, system.getAdministrators().size() );
        assertTrue( system.getAdministrators().contains( admin ) );
        assertEquals( 1, admin.getVetoableChangeListeners( "username" ).length );
    }

    /**
     * Test method for {@link SystemImpl#setAdministrators(java.util.SortedSet)}.
     * @throws UserExistsException never
     */
    @Test( expected = NullPointerException.class )
    public final void testSetAdministrators() throws UserExistsException {
        system.setAdministrators( null );
        fail();
    }

    /**
     * Test method for {@link SystemImpl#setAdministrators(java.util.SortedSet)}.
     * @throws UserExistsException never
     */
    @Test
    public final void testSetAdministrators_1() throws UserExistsException {
        TreeSet<User> users = new TreeSet<User>();
        system.setAdministrators( users );
        assertEquals( 0, system.getAdministrators().size() );
        
        users.add( user );
        assertEquals( 0, system.getAdministrators().size() );
        system.setAdministrators( users );
        assertEquals( 1, system.getAdministrators().size() );
        assertTrue( system.isAdministrator( user ) );        
    }

    /**
     * Test method for {@link SystemImpl#addUser(User)}.
     * @throws UserExistsException never
     */
    @Test
    public final void testAddUser_1() throws UserExistsException {
        assertEquals( 0, system.getUsers().size() );
        system.addUser( user );
        assertTrue( system.isUser( user ) );
        assertFalse( system.isAdministrator( user ) );
        assertEquals( 1, system.getUsers().size() );
        assertSame( user, system.getUsers().iterator().next() );
    }

    /**
     * Test method for {@link SystemImpl#addUser(User)}.
     * @throws UserExistsException never
     */
    @Test( expected = UserExistsException.class )
    public final void testAddUser_2() throws UserExistsException {
        system.addUser( user );
        system.addUser( user );
        fail();
    }

    /**
     * Test method for {@link SystemImpl#setUsers(java.util.SortedSet)}.
     * @throws UserExistsException never
     */
    @Test
    public final void testSetUsers() throws UserExistsException {
        TreeSet<User> users = new TreeSet<User>();
        system.setUsers( users );
        assertEquals( 0, system.getUsers().size() );
        
        users.add( user );
        assertEquals( 0, system.getUsers().size() );
        system.setUsers( users );
        assertEquals( 1, system.getUsers().size() );
        assertTrue( system.isUser( user ) );        
        assertEquals( 1, user.getVetoableChangeListeners( "username" ).length );

        system.setUsers( users );
        assertEquals( 1, system.getUsers().size() );
        assertTrue( system.isUser( user ) );        
        assertEquals( 1, user.getVetoableChangeListeners( "username" ).length );
    }

    /**
     * Test method for {@link SystemImpl#setUsers(java.util.SortedSet)}.
     * @throws UserExistsException never
     */
    @Test( expected = NullPointerException.class )
    public final void testSetUsers_1() throws UserExistsException {
        system.setUsers( null );
        fail();
    }

    /**
     * Test method for {@link SystemImpl#getProjects(User)}.
     * @throws UserExistsException never
     * @throws PropertyVetoException 
     */
    @Test
    public final void testGetProjectsUser() throws UserExistsException, PropertyVetoException {
        ProjectImpl p1 = new ProjectImpl( "project 1" );
        ProjectImpl p2 = new ProjectImpl( "project 2" );
        
        system.addProject( p1 );
        system.addProject( p2 );
        system.addUser( user );
        system.addAdministrator( admin );
        
        //---------
        p1.addManager( user );        
        Set<Project> projects = system.getProjects( user );
        
        assertEquals( 1, projects.size() );
        assertTrue( projects.contains( p1 ) );
        assertFalse( projects.contains( p2 ) );
        
        //---------
        p2.addParticipant( user );
        projects = system.getProjects( admin );

        assertEquals( 2, projects.size() );
        assertTrue( projects.contains( p1 ) );
        assertTrue( projects.contains( p2 ) );
        
        //---------
        projects = system.getProjects( admin );
        assertEquals( 2, projects.size() );
        assertTrue( projects.contains( p1 ) );
        assertTrue( projects.contains( p2 ) );
        
        
    }
    
    /**
     * Test method for {@link SystemImpl#loadUserByUsername(User)}.
     */
    @Test( expected = UsernameNotFoundException.class )
    public final void testLoadUserByUsername_1() throws UsernameNotFoundException {
        system.loadUserByUsername( "bob" );
    }
    
    /**
     * Test method for {@link SystemImpl#loadUserByUsername(User)}.
     * @throws UserExistsException 
     */
    @Test
    public final void testLoadUserByUsername_2() 
        throws UsernameNotFoundException, UserExistsException {
        
        system.addUser( user );
        User user2 = (User) system.loadUserByUsername( "user" );
        assertEquals( user, user2 );
    }
    
    /**
     * Test method for {@link SystemImpl#getProject(String)}.
     * @throws PropertyVetoException 
     */
    @Test
    public final void testGetProject_1() throws PropertyVetoException {
        ProjectImpl project = new ProjectImpl( "test" );
        system.addProject( project );

        assertNull( system.getProject( "bla" ) );
        assertNull( system.getProject( null ) );
        assertSame( project, system.getProject( "test" ) );
        
    }
    
    /**
     * Test method for renaming of projects.
     * @throws PropertyVetoException 
     */
    @Test
    public final void testProjectRename_1() throws PropertyVetoException {
        ProjectImpl project1 = new ProjectImpl( "test" );
        ProjectImpl project2 = new ProjectImpl( "test2" );
        system.addProject( project1 );
        system.addProject( project2 );
        
        assertSame( project1, system.getProject( "test" ) );
        assertSame( project2, system.getProject( "test2" ) );

        try {
            project2.setName( "test" );
            fail();
        } catch ( PropertyVetoException e ) {
            assertEquals( "name", e.getPropertyChangeEvent().getPropertyName() );
            assertEquals( "test", e.getPropertyChangeEvent().getNewValue() );
            assertEquals( "test2", e.getPropertyChangeEvent().getOldValue() );
        }
    }
    
    /**
     * Test method for renaming of projects.
     * @throws PropertyVetoException never
     */
    @Test
    public final void testProjectRename_2() throws PropertyVetoException {
        ProjectImpl project1 = new ProjectImpl( "test" );
        ProjectImpl project2 = new ProjectImpl( "test2" );
        system.addProject( project1 );
        system.addProject( project2 );
        
        assertSame( project1, system.getProject( "test" ) );
        assertSame( project2, system.getProject( "test2" ) );

        project2.setName( "bla" );
        assertNull( system.getProject( "test2" ) );
        assertSame( project2, system.getProject( "bla" ) );
        
    }
    
    /**
     * Test method for renaming of users.
     * @throws UserExistsException never
     */
    @Test
    public final void testUserRename_1() throws UserExistsException {
        system.addUser( user );
        system.addUser( admin );
        
        assertSame( user, system.loadUserByUsername( "user" ) );
        assertSame( admin, system.loadUserByUsername( "admin" ) );

        try {
            user.setUsername( "admin" );
            fail();
        } catch ( PropertyVetoException e ) {
            assertEquals( "username", e.getPropertyChangeEvent().getPropertyName() );
            assertEquals( "admin", e.getPropertyChangeEvent().getNewValue() );
            assertEquals( "user", e.getPropertyChangeEvent().getOldValue() );
        }
    }
    
    /**
     * Test method for renaming of users.
     * @throws UserExistsException never
     * @throws PropertyVetoException never
     */
    @Test
    public final void testUserRename_2() throws UserExistsException, PropertyVetoException {
        system.addUser( user );
        system.addAdministrator( admin );
        
        assertSame( user, system.loadUserByUsername( "user" ) );
        assertSame( admin, system.loadUserByUsername( "admin" ) );

        user.setUsername( "bla" );
        try {
            assertSame( user, system.loadUserByUsername( "bla" ) );
            assertSame( admin, system.loadUserByUsername( "admin" ) );
        } catch ( UsernameNotFoundException e ) {
            fail();
        }
        try {
            assertSame( admin, system.loadUserByUsername( "user" ) );
            fail();
        } catch ( UsernameNotFoundException e ) {
            // OK
        }
        
        admin.setUsername( "user" );
        assertSame( user, system.loadUserByUsername( "bla" ) );
        assertSame( admin, system.loadUserByUsername( "user" ) );
        try {
            assertSame( admin, system.loadUserByUsername( "admin" ) );
            fail();
        } catch ( UsernameNotFoundException e ) {
            // OK
        }
        
    }
    
    @Test
    public void testGetSetOrganizations() throws PropertyVetoException {
        assertEquals( 0, system.getOrganizations().size() );

        Organization org1 = new Organization( "MAS" );        
        system.addOrganization( org1 );
        assertEquals( 1, system.getOrganizations().size() );
        assertSame( org1, system.getOrganizations().iterator().next() );

        Organization org2 = new Organization( "EFF" );        
        system.addOrganization( org2 );
        assertEquals( 2, system.getOrganizations().size() );
        assertTrue( system.getOrganizations().contains( org1 ) );
        assertTrue( system.getOrganizations().contains( org2 ) );
        
        system.removeOrganization( org1 );
        assertEquals( 1, system.getOrganizations().size() );
        assertSame( org2, system.getOrganizations().iterator().next() );
        
        system.removeOrganization( org2 );
        assertEquals( 0, system.getOrganizations().size() );
        
        Set<Organization> set = new HashSet<Organization>();
        set.add( org1 );
        set.add( org2 );
        system.setOrganizations( set );
        assertEquals( 2, system.getOrganizations().size() );
        assertTrue( system.getOrganizations().contains( org1 ) );
        assertTrue( system.getOrganizations().contains( org2 ) );
    }
    
}
