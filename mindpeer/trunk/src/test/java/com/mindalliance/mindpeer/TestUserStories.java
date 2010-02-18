// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer;

import com.mindalliance.mindpeer.pages.PublicHomePage;
import com.mindalliance.mindpeer.pages.UserHomePage;
import org.junit.Test;

/**
 * Integrated tests of common user interactions.
 */
public class TestUserStories extends IntegrationTest {

    /**
     * Create a new TestUserStories instance.
     */
    public TestUserStories() {
    }

    /**
     * ...
     */
    @Test
    public void userLogsIn() {
        // go to public home page
        runInRequest( "", guest, "", new Runnable() {
            public void run() {
                tester.startPage( wicketApplication.getHomePage() );
                tester.assertRenderedPage( PublicHomePage.class );

                // click on user home page link
                tester.clickLink( "home" );
                tester.assertRenderedPage( UserHomePage.class );
            }
        } );
    }
}
