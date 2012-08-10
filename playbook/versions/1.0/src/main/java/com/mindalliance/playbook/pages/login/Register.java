/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.EmailMedium;
import com.mindalliance.playbook.pages.MobilePage;
import com.octo.captcha.service.image.ImageCaptchaService;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator.MinimumLengthValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * The registration page.
 */
public class Register extends MobilePage {

    private static final long serialVersionUID = 2536370629247274187L;

    @SpringBean( name = "serverUrl" )
    private String serverUrl;

    private String email = "";

    private String password;

    // TODO externalize this list
    private static final String[] RESTRICTIONS = {
        ".*@mind-alliance\\.com"
    };

    @SpringBean
    private AccountDao accountDao;

    @SpringBean
    private ImageCaptchaService captchaService;

    @SpringBean
    private JavaMailSender mailSender;

    private static final Logger LOG = LoggerFactory.getLogger( Register.class );

    @SpringBean( name = "from" )
    private String from;

    @SuppressWarnings( "unchecked" )
    public Register( PageParameters parameters ) {
        super( parameters );
        setDefaultModel( new CompoundPropertyModel<Register>( this ) );

        LOG.debug( "Creating page for session {}", getSession().getId() );

        add(
            new FeedbackPanel( "error" ), new RegistrationForm() );
    }

    @Override
    public String getPageTitle() {
        return "Playbook - Registration";
    }

    static String getKey( String email, Date date ) {
        try {
            MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
            if ( email != null )
                digest.update( email.getBytes() );

            digest.update( "key".getBytes() );
            digest.update( Long.valueOf( date.getTime() ).toString().getBytes() );

            return encode( digest.digest() );
        } catch ( NoSuchAlgorithmException e ) {
            LOG.error( "Missing SHA-1 digest", e );
        } catch ( IOException e ) {
            LOG.error( "Exception", e );
        }

        return null;
    }

    private static String encode( byte[] digested ) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Base64OutputStream out = new Base64OutputStream( bytes );
        try {
            out.write( digested, 0, digested.length );
            out.flush();
            return bytes.toString( "UTF8" );
        } finally {
            out.close();
        }
    }

    static String getContent( String serverUrl, String template, String key ) throws IOException {
        InputStream stream = Register.class.getResourceAsStream( template );
        try {
            String text = Streams.readString( stream );
            return text.replace(
                "%link%", serverUrl + "/confirm.html?key=" + URLEncoder.encode( key, "UTF8" ) );
        } finally {
            stream.close();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    //================================================
    private class RegistrationForm extends Form {

        private static final long serialVersionUID = 8440840123115080779L;

        private RegistrationForm() {
            super( "form" );

            PasswordTextField pwField = new PasswordTextField( "password" );
            pwField.setRequired( true );
            pwField.add( new MinimumLengthValidator( 5 ) );
            //            PasswordTextField cField = new PasswordTextField( "confirm" );
            //            cField.setRequired( true );

            add(
                new RequiredTextField<String>( "email" ).add(
                    (IValidator<String>) EmailAddressValidator.getInstance(), new AbstractValidator<String>() {
                        @Override
                        protected void onValidate( IValidatable<String> validatable ) {
                            Account account = accountDao.findByMedium( 
                                new EmailMedium( null, validatable.getValue() ) );
                            if ( account != null ) {
                                ValidationError error = new ValidationError();
                                error.setMessage( "Email address already registered." );
                                validatable.error( error );
                            }
                        }
                    }, 
                    new AbstractValidator<String>() {
                        @Override
                        protected void onValidate( IValidatable<String> validatable ) {
                            String newEmail = validatable.getValue();
                            for ( String restriction : RESTRICTIONS )
                                if ( Pattern.matches( restriction, newEmail ) )
                                    return;
    
                            ValidationError error = new ValidationError();
                            error.setMessage(
                                "This server is restricted to certain email addresses." + " This is not one of them..." );
                            validatable.getModel().setObject( null );
                            validatable.error( error );
                        }
                    }
                ),

                //                cField,
                pwField

                //                new RequiredTextField( "captcha" ).add(
                //                    new AbstractValidator<String>() {
                //                        @Override
                //                        protected void onValidate( IValidatable<String> validatable ) {
                //                            Session session = getSession();
                //                            LOG.debug( "Validating for session {}", session.getId() );
                //                            try {
                //    
                //                                if ( !captchaService.validateResponseForID(
                //                                    session.getId(), validatable.getValue() ) )
                //                                {
                //                                    ValidationError error = new ValidationError();
                //                                    error.setMessage( "Incorrect key. Try again." );
                //                                    validatable.error( error );
                //                                    session.invalidate();
                //                                }
                //                            } catch ( CaptchaServiceException e ) {
                //                                LOG.warn( "Captcha exception", e );
                //                                ValidationError error = new ValidationError();
                //                                error.setMessage( "Incorrect key. Reload page and try again." );
                //                                validatable.error( error );
                //                                session.invalidate();
                //                            }
                //                        }
                //                    } )

            );

            //            add(
            //                new EqualPasswordInputValidator(
            //                    pwField,
            //                    cField ) );

        }

        @Override
        public void onSubmit() {
            try {
                createAccount( email );
                setResponsePage( Thanks.class );

            } catch ( MessagingException e ) {
                LOG.error( "Unable to send registration email", e );
            } catch ( IOException e ) {
                LOG.error( "Unable to send registration email", e );
            }
        }

        private void redirect( Exception e, String msg, String code ) {
            LOG.error( msg, e );
            PageParameters parms = new PageParameters();
            parms.set( "error", code );
            setResponsePage( Register.class, parms );
        }

        private void createAccount( String email ) throws MessagingException, IOException {
            Date date = new Date();
            String key = getKey( email, date );

            Account account = new Account( "playbook", email, new Contact( new EmailMedium( null, email ) ) );
            account.setConfirmation( key );
            account.setPassword( getHash( password ) );
            accountDao.save( account );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper( message );

            helper.setFrom( from );
            helper.setTo( email );
            helper.setSubject( "Playbook registration" );
            helper.setText( getContent( serverUrl, "registration.html", key ), true );

            mailSender.send( message );
            LOG.info( "Sent activation message to {}", email );
        }

        private String getHash( String password ) {
            PasswordEncoder encoder = new MessageDigestPasswordEncoder( "sha", true );
            return encoder.encodePassword( password, null );
        }
    }
}
