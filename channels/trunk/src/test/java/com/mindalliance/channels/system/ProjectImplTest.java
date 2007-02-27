// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.system;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.HashSet;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindalliance.channels.Model;
import com.mindalliance.channels.User;
import com.mindalliance.channels.util.TestListener;


/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class ProjectImplTest {

    private ProjectImpl project;
    private TestListener listener;
    
    private User user1;
    private User user2;
    
    private Model model1;
    private Model model2;
        
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        project = new ProjectImpl();
        project.setName( "test" );
        listener = new TestListener();
        project.addPropertyChangeListener( listener );
        project.addVetoableChangeListener( listener );
    
        user1 = new UserImpl( "user1", "pass1", new String[]{ "ROLE_USER" } );
        user2 = new UserImpl( "user2", "pass2", new String[]{ "ROLE_USER" } );
        model1 = createNiceMock( Model.class );
        model2 = createNiceMock( Model.class );
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test method for {@link ProjectImpl#getName()}.
     */
    @Test
    public final void testGetName() {
        assertEquals( "test", project.getName() );
    }

    /**
     * Test method for {@link ProjectImpl#getManagers()}.
     */
    @Test
    public final void testGetManagers() {
        assertNotNull( project.getManagers() );
        assertEquals( 0, project.getManagers().size() );
        
        project.addManager( user1 );
        project.addParticipant( user2 );
        assertEquals( 1, project.getManagers().size() );
        assertTrue( project.getManagers().contains( user1 ) );
        
    }

    /**
     * Test method for {@link ProjectImpl#getParticipants()}.
     */
    @Test
    public final void testGetParticipants() {
        assertNotNull( project.getParticipants() );
        assertEquals( 0, project.getParticipants().size() );
    }

    /**
     * Test method for {@link ProjectImpl#getModels()}.
     */
    @Test
    public final void testGetModels() {
        assertNotNull( project.getModels() );
        assertEquals( 0, project.getModels().size() );
    }

    /**
     * Test method for {@link ProjectImpl#setModels(java.util.Set)}.
     */
    @Test
    public final void testSetModels() {
        try {
            project.setModels( null );
            fail();
        } catch ( NullPointerException e ) {
            // OK
        }

        replay( model1, model2 );
        Set<Model> models = new HashSet<Model>();
        models.add( model1 );
        models.add( model2 );
        
        project.setModels( models );
        assertSame( models, project.getModels() );
        
        assertEquals( 1, listener.getPropCount() );
        assertEquals( "models", listener.getLastProp().getPropertyName() );
        assertEquals( models, listener.getLastProp().getNewValue() );

        verify( model1, model2 );
    }

    /**
     * Test method for {@link ProjectImpl#addManager(User)}.
     */
    @Test
    public final void testAddManager() {
        assertFalse( project.isManager( user1 ) );
        project.addManager( user1 );
        
        assertEquals( 1, listener.getPropCount() );
        assertEquals( "managers", listener.getLastProp().getPropertyName() );
        
        assertTrue( project.isManager( user1 ) );
        assertTrue( project.isParticipant( user1 ) );
    }

    /**
     * Test method for {@link ProjectImpl#isManager(User)}.
     */
    @Test
    public final void testIsManager() {
        assertFalse( project.isManager( user2 ) );
    }

    /**
     * Test method for {@link ProjectImpl#isParticipant(User)}.
     */
    @Test
    public final void testIsParticipant() {
        assertFalse( project.isParticipant( user2 ) );
    }

    /**
     * Test method for {@link ProjectImpl#addModel(com.mindalliance.channels.Model)}.
     */
    @Test
    public final void testAddModel() {
        assertNotNull( project.getModels() );
        assertEquals( 0, project.getModels().size() );
        
        project.addModel( model1 );
        assertEquals( 1, project.getModels().size() );
        assertEquals( 1, listener.getPropCount() );
        assertEquals( "models", listener.getLastProp().getPropertyName() );
        
        listener.reset();
        project.addModel( model2 );
        assertEquals( 2, project.getModels().size() );
        assertEquals( 1, listener.getPropCount() );
        assertEquals( "models", listener.getLastProp().getPropertyName() );        
    }

    /**
     * Test method for {@link ProjectImpl#addParticipant(com.mindalliance.channels.User)}.
     */
    @Test
    public final void testAddParticipant() {
        assertNotNull( project.getParticipants() );
        assertEquals( 0, project.getParticipants().size() );        
        assertFalse( project.isParticipant( user1 ) );
        
        project.addParticipant( user1 );
        assertEquals( 1, project.getParticipants().size() );
        assertEquals( 1, listener.getPropCount() );
        assertEquals( "participants", listener.getLastProp().getPropertyName() );
        assertTrue( project.isParticipant( user1 ) );
        
        listener.reset();
        assertFalse( project.isParticipant( user2 ) );
        project.addParticipant( user2 );
        assertEquals( 2, project.getParticipants().size() );
        assertEquals( 1, listener.getPropCount() );
        assertEquals( "participants", listener.getLastProp().getPropertyName() );
        
    }

    /**
     * Test method for {@link ProjectImpl#removeManager(com.mindalliance.channels.User)}.
     */
    @Test
    public final void testRemoveManager() {
        assertFalse( project.isManager( user1 ) );
        assertFalse( project.isParticipant( user1 ) );
        
        project.addManager( user1 );
        assertTrue( project.isManager( user1 ) );
        assertTrue( project.isParticipant( user1 ) );
        
        project.removeManager( user1 );
        assertFalse( project.isManager( user1 ) );
        assertTrue( project.isParticipant( user1 ) );

        // Removing non-existent user should be a problem...
        assertFalse( project.isManager( user2 ) );
        assertFalse( project.isParticipant( user2 ) );
        project.removeManager( user2 );
    }

    /**
     * Test method for {@link ProjectImpl#removeModel(com.mindalliance.channels.Model)}.
     */
    @Test
    public final void testRemoveModel() {
        
        project.addModel( model1 );
        assertEquals( 1, project.getModels().size() );        
        assertTrue( project.getModels().contains( model1 ) );
        
        project.addModel( model2 );
        assertEquals( 2, project.getModels().size() );        
        assertTrue( project.getModels().contains( model1 ) );
        assertTrue( project.getModels().contains( model2 ) );
        
        project.removeModel( model1 );
        assertEquals( 1, project.getModels().size() );        
        assertFalse( project.getModels().contains( model1 ) );
        assertTrue( project.getModels().contains( model2 ) );

        project.removeModel( model1 );
        assertEquals( 1, project.getModels().size() );        
        assertFalse( project.getModels().contains( model1 ) );
        assertTrue( project.getModels().contains( model2 ) );
        
        project.removeModel( model2 );
        assertEquals( 0, project.getModels().size() );        
        assertFalse( project.getModels().contains( model1 ) );
        assertFalse( project.getModels().contains( model2 ) );
    }

    /**
     * Test method for {@link ProjectImpl#removeParticipant(com.mindalliance.channels.User)}.
     */
    @Test
    public final void testRemoveParticipant() {
        assertFalse( project.isParticipant( user1 ) );
        
        project.addParticipant( user1 );
        assertTrue( project.isParticipant( user1 ) );
        assertEquals( 1, project.getParticipants().size() );
        assertTrue( project.getParticipants().contains( user1 ) );
        
        project.addParticipant( user2 );
        assertTrue( project.isParticipant( user2 ) );
        assertEquals( 2, project.getParticipants().size() );
        assertTrue( project.getParticipants().contains( user1 ) );
        assertTrue( project.getParticipants().contains( user2 ) );
        
        project.removeParticipant( user1 );
        assertFalse( project.isParticipant( user1 ) );
        assertEquals( 1, project.getParticipants().size() );
        assertFalse( project.getParticipants().contains( user1 ) );
        assertTrue( project.getParticipants().contains( user2 ) );
        
        project.removeParticipant( user2 );
        assertFalse( project.isParticipant( user2 ) );
        assertEquals( 0, project.getParticipants().size() );
        assertFalse( project.getParticipants().contains( user1 ) );
        assertFalse( project.getParticipants().contains( user2 ) );
    }

    /**
     * Test method for {@link ProjectImpl#setName(java.lang.String)}.
     */
    @Test
    public final void testSetName_1() {
        try {
            project.setName( "x" );
            assertEquals( "x", project.getName() );
            assertEquals( 1, listener.getPropCount() );
            assertEquals( "name", listener.getLastProp().getPropertyName() );
            assertEquals( "test", listener.getLastProp().getOldValue() );
            assertEquals( "x", listener.getLastProp().getNewValue() );

            assertEquals( 1, listener.getVetoCount() );
            assertEquals( "name", listener.getLastVeto().getPropertyName() );
            assertEquals( "test", listener.getLastVeto().getOldValue() );
            assertEquals( "x", listener.getLastVeto().getNewValue() );
        } catch ( PropertyVetoException e ) {
            fail();
        }
    }

    /**
     * Test method for {@link ProjectImpl#setName(java.lang.String)}.
     */
    @Test( expected = PropertyVetoException.class )
    public final void testSetName_2() throws PropertyVetoException {
        project.addVetoableChangeListener( new VetoableChangeListener(){
            public void vetoableChange( PropertyChangeEvent evt ) 
                        throws PropertyVetoException {
                throw new PropertyVetoException( "I object!", evt );
            }} );
        
        project.setName( "x" );
        fail();
    }
    
    /**
     * Test method for {@link ProjectImpl#setName(java.lang.String)}.
     * @throws PropertyVetoException 
     */
    @Test( expected = NullPointerException.class )
    public final void testSetName_3() throws PropertyVetoException {
        project.setName( null );
        fail();
    }
    
    /**
     * Test method for {@link ProjectImpl#compareTo(ProjectImpl)}.
     */
    @Test
    public final void testCompareTo() throws PropertyVetoException {
        assertEquals( 0, project.compareTo( project ) );
        
        ProjectImpl p2 = new ProjectImpl();
        p2.setName( "z" );
        assertTrue( project.compareTo( p2 ) < 0 );        

        p2.setName( "a" );
        assertTrue( project.compareTo( p2 ) > 0 );        
    }

    /**
     * Test method for {@link ProjectImpl#toString()}.
     */
    @Test
    public final void testToString() {
        assertEquals( project.getName(), project.toString() );
    }

    /**
     * Test method for {@link ProjectImpl#setManagers()}.
     */
    @Test
    public final void testSetManagers() {
        Set<User> managers = new HashSet<User>();
        managers.add( user1 );
        
        project.setManagers( managers );
        assertEquals( 1, project.getManagers().size() );
        assertTrue( project.getManagers().contains( user1 ) );
        assertTrue( project.isManager( user1 ) );
        assertTrue( project.isParticipant( user1 ) );
        
        managers.add( user2 );
        assertEquals( 1, project.getManagers().size() );
        assertFalse( project.isManager( user2 ) );
        assertFalse( project.isParticipant( user2 ) );

        project.setManagers( managers );
        assertEquals( 2, project.getManagers().size() );
        assertTrue( project.getManagers().contains( user1 ) );
        assertTrue( project.getManagers().contains( user2 ) );
        assertTrue( project.isManager( user1 ) );
        assertTrue( project.isManager( user2 ) );
        assertTrue( project.isParticipant( user1 ) );
        assertTrue( project.isParticipant( user2 ) );
        
        managers.remove( user2 );
        project.setManagers( managers );
        assertEquals( 1, project.getManagers().size() );
        assertTrue( project.getManagers().contains( user1 ) );
        assertTrue( project.isManager( user1 ) );
        assertFalse( project.isManager( user2 ) );
        assertTrue( project.isParticipant( user1 ) );
        assertTrue( project.isParticipant( user2 ) );
        
    }

    
}
