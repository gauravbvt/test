package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.WebResponse;
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


    public ErrorPage( Exception e ) {
        exception = e;
        init();
    }

    private void init() {
        //todo - unhack
        if ( exception == null ) {
            exception = ( (Channels) Channels.get() ).getExceptionOnce();
        }
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
                new Model<String>(
                        ( getUser().isAdmin() )
                                ? "padding-top: 20px;display:block;"
                                : "display:none;"
                ) )
        );
    }

    @Override
    protected void configureResponse( WebResponse response ) {
        super.configureResponse( response );
        String supportCommunity = getSupportCommunity();
        if ( exception != null ) {
            emailException(
                    exception,
                    mailSender,
                    supportCommunity,
                    getUser() );
        }
        response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
    }

    static public void emailException(
            Exception exception,
            MailSender mailSender,
            String supportCommunity,
            ChannelsUser user ) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo( supportCommunity );
            email.setFrom( user.getEmail() );
            email.setReplyTo( user.getEmail() );
            String subject = "SERVER ERROR from " + user.getFullName();
            email.setSubject( subject );
            email.setText( stackTraceToString( exception ) );
            LOG.info( user.getUsername()
                    + " emailing \"" + subject + "\" to "
                    + supportCommunity );
            mailSender.send( email );
        } catch ( Exception exc ) {
            LOG.warn( user.getUsername()
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
