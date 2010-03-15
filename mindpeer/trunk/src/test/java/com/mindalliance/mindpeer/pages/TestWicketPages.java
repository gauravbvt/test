package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.IntegrationTest;
import com.mindalliance.mindpeer.model.Focus;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.markup.html.WebPage;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    @Transactional
    @Rollback
    public void testHome() throws URISyntaxException {
        assertRendered( null, PublicHomePage.class );
        tester.clickLink( "home" );
        assertEquals( SC_FORBIDDEN, tester.getServletResponse().getCode() );

        login( "guest", "" );
        try {
            tester.clickLink( "home" );
            tester.assertRenderedPage( FocusPage.class );
        } finally {
            logout();
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testUserPublic() throws URISyntaxException {
        assertErrorRendering( "bob", SC_NOT_FOUND );
        assertRendered( "guest", ProfilePage.class );

        assertRendered( "guest/avatar.png", PicturePage.class );
        assertEquals( "image/png", tester.getServletResponse().getContentType() );

        assertErrorRendering( "bob/avatar.png", SC_NOT_FOUND );
        assertErrorRendering( "guest/bla", SC_NOT_FOUND );
    }

    @Test
    @Transactional
    @Rollback
    public void testAccount() throws URISyntaxException {
        assertErrorRendering( "account", SC_FORBIDDEN );
        assertErrorRendering( "account/profile", SC_FORBIDDEN );

        login( "guest", "" );
        try {
            assertRendered( "account", AccountPage.class );
            assertRendered( "account/profile", EditProfilePage.class );

        } finally {
            logout();
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testLoginRelated() throws URISyntaxException {
        assertRendered( "register", RegistrationPage.class );
        assertRendered( "login", LoginPage.class );
        WebPage page = (WebPage) tester.getLastRenderedPage();

        tester.clickLink( "signup" );
        tester.assertRenderedPage( RegistrationPage.class );
    }

    @Test
    @Transactional
    @Rollback
    public void testProducts() throws URISyntaxException {
        assertErrorRendering( "products", SC_FORBIDDEN );

        login( "guest", "" );
        try {
            assertRendered( "products", ProductPage.class );
        } finally {
            logout();
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testFocus() throws URISyntaxException {
        assertErrorRendering( "focus", SC_FORBIDDEN );

        login( "guest", "" );
        try {
            Focus focus = new Focus();
            focus.setUser( guest );
            focus.setName( "Test" );

            List<Focus> list = new ArrayList<Focus>();
            list.add( focus );
            guest.setFocusList( list );
            User g = userDao.save( guest );
            String id = g.getDefaultFocus().getName();

            assertRendered( "focus", FocusPage.class );
            assertRendered( "focus/" + id , FocusPage.class );
            assertErrorRendering( "focus/123", SC_NOT_FOUND );
            assertErrorRendering( "focus/bla", SC_NOT_FOUND );
        } finally {
            logout();
        }
    }
    
    @Test
    @Transactional
    @Rollback
    public void testSearch() throws URISyntaxException {
        assertErrorRendering( "search", SC_FORBIDDEN );
        assertErrorRendering( "search?q=bla", SC_FORBIDDEN );
        login( "guest", "" );
        try {
            assertRendered( "search", SearchResultPage.class );
            assertRendered( "search?q=bla", SearchResultPage.class );
        } finally {
            logout();
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testAdmin() throws URISyntaxException {
        assertErrorRendering( "admin", SC_FORBIDDEN );
        try {
            login( "guest", "" );
            assertErrorRendering( "admin", SC_FORBIDDEN );
            login( "support", "eisenhower" );   // oops

            assertRendered( "admin", UserListPage.class );
        } finally {
            logout();
        }
    }
}
