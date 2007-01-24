// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import junit.framework.TestCase;

import org.easymock.IMocksControl;

import com.mindalliance.channels.impl.GUIDFactoryImpl;
import com.mindalliance.channels.remoting.AbstractRemotableBean;
import com.mindalliance.channels.remoting.Exporter;
import com.mindalliance.channels.remoting.GUIDFactory;
import com.mindalliance.channels.remoting.Importer;
import com.mindalliance.channels.remoting.ProxyFactoryImpl;

/**
 * Tests for ProxyFactoryImpl.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class ProxyFactoryImplTest extends TestCase {

    private IMocksControl ctrl;
    private TestObject object;
    private ProxyFactoryImpl factory;
    private Exporter exporter;
    private Importer importer;
    private GUIDFactory guidFactory = new GUIDFactoryImpl( "test" );

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.object = new TestObject( this.guidFactory );
        this.factory = new ProxyFactoryImpl();

        this.ctrl = createStrictControl();
        this.exporter = ctrl.createMock( Exporter.class );
        this.importer = ctrl.createMock( Importer.class );
    }

    /**
     * Test method for {@link ProxyFactoryImpl#createProxy(
     *      AbstractRemotableBean, Class)}.
     */
    public void testCreateProxy_1() {
        expect( exporter.getClient() ).andReturn( importer );
        ctrl.replay();

        factory.setExporter( exporter );
        this.factory.createProxy(
              this.object, TestRemoteInterface.class );

        ctrl.verify();
    }

    /**
     * Test method for {@link ProxyFactoryImpl#createProxy(
     *      AbstractRemotableBean, Class)}.
     */
    public void testCreateProxy_2() {
        try {
            this.factory.createProxy(
                    this.object, TestRemoteInterface.class );

            fail( "Able to proxy without an exporter" );
        } catch ( IllegalStateException e ) {
            // OK
        }
    }

    public void testUseProxy_1() throws Exception {
        expect( exporter.getClient() ).andReturn( importer );
        expect( importer.invoke(
                TestRemoteInterface.class.getMethod(
                        "getAge", (Class[]) null ),
                (Object[]) null ) ).andReturn( 10 );
        ctrl.replay();

        factory.setExporter( exporter );
        TestRemoteInterface proxy =
            factory.createProxy( object, TestRemoteInterface.class );

        assertEquals( proxy.getAge(), 10 );

        ctrl.verify();
    }

    /**
     * Test method for {@link ProxyFactoryImpl#setExporter(Exporter)}.
     */
    public void testSetExporter() {
        ctrl.replay();

        assertNull( this.factory.getExporter() );
        this.factory.setExporter( this.exporter );
        assertSame( this.exporter, this.factory.getExporter() );

        ctrl.verify();
    }
}
