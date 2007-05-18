// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import static org.junit.Assert.*;

import java.beans.PropertyDescriptor;
import java.beans.PropertyVetoException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanWrapperImpl;

import com.mindalliance.channels.data.elements.project.Model;
import com.mindalliance.channels.data.elements.project.Project;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.data.elements.scenario.Agent;
import com.mindalliance.channels.data.elements.scenario.Event;
import com.mindalliance.channels.data.elements.scenario.RoleAgent;
import com.mindalliance.channels.data.elements.scenario.Task;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.data.system.System;
import com.mindalliance.channels.ui.TreeGraphPane.Arc;

/**
 * Test for TreeGraphPanes.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class TreeGraphPaneTest {

    private TreeGraphPane tgp;
    private SystemService system;
    private Event event1;
    private Task task1;
    private Scenario scenario;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.system = createSystem() ;
        this.tgp = new TreeGraphPane( 200, scenario, system, null );
        this.tgp.setIconManager( new IconManager() );
    }

    /**
     * Create a bogus system for testing.
     * @throws PropertyVetoException
     */
    protected SystemService createSystem() throws PropertyVetoException {
        SystemService result = new System();
        Project p = new Project();
        p.setName( "the project" );
        Model m = new Model();
        m.setName( "the model" );
        scenario = new Scenario();
        result.getPortfolioService().addProject( p );
        p.addModel( m );
        m.addScenario( scenario );
        
        event1 = new Event();
        event1.setName( "event 1" );
        scenario.addOccurrence( event1 );

        task1 = new Task();
        task1.setName( "event 2" );
        Agent agent = new RoleAgent();
        agent.setName( "bob" );
        task1.addAgent( agent );

        agent = new RoleAgent();
        agent.setName( "bill" );
        task1.addAgent( agent );
        scenario.addOccurrence( task1 );

        return result;
    }

    @Test
    public void testGetArcs() {
        Set<Arc> arcs = tgp.getArcs( event1 );
        assertEquals( 0, arcs.size() );

        arcs = tgp.getArcs( task1 );
        assertEquals( 2, arcs.size() );
    }

    @Test
    public void testSetRootElement() {
        assertNull( tgp.getRootElement() );
        tgp.setRootElement( event1 );
        assertSame( event1, tgp.getRootElement() );
    }
    
    @Test
    public void testGetScenario() {
        assertSame( scenario, tgp.getScenario() );
    }
    
    @Test( expected=NullPointerException.class )
    public void testNewArc() throws NullPointerException {
        new Arc( null, null, Arc.Direction.to );
    }
    
    @Test
    public void testArcEquals() {
        PropertyDescriptor p1 = 
            new BeanWrapperImpl( event1 ).getPropertyDescriptor( "cause" );
        Arc a1 = new Arc( event1, p1, Arc.Direction.to );
        Arc a2 = new Arc( event1, p1, Arc.Direction.to );
        
        assertFalse( a1.equals( null ) );
        assertFalse( a1.equals( "bla" ) );
        assertTrue( a1.equals( a1 ) );
        assertTrue( a1.equals( a2 ) );
    }
    
    @Test
    public void testArcHashcode() {
        PropertyDescriptor p1 = 
            new BeanWrapperImpl( event1 ).getPropertyDescriptor( "cause" );
        PropertyDescriptor p2 = 
            new BeanWrapperImpl( event1 ).getPropertyDescriptor( "name" );
        Arc a1 = new Arc( event1, p1, Arc.Direction.to );
        Arc a2 = new Arc( event1, p1, Arc.Direction.to );
        
        assertEquals( a1.hashCode(), a2.hashCode() );

        p2 = new BeanWrapperImpl( event1 ).getPropertyDescriptor( "terminatingTasks" );
        a2 = new Arc( event1, p2, Arc.Direction.to );
        assertFalse( a1.hashCode() == a2.hashCode() );

        a2 = new Arc( task1, p1, Arc.Direction.to );
        assertFalse( a1.hashCode() == a2.hashCode() );

        a2 = new Arc( event1, p1, Arc.Direction.from );
        assertFalse( a1.hashCode() == a2.hashCode() );
    }
}
