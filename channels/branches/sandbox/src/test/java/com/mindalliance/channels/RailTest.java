// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import org.springframework.test.context.TestExecutionListeners;

/**
 * Test Rail Security plan.
 */
@TestExecutionListeners( AbstractChannelsTest.InstallSamplesListener.class )
public class RailTest extends WalkthroughTest {

    public RailTest() {
        super( "guest", "mindalliance.com/channels/plans/railsec" );
    }
}
