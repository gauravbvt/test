package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Plan;
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
    private final Logger LOG = LoggerFactory.getLogger( ErrorPage.class );
    private String stackTrace;

    @SpringBean
    private MailSender mailSender;

    public ErrorPage() {
    }

    public ErrorPage( RuntimeException e ) {
        StringWriter writer = new StringWriter();
        e.printStackTrace( new PrintWriter( writer ) );
        stackTrace = writer.toString();
    }

    @Override
    protected void configureResponse() {
        super.configureResponse();
        if ( stackTrace != null ) emailException();
        getWebRequestCycle().getWebResponse().getHttpServletResponse().setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
    }

    private void emailException() {
        User currentUser = User.current();
        Plan plan = currentUser.getPlan();
        String toAddress = plan.getPlannerSupportCommunityUri( getApp().getSupportCommunityUri() );
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo( toAddress );
            email.setFrom( currentUser.getEmail() );
            email.setReplyTo( currentUser.getEmail() );
            String subject = "SERVER ERROR from " + currentUser.getFullName();
            email.setSubject( subject );
            email.setText( stackTrace );
            LOG.info( currentUser.getUsername()
                    + " emailing \"" + subject + "\" to "
                    + toAddress );
            mailSender.send( email );
        } catch ( Exception e ) {
            LOG.warn( currentUser.getUsername()
                    + " failed to email server error ", e );
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
