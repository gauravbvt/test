// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.services;

import com.mindalliance.mindpeer.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * External email interface using javamail.
 */
public class MailerImpl implements Mailer {

    private static final Logger LOG = LoggerFactory.getLogger( MailerImpl.class );

    private JavaMailSender mailSender;
    private String supportEmail;

    /**
     * Create a new MailerImpl instance.
     */
    public MailerImpl() {
    }

    /**
     * Send an account creation email to the given user.
     * @param user the user
     * @param link the link to include in the message
     */
    public void sendConfirmation( User user, CharSequence link ) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper( mimeMessage, true, "UTF-8" );

            // TODO Convert email to template

            helper.setTo( user.getEmail() );
            helper.setFrom( supportEmail );
            helper.setSubject( "MindPeer account registration" );
            helper.setText( "This is a test.\nGo to " + link,
                            "<p>This is a <b>test</b>.</p>\n"
                            + "<p>Click <a href=\"" + link + "\">here</a> "
                            + "to complete your registration.</p>" );

            mailSender.send( mimeMessage );

            LOG.info( "Confirmation message sent to {}", user.getEmail() );

        } catch ( MessagingException e ) {
            LOG.warn( "Error while creating confirmation message", e );
        } catch ( MailException e ) {
            LOG.warn( "Problem sending confirmation message", e );
        }
    }

    /**
     * Return the mail sender.
     * @return the value of mailSender
     */
    public JavaMailSender getMailSender() {
        return mailSender;
    }

    /**
     * Sets the mail sender of.
     * @param mailSender the new mail sender.
     */
    public void setMailSender( JavaMailSender mailSender ) {
        this.mailSender = mailSender;
    }

    /**
     * Return the MailerImpl's supportEmail.
     * @return the value of supportEmail
     */
    public String getSupportEmail() {
        return supportEmail;
    }

    /**
     * Sets the support email. This is the "From" address for all messages sent by this mailer.
     * @param supportEmail the new support email value.
     */
    public void setSupportEmail( String supportEmail ) {
        this.supportEmail = supportEmail;
    }
}
