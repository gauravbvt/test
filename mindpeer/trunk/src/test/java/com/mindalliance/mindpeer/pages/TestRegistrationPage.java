// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.IntegrationTest;
import com.mindalliance.mindpeer.model.User;
import com.mindalliance.mindpeer.services.Mailer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.util.tester.FormTester;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;

/**
 * ...
 */
public class TestRegistrationPage extends IntegrationTest {

    private RegistrationPage page;

    private FormTester formTester;

    private User user;

    @Autowired
    private Mailer mailer;

    /**
     * Create a new TestRegistrationPage instance.
     */
    public TestRegistrationPage() {
    }

    /**
     * ...
     */
    @Override
    @Before
    public void init() {
        super.init();

        page = (RegistrationPage) tester.startPage( RegistrationPage.class );
        user = page.getUser();
        formTester = tester.newFormTester( "form" );
    }

    @After
    public void cleanUp() {
    }

    /** Silly tests for coverage. */
    @Test
    public void testAccessors() {
        assertNull( page.getEmail() );
        page.setEmail( "bla" );
        assertEquals( "bla", page.getEmail() );

        assertNull( page.getUsername() );
        page.setUsername( "bla" );
        assertEquals( "bla", page.getUsername() );

        assertEquals( "", page.getPassword() );
        page.setPassword( "bla" );
        assertEquals( "bla", page.getPassword() );

        assertEquals( "", page.getVerify() );
        page.setVerify( "bla" );
        assertEquals( "bla", page.getVerify() );
    }

    /**
     * ...
     */
    @Test
    @Transactional
    @Rollback
    public void testValid() {
        MimeMessage message = mock( MimeMessage.class );
        when( mailSender.createMimeMessage() ).thenReturn( message );

        formTester.setValue( "username", "bob" );
        formTester.setValue( "password", "Robert" );
        formTester.setValue( "verify",   "Robert" );
        formTester.setValue( "email",    "bob@example.com" );

        formTester.submit();

        assertEquals( DigestUtils.shaHex( "Robert" ), user.getPassword() );

        assertNotNull( userDao.findByName( "bob" ) );
        verify( mailSender ).createMimeMessage();
        verify( mailSender ).send( message );
        verifyNoMoreInteractions( mailSender );

        tester.assertRenderedPage( ConfirmationSent.class );
        tester.assertNoErrorMessage();
        tester.assertModelValue( "email", user.getEmail() );
    }

    /**
     * ...
     */
    @Test
    @Transactional
    @Rollback
    public void testInvalidName() {

        formTester.setValue( "username", "bo" );
        formTester.setValue( "password", "Robert" );
        formTester.setValue( "verify",   "Robert" );
        formTester.setValue( "email",    "bob@example.com" );

        formTester.submit();

        assertNull( userDao.findByName( "bo" ) );
        verifyNoMoreInteractions( mailSender );

        tester.assertRenderedPage( RegistrationPage.class );
        tester.assertErrorMessages( new String[]{
                "username needs to be 3 characters or more." } );
    }

    /**
     * ...
     */
    @Test
    @Transactional
    @Rollback
    public void testInvalidName2() {

        formTester.setValue( "username", "guest" );
        formTester.setValue( "password", "Robert" );
        formTester.setValue( "verify",   "Robert" );
        formTester.setValue( "email",    "bob@example.com" );

        formTester.submit();

        verifyNoMoreInteractions( mailSender );
        tester.assertRenderedPage( RegistrationPage.class );
        tester.assertErrorMessages( new String[]{
                "Username already exists. Pick another one." } );
    }

    /**
     * ...
     */
    @Test
    @Transactional
    @Rollback
    public void testInvalidName3() {

        formTester.setValue( "username", "focus" );
        formTester.setValue( "password", "Robert" );
        formTester.setValue( "verify",   "Robert" );
        formTester.setValue( "email",    "bob@example.com" );

        formTester.submit();

        verifyNoMoreInteractions( mailSender );
        tester.assertRenderedPage( RegistrationPage.class );
        tester.assertErrorMessages( new String[]{
                "Username focus is reserved." } );
    }

    /**
     * ...
     */
    @Test
    public void testInvalidEmail() {
        formTester.setValue( "username", "bob" );
        formTester.setValue( "password", "Robert" );
        formTester.setValue( "verify",   "Robert" );
        formTester.setValue( "email",    "guest@example.com" );

        formTester.submit();
        verifyNoMoreInteractions( mailSender );

        tester.assertRenderedPage( RegistrationPage.class );
        tester.assertErrorMessages( new String[]{
                "Email already registered. Forgot your username or password?" } );
    }

    // TODO Test all validators in registration page

}
