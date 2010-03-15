// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import com.mindalliance.mindpeer.pages.components.ValidationStyler;
import com.mindalliance.mindpeer.services.MindPeer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The registration page.
 * @todo Add captchas
 */
public class RegistrationPage extends WebPage {

    private static final char SLASH = '/';
    private static final int PWD_MIN_LEN = 6;
    private static final int NAME_MIN_LEN = 3;

    private String password = "";
    private String verify = "";

    @SpringBean
    private UserDao userDao;

    @SpringBean
    private MindPeer mindPeer;

    private User user;

    /**
     * Create a new RegistrationPage instance.
     */
    public RegistrationPage() {
        setDefaultModel( new CompoundPropertyModel<String>( this ) );
        user = new User();
        add( new FeedbackPanel( "feedback" ),
             initialize( new Form<RegistrationPage>( "form" ) {
                 @Override
                 protected void onSubmit() {
                     user.setPassword( DigestUtils.shaHex( password ) );
                     WebRequest webRequest = (WebRequest) getRequest();
                     HttpServletRequest request = webRequest.getHttpServletRequest();
                     String requestUrl = request.getRequestURL().toString();
                     String path =
                             requestUrl.substring( 0, requestUrl.indexOf( webRequest.getPath() ) );
                     mindPeer.register( user, path );

                     PageParameters pageParameters = new PageParameters();
                     pageParameters.add( "email", user.getEmail() );
                     setRedirect( false );
                     setResponsePage( ConfirmationSent.class, pageParameters );
                 }
             } )
        );
    }

    /**
     * ...
     *
     * @param form of type Form<RegistrationPage>
     * @return Form<RegistrationPage>
     */
    private Form<RegistrationPage> initialize( Form<RegistrationPage> form ) {

        FormComponent<String> pw1 = new PasswordTextField( "password" );
        FormComponent<String> pw2 = new PasswordTextField( "verify" );
        FormComponent<String> usernameField = new RequiredTextField<String>( "username" );
        FormComponent<String> emailField = new RequiredTextField<String>( "email" );

        form.add(
            usernameField.add(
                new StringValidator.MinimumLengthValidator( NAME_MIN_LEN ),
                new PatternValidator( "\\p{Alpha}[\\p{Alnum}\\.\\-_]*" ),
                new AbstractValidator<String>() {
                        @Override
                        protected void onValidate( IValidatable<String> validatable ) {
                            String newName = validatable.getValue();
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put( "name", newName );
                            if ( UserDispatchPage.Reserved.contains( newName ) )
                                error( validatable, "user.invalidName", map );
                            if ( userDao.findByName( newName ) != null )
                                error( validatable, "user.exists", map );
                        }


                    } ),
            pw1.add( new StringValidator.MinimumLengthValidator( PWD_MIN_LEN ) ),
            pw2,
            emailField.add(
                EmailAddressValidator.getInstance(),
                new AbstractValidator<String>() {
                    @Override
                    protected void onValidate( IValidatable<String> validatable ) {
                        if ( userDao.findByEmail( validatable.getValue() ) != null )
                            error( validatable, "email.exists" );
                    }
                } )
        );

        form.add( new EqualPasswordInputValidator( pw1, pw2 ) );

        // Add valid/invalid style to all components
        Iterator<? extends Component> components = form.iterator();
        while ( components.hasNext() )
            components.next().add( new ValidationStyler() );

        return form;
    }

    /**
     * Return the RegistrationPage's password.
     * @return the value of the first password field
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password1 of this RegistrationPage.
     * @param password the new password value.
     */
    public void setPassword( String password ) {
        this.password = password;
    }

    /**
     * Return the RegistrationPage's verify field.
     * @return the value of verify field
     */
    public String getVerify() {
        return verify;
    }

    /**
     * Sets the verify field of this RegistrationPage.
     * @param verify the confirmed password value.
     */
    public void setVerify( String verify ) {
        this.verify = verify;
    }

    /**
     * Return the RegistrationPage's email.
     * @return the value of email
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * Sets the email of this RegistrationPage.
     * @param email the new email value.
     *
     */
    public void setEmail( String email ) {
        user.setEmail( email );
    }

    /**
     * Sets the username of this RegistrationPage.
     * @param username the new username value.
     *
     */
    public void setUsername( String username ) {
        user.setUsername( username.toLowerCase() );
    }

    /**
     * Return the RegistrationPage's username.
     * @return the value of username
     */
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Return the RegistrationPage's user.
     * @return the value of user
     */
    public User getUser() {
        return user;
    }
}
