package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.util.NameRange;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.communities.UserParticipationConfirmationService;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Users participation panel in participation manager.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/12
 * Time: 11:14 AM
 */
public class UsersParticipationPanel extends AbstractUpdatablePanel implements NameRangeable {

    private static final String ALL_USERS = "All users";
    private static final String SUPERVISED = "All users I supervise";
    private static final String SUPERVISORS = "All users that supervise me";
    private static final String COLLEAGUES = "All users who are my co-employees";
    private static final String SELF = "Only myself";

    private static final String[] USER_CHOICES = {ALL_USERS, SUPERVISED, SUPERVISORS, COLLEAGUES, SELF};

    /**
     * Name index panel.
     */
    private NameRangePanel nameRangePanel;
    /**
     * Selected name range.
     */
    private NameRange nameRange = new NameRange();

    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_ROWS = 12;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy/MM/dd H:mm z" );

    private String userRelationship = ALL_USERS;
    private boolean showingUnassignedAgents = false;
    private UsersParticipationTable usersParticipationTable;
    private List<UserParticipationWrapper> userParticipationWrappers;

    public UsersParticipationPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        resetUserParticipationWrappers();
        addUsersDropDown();
        addShowUnassignedAgents();
        addNameRangePanel();
        addUsersParticipationTable();
    }

    @Override
    public String getSectionId() {
        return "participation-page";
    }

    @Override
    public String getTopicId() {
        return "viewing-user-participation";
    }


    private void addUsersDropDown() {
        DropDownChoice<String> usersRelationshipChoice = new DropDownChoice<String>(
                "users",
                new PropertyModel<String>( this, "userRelationship" ),
                Arrays.asList( USER_CHOICES )
        );
        usersRelationshipChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateFields( target );
            }
        } );
        add( usersRelationshipChoice );
    }

    private void updateFields( AjaxRequestTarget target ) {
        resetUserParticipationWrappers();
        nameRange = new NameRange();
        addNameRangePanel();
        addUsersParticipationTable();
        target.add( usersParticipationTable );
    }

    private void addShowUnassignedAgents() {
        AjaxCheckBox showUnassignedCheckBox = new AjaxCheckBox(
                "unassigned",
                new PropertyModel<Boolean>( this, "showingUnassignedAgents" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateFields( target );
            }
        };
        add( showUnassignedCheckBox );
    }


    private void addNameRangePanel() {
        nameRangePanel = new NameRangePanel(
                "nameRanges",
                new PropertyModel<List<String>>( this, "indexedNames" ),
                MAX_ROWS,
                this,
                "All names"
        );
        nameRangePanel.setOutputMarkupId( true );
        addOrReplace( nameRangePanel );
    }


    private void addUsersParticipationTable() {
        usersParticipationTable = new UsersParticipationTable(
                "participationTable",
                new PropertyModel<List<UserParticipationWrapper>>( this, "userParticipationWrappers" )
        );
        addOrReplace( usersParticipationTable );
    }


    /**
     * Change the selected name range.
     *
     * @param target an ajax request target
     * @param range  a name range
     */
    public void setNameRange( AjaxRequestTarget target, NameRange range ) {
        nameRange = range;
        nameRangePanel.setSelected( target, range );
        resetUserParticipationWrappers();
        addUsersParticipationTable();
        target.add( usersParticipationTable );
    }

    public String getUserRelationship() {
        return userRelationship;
    }

    public void setUserRelationship( String userRelationship ) {
        this.userRelationship = userRelationship;
    }

    public List<String> getIndexedNames() {
        UserRecordService userDao = getCommunityService().getUserRecordService();
        List<String> normalizedUserNames = new ArrayList<String>();
        for ( ChannelsUser user : userDao.getUsers( getCommunityService().getPlanCommunity().getUri() ) ) {
            normalizedUserNames.add( user.getNormalizedFullName() );
        }
        return normalizedUserNames;
    }

    public List<UserParticipationWrapper> getUserParticipationWrappers() {
        if ( userParticipationWrappers == null ) {
            resetUserParticipationWrappers();
        }
        return userParticipationWrappers;
    }

    public void resetUserParticipationWrappers() {
        CommunityService communityService = getCommunityService();
        UserRecordService userDao = getCommunityService().getUserRecordService();
        UserParticipationService userParticipationService = communityService.getUserParticipationService();
        userParticipationWrappers = new ArrayList<UserParticipationWrapper>();
        for ( ChannelsUser user : userDao.getUsers( communityService.getPlanCommunity().getUri() ) ) {
            if ( nameRange.contains( user.getNormalizedFullName() )
                    && hasUserRelationship( user, userRelationship ) ) {
                List<UserParticipation> userParticipationList = communityService.getParticipationManager().getUserParticipations(
                        user,
                        communityService
                );
                for ( UserParticipation participation : userParticipationList ) {
                    userParticipationWrappers.add( new UserParticipationWrapper( user, participation ) );
                }
                if ( userParticipationList.isEmpty() ) {
                    userParticipationWrappers.add( new UserParticipationWrapper( user ) );
                }
            }
        }
        if ( showingUnassignedAgents ) {
            for ( Agent unassignedAgent
                    : communityService.getParticipationManager().findAllUnassignedAgents( communityService ) ) {
                userParticipationWrappers.add( new UserParticipationWrapper( unassignedAgent ) );
            }
        }
    }

    private boolean hasUserRelationship( ChannelsUser user, String userRelationship ) {
        boolean isSelf = user.getUsername().equals( getUsername() );
        if ( userRelationship.equals( ALL_USERS ) ) return true;
        else if ( userRelationship.equals( SELF ) ) return isSelf;
        else if ( isSelf ) return false;
        else {
            CommunityService communityService = getCommunityService();
            ParticipationManager participationManager = communityService.getParticipationManager();
            /*if ( userRelationship.equals( COLLABORATORS ) )
                return participationManager.areCollaborators( communityService, user, getUser() ); // todo - implement
            else */
            if ( userRelationship.equals( SUPERVISED ) )
                return participationManager.isSupervisedBy( communityService, user, getUser() );
            else if ( userRelationship.equals( SUPERVISORS ) )
                return participationManager.isSupervisorOf( communityService, user, getUser() );
            else if ( userRelationship.equals( COLLEAGUES ) )
                return participationManager.areColleagues( communityService, user, getUser() );
            else
                return false;
        }
    }

    public boolean isShowingUnassignedAgents() {
        return showingUnassignedAgents;
    }

    public void setShowingUnassignedAgents( boolean showingUnassignedAgents ) {
        this.showingUnassignedAgents = showingUnassignedAgents;
    }

    /**
     * User participation wrapper.
     */
    public class UserParticipationWrapper implements Serializable {

        private ChannelsUser user;
        private UserParticipation userParticipation;
        private Agent agent;

        public UserParticipationWrapper( ChannelsUser user ) {
            this.user = user;
        }

        public UserParticipationWrapper( ChannelsUser user, UserParticipation userParticipation ) {
            this.user = user;
            this.userParticipation = userParticipation;
        }

        public UserParticipationWrapper( Agent agent ) {
            this.agent = agent;
        }


        public UserParticipation getUserParticipation() {
            return userParticipation;
        }

        public void setUserParticipation( UserParticipation userParticipation ) {
            this.userParticipation = userParticipation;
        }

        public ChannelsUser getUser() {
            return user;
        }

        public String getNormalizedFullName() {
            return user == null ? null : user.getSimpleNormalizedFullName();
        }

        public String getEmail() {
            return user == null ? null : user.getEmail();
        }

        public String getAgentName() {
            Agent agent = getAgent();
            return agent == null ? null : agent.getName();
        }

        public Agent getAgent() {
            return agent != null
                    ? agent :
                    userParticipation != null
                            ? userParticipation.getAgent( getCommunityService() )
                            : null;
        }

        public String getAgentDescription() {
            Agent agent = getAgent();
            return agent != null
                    ? agent.getRequirementsDescription( getCommunityService() )
                    : null;
        }

        public String getRelationshipsToUser() {
            List<String> rels = new ArrayList<String>();
            if ( userParticipation != null ) {
                CommunityService communityService = getCommunityService();
                ChannelsUser user = UsersParticipationPanel.this.getUser();
                UserRecord participantInfo = userParticipation.getParticipant( communityService );
                if ( participantInfo == null ) {
                    return null;
                } else {
                    ChannelsUser otherUser = new ChannelsUser( participantInfo, communityService );
                    if ( user.equals( otherUser ) ) {
                        rels.add( "self" );
                    } else {
                        ParticipationManager participationManager = communityService.getParticipationManager();
                        if ( participationManager.areCollaborators( communityService, user, otherUser ) ) {
                            rels.add( "collaborator" );
                        }
                        if ( participationManager.isSupervisorOf( communityService, otherUser, user ) ) {
                            rels.add( "supervisor" );
                        }
                        if ( participationManager.isSupervisedBy( communityService, otherUser, user ) ) {
                            rels.add( "direct report" );
                        }
                        if ( participationManager.areColleagues( communityService, otherUser, user ) ) {
                            rels.add( "colleague" );
                        }
                    }
                }
            }
            return rels.isEmpty() ? null : ChannelsUtils.listToString( rels, " and " );
        }

        public String getAccepted() {
            if ( userParticipation == null )
                return null;
            else
                return userParticipation.isAccepted()
                        ? "Yes"
                        : "No";
        }

        public String getWhenAccepted() {
            if ( userParticipation == null )
                return null;
            else {
                Date date = userParticipation.getWhenAccepted();
                return date == null
                        ? null
                        : DATE_FORMAT.format( date );
            }
        }

        public String getConfirmed() {
            if ( userParticipation == null
                    || !userParticipation.getAgent( getCommunityService() ).isSupervisedParticipation() )
                return null;
            else {
                UserParticipationConfirmationService userParticipationConfirmationService
                        = getCommunityService().getUserParticipationConfirmationService();
                return userParticipationConfirmationService.isConfirmedByAllSupervisors(
                        userParticipation,
                        getCommunityService() )
                        ? "Yes"
                        : "Not yet";
            }
        }

        public boolean isUserInCharge() {
            if ( userParticipation != null ) {
                ChannelsUser user = getUser();
                CommunityService communityService = getCommunityService();
                return user.isPlannerOrAdmin( getPlan().getUri() ) ||
                        communityService.getParticipationManager()
                                .hasAuthorityOverParticipation( communityService, user, userParticipation );
            } else {
                return false;
            }
        }

        public String toString() {
            if ( userParticipation != null ) {
                return userParticipation.asString( getCommunityService() );
            } else if ( user != null ) {
                return user.getFullName() + " is not assigned";
            } else {
                assert agent != null;
                return "No one is assigned as " + agent.getName();
            }
        }
    }


    /**
     * User participation table.
     */
    private class UsersParticipationTable extends AbstractFilterableTablePanel {

        private IModel<List<UserParticipationWrapper>> userParticipationModel;

        public UsersParticipationTable( String id, IModel<List<UserParticipationWrapper>> userParticipationModel ) {
            super( id, MAX_ROWS );
            this.userParticipationModel = userParticipationModel;
            initTable();
        }

        @SuppressWarnings("unchecked")
        private void initTable() {
            final List<IColumn<UserParticipationWrapper>> columns = new ArrayList<IColumn<UserParticipationWrapper>>();
            columns.add( makeColumn( "User", "normalizedFullName", EMPTY ) );
            columns.add( makeColumn( "Email", "email", EMPTY ) );
            columns.add( makeColumn( "Is my", "relationshipsToUser", EMPTY ) );
            columns.add( makeFilterableColumn(
                    "Participates as",
                    "agent",
                    "agentName",
                    EMPTY,
                    "agentDescription",
                    UsersParticipationTable.this ) );
            columns.add( makeColumn( "Accepted?", "accepted", EMPTY ) );
            columns.add( makeColumn( "As of", "whenAccepted", EMPTY ) );
            columns.add( makeColumn( "OKed by supervisors?", "confirmed", EMPTY ) );
/*
            columns.add( makeActionLinkColumn( "",
                    "unassign",
                    "unassign",
                    "Delete participation?",
                    "userIfInCharge",
                    "more",
                    UsersParticipationPanel.this ) );
*/
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable<UserParticipationWrapper>(
                    "userParticipationTable",
                    columns,
                    new SortableBeanProvider<UserParticipationWrapper>(
                            getFilteredParticipations(),
                            "normalizedFullName" ),
                    getPageSize() ) );
        }

        @SuppressWarnings("unchecked")
        private List<UserParticipationWrapper> getFilteredParticipations() {
            return (List<UserParticipationWrapper>) CollectionUtils.select(
                    userParticipationModel.getObject(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( obj );
                        }
                    }
            );
        }

        @Override
        protected void resetTable( AjaxRequestTarget target ) {
            initTable();
            target.add( this );
        }

    }

}
