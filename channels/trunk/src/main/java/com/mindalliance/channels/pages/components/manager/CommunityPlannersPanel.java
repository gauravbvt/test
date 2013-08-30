package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Community planners panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/19/13
 * Time: 9:47 AM
 */
public class CommunityPlannersPanel extends AbstractUpdatablePanel {

    private static final int MAX_ROWS = 10;

    @SpringBean
    private UserRecordService userRecordService;

    @SpringBean
    private UserRecordService userInfoService;

    private CommunityPlannersTable communityPlannersTable;

    private ChannelsUser authorizedUser;
    private WebMarkupContainer authorizationContainer;
    private ConfirmedAjaxFallbackLink<String> authorizeButton;

    public CommunityPlannersPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addCommunityPlannersTable();
        addPlannerAuthorization();
    }

    private void addCommunityPlannersTable() {
        communityPlannersTable = new CommunityPlannersTable(
                "plannersTable",
                new PropertyModel<List<CommunityPlannerWrapper>>( this, "communityPlannerWrappers" )
        );
        addOrReplace( communityPlannersTable );
    }

    private void addPlannerAuthorization() {
        authorizationContainer = new WebMarkupContainer( "authorizingContainer" );
        authorizationContainer.setVisible( getUser().isPlannerOrAdmin( getPlanCommunityUri() ) );
        authorizationContainer.setOutputMarkupId( true );
        addOrReplace( authorizationContainer );
        addUserField();
        addAuthorizeButton();
    }

    private void addUserField() {
        final List<String> choices = getUserFullNamesAndEmails();
        AutoCompleteTextField<String> authorizedUserField = new AutoCompleteTextField<String>(
                "authorizedUser",
                new PropertyModel<String>( this, "authorizedUserFullNameAndEmail" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                if ( choices != null ) {
                    for ( String choice : choices ) {
                        if ( Matcher.matches( choice, input ) )
                            candidates.add( choice );
                    }
                    Collections.sort( candidates );
                }
                return candidates.iterator();
            }
        };
        authorizedUserField.setOutputMarkupId( true );
        authorizedUserField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addAuthorizeButton();
                target.add( authorizeButton );
            }
        } );
        authorizationContainer.addOrReplace( authorizedUserField );
    }

    private void addAuthorizeButton() {
        authorizeButton = new ConfirmedAjaxFallbackLink<String>(
                "authorize",
                authorizedUser != null
                        ? "Authorize " + authorizedUser.getFullName() + " as community planner?"
                        : "Please identify a user to be authorized as planner."
        ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( authorizedUser != null ) {
                    Change change;
                    UserRecord planner = userRecordService.authorizeCommunityPlanner(
                            getUsername(),
                            authorizedUser,
                            getCommunityService() );
                    if ( planner != null ) {
                        addCommunityPlannersTable();
                        addPlannerAuthorization();
                        target.add( communityPlannersTable );
                        target.add( authorizationContainer );
                        change = new Change( Change.Type.Updated, getPlanCommunity(), "participation" );
                    } else {
                        change = new Change( Change.Type.None );
                        change.setMessage( "Failed to authorize " + authorizedUser.getFullName() + " as community planner" );
                    }
                    authorizedUser = null;
                    update( target, change );
                }
            }
        };
        authorizeButton.setOutputMarkupId( true );
        authorizationContainer.addOrReplace( authorizeButton );
    }

    private List<String> getUserFullNamesAndEmails() {
        CommunityService communityService = getCommunityService();
        List<String> fullNamesAndEmails = new ArrayList<String>();
        for ( ChannelsUser user : communityService.getUserRecordService().getAllEnabledUsers() ) {
            if ( !communityService.isCommunityPlanner( user ) )
                fullNamesAndEmails.add( user.getFullName() + " (" + user.getEmail() + ")" );
        }
        Collections.sort( fullNamesAndEmails );
        return fullNamesAndEmails;
    }

    public String getAuthorizedUserFullNameAndEmail() {
        return authorizedUser == null
                ? null
                : authorizedUser.getFullName() + " (" + authorizedUser.getEmail() + ")";
    }

    public void setAuthorizedUserFullNameAndEmail( String val ) {
        if ( val != null && !val.isEmpty() ) {
            String identifier = null;
            String email = null;
            int openParIndex = val.indexOf( "(" );
            identifier = ( openParIndex >= 0
                    ? val.substring( 0, openParIndex )
                    : val ).trim();
            if ( openParIndex >= 0 && openParIndex < val.length() - 2 ) {
                int closedParIndex = val.indexOf( ")", openParIndex );
                email = ( closedParIndex > -1
                        ? val.substring( openParIndex + 1, closedParIndex )
                        : val.substring( openParIndex + 1 ) ).trim();
            }
            UserRecordService userDao = getCommunityService().getUserRecordService();
            if ( email != null )
                authorizedUser = userDao.getUserWithIdentity( email );
            if ( authorizedUser == null ) {
                authorizedUser = userDao.getUserWithIdentity( identifier );
            }
            if ( authorizedUser == null ) {
                List<ChannelsUser> users = userDao.findAllUsersWithFullName( identifier, getCommunityService().getPlanCommunity().getUri() );
                if ( users.size() == 1 )
                    authorizedUser = users.get( 0 );
            }
        } else {
            authorizedUser = null;
        }
    }


    public List<CommunityPlannerWrapper> getCommunityPlannerWrappers() {
        List<CommunityPlannerWrapper> wrappers = new ArrayList<CommunityPlannerWrapper>();
        for ( ChannelsUser communityPlanner : userRecordService.getCommunityPlanners( getCommunityService().getPlanCommunity().getUri() ) ) {
            wrappers.add( new CommunityPlannerWrapper( communityPlanner.getUserRecord() ) );
        }
        return wrappers;
    }

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof CommunityPlannerWrapper ) {
            CommunityPlannerWrapper wrapper = (CommunityPlannerWrapper) object;
            if ( action.equals( "resign" ) ) {
                boolean success = wrapper.resign();
                addCommunityPlannersTable();
                target.add( communityPlannersTable );
                Change change = new Change( Change.Type.Updated, getPlanCommunity(), "participation" );
                change.setMessage( success
                        ? "Resignation refused"
                        : "Resignation accepted"
                );
                update( target, change );
            }
        }
    }

    public class CommunityPlannerWrapper implements Serializable {

        private UserRecord communityPlanner;

        public CommunityPlannerWrapper( UserRecord communityPlanner ) {
            this.communityPlanner = communityPlanner;
        }

        public String getNormalizedFullName() {
            ChannelsUser planner = getPlannerUser();
            return planner != null ? planner.getNormalizedFullName() : null;
        }

        public String getEmail() {
            return communityPlanner.getEmail();
        }

        public String getAuthorizedBy() {
            ChannelsUser authorizedBy = userInfoService.getUserWithIdentity( communityPlanner.getUsername() );
            return authorizedBy != null
                    ? authorizedBy.getUserRecord().equals( communityPlanner )
                    ? "(Founder)"
                    : authorizedBy.getNormalizedFullName()
                    : null;
        }

        public String getAuthorizationDate() {
            return getDateFormat().format( communityPlanner.getCreated() );
        }

        public boolean resign() {
            ChannelsUser planner = getPlannerUser();
            return userRecordService.resignAsCommunityPlanner( getUsername(), planner, getCommunityService() );
        }

        public UserRecord getCommunityPlannerIfCanResign() {
            ChannelsUser planner = getPlannerUser();
            if ( ( getUser().isAdmin() || ( planner != null && planner.equals( getUser() ) ) )
                    && userRecordService.getCommunityPlanners( getCommunityService().getPlanCommunity().getUri() ).size() > 1 )
                return communityPlanner;
            else
                return null;
        }

        private ChannelsUser getPlannerUser() {
            return userInfoService.getUserWithIdentity( communityPlanner.getUsername() );
        }
    }

    private class CommunityPlannersTable extends AbstractTablePanel<CommunityPlannerWrapper> {

        private IModel<List<CommunityPlannerWrapper>> wrappers;

        private CommunityPlannersTable( String s, IModel<List<CommunityPlannerWrapper>> wrappers ) {
            super( s );
            this.wrappers = wrappers;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            final List<IColumn<CommunityPlannerWrapper>> columns = new ArrayList<IColumn<CommunityPlannerWrapper>>();
            columns.add( makeColumn( "Name", "normalizedFullName", EMPTY ) );
            columns.add( makeColumn( "Email", "email", EMPTY ) );
            columns.add( makeColumn( "Authorized by", "authorizedBy", EMPTY ) );
            columns.add( makeColumn( "As of", "authorizationDate", EMPTY ) );
            columns.add( makeActionLinkColumn(
                    "",
                    "resign",
                    "resign",
                    "Resign as planner?",
                    "communityPlannerIfCanResign",
                    "more",
                    CommunityPlannersPanel.this
            ) );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable<CommunityPlannerWrapper>(
                    "communityPlannersTable",
                    columns,
                    new SortableBeanProvider<CommunityPlannerWrapper>(
                            wrappers.getObject(),
                            "normalizedFullName" ),
                    MAX_ROWS ) );

        }

    }
}
