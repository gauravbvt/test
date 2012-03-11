package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.EmailMedium;
import com.mindalliance.playbook.pages.MobilePage;
import com.octo.captcha.service.CaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;

/**
 * Password reset page.
 */
public class Reset extends MobilePage {

    private static final Logger LOG = LoggerFactory.getLogger( Reset.class );

    @SpringBean
    AccountDao accountDao;

    @SpringBean
    CaptchaService captchaService;

    @SpringBean
    private JavaMailSender mailSender;

    private String email;

    private String captcha;

    @SpringBean( name = "serverUrl" )
    private String serverUrl;

    @SpringBean( name = "from" )
    private String from;

    @SuppressWarnings( "unchecked" )
    public Reset( PageParameters parameters ) {
        super( parameters );

        setStatelessHint( true );
        setDefaultModel( new CompoundPropertyModel<Reset>( this ) );
       
        add(
            new FeedbackPanel( "feedback" ),
            new StatelessForm( "form" ) {
                @Override
                protected void onSubmit() {
                    try {
                        sendReset( email );
                        WebResponse response = (WebResponse) getResponse();
                        response.sendRedirect( "resetSent" );
                        
                    } catch ( MessagingException e ) {
                        LOG.error( "Unable to send message to " + email, e );
                        throw new RuntimeException( e );

                    } catch ( IOException e ) {
                        LOG.error( "Exception while resetting password", e );
                        throw new RuntimeException( e );
                    }
                }
            }.add(
                new RequiredTextField<String>( "email" ).add(
                    (IValidator<String>) EmailAddressValidator.getInstance(),
                    new AbstractValidator<String>() {
                        @Override
                        protected void onValidate( IValidatable<String> validatable ) {
                            if ( accountDao.findByMedium( new EmailMedium( null, validatable.getValue() ) ) == null ) {
                                ValidationError error = new ValidationError();
                                error.setMessage( "Invalid email address." );
                                validatable.error( error );
                            }
                        }
                    } ),
                new RequiredTextField( "captcha" ).add(
                    new AbstractValidator<String>() {
                        @Override
                        protected void onValidate( IValidatable<String> validatable ) {
                            Session session = getSession();
                            try {

                                if ( !captchaService.validateResponseForID( session.getId(),
                                                                            validatable.getValue() ) )
                                {
                                    ValidationError error = new ValidationError();
                                    error.setMessage( "Incorrect key. Reload page and try again." );
                                    validatable.error( error );
                                    session.invalidate();
                                }
                            } catch ( CaptchaServiceException e ) {
                                LOG.warn( "Captcha exception", e );
                                ValidationError error = new ValidationError();
                                error.setMessage( "Incorrect key. Reload page and try again." );
                                validatable.error( error );
                                session.invalidate();
                            }
                        }
                    } )

            ) );
    }

    @Override
    public String getPageTitle() {
        return "Playbook - Password reset";
    }

    private void sendReset( String email ) throws MessagingException, IOException {
        Date date = new Date();
        String key = Register.getKey( email, date );

        Account account = accountDao.findByMedium( new EmailMedium( null, email ) );
        account.setConfirmation( key );
        accountDao.save( account );

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper( message );

        from = "denis@ranger.homelinux.net";
        helper.setFrom( from );
        helper.setTo( email );
        helper.setSubject( "Playbook password reset" );
        helper.setText( Register.getContent( serverUrl, "reset-template.html", key ), true );

        mailSender.send( message );
        LOG.info( "Sent password reset message to {}", email );
    }
    

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha( String captcha ) {
        this.captcha = captcha;
    }
}
