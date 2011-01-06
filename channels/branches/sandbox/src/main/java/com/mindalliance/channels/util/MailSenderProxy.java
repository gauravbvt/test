// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * A mail sender that does nothing if not configured right.
 * When actually configured, delegate to a real mail sender.
 *
 * Messages will be sent if host, username and password are specified.
 */
public class MailSenderProxy implements MailSender, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger( MailSenderProxy.class );

    private final JavaMailSenderImpl actualSender = new JavaMailSenderImpl();

    public MailSenderProxy() {
    }

    public void setJavaMailProperties( Properties javaMailProperties ) {
        actualSender.setJavaMailProperties( javaMailProperties );
    }

    public Properties getJavaMailProperties() {
        return actualSender.getJavaMailProperties();
    }

    public void setProtocol( String protocol ) {
        actualSender.setProtocol( protocol );
    }

    public String getProtocol() {
        return actualSender.getProtocol();
    }

    public void setHost( String host ) {
        actualSender.setHost( host );
    }

    public String getHost() {
        return actualSender.getHost();
    }

    public void setPort( int port ) {
        actualSender.setPort( port );
    }

    public int getPort() {
        return actualSender.getPort();
    }

    public void setUsername( String username ) {
        actualSender.setUsername( username );
    }

    public String getUsername() {
        return actualSender.getUsername();
    }

    public void setPassword( String password ) {
        actualSender.setPassword( password );
    }

    public String getPassword() {
        return actualSender.getPassword();
    }

    @Override
    public void send( SimpleMailMessage simpleMessage ) throws MailException {
        if ( isConfigured() )
            actualSender.send( simpleMessage );
        else
            LOG.info( "Mail sender not configured. Message dropped." );
    }

    @Override
    public void send( SimpleMailMessage[] simpleMessages ) throws MailException {
        if ( isConfigured() )
            actualSender.send( simpleMessages );
        else
            LOG.warn( "Mail sender not configured. Message dropped." );
    }

    /**
     * Test if mail message will be forwarded to the actual mailer.
     * @return a boolean
     */
    public boolean isConfigured() {
        return !( getHost() == null || getUsername() == null || getPassword() == null );
    }

    @Override
    public void afterPropertiesSet() {
        LOG.info( "Mail sender configured: {}", isConfigured() );
    }
}
