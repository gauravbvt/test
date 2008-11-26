package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Test Spring configuration.
 */
public class TestConfig extends TestCase {

    public TestConfig() {
    }

    public void testContext() {
        final ApplicationContext ac = new FileSystemXmlApplicationContext( "src/main/webapp/WEB-INF/applicationContext.xml" );
        final Project p = (Project) ac.getBean( "wicketApplication" );
        assertNotNull( p );
    }
}
