// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.impl;

import java.beans.PropertyVetoException;
import java.util.List;

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
    public void testVetoes_1() throws Exception {
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

    /**
     * Test actual vetoes.
     */
    public void testVetoes_2() throws Exception {
        TestListener l = new TestListener();
        testObject.addVetoableChangeListener( l );

        try {
            l.setObjecting( true );
            testObject.setAge( 20 );
            fail();
        } catch ( PropertyVetoException e ) {
            assertEquals( 2, l.getVetoCount() );
            // note: 1 for change request, 2 for reverting to previous

            assertEquals( 20, e.getPropertyChangeEvent().getNewValue() );
            assertEquals( 0, l.getLastVeto().getNewValue() );
            assertEquals( 0, l.getPropCount() );
        }
        l.reset();

        try {
            l.setObjecting( true );
            testObject.setKey( "burp" );
            fail();
        } catch ( PropertyVetoException e ) {
            assertEquals( 2, l.getVetoCount() );
            assertEquals( null, l.getLastVeto().getNewValue() );
            assertEquals( "burp", e.getPropertyChangeEvent().getNewValue() );
            assertEquals( null, testObject.getKey() );
            assertEquals( 0, l.getPropCount() );
        }
        l.reset();

        try {
            l.setObjecting( true );
            testObject.setOk( true );
            fail();
        } catch ( PropertyVetoException e ) {
            assertEquals( 2, l.getVetoCount() );
            assertEquals( false, l.getLastVeto().getNewValue() );
            assertEquals( true, e.getPropertyChangeEvent().getNewValue() );
            assertFalse( testObject.isOk() );
            assertEquals( 0, l.getPropCount() );
        }
        l.reset();
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

    @SuppressWarnings("unchecked")
    public void testAddValue_1() {
        TestListener l = new TestListener();
        testObject.addPropertyChangeListener( l );

        testObject.addValue( "blah" );
        assertEquals( 1, l.getPropCount() );
        assertEquals( "values", l.getLastProp().getPropertyName() );
        assertNull( l.getLastProp().getOldValue() );
        List<String> v = (List<String>) l.getLastProp().getNewValue();
        assertEquals( 1, v.size() );
        assertEquals( "blah", v.get( 0 ) );

        l.reset();
        testObject.removeValue( "blah" );
        assertEquals( 1, l.getPropCount() );
        assertEquals( "values", l.getLastProp().getPropertyName() );
        assertNull( l.getLastProp().getOldValue() );
        v = (List<String>) l.getLastProp().getNewValue();
        assertEquals( 0, v.size() );
    }

    public void testAddValue_2() {
        TestListener l = new TestListener();
        testObject.addPropertyChangeListener( l );

        testObject.addSomething( "blah" );
        assertEquals( 0, l.getPropCount() );
    }
}
