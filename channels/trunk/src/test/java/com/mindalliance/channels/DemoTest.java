// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import org.springframework.test.context.TestExecutionListeners;

/**
 * Test the empty  demo plan.
 */
@TestExecutionListeners( AbstractChannelsTest.InstallSamplesListener.class )
public class DemoTest extends WalkthroughTest {

    public DemoTest() {
        super( "guest", "mindalliance.com/channels/plans/railsec" );
    }
}
