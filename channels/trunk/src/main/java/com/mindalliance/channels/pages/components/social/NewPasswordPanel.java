package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.users.UserRecordService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/10/13
 * Time: 10:15 AM
 */
public class NewPasswordPanel extends AbstractSocialListPanel {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( NewPasswordPanel.class );

    private WebMarkupContainer newPasswordContainer;
    private ChannelsUser temp;
    private String passwordHash;
    private boolean passwordOk = false;
    private String newPassword = "";
    private String repeatNewPassword = "";
    private TextField<String> newPasswordText;
    private TextField<String> repeatNewPasswordText;
    private List<String> errors;
    private WebMarkupContainer errorsContainer;
    private WebMarkupContainer userPasswordContainer;

    @SpringBean
    private UserRecordService userInfoService;


    public NewPasswordPanel( String id, SocialPanel socialPanel, boolean collapsible ) {
        super( id, collapsible );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return null;  // Todo
    }

    @Override
    public String getHelpTopicId() {
        return null;  // Todo
    }

    protected void init() {
        super.init();
        resetAll();
    }

    private void resetAll() {
        resetTemp();
        errors = new ArrayList<String>();
        addNewPasswordContainer();
        addPassword();
        addErrors();
        addButtons();
    }

    private void resetTemp() {
        ChannelsUser user = getUser();
        UserRecord tempUserInfo = new UserRecord( user.getUserRecord() );
        temp = new ChannelsUser( tempUserInfo );
        newPassword = "";
        repeatNewPassword = "";
    }

    private void addNewPasswordContainer() {
        newPasswordContainer = new WebMarkupContainer( "newPasswordContainer" );
        newPasswordContainer.setOutputMarkupId( true );
        addOrReplace( newPasswordContainer );
    }


    private void addPassword() {
        userPasswordContainer = new WebMarkupContainer( "userPassword" );
        addPasswordFields();
        userPasswordContainer.add( makeHelpIcon( "helpPassword", "about-me", "my-password", "images/help_guide_gray.png" ) );
        newPasswordContainer.add( userPasswordContainer );
    }

    private void addPasswordFields() {
        PasswordTextField passwordText = new PasswordTextField( "password", new PropertyModel<String>( this, "password" ) );
        passwordText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
            }
        } );
        userPasswordContainer.add( passwordText );
        newPasswordText = new TextField<String>( "newPassword", new PropertyModel<String>( this, "newPassword" ) );
        newPasswordText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
            }
        } );
        newPasswordText.setEnabled( false );
        userPasswordContainer.add( newPasswordText );
        repeatNewPasswordText = new TextField<String>( "repeatNewPassword", new PropertyModel<String>( this, "repeatNewPassword" ) );
        repeatNewPasswordText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
            }
        } );
        repeatNewPasswordText.setEnabled( false );
        userPasswordContainer.add( repeatNewPasswordText );

    }

    private void addErrors() {
        errorsContainer = new WebMarkupContainer( "errorsContainer" );
        errorsContainer.setOutputMarkupId( true );
        newPasswordContainer.addOrReplace( errorsContainer );
        ListView<String> errorsList = new ListView<String>(
                "errors",
                errors ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                item.add( new Label( "error", new Model<String>( item.getModelObject() ) ) );
            }
        };
        errorsContainer.add( errorsList );
        makeVisible( errorsContainer, !errors.isEmpty() );
    }

    private void addButtons() {
        AjaxLink<String> resetButton = new AjaxLink<String>( "reset" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetAll();
                target.add( NewPasswordPanel.this );
            }
        };
        userPasswordContainer.add( resetButton );
         AjaxLink<String> applyButton = new AjaxLink<String>( "apply" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                applyChanges( target );
            }
        };
        userPasswordContainer.add( applyButton );
    }


    private void applyChanges( AjaxRequestTarget target ) {
        try {
            if ( save() ) {
                Change change = new Change( Change.Type.Updated, getCollaborationModel() );
                change.setProperty( "user" );
                if ( !newPassword.isEmpty() ) {
                    change.setMessage( isValidNewPassword()
                            ? "Your password is changed."
                            : "Your password was NOT changed (new password not confirmed)." );
                } else {
                    change.setMessage( "Changes were applied." );
                }
                resetAll();
                target.add( NewPasswordPanel.this );
                update( target, change );
            } else {
                Change change = new Change( Change.Type.None );
                change.setMessage( "No changes were made" );
                update( target, change );
            }
        } catch ( IOException e ) {
            LOG.error( "Failed to save new password", e );
            target.appendJavaScript( "alert('Failed to save new password');" );
            target.add( NewPasswordPanel.this );
        }
    }

    private void adjustFields( AjaxRequestTarget target ) {
        resetErrors();
        addErrors();
        target.add( errorsContainer );
        newPasswordText.setEnabled( passwordOk );
        repeatNewPasswordText.setEnabled( passwordOk );
        target.add( newPasswordText );
        target.add( repeatNewPasswordText );
    }

    private void resetErrors() {
        errors = new ArrayList<String>();
         if ( !newPassword.isEmpty() && !repeatNewPassword.isEmpty() && !isValidNewPassword() ) {
            if ( !newPassword.equals( repeatNewPassword ) )
                errors.add( "The new password is not correctly repeated" );
            else
                errors.add( "The new password must have at least 6 characters" );
        }
    }

    private boolean isRobustPassword( String val ) {
        return val.length() > 5;
    }

    private boolean save() throws IOException {
        boolean changed =  !getUser().getUserRecord().sameAs( temp.getUserRecord() );
        if ( passwordOk && !newPassword.isEmpty() && isValidNewPassword() ) {
            temp.getUserRecord().setPassword( newPassword );
            changed = true;
        }
        if ( changed && canSave() ) {
            return userInfoService.updateUserRecord(
                    getUser().getUserRecord(),
                    temp.getUserRecord(),
                    getCommunityService() );
        } else {
            return false;
        }
    }

    private boolean canSave() {
        return newPassword.isEmpty() || isValidNewPassword();
    }


    private boolean isValidNewPassword() {
        return passwordOk &&
                isRobustPassword( newPassword )
                && repeatNewPassword.equals( newPassword );
    }

    public String getPassword() {
        return "";
    }

    public void setPassword( String val ) {
        passwordHash = val == null ? "" : UserRecord.digestPassword( val.trim() );
        passwordOk = isValidPassword();
        newPassword = "";
        repeatNewPassword = "";
    }

    private boolean isValidPassword() {
        return passwordHash.equals( getUser().getPassword() );
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword( String val ) {
        newPassword = val == null ? "" : val;
        repeatNewPassword = "";
    }

    public String getRepeatNewPassword() {
        return repeatNewPassword;
    }

    public void setRepeatNewPassword( String val ) {
        repeatNewPassword = val == null ? "" : val;
    }



}
