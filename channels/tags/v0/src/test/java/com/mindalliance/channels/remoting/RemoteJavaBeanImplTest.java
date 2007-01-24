// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import com.mindalliance.channels.impl.GUIDFactoryImpl;
import com.mindalliance.channels.remoting.Exporter;
import com.mindalliance.channels.remoting.GUID;
import com.mindalliance.channels.remoting.GUIDFactory;
import com.mindalliance.channels.remoting.Importer;
import com.mindalliance.channels.remoting.RemoteJavaBean;
import com.mindalliance.channels.remoting.RemoteJavaBeanImpl;
import com.mindalliance.channels.remoting.RemoteListener;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class RemoteJavaBeanImplTest extends TestCase {

    private Importer importer;
    private Exporter exporter;
    private IMocksControl ctrl;

    private RemoteJavaBeanImpl remote;
    private GUIDFactory guidFactory = new GUIDFactoryImpl( "test" );

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.ctrl = EasyMock.createStrictControl();
        this.importer = this.ctrl.createMock( Importer.class );
        this.exporter = this.ctrl.createMock( Exporter.class );
        this.remote = new RemoteJavaBeanImpl( this.importer,
                this.guidFactory.newGuid(), TestRemoteInterface.class );
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testInvoke_1() throws Throwable {

        Method m = TestRemoteInterface.class.getMethod(
                "getName", (Class[]) null );
        expect( this.importer.invoke( m, null ) ).andReturn( "Bob" );
        this.ctrl.replay();

        assertEquals( "Bob", this.remote.invoke( this.remote, m, null ) );

        m = RemoteJavaBean.class.getMethod( "isCopied", (Class[]) null );
        assertEquals( false, this.remote.invoke( this.remote, m, null ) );

        m = Object.class.getMethod( "hashCode", (Class[]) null );
        assertEquals( this.remote.hashCode(),
                this.remote.invoke( this.remote, m, null ) );

        this.ctrl.verify();
    }

    public void testInvoke_2() throws Throwable {

        Method m = TestRemoteInterface.class.getMethod(
                "getName", (Class[]) null );
        Exception e1 = new IOException( "something happened" );
        Exception e2 = new Exception( "underlying exception" );
        Exception e3 = new InvocationTargetException( e2 );

        expect( this.importer.invoke( m, null ) ).andThrow( e1 );
        expect( this.importer.invoke( m, null ) ).andThrow( e3 );
        this.ctrl.replay();

        try {
            this.remote.invoke( this.remote, m, null );
            fail( "Exception passed through" );
        } catch ( RuntimeException ex ) {
            assertSame( e1, ex.getCause() );
        }

        try {
            this.remote.invoke( this.remote, m, null );
            fail( "Exception passed through" );
        } catch ( Exception ex ) {
            assertSame( e2, ex );
        }

        this.ctrl.verify();
    }

    public void testAddPropertyChangeListener_1() {
        this.ctrl.replay();

        TestListener testListener = new TestListener();

        try {
            this.remote.addPropertyChangeListener( testListener );
        } catch ( IllegalStateException e ) {
            // OK
        }

        assertNull( testListener.getLastProp() );
        assertNull( testListener.getLastVeto() );
        this.ctrl.verify();
    }

    public void testAddPropertyChangeListener_2() {
        this.remote.setExporter( this.exporter );
        Importer listenerImporter = new Importer() {

            public void addListener(
                    GUID guid, Importer client ) {
            }

            public Object invoke( Method method, Object[] args )
                throws InvocationTargetException, IOException {

                return null;
            }

            public void removeListener(
                    GUID guid, Importer client ) {
            }
        };

        this.exporter.addListener( eq( this.remote.getGUID() ),
                isA( RemoteListener.class ) );
        expect( this.exporter.getClient() ).andReturn( listenerImporter );
        this.importer.addListener( this.remote.getGUID(), listenerImporter );
        expect( this.exporter.getClient() ).andReturn( listenerImporter );
        this.importer.removeListener(
                this.remote.getGUID(), listenerImporter );
        this.exporter.removeListener( eq( this.remote.getGUID() ),
                isA( RemoteListener.class ) );

        this.ctrl.replay();

        TestListener testListener = new TestListener();
        this.remote.addPropertyChangeListener( testListener );
        this.remote.addPropertyChangeListener( "name", testListener );

        this.remote.removePropertyChangeListener( "name", testListener );
        this.remote.removePropertyChangeListener( testListener );

        assertNull( testListener.getLastProp() );
        assertNull( testListener.getLastVeto() );
        this.ctrl.verify();
    }

    public void testAddVetoableChangeListener() {
        this.remote.setExporter( this.exporter );
        Importer listenerImporter = new Importer() {

            public void addListener(
                    GUID guid, Importer client ) {
            }

            public Object invoke( Method method, Object[] args )
                throws InvocationTargetException, IOException {

                return null;
            }

            public void removeListener(
                    GUID guid, Importer client ) {
            }
        };

        this.exporter.addListener( eq( this.remote.getGUID() ),
                isA( RemoteListener.class ) );
        expect( this.exporter.getClient() ).andReturn( listenerImporter );
        this.importer.addListener( this.remote.getGUID(), listenerImporter );
        expect( this.exporter.getClient() ).andReturn( listenerImporter );
        this.importer.removeListener(
                this.remote.getGUID(), listenerImporter );
        this.exporter.removeListener( eq( this.remote.getGUID() ),
                isA( RemoteListener.class ) );

        this.ctrl.replay();

        TestListener testListener = new TestListener();
        this.remote.addVetoableChangeListener( testListener );
        this.remote.addVetoableChangeListener( "key", testListener );

        this.remote.removeVetoableChangeListener( "key", testListener );
        this.remote.removeVetoableChangeListener( testListener );

        assertNull( testListener.getLastProp() );
        assertNull( testListener.getLastVeto() );
        this.ctrl.verify();
    }

    public void testTakeCopy() {
        assertNull( "Rewrite this test...", this.remote.takeCopy() );
    }
}
