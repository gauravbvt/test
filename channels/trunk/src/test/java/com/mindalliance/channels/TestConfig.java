package com.mindalliance.channels;

import com.mindalliance.channels.pages.Channels;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ContextLoader;

/**
 * Test Spring configuration.
 */
public class TestConfig extends AbstractChannelsTest {

    public TestConfig() {
    }

    public void testContext() {
        MockServletContext context = new MockServletContext();
        context.addInitParameter(
                ContextLoader.CONFIG_LOCATION_PARAM,
                "WEB-INF/applicationContext.xml WEB-INF/hibernateConfig.xml" );           // NON-NLS
        ApplicationContext ctx = new ContextLoader().initWebApplicationContext( context );

        Channels p = (Channels) ctx.getBean( "wicketApplication" );                         // NON-NLS
        assertNotNull( p );
    }

    /**
 * ...
     */
    public static class TestConnector {

    }
}
