// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.playbook.MainPage;
import com.mindalliance.channels.pages.reports.PlanReportPage;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;

import java.io.IOException;

/**
 * Test sample models.
 */
@TestExecutionListeners( AbstractChannelsTest.InstallSamplesListener.class )
public class AcmeTest extends AbstractChannelsTest {

    public AcmeTest() {
        super( "guest", "mindalliance.com/channels/plans/acme" );
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
    }

    @Test
    public void testPlan() {
        assertRendered( "plan", PlanPage.class );
    }

    @Test
    public void testPlaybook() {
        assertRendered( "playbooks", MainPage.class );
    }

    @Test
    public void testReport() {
        assertRendered( "report", PlanReportPage.class );
    }

}
