package com.mindalliance.channels.pages.components.settings;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.db.data.users.UserAccess;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User record editor
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/25/13
 * Time: 3:34 PM
 */
public class UserRecordPanel extends AbstractUpdatablePanel {

    private final static String YES = "Yes";
    private final static String NO = "No";
    private final static String[] YES_NO = {YES, NO};

    private static final String NOT_AUTHORIZED = "Not authorized";
    private static final String GUEST = "Guest";
    private UserRecord userRecord;
    private UserRecord userRecordUpdate;
    private WebMarkupContainer userIdentityContainer;
    private WebMarkupContainer privilegesContainer;
    private CollaborationModel selectedCollaborationModel;

    @SpringBean
    private UserRecordService userRecordService;

    @SpringBean
    private ModelManager modelManager;
    private WebMarkupContainer modelPrivilegesContainer;
    private WebMarkupContainer userRecordContainer;

    public UserRecordPanel( String id, UserRecord userRecord ) {
        super( id );
        this.userRecord = userRecord;
        userRecordUpdate = new UserRecord( getUsername(), userRecord );
        init();
    }

    private void init() {
        addUserRecord();
    }

    private void addUserRecord() {
        userRecordContainer = new WebMarkupContainer( "userRecordContainer" );
        userRecordContainer.setOutputMarkupId( true );
        addOrReplace( userRecordContainer );
        addUserIdentityFields();
        addUserIdentityResetAndApply();
        addPrivileges();
    }

    private void addUserIdentityFields() {
        userIdentityContainer = new WebMarkupContainer( "userIdentityContainer" );
        userIdentityContainer.setOutputMarkupId( true );
        userRecordContainer.addOrReplace( userIdentityContainer );
        addFullName();
        addEmail();
        addPassword();
        addGlobalAccess();
    }

    private void addFullName() {
        TextField<String> fullNameField = new TextField<String>(
                "fullName",
                new PropertyModel<String>( this, "fullName" ) );
        fullNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        fullNameField.setOutputMarkupId( true );
        userIdentityContainer.addOrReplace( fullNameField );
    }

    private void addEmail() {
        TextField<String> emailField = new TextField<String>(
                "email",
                new PropertyModel<String>( this, "email" ) );
        emailField.setOutputMarkupId( true );
        emailField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        userIdentityContainer.addOrReplace( emailField );
    }

    private void addPassword() {
        TextField<String> passwordField = new TextField<String>(
                "password",
                new PropertyModel<String>( this, "password" ) );
        passwordField.setOutputMarkupId( true );
        passwordField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        userIdentityContainer.addOrReplace( passwordField );
    }

    private void addPrivileges() {
        privilegesContainer = new WebMarkupContainer( "privilegesContainer" );
        privilegesContainer.setOutputMarkupId( true );
        addModelPrivileges();
        addModelPrivilegesResetAndApply();
        makeVisible( privilegesContainer, !isDisabled()
                // && !isAdmin()
                && ( isAdmin() || !userRecord.getUsername().equals( getUsername() ) ) );
        userRecordContainer.addOrReplace( privilegesContainer );
    }

    private void addModelPrivilegesResetAndApply() {
        // reset
        AjaxLink<String> resetLink = new AjaxLink<String>( "reset2" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetUserRecord();
                addUserRecord();
                target.add( userRecordContainer );
            }
        };
        resetLink.setOutputMarkupId( true );
        privilegesContainer.addOrReplace( resetLink );
        // apply
        AjaxLink<String> applyLink = new AjaxLink<String>( "apply2" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( !userRecord.sameAs( userRecordUpdate ) ) {
                    userRecordService.updateUserRecord( userRecord, userRecordUpdate, getCommunityService() );
                    resetUserRecord();
                    addUserRecord();
                    target.add( userRecordContainer );
                    Change change = new Change( Change.Type.NeedsRefresh );
                    change.setMessage( "Settings changed for " + userRecord.getUsername() );
                    update( target, change );
                } else {
                    Change change = Change.message( "Settings not changed" );
                    update( target, change );
                }
            }
        };
        applyLink.setOutputMarkupId( true );
        privilegesContainer.addOrReplace( applyLink );

    }


    private void addGlobalAccess() {
        // admin
        DropDownChoice<String> adminChoice = new DropDownChoice<String>(
                "isAdmin",
                new PropertyModel<String>( this, "adminYesNo" ),
                Arrays.asList( YES_NO )
        );
        adminChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addPrivileges();
                target.add( privilegesContainer );
            }
        } );
        adminChoice.setOutputMarkupId( true );
        userIdentityContainer.addOrReplace( adminChoice );
        adminChoice.setEnabled( !userRecordUpdate.isDisabled() && !userRecord.getUsername().equals( getUsername() )  );
        // disabled
        DropDownChoice<String> disabledChoice = new DropDownChoice<String>(
                "isDisabled",
                new PropertyModel<String>( this, "disabledYesNo" ),
                Arrays.asList( YES_NO )
        );
        disabledChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addPrivileges();
                target.add( privilegesContainer );
            }
        } );
        disabledChoice.setOutputMarkupId( true );
        userIdentityContainer.addOrReplace( disabledChoice );
        disabledChoice.setEnabled( !userRecord.getUsername().equals( getUsername() ) );

    }

    private void addModelPrivileges() {
        modelPrivilegesContainer = new WebMarkupContainer( "modelPrivilegesContainer" );
        modelPrivilegesContainer.setOutputMarkupId( true );
        privilegesContainer.addOrReplace( modelPrivilegesContainer );
        addModelPrivilegesList();
    }

    private void addModelPrivilegesList() {
        ListView<ModelAccessWrapper> modelPrivilegesListView = new ListView<ModelAccessWrapper>(
                "modelPrivileges",
                getModelAccessWrappers()
        ) {
            @Override
            protected void populateItem( ListItem<ModelAccessWrapper> item ) {
                ModelAccessWrapper modelAccessWrapper = item.getModelObject();
                item.add( new AttributeModifier( "class", item.getIndex() % 2 == 0 ? "even" : "odd" ) );
                item.add( new Label( "modelName", modelAccessWrapper.getModelName() ) );
                addUserRoleChoice( item );
            }
        };
        modelPrivilegesContainer.add( modelPrivilegesListView );
    }

    @SuppressWarnings("unchecked")
    private List<ModelAccessWrapper> getModelAccessWrappers() {
        final List<String> modelUris = modelManager.getModelUris();
        List<ModelAccessWrapper> wrappers = new ArrayList<ModelAccessWrapper>();
        for ( String modelUri : modelUris ) {
            CollaborationModel collaborationModel = modelManager.getDevelopmentModel( modelUri );
            ModelAccessWrapper wrapper = new ModelAccessWrapper( collaborationModel );
            wrappers.add( wrapper );
        }
        return wrappers;
    }

    private void addUserRoleChoice( ListItem<ModelAccessWrapper> item ) {
        ModelAccessWrapper wrapper = item.getModelObject();
        DropDownChoice<String> userRoleDropDownChoice = new DropDownChoice<String>(
                "userRole",
                new PropertyModel<String>( wrapper, "userRoleName" ),
                getUserRoleChoices()
        );
        userRoleDropDownChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // Do nothing
            }
        } );
        userRoleDropDownChoice.setEnabled( isAdmin() || !userRecord.getUsername().equals( getUsername() ) );
        item.add( userRoleDropDownChoice );
    }

    private List<String> getUserRoleChoices() {
        List<String> roleChoices = new ArrayList<String>();
        roleChoices.add( GUEST );
        roleChoices.add( UserAccess.UserRole.Planner.getLabel() );
        roleChoices.add( NOT_AUTHORIZED );
        return roleChoices;
    }

    public String getUserRoleName() {
        if ( selectedCollaborationModel == null ) return null;
        UserAccess userAccess = userRecordUpdate.getUserAccessForContext( selectedCollaborationModel.getUri() );
        return userAccess == null
                ? NOT_AUTHORIZED
                : userAccess.getUserRole() == UserAccess.UserRole.Participant
                ? GUEST
                : userAccess.getUserRole().getLabel();
    }

    public void setUserRoleName( String val ) {
        if ( val != null ) {
            UserAccess.UserRole userRole;
            if ( val.equals( NOT_AUTHORIZED ) )
                userRole = null;
            else if ( val.equals( GUEST ) )
                userRole = UserAccess.UserRole.Participant;
            else
                userRole = UserAccess.UserRole.fromLabel( val );
            if ( selectedCollaborationModel != null ) {
                userRecordUpdate.setUserAccessForContext( selectedCollaborationModel.getUri(), userRole );
            } else if ( userRole == UserAccess.UserRole.Disabled || userRole == UserAccess.UserRole.Admin ) {
                userRecordUpdate.setUserRole( userRole );
            }
        }
    }


    private void addUserIdentityResetAndApply() {
        // reset
        AjaxLink<String> resetLink = new AjaxLink<String>( "reset1" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetUserRecord();
                addUserRecord();
                target.add( userRecordContainer );
            }
        };
        resetLink.setOutputMarkupId( true );
        userRecordContainer.addOrReplace( resetLink );
        // apply
        AjaxLink<String> applyLink = new AjaxLink<String>( "apply1" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( userRecordUpdate.getPassword() != null
                        || !userRecord.sameAs( userRecordUpdate ) ) {
                    userRecordService.updateUserRecord( userRecord, userRecordUpdate, getCommunityService() );
                resetUserRecord();
                addUserRecord();
                target.add( userRecordContainer );
                Change change = new Change( Change.Type.NeedsRefresh );
                change.setMessage( "Settings changed for " + userRecord.getUsername() );
                update( target, change );
                } else {
                    Change change = Change.message( "Settings not changed" );
                    update( target, change );
                }
            }
        };
        applyLink.setOutputMarkupId( true );
        userRecordContainer.addOrReplace( applyLink );
    }

    private void resetUserRecord() {
        userRecordUpdate = new UserRecord( getUsername(), userRecord );
    }

    public String getFullName() {
        return userRecordUpdate.getFullName();
    }

    public void setFullName( String val ) {
        userRecordUpdate.setFullName( val == null ? "" : val );
    }

    public String getEmail() {
        return userRecordUpdate.getEmail();
    }

    public void setEmail( String val ) {
        userRecordUpdate.setEmail( val == null ? "" : val );
    }

    public String getPassword() {
        String encodedPassword = userRecordUpdate.getPassword();
        return encodedPassword == null ? "" : "******";
    }

    public void setPassword( String val ) {
        userRecordUpdate.setPassword( val );
    }

    public boolean isAdmin() {
        return userRecordUpdate.isAdmin();
    }

    public void setAdmin( boolean val ) {
        userRecordUpdate.makeAdmin( val );
    }

    public boolean isDisabled() {
        return userRecordUpdate.isDisabled();
    }

    public void setDisabled( boolean val ) {
        userRecordUpdate.makeDisabled( val );
    }

    public CollaborationModel getSelectedCollaborationModel() {
        return selectedCollaborationModel;
    }

    public void setSelectedCollaborationModel( CollaborationModel selectedCollaborationModel ) {
        this.selectedCollaborationModel = selectedCollaborationModel;
    }

    public String getAdminYesNo() {
        return isAdmin() ? YES : NO;
    }

    public void setAdminYesNo( String val ) {
        setAdmin( val.equals( YES ) );
    }

    public String getDisabledYesNo() {
        return isDisabled() ? YES : NO;
    }

    public void setDisabledYesNo( String val ) {
        setDisabled( val.equals( YES ) );
    }


    public class ModelAccessWrapper implements Serializable {

        private CollaborationModel collaborationModel;

        public ModelAccessWrapper( CollaborationModel collaborationModel ) {
            this.collaborationModel = collaborationModel;
        }

        public String getModelName() {
            return collaborationModel.getName();
        }

        public String getUserRoleName() {
            UserAccess userAccess = userRecordUpdate.getUserAccessForContext( collaborationModel.getUri() );
            return userAccess == null
                    ? NOT_AUTHORIZED
                    : userAccess.getUserRole().getLabel();
        }

        public void setUserRoleName( String val ) {
            if ( val != null ) {
                if ( val.equals( NOT_AUTHORIZED ) )
                    userRecordUpdate.clearAccess( collaborationModel.getUri() );
                else if ( val.equals( GUEST ) ) {
                    UserAccess userAccess = new UserAccess( collaborationModel.getUri(), UserAccess.UserRole.Participant );
                    userRecordUpdate.addUserAccess( userAccess );
                } else {
                    UserAccess userAccess = new UserAccess( collaborationModel.getUri(), UserAccess.UserRole.fromLabel( val ) );
                    userRecordUpdate.addUserAccess( userAccess );
                }
            }

        }
    }

}
