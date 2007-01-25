// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.impl;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class JavaBeanTest extends TestCase {

    private TestObject testObject;


    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.testObject = new TestObject();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link AbstractJavaBean#hasListeners()}.
     */
    public void testHasListeners() {
        assertFalse( testObject.hasListeners() );

        TestListener t1 = new TestListener();
        testObject.addPropertyChangeListener( t1 );
        assertTrue( testObject.hasListeners() );
        testObject.removePropertyChangeListener( t1 );
        assertFalse( testObject.hasListeners() );

        testObject.addPropertyChangeListener( "name", t1 );
        assertTrue( testObject.hasListeners() );
        testObject.removePropertyChangeListener( "name", t1 );
        assertFalse( testObject.hasListeners() );

        testObject.addVetoableChangeListener( t1 );
        assertTrue( testObject.hasListeners() );
        testObject.removeVetoableChangeListener( t1 );
        assertFalse( testObject.hasListeners() );

        testObject.addVetoableChangeListener( "name", t1 );
        assertTrue( testObject.hasListeners() );
        testObject.removeVetoableChangeListener( "name", t1 );
        assertFalse( testObject.hasListeners() );
    }

    /**
     * Coverage test... Make sure no errors happen there.
     */
    public void testFire() throws Exception {
        testObject.setAge( 10 );
        testObject.setKey( "bla" );
        testObject.setName( "bla" );
        testObject.setOk( true );

        TestListener l = new TestListener();
        testObject.removePropertyChangeListener( l );
        testObject.removePropertyChangeListener( "name", l );
        testObject.removeVetoableChangeListener( l );
        testObject.removeVetoableChangeListener( "name", l );

        testObject.addPropertyChangeListener( l );
        testObject.setAge( 20 );
        assertEquals( 1, l.getPropCount() );
        assertEquals( 20, l.getLastProp().getNewValue() );
        l.reset();

        testObject.setKey( "burp" );
        assertEquals( 1, l.getPropCount() );
        assertEquals( "burp", l.getLastProp().getNewValue() );
        l.reset();

        testObject.setName( "foo" );
        assertEquals( 1, l.getPropCount() );
        assertEquals( "foo", l.getLastProp().getNewValue() );
        l.reset();

        testObject.setOk( false );
        assertEquals( 1, l.getPropCount() );
        assertEquals( false, l.getLastProp().getNewValue() );
        l.reset();

        testObject.setOk( false );
        assertEquals( 0, l.getPropCount() );
    }

    /**
     * Coverage test... Make sure no errors happen there.
     */
    public void testVetoes() throws Exception {
        TestListener l = new TestListener();
        assertFalse( testObject.hasVetoableListeners( null ) );

        testObject.addVetoableChangeListener( l );
        assertSame( l, testObject.getVetoableChangeListeners()[0] );
        
        testObject.setAge( 20 );
        assertEquals( 1, l.getVetoCount() );
        assertEquals( 20, l.getLastVeto().getNewValue() );
        l.reset();

        testObject.setKey( "burp" );
        assertEquals( 1, l.getVetoCount() );
        assertEquals( "burp", l.getLastVeto().getNewValue() );
        l.reset();

        testObject.setOk( true );
        assertEquals( 1, l.getVetoCount() );
        assertEquals( true, l.getLastVeto().getNewValue() );
        l.reset();

        testObject.setOk( true );
        assertEquals( 0, l.getVetoCount() );
    }
    
    public void testGetVetoableChangeListeners() {
        TestListener l = new TestListener();
        testObject.addVetoableChangeListener( "age", l );
        assertSame( l, testObject.getVetoableChangeListeners( "age" )[0] );
    }

    /**
     * Test method for {@link AbstractJavaBean#getPropertyChangeListeners()}.
     */
    public void testGetPropertyChangeListeners() {
        TestListener l = new TestListener();

        assertEquals( 0, testObject.getPropertyChangeListeners().length );
        testObject.addPropertyChangeListener( l );
        assertEquals( l, testObject.getPropertyChangeListeners()[0] );
        assertTrue( testObject.hasPropertyListeners( null ) );
        testObject.removePropertyChangeListener( l );
        assertEquals( 0, testObject.getPropertyChangeListeners().length );

        assertFalse( testObject.hasPropertyListeners( null ) );
    }

    /**
     * Test method for {@link AbstractJavaBean#getPropertyChangeListeners(
     *      String)}.
     */
    public void testGetPropertyChangeListenersString() {
        TestListener l = new TestListener();

        assertEquals( 0, testObject.getPropertyChangeListeners("name").length );
        testObject.addPropertyChangeListener( "name", l );
        assertEquals( l, testObject.getPropertyChangeListeners("name")[0] );
        assertTrue( testObject.hasPropertyListeners( "name" ) );
        testObject.removePropertyChangeListener( "name",l );
        assertEquals( 0, testObject.getPropertyChangeListeners("name").length );

        assertFalse( testObject.hasPropertyListeners("name") );
    }
}
