package com.mindalliance.channels.pages.components.settings;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Users setting panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/22/13
 * Time: 12:07 PM
 */
public class UsersSettingsPanel extends AbstractUpdatablePanel {

    private String newUsername;
    private String searchString;
    private String selectedUsername;
    private boolean showDisabledUsers = false;

    @SpringBean
    private UserRecordService userRecordService;

    private WebMarkupContainer selectUserContainer;
    private Component userRecordPanel;
    private WebMarkupContainer usernamesContainer;
    private TextField<String> newUserField;

    public UsersSettingsPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        resetAll();
        addNewUser();
        addSelectUser();
        addUserRecordPanel();
    }

    private void resetAll() {
        newUsername = null;
        searchString = null;
        List<UserRecord> userRecords = getFoundUserRecords();
        selectedUsername = userRecords.isEmpty() ? null : userRecords.get( 0 ).getUsername();
    }

    private void addNewUser() {
        // username field
        newUserField = new TextField<String>(
                "newUsername",
                new PropertyModel<String>( this, "newUsername" )
        );
        newUserField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        newUserField.setOutputMarkupId( true );
        addTipTitle( newUserField, "Enter a new username (alphanumerics, no spaces)" );
        addOrReplace( newUserField );
        // add user button
        AjaxLink<String> addUserLink = new AjaxLink<String>( "addUser" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( newUsername != null && !newUsername.isEmpty() ) {
                    if ( userRecordService.getUserWithIdentity( newUsername ) != null ) {
                        Change change = Change.failed( newUsername + " is already taken." );
                        update( target, change );
                    } else {
                        userRecordService.createUser( getUsername(), newUsername, getCommunityService() );
                        Change change = Change.message( "User " + newUsername + " was added." );
                        selectedUsername = newUsername;
                        newUsername = null;
                        addSelectUser();
                        addNewUser();
                        addUserRecordPanel();
                        target.add( newUserField );
                        target.add( selectUserContainer );
                        target.add( userRecordPanel );
                        update( target, change );
                    }
                } else {
                    Change change = Change.failed( "Please provide a unique user name" );
                    update( target, change );
                }

            }
        };
        addUserLink.setOutputMarkupId( true );
        addOrReplace( addUserLink );
    }

    private void addSelectUser() {
        selectUserContainer = new WebMarkupContainer( "selectUserContainer" );
        selectUserContainer.setOutputMarkupId( true );
        addOrReplace( selectUserContainer );
        addUserSearch();
        addUsersList();
    }

    private void addUserSearch() {
        // search string
        TextField<String> searchField = new TextField<String>(
                "search",
                new PropertyModel<String>( this, "searchString" )
        );
        searchField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addUsersList();
                target.add( usernamesContainer );
            }
        } );
        searchField.setOutputMarkupId( true );
        addTipTitle( searchField, "Filter users" );
        selectUserContainer.addOrReplace( searchField );
        // hide-show disabled users
        WebMarkupContainer hideShowDisabled = new WebMarkupContainer( "hideShowDisabled" );
        hideShowDisabled.setOutputMarkupId( true );
        addTipTitle( hideShowDisabled, showDisabledUsers ? "Hide disabled users" : "Show disabled users" );
        hideShowDisabled.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                toggleShowDisabledUsers();
                addUserSearch();
                addUsersList();
                target.add( selectUserContainer );
                target.add( usernamesContainer );
            }
        } );
        selectUserContainer.addOrReplace( hideShowDisabled );
    }

    private void toggleShowDisabledUsers() {
        showDisabledUsers = !showDisabledUsers;
    }

    private void addUsersList() {
        usernamesContainer = new WebMarkupContainer( "usernamesContainer" );
        usernamesContainer.setOutputMarkupId( true );
        selectUserContainer.addOrReplace( usernamesContainer );
        ListView<UserRecord> usersListView = new ListView<UserRecord>(
                "usernames",
                getFoundUserRecords()
        ) {
            @Override
            protected void populateItem( ListItem<UserRecord> item ) {
                final UserRecord userRecord = item.getModelObject();
                boolean selected = selectedUsername != null && selectedUsername.equals( userRecord.getUsername() );
                if ( selected ) item.add( new AttributeModifier( "class", "selected" ) );
                AjaxLink<String> userLink = new AjaxLink<String>( "userLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        selectedUsername = userRecord.getUsername();
                        addUsersList();
                        target.add( usernamesContainer );
                        addUserRecordPanel();
                        target.add( userRecordPanel );
                    }
                };
                if ( selected ) userLink.add( new AttributeModifier( "class", "selected" ) );
                item.add( userLink );
                item.add( new AttributeModifier( "class", item.getIndex() % 2 == 0 ? "even" : "odd" ) );
                Label nameLabel = new Label( "name", userRecord.getFullName() );
                nameLabel.add( new AttributeModifier( "class", userRecord.isDisabled() ? "disabled-name" : "name" ) );
                userLink.add( nameLabel );
                userLink.add( new Label( "username", userRecord.getUsername() ) );
            }
        };
        usernamesContainer.add( usersListView );
    }

    @SuppressWarnings( "unchecked" )
    private List<UserRecord> getFoundUserRecords() {
        return (List<UserRecord>) CollectionUtils.select(
                userRecordService.getAllUserRecords(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserRecord userRecord = (UserRecord) object;
                        return ( showDisabledUsers || !userRecord.isDisabled() )
                                && ( getSearchString().isEmpty()
                                || Matcher.contains( userRecord.getFullName(), getSearchString() )
                                || Matcher.contains( userRecord.getUsername(), getSearchString() )
                                || Matcher.contains( userRecord.getEmail(), getSearchString() ) );
                    }
                }
        );
    }

    private void addUserRecordPanel() {
        UserRecord userRecord = getSelectedUserRecord();
        if ( userRecord == null ) {
            userRecordPanel = new Label( "userRecord", "" );
        } else {
            userRecordPanel = new UserRecordPanel( "userRecord", userRecord );
        }
        userRecordPanel.setOutputMarkupId( true );
        makeVisible( userRecordPanel, selectedUsername != null );
        addOrReplace( userRecordPanel );
    }

    private UserRecord getSelectedUserRecord() {
        if ( selectedUsername == null ) {
            return null;
        } else {
            return userRecordService.getUserRecord( selectedUsername );
        }
    }

    public String getNewUsername() {
        return "";
    }

    public void setNewUsername( String newUsername ) {
        this.newUsername = newUsername;
    }

    public String getSearchString() {
        return searchString == null ? "" : searchString;
    }

    public void setSearchString( String searchString ) {
        this.searchString = searchString;
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isRefreshNeeded() ) {
            addUsersList();
            target.add( usernamesContainer );
        }
        super.updateWith( target, change, updated );
    }
}
