package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.IntegrationTest;
import org.apache.wicket.PageParameters;
import org.junit.Test;

/**
 * Test dynamic web pages loading.
 * This only checks that wicket:id defined in templates are processed in java, and vice-versa.
 */

public class TestWicketPages extends IntegrationTest {

    /**
     * Create a new TestWicketPages instance.
     */
    public TestWicketPages() {
    }

    /**
     * ...
     */
    @Test
    public void testStartPage() {
        runInRequest( "", new Runnable() {
            public void run() {
                tester.startPage( PublicHomePage.class );
            }
        } );



    }

    /**
     * ...
     */
    @Test
    public void testUserPage() {
        runInRequest( "home.html", guest, "", new Runnable() {
            public void run() {
                tester.startPage( UserHomePage.class );
            }
        } );
    }

    /**
     * ...
     */
    @Test
    public void testUserPage2() {
        runInRequest( "home.html", guest, "", new Runnable() {
            public void run() {
                tester.startPage( UserHomePage.class, new PageParameters( "username=guest" ) );
            }
        } );
    }

    /**
     * ...
     */
    @Test
    public void testLoginPage() {
        runInRequest( "home.html", new Runnable() {
            public void run() {
                tester.startPage( LoginPage.class );
            }
        } );
    }

    /**
     * ...
     */
    @Test
    public void testConfirmationSent() {
        runInRequest( "home.html", new Runnable() {
            public void run() {
                PageParameters pageParameters = new PageParameters();
                pageParameters.add( "email", "bla@somewhere.com" );
                tester.startPage( ConfirmationSent.class, pageParameters );
            }
        } );
    }
}
