// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.IntegrationTest;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * ...
 */
public class TestUserDispatchPage extends IntegrationTest {

    private Class<UserDispatchPage> clazz = UserDispatchPage.class;

    public TestUserDispatchPage() {
    }

    @Override
    @Before
    public void init() {
        super.init();
    }

    @Test
    public void testNoParm() {
        tester.startPage( clazz );
        tester.assertRenderedPage( PublicHomePage.class );
    }

    @Test
    public void testUser() {
        parameters.put( "0", "guest" );
        tester.startPage( clazz, parameters );
        tester.assertRenderedPage( ProfilePage.class );
    }

    @Test
    public void testBadUser() {
        parameters.put( "0", "bob" );
        tester.startPage( clazz, parameters );
        assertEquals( SC_NOT_FOUND, tester.getServletResponse().getCode() );
    }

    @Test
    public void testUserPic() {
        parameters.put( "0", "guest" );
        parameters.put( "1", "avatar.png" );
        tester.startPage( clazz, parameters );
        tester.assertRenderedPage( PicturePage.class );
    }

}
