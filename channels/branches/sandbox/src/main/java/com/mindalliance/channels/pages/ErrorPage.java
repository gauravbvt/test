package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.User;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
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
public class ErrorPage extends AbstractChannelsWebPage {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ErrorPage.class );
    private Exception exception;

    @SpringBean
    private MailSender mailSender;

    public ErrorPage() {
        init();
    }

    public ErrorPage( RuntimeException e ) {
        exception = e;
        init();
    }

    private void init() {
        addStacktrace();
    }

    private static String stackTraceToString( Exception exc ) {
        StringWriter writer = new StringWriter();
        exc.printStackTrace( new PrintWriter( writer ) );
        return writer.toString();
     }

    private void addStacktrace() {
        WebMarkupContainer stackTraceDiv = new WebMarkupContainer( "stackTraceContainer" );
        add( stackTraceDiv );
        stackTraceDiv.add( new Label(
                "stackTrace",
                exception == null
                    ? ""
                    : stackTraceToString( exception ) ) );
        stackTraceDiv.add( new AttributeModifier(
                "style",
                true,
                new Model<String>(
                        ( User.current().isAdmin() )
                        ? "padding-top: 20px;display:block;"
                        : "display:none;"
                ) )
         );
    }

    @Override
    protected void configureResponse() {
        super.configureResponse();
        String supportCommunity = getSupportCommunity();
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
            Exception exception,
            MailSender mailSender,
            String supportCommunity ) {
        User currentUser = User.current();
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo( supportCommunity );
            email.setFrom( currentUser.getEmail() );
            email.setReplyTo( currentUser.getEmail() );
            String subject = "SERVER ERROR from " + currentUser.getFullName();
            email.setSubject( subject );
            email.setText( stackTraceToString( exception ) );
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


}
