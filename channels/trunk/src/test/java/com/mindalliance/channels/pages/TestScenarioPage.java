package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.Memory;
import junit.framework.TestCase;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Simple test using the WicketTester.
 */
public class TestScenarioPage extends TestCase {

    private WicketTester tester;

    public TestScenarioPage() {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        final Project project = new Project();
        project.setScenarioDao( new Memory() );
        tester = new WicketTester( project );
    }

    public void testRenderMyPage() {
        //start and render the test page
        tester.startPage( ScenarioPage.class );

        //assert rendered page class
        tester.assertRenderedPage( ScenarioPage.class );

        //assert rendered label component
        //tester.assertLabel("message",
        // "If you see this message wicket is properly configured and running");
    }
}
