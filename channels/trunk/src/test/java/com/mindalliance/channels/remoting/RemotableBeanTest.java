// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

import com.mindalliance.channels.impl.GUIDFactoryImpl;
import com.mindalliance.channels.remoting.AbstractJavaBean;
import com.mindalliance.channels.remoting.AbstractRemotableBean;
import com.mindalliance.channels.remoting.GUID;
import com.mindalliance.channels.remoting.GUIDFactory;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class RemotableBeanTest extends TestCase {

    private TestObject testObject;
    private GUID guid ;
    private GUIDFactory guidFactory = new GUIDFactoryImpl( "test" );


    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.guid = this.guidFactory.newGuid();
        this.testObject = new TestObject( this.guid );
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link AbstractRemotableBean#AbstractRemotableBean(GUIDFactory)}.
     */
    public void testAbstractRemotableBeanGUIDFactory() {
        new TestObject( this.guidFactory );
    }

    /**
     * Test method for {@link AbstractRemotableBean#getGUID()}.
     */
    public void testGetGUID() {
        assertEquals( this.guid, this.testObject.getGUID() );
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
