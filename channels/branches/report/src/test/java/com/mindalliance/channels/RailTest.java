// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import com.mindalliance.channels.pages.AdminPage;
import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;

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
}
