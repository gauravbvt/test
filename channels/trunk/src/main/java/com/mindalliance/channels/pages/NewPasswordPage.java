package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.mail.MailSender;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/20/11
 * Time: 10:06 AM
 */
public class NewPasswordPage extends WebPage {

    @SpringBean
    private QueryService queryService;

    @SpringBean
    private MailSender mailSender;

    private static final String UNKNOWN_USERNAME = "Unrecognized user name.";
    private static final String UNKNOWN_EMAIL = "Unrecognized email address.";
    private static final String UNKNOWN_USERNAME_AND_EMAIL = "Unrecognized user name and email address.";
    private static final String MISSING_USERNAME_OR_EMAIL = "Provide a user name or email address.";
    private static final String NEW_PASSWORD_SENT = "A new password was emailed to you.";

    private String username = "";
    private String email = "";
    private String outcome = "";
    private WebMarkupContainer outcomeContainer;
    private Form requestForm;

    public NewPasswordPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    private void init() {
        addForm();
        addOutcome();
    }

    private void addForm() {
        requestForm = new Form( "requestForm" );
        add( requestForm );
        addIdentityFields();
        AjaxLink<String> submitLink = new AjaxLink<String>( "submit" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                requestNewPassword();
                addOutcome();
                if ( getOutcome().equals( NEW_PASSWORD_SENT ) ) {
                    username = "";
                    email= "";
                    addIdentityFields();
                    target.addComponent( requestForm );
                }
                target.addComponent( outcomeContainer );
            }
        };
        requestForm.add( submitLink );
    }

    private void addIdentityFields() {
        TextField<String> usernameField = new TextField<String>( "username", new PropertyModel<String>( this, "username" ) );
        usernameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        usernameField.setOutputMarkupId( true );
        requestForm.addOrReplace( usernameField );
        TextField<String> emailField = new TextField<String>( "email", new PropertyModel<String>( this, "email" ) );
        emailField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        emailField.setOutputMarkupId( true );
        requestForm.addOrReplace( emailField );
    }


    private void addOutcome() {
        outcomeContainer = new WebMarkupContainer( "outcome" );
        outcomeContainer.setOutputMarkupId( true );
        Label outcomeLabel = new Label( "message", getOutcome() );
        if ( !outcome.equals(  NEW_PASSWORD_SENT ) ) {
            outcomeLabel.add(  new AttributeModifier( "class", true, new Model<String>( "error" ) ) );
        }
        outcomeContainer.add( outcomeLabel );
        AjaxLink<String> loginLink = new AjaxLink<String>( "login" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setResponsePage( LoginPage.class );
            }
        };
        loginLink.setVisible( getOutcome().equals( NEW_PASSWORD_SENT ) );
        outcomeContainer.add( loginLink );
        addOrReplace( outcomeContainer );
    }

    private void requestNewPassword() {
        if ( getUsername().isEmpty() && getEmail().isEmpty() ) {
            setOutcome( MISSING_USERNAME_OR_EMAIL );
        } else {
            UserService userService = queryService.getUserService();
            User user = null;
            boolean unrecognizedUsername = false;
            boolean unrecognizedEmailAddress = false;
            if ( !getUsername().isEmpty() ) {
                user = userService.getUserNamed( getUsername() );
                unrecognizedUsername = user == null;
            }
            if ( !getEmail().isEmpty() ) {
                user = (User) CollectionUtils.find(
                        userService.getUsers(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (User) object ).getEmail().equals( getEmail() );
                            }
                        }
                );
                unrecognizedEmailAddress = user == null;
            }

            if ( unrecognizedEmailAddress && unrecognizedUsername ) {
                setOutcome( UNKNOWN_USERNAME_AND_EMAIL );
            } else if ( unrecognizedEmailAddress ) {
                setOutcome( UNKNOWN_EMAIL );
            } else if ( unrecognizedUsername ) {
                setOutcome( UNKNOWN_USERNAME );
            }
            if ( !unrecognizedUsername && !unrecognizedEmailAddress ) {
                boolean passwordChanged = userService.changePassword(
                        user,
                        queryService.getPlanManager(),
                        mailSender );
                if ( passwordChanged ) {
                    setOutcome( NEW_PASSWORD_SENT );
                }
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String val ) {
        username = val == null ? "" : val;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String val ) {
        email = val == null ? "" : val.toLowerCase();
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome( String outcome ) {
        this.outcome = outcome;
    }
}
