package com.mindalliance.channels.pages.components.settings;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.db.data.users.UserAccess;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
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

    private static final String NOT_AUTHORIZED = "Not authorized";
    private static final String GUEST = "Guest"; // == plan participant
    private UserRecord userRecord;
    private UserRecord userRecordUpdate;
    private WebMarkupContainer userIdentityContainer;
    private WebMarkupContainer privilegesContainer;
    private Plan selectedPlan;

    @SpringBean
    private UserRecordService userRecordService;

    @SpringBean
    private PlanManager planManager;
    private WebMarkupContainer planPrivilegesContainer;
    private WebMarkupContainer userRecordContainer;

    public UserRecordPanel( String id, UserRecord userRecord ) {
        super( id );
        this.userRecord = userRecord;
        userRecordUpdate = new UserRecord( getUsername(), userRecord );
        init();
    }

    private void init() {
        addUserRecord();
        addResetAndApply();
    }

    private void addUserRecord() {
        userRecordContainer = new WebMarkupContainer( "userRecordContainer"  );
        userRecordContainer.setOutputMarkupId( true );
        addOrReplace( userRecordContainer );
        addUserIdentityFields();
        addPrivileges();
    }

    private void addUserIdentityFields() {
        userIdentityContainer = new WebMarkupContainer( "userIdentityContainer" );
        userIdentityContainer.setOutputMarkupId( true );
        userRecordContainer.addOrReplace( userIdentityContainer );
        addFullName();
        addEmail();
        addPassword();
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
        addUsernameLabels();
        addGlobalAccess();
        addPlanPrivileges();
        userRecordContainer.addOrReplace( privilegesContainer );
    }

    private void addUsernameLabels() {
        privilegesContainer.addOrReplace( new Label( "username", userRecord.getUsername() ).setOutputMarkupId( true ) );
    }

    private void addGlobalAccess() {
        // admin
        AjaxCheckBox adminCheckBox = new AjaxCheckBox(
                "isAdmin",
                new PropertyModel<Boolean>( this, "admin" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addPrivileges();
                target.add( privilegesContainer );
            }
        };
        adminCheckBox.setOutputMarkupId( true );
        adminCheckBox.setEnabled( !userRecordUpdate.isDisabled() );
        privilegesContainer.addOrReplace( adminCheckBox );
        // disabled
        AjaxCheckBox disabledCheckBox = new AjaxCheckBox(
                "isDisabled",
                new PropertyModel<Boolean>( this, "disabled" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addPrivileges();
                target.add( privilegesContainer );
            }
        };
        disabledCheckBox.setOutputMarkupId( true );
        privilegesContainer.addOrReplace( disabledCheckBox );

    }

    private void addPlanPrivileges() {
        planPrivilegesContainer = new WebMarkupContainer( "planPrivilegesContainer" );
        planPrivilegesContainer.setOutputMarkupId( true );
        privilegesContainer.addOrReplace( planPrivilegesContainer );
        makeVisible( planPrivilegesContainer, !userRecordUpdate.isAdmin() && !userRecordUpdate.isDisabled() );
        addPlanPrivilegesList();
    }

    private void addPlanPrivilegesList() {
        ListView<PlanAccessWrapper> planPrivilegesListView = new ListView<PlanAccessWrapper>(
                "planPrivileges",
                getPlanAccessWrappers()
        ) {
            @Override
            protected void populateItem( ListItem<PlanAccessWrapper> item ) {
                PlanAccessWrapper planAccessWrapper = item.getModelObject();
                item.add( new Label( "planName", planAccessWrapper.getPlanName() ) );
                addUserRoleChoice( item );
            }
        };
        planPrivilegesContainer.add( planPrivilegesListView );
    }

    @SuppressWarnings( "unchecked" )
    private List<PlanAccessWrapper> getPlanAccessWrappers() {
        final List<String> planUris = planManager.getPlanUris();
        List<PlanAccessWrapper> wrappers = new ArrayList<PlanAccessWrapper>(  );
        for ( String planUri : planUris ) {
            Plan plan = planManager.getDevelopmentPlan( planUri );
            PlanAccessWrapper wrapper = new PlanAccessWrapper( plan );
            wrappers.add( wrapper );
        }
        return wrappers;
    }

    private void addUserRoleChoice( ListItem<PlanAccessWrapper> item ) {
        PlanAccessWrapper wrapper = item.getModelObject();
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
        item.add( userRoleDropDownChoice );
    }

    private List<String> getUserRoleChoices() {
        List<String> roleChoices = new ArrayList<String>();
        roleChoices.add( GUEST );
        roleChoices.add( UserAccess.UserRole.Planner.name() );
        roleChoices.add( NOT_AUTHORIZED );
        return roleChoices;
    }

    public String getUserRoleName() {
        if ( selectedPlan == null ) return null;
        UserAccess userAccess = userRecordUpdate.getUserAccessForContext( selectedPlan.getUri() );
        return userAccess == null
                ? NOT_AUTHORIZED
                : userAccess.getUserRole() == UserAccess.UserRole.Participant
                ? GUEST
                : userAccess.getUserRole().name();
    }

    public void setUserRoleName( String val ) {
        if ( val != null ) {
            UserAccess.UserRole userRole;
            if ( val.equals( NOT_AUTHORIZED ) )
                userRole = null;
            else if ( val.equals( GUEST ) )
                userRole = UserAccess.UserRole.Participant;
            else
                userRole = UserAccess.UserRole.valueOf( val );
            if ( selectedPlan != null ) {
                userRecordUpdate.setUserAccessForContext( selectedPlan.getUri(), userRole );
            } else if ( userRole == UserAccess.UserRole.Disabled || userRole == UserAccess.UserRole.Admin ) {
                userRecordUpdate.setUserRole( userRole );
            }
        }
    }


    private void addResetAndApply() {
        // reset
        AjaxLink<String> resetLink = new AjaxLink<String>( "reset" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetUserRecord();
                addUserRecord();
                target.add( userRecordContainer );
             }
        };
        add( resetLink );
        // apply
        AjaxLink<String> applyLink = new AjaxLink<String>( "apply" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                userRecordService.updateUserRecord( userRecord, userRecordUpdate );
                resetUserRecord();
                addUserRecord();
                target.add( userRecordContainer );
                Change change = new Change( Change.Type.NeedsRefresh );
                change.setMessage( "Settings changed for " + userRecord.getUsername() );
                update( target, change );
            }
        };
        add( applyLink );
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

    public Plan getSelectedPlan() {
        return selectedPlan;
    }

    public void setSelectedPlan( Plan selectedPlan ) {
        this.selectedPlan = selectedPlan;
    }

    public class PlanAccessWrapper implements Serializable {

        private Plan plan;

        public PlanAccessWrapper( Plan plan ) {
            this.plan= plan;
        }

        public String getPlanName() {
            return plan.getName();
        }

        public String getUserRoleName() {
            UserAccess userAccess = userRecordUpdate.getUserAccessForContext( plan.getUri() );
            return userAccess == null
                    ? NOT_AUTHORIZED
                    : userAccess.getUserRole().name();
        }

        public void setUserRoleName( String val ) {
            if ( val != null ) {
                 if ( val.equals( NOT_AUTHORIZED ) )
                    userRecordUpdate.clearAccess( plan.getUri() );
                else if ( val.equals( GUEST ) ) {
                    UserAccess userAccess = new UserAccess( plan.getUri(), UserAccess.UserRole.Participant );
                    userRecordUpdate.addUserAccess( userAccess );
                }
                else {
                    UserAccess userAccess = new UserAccess( plan.getUri(), UserAccess.UserRole.valueOf( val ) );
                     userRecordUpdate.addUserAccess( userAccess );
                 }
            }

        }
    }

}
