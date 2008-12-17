package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ContextLoader;

/**
 * Test Spring configuration.
 */
public class TestConfig extends TestCase {

    public TestConfig() {
    }

    public void testContext() {
        final MockServletContext context = new MockServletContext();
        context.addInitParameter(
                ContextLoader.CONFIG_LOCATION_PARAM,
                "WEB-INF/applicationContext.xml" );                                       // NON-NLS
        final ApplicationContext ctx = new ContextLoader().initWebApplicationContext( context );

        final Project p = (Project) ctx.getBean( "wicketApplication" );                   // NON-NLS
        assertNotNull( p );
    }
}
