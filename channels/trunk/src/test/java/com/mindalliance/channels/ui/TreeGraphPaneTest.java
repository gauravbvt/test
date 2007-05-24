// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.beans.PropertyDescriptor;
import java.beans.PropertyVetoException;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanWrapperImpl;

import com.mindalliance.channels.AbstractSecurityTest;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.elements.project.Model;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.data.elements.scenario.Event;
import com.mindalliance.channels.data.elements.scenario.Task;
import com.mindalliance.channels.ui.TreeGraphPane.Arc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Test for TreeGraphPanes.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class TreeGraphPaneTest extends AbstractSecurityTest {

    private TreeGraphPane tgp;
    private Event event1;
    private Task task1;
    private Scenario scenario;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws PropertyVetoException, UserExistsException {
        super.setUp();
        
        assertEquals( "ACME Business Continuity", project.getName() );
        Model model = project.getModels().iterator().next();
        assertEquals( "Headquarters", model.getName() );

        final Iterator<Scenario> iterator = model.getScenarios().iterator();
        iterator.next();
        scenario = iterator.next();
        assertEquals( "Building Fire", scenario.getName() );
        event1 = scenario.getEvents().iterator().next();
        task1 = scenario.getTasks().iterator().next();
        this.tgp = new TreeGraphPane( 200, scenario, system, null );
        this.tgp.setIconManager( new IconManager() );
                
    }

    @Test
    public void testGetArcs() {
//        Set<Arc> arcs = tgp.getArcs( event1 );
//        assertEquals( 2, arcs.size() );
//
//        arcs = tgp.getArcs( task1 );
//        assertEquals( 2, arcs.size() );
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
