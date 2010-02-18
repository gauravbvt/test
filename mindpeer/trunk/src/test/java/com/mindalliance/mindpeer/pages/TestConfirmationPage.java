// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.IntegrationTest;
import com.mindalliance.mindpeer.SecuredAspects;
import com.mindalliance.mindpeer.model.User;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * ...
 */
public class TestConfirmationPage extends IntegrationTest {

    /**
     * Create a new TestConfirmationPage instance.
     */
    public TestConfirmationPage() {
    }

    @Override
    @Before
    public void init() {
        super.init();
        assertNotNull( userDao );
        SecuredAspects.bypass( true );
    }

    /**
     * ...
     */
    @Test
    public void testNoParms() {
        assertNull( tester.startPage( ConfirmationPage.class, parameters ) );
        assertEquals( (long) SC_NOT_FOUND,  (long) tester.getServletResponse().getCode() );
    }

    /**
     * ...
     */
    @Test
    public void testNoUser() {
        parameters.put( "confirmation", "1234" );

        assertNull( tester.startPage( ConfirmationPage.class, parameters ) );
        assertEquals( (long) SC_NOT_FOUND,  (long) tester.getServletResponse().getCode() );
    }

    /**
     * ...
     */
    @Test
    public void testBadNumber() {
        parameters.put( "user", "1" );
        parameters.put( "confirmation", "1234" );

        assertNull( tester.startPage( ConfirmationPage.class, parameters ) );
        assertEquals( (long) SC_NOT_FOUND, (long)  tester.getServletResponse().getCode() );
    }

    /**
     * ...
     */
    @Test
    @Transactional
    @Rollback
    public void testConfirmed() {
        User user = userDao.load( 1L );
        user.setConfirmation( 1234L );
        userDao.save( user );

        parameters.put( "user", "1" );
        parameters.put( "confirmation", "1234" );

        tester.startPage( ConfirmationPage.class, parameters );
        tester.assertRenderedPage( ConfirmationPage.class );
    }
}
