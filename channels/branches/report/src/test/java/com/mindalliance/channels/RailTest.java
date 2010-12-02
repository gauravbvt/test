// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import com.mindalliance.channels.pages.AdminPage;
import com.mindalliance.channels.pages.reports.FlowReportPage;
import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;

import javax.servlet.http.HttpServletResponse;

/**
 * Test Rail Security plan.
 */
@TestExecutionListeners( AbstractChannelsTest.InstallSamplesListener.class )
public class RailTest extends WalkthroughTest {

    public RailTest() {
        super( "guest", "mindalliance.com/channels/plans/railsec" );
    }

    @Test
    public void testGuest() {
        login( "jf" );
        assertRendered( "admin", AdminPage.class );
    }

    @Test
    public void testFlowReport() {
        login( "denis" );
        assertErrorRendering( "flow?plan=mindalliance.com/channels/plans/railsec&v=2&agent=293&task=235&flow=527",
                              HttpServletResponse.SC_NOT_FOUND );
    }

    @Test
    public void testFlowReport2() {
        login( "denis" );
        wicketApplication.getDebugSettings().setComponentUseCheck( false );
        assertRendered( "flow?plan=mindalliance.com/channels/plans/railsec&v=1&agent=293&task=235&flow=527",
                              FlowReportPage.class );
    }
}
