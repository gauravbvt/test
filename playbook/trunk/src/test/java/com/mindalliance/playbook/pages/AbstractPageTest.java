package com.mindalliance.playbook.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

/**
 * Test harness for wicket pages with @SpringBeans.
 */
public abstract class AbstractPageTest {

    protected WicketTester tester;

    protected ApplicationContextMock context;

    @Before
    public void setUp() {
        tester = new WicketTester();

        context = new ApplicationContextMock();
        init( context );

        WebApplication application = tester.getApplication();
        application.getComponentInstantiationListeners().add( new SpringComponentInjector( application, context ) );
    }

    protected abstract Class<? extends WebPage> getTestedClass();

    protected abstract void init( ApplicationContextMock context );

    @Test
    public void render() {
        Class<? extends WebPage> pageClass = getTestedClass();
        tester.startPage( pageClass );
        tester.assertRenderedPage( pageClass );
    }
}
