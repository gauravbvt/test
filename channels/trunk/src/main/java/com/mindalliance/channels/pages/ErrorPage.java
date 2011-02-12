package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.User;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A custom error page, nothing fancy...
 */
public class ErrorPage extends WebPage {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ErrorPage.class );
    private String stackTrace;
    private Exception exception;

    @SpringBean
    private MailSender mailSender;

    public ErrorPage() {
    }

    public ErrorPage( RuntimeException e ) {
        exception = e;
    }

    @Override
    protected void configureResponse() {
        super.configureResponse();
        String supportCommunity = getApp().getPlannerSupportCommunity();
        if ( exception != null ) {
            emailException(
                    exception,
                    mailSender,
                    supportCommunity );
        }
        getWebRequestCycle().getWebResponse().getHttpServletResponse().setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
    }

    static public void emailException(
            Exception e,
            MailSender mailSender,
            String supportCommunity ) {
        StringWriter writer = new StringWriter();
        e.printStackTrace( new PrintWriter( writer ) );
        String stackTrace = writer.toString();
        User currentUser = User.current();
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo( supportCommunity );
            email.setFrom( currentUser.getEmail() );
            email.setReplyTo( currentUser.getEmail() );
            String subject = "SERVER ERROR from " + currentUser.getFullName();
            email.setSubject( subject );
            email.setText( stackTrace );
            LOG.info( currentUser.getUsername()
                    + " emailing \"" + subject + "\" to "
                    + supportCommunity );
            mailSender.send( email );
        } catch ( Exception exc ) {
            LOG.warn( currentUser.getUsername()
                    + " failed to email server error ", exc );
        }
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }

    private Channels getApp() {
        return (Channels) getApplication();
    }


}
