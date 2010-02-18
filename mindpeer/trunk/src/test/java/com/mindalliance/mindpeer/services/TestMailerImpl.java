// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.services;

import com.mindalliance.mindpeer.model.User;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.AtLeast;
import org.mockito.internal.verification.Times;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

/**
 * ...
 */
public class TestMailerImpl {

    private MailerImpl mailer;

    @Mock
    private User user;

    @Mock
    private JavaMailSender mailSender;

    /**
     * ...
     */
    @Before
    public void init() {
        MockitoAnnotations.initMocks( this );
        mailer = new MailerImpl();
        mailer.setMailSender( mailSender );
        mailer.setSupportEmail( "support@mind-alliance.com" );
    }

    /**
     * ...
     */
    @Test
    public void testSendConfirmation() {
        MimeMessage message = mock( MimeMessage.class );
        when( mailSender.createMimeMessage() ).thenReturn( message );
        when( user.getEmail() ).thenReturn( "denis@ranger.homelinux.net" );

        mailer.sendConfirmation( user, "bla" );

        verify( user, new AtLeast( 1 ) ).getEmail();
        verify( mailSender ).createMimeMessage();
        verify( mailSender ).send( (MimeMessage) any() );
        verifyNoMoreInteractions( mailSender, user );
    }

    /**
     * ...
     * @throws Exception on errors
     */
    @Test
    public void testSendConfirmation1() throws Exception {
        MimeMessage message = mock( MimeMessage.class );
        when( mailSender.createMimeMessage() ).thenReturn( message );
        when( user.getEmail() ).thenReturn( "denis@ranger.homelinux.net" );

        doThrow( new AddressException( "bla" ) ).when( message ).setContent( (Multipart) any() );

        mailer.sendConfirmation( user, "bla" );

        verify( mailSender ).createMimeMessage();
        verify( mailSender, new Times( 0 ) ).send( (MimeMessage) any() );
        verify( message ).setContent( (Multipart) any() );
        verifyNoMoreInteractions( mailSender, user, message );
    }

    /**
     * ...
     * @throws Exception on errors
     */
    @Test
    public void testSendConfirmation2() throws Exception {
        MimeMessage message = mock( MimeMessage.class );
        when( mailSender.createMimeMessage() ).thenReturn( message );
        when( user.getEmail() ).thenReturn( "denis@ranger.homelinux.net" );

        doThrow( new MailSendException( "failed." ) ).when( mailSender ).send( message );

        mailer.sendConfirmation( user, "bla" );
        verify( user, new AtLeast( 1 ) ).getEmail();
        verify( mailSender ).createMimeMessage();
        verify( mailSender, new Times( 1 ) ).send( (MimeMessage) any() );
        verifyNoMoreInteractions( mailSender, user );
    }

    /**
     * ...
     */
    @Test
    public void testAccessors() {
        mailer.setSupportEmail( "bla" );
        assertEquals( "bla", mailer.getSupportEmail() );

        assertSame( mailSender, mailer.getMailSender() );
    }

}
