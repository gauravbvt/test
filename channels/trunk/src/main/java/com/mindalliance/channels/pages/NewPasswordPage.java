package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
    private static final String MISSING_USERNAME_OR_EMAIL = "Please provide a user name or email address.";
    private static final String NEW_PASSWORD_SENT = "A new password has been emailed to you.";

    private String username = "";
    private String email = "";
    private String outcome = "";
    private WebMarkupContainer outcomeContainer;
    private Form requestForm;
    private AjaxLink<String> loginLink;

    public NewPasswordPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    private void init() {
        addForm();
        addOutcome();
        addLogin();
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
                    target.add( requestForm );
                }
                target.add( outcomeContainer );
                target.add(  loginLink );
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
            outcomeLabel.add(  new AttributeModifier( "class", new Model<String>( "error" ) ) );
        }
        outcomeContainer.add( outcomeLabel );
        makeVisible( outcomeLabel, !outcome.isEmpty() );
        addOrReplace( outcomeContainer );
    }

    private void addLogin() {
        loginLink = new AjaxLink<String>( "login" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setResponsePage( LoginPage.class );
            }
        };
        requestForm.add(  loginLink );
    }

    private void requestNewPassword() {
        if ( getUsername().isEmpty() && getEmail().isEmpty() ) {
            setOutcome( MISSING_USERNAME_OR_EMAIL );
        } else {
            UserRecordService userDao = queryService.getUserInfoService();
            ChannelsUser user = null;
            boolean unrecognizedUsername = false;
            boolean unrecognizedEmailAddress = false;
            if ( !getUsername().isEmpty() ) {
                user = userDao.getUserWithIdentity( getUsername() );
                unrecognizedUsername = user == null;
            }
            if ( !getEmail().isEmpty() ) {
                user = (ChannelsUser) CollectionUtils.find(
                        userDao.getAllEnabledUsers(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (ChannelsUser) object ).getEmail().equals( getEmail() );
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
                boolean passwordChanged = userDao.changePassword(
                        user,
                        queryService.getModelManager(),
                        mailSender );
                if ( passwordChanged ) {
                    setOutcome( NEW_PASSWORD_SENT );
                }
            }
        }
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    private static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style", new Model<String>(
                visible ? "" : "display:none" ) ) );
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
