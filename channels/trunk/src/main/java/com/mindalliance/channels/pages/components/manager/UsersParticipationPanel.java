package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
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
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * // todo - REMOVE - OBSOLETE
 * Users participation panel in participation manager.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/12
 * Time: 11:14 AM
 */
public class UsersParticipationPanel extends AbstractUpdatablePanel implements NameRangeable {

    private static final String ALL_USERS = "All users";
    private static final String COLLABORATORS = "Users I collaborate with";
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

    private String userRelationship = ALL_USERS;
    private boolean showingUnassignedAgents = false;
    private UsersParticipationTable usersParticipationTable;
    private List<UserParticipationWrapper> userParticipationWrappers;
    private WebMarkupContainer assignmentContainer;
    private ChannelsUser assignmentUser;
    private Agency assignmentAgency;
    private Agent assignmentAgent;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy/MM/dd H:mm z" );
    private UserParticipation addedParticipation;
    private WebMarkupContainer agentAssignmentContainer;
    private AjaxLink<String> participateButton;

    public UsersParticipationPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model );
        init();
    }

    private void init() {
        resetUserParticipationWrappers();
        addUsersDropDown();
        addShowUnassignedAgents();
        addNameRangePanel();
        addUsersParticipationTable();
        addAssigning();
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
                List<UserParticipation> userParticipationList = userParticipationService.getUserParticipations(
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
            if ( userRelationship.equals( COLLABORATORS ) )
                return participationManager.areCollaborators( communityService, user, getUser() ); // todo - implement
            else if ( userRelationship.equals( SUPERVISED ) )
                return participationManager.isSupervisedBy( communityService, user, getUser() );
            else if ( userRelationship.equals( SUPERVISORS ) )
                return participationManager.isSupervisorOf( communityService, user, getUser() );
            else if ( userRelationship.equals( COLLEAGUES ) )
                return participationManager.areColleagues( communityService, user, getUser() );
            else throw new RuntimeException( "Unknown relationship" );
        }
    }

/*
    private List<String> getParticipationTodos() {
        final PlanCommunity planCommunity = getCommunityService();
        UserParticipationService userParticipationService = planCommunity.getUserParticipationService();
        final UserParticipationConfirmationService userParticipationConfirmationService
                = planCommunity.getUserParticipationConfirmationService();
        List<String> notes = new ArrayList<String>();
        int unacceptedCount = CollectionUtils.select(
                userParticipationService.getUserParticipations( getUser(), planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (UserParticipation) object ).isAccepted();
                    }
                }
        ).size();
        if ( unacceptedCount > 0 ) {
            notes.add( "You are requested to accept "
                    + unacceptedCount
                    + ( unacceptedCount > 1 ? " assignments." : " assignment." ) );
        }
        int confirmationCount = CollectionUtils.select(
                userParticipationService.getParticipationsSupervisedByUser( getUser(), planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation userParticipation = (UserParticipation) object;
                        return userParticipationConfirmationService.isConfirmationByUserRequired(
                                userParticipation,
                                getUser(),
                                planCommunity );
                    }
                } ).size();
        if ( confirmationCount > 0 ) {
            notes.add( confirmationCount
                    + ( confirmationCount > 1 ? " assignments" : " assignment" )
                    + " await your confirmation as supervisor." );
        }
        return notes;
    }
*/

    private void addAssigning() {
        assignmentContainer = new WebMarkupContainer( "assignmentContainer" );
        assignmentContainer.setOutputMarkupId( true );
        addOrReplace( assignmentContainer );
        addAssignmentUser();
        addAssignmentAgency();
        addAssignmentAgent();
        addParticipateButton();
    }

    private void addAssignmentUser() {
        final List<String> choices = getUserFullNamesAndEmails();
        AutoCompleteTextField<String> assignmentUserField = new AutoCompleteTextField<String>(
                "assignedUser",
                new PropertyModel<String>( this, "assignmentUserFullNameAndEmail" ),
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
        assignmentUserField.setOutputMarkupId( true );
        assignmentUserField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addAssignmentUser();
                addAssignmentAgency();
                addAssignmentAgent();
                participateButton.setEnabled( canAssign() );
                target.add( assignmentContainer );
            }
        } );
        assignmentContainer.addOrReplace( assignmentUserField );
    }

    private boolean canAssign() {
        return assignmentUser != null && assignmentAgency != null && assignmentAgent != null;
    }

    private List<String> getUserFullNamesAndEmails() {
        CommunityService communityService = getCommunityService();
        List<String> fullNamesAndEmails = new ArrayList<String>();
        for ( ChannelsUser user : communityService.getUserRecordService().getAllEnabledUsers() ) {
            fullNamesAndEmails.add( user.getFullName() + " (" + user.getEmail() + ")" );
        }
        Collections.sort( fullNamesAndEmails );
        return fullNamesAndEmails;
    }

    private void addAssignmentAgency() {
        WebMarkupContainer container = new WebMarkupContainer( "agenciesContainer" );
        container.setOutputMarkupId( true );
        List<Agency> agenciesWithAvailableParticipation = agenciesWithAvailableParticipationFor( assignmentUser );
        DropDownChoice<Agency> agencyChoice = new DropDownChoice<Agency>(
                "agencies",
                new PropertyModel<Agency>( this, "assignmentAgency" ),
                agenciesWithAvailableParticipation,
                new ChoiceRenderer<Agency>() {
                    @Override
                    public Object getDisplayValue( Agency agency ) {
                        return agency.getName();
                    }

                    @Override
                    public String getIdValue( Agency object, int index ) {
                        return Integer.toString( index );
                    }
                } );
        agencyChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addAssignmentAgent();
                target.add( agentAssignmentContainer );
                participateButton.setEnabled( canAssign() );
                target.add( assignmentContainer );
            }
        } );
        container.add( agencyChoice );
        container.setEnabled( !agenciesWithAvailableParticipation.isEmpty() );
        assignmentContainer.addOrReplace( container );
    }

    @SuppressWarnings("unchecked")
    private List<Agency> agenciesWithAvailableParticipationFor( final ChannelsUser assignmentUser ) {
        if ( assignmentUser == null )
            return new ArrayList<Agency>();
        else {
            List<Agency> sortedAgencies = (List<Agency>) CollectionUtils.select(
                    getCommunityService().getParticipationManager().getAllKnownAgencies( getCommunityService() ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Agency agency = (Agency) object;
                            return CollectionUtils.exists(
                                    agency.getAgents( getCommunityService() ),
                                    new Predicate() {
                                        @Override
                                        public boolean evaluate( Object object ) {
                                            final Agent agent = (Agent) object;
                                            return getCommunityService().getParticipationManager().isParticipationAvailable(
                                                    agent,
                                                    assignmentUser,
                                                    getCommunityService() );
                                        }
                                    }
                            );
                        }
                    }
            );
            Collections.sort( sortedAgencies,
                    new Comparator<Agency>() {
                        @Override
                        public int compare( Agency a1, Agency a2 ) {
                            return a1.getName().compareToIgnoreCase( a2.getName() );
                        }
                    } );
            return sortedAgencies;
        }
    }

    private void addAssignmentAgent() {
        agentAssignmentContainer = new WebMarkupContainer( "agentsContainer" );
        agentAssignmentContainer.setOutputMarkupId( true );
        assignmentContainer.addOrReplace( agentAssignmentContainer );
        List<Agent> availableParticipationAgents = agentsWithAvailableParticipation();
        DropDownChoice<Agent> agentChoice = new DropDownChoice<Agent>(
                "agents",
                new PropertyModel<Agent>( this, "assignmentAgent" ),
                availableParticipationAgents,
                new ChoiceRenderer<Agent>() {
                    @Override
                    public Object getDisplayValue( Agent agent ) {
                        return agent.getActorName();
                    }

                    @Override
                    public String getIdValue( Agent agent, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        agentChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                participateButton.setEnabled( canAssign() );
                target.add( assignmentContainer );
            }
        } );
        agentChoice.setEnabled( !availableParticipationAgents.isEmpty() );
        agentAssignmentContainer.add( agentChoice );
    }

    private List<Agent> agentsWithAvailableParticipation() {
        List<Agent> agents = new ArrayList<Agent>();
        ParticipationManager participationManager = getCommunityService().getParticipationManager();
        if ( assignmentUser != null && assignmentAgency != null ) {
            for ( final Agent agent : assignmentAgency.getAgents( getCommunityService() ) ) {
                if ( participationManager.isParticipationAvailable( agent, assignmentUser, getCommunityService() ) ) {
                    agents.add( agent );
                }
            }
        }
        Collections.sort( agents, new Comparator<Agent>() {
            @Override
            public int compare( Agent a1, Agent a2 ) {
                return a1.getName().compareToIgnoreCase( a2.getName() );
            }
        } );
        return agents;
    }

    private void addParticipateButton() {
        participateButton = new AjaxLink<String>( "participate" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                assignParticipation();
                resetUserParticipationWrappers();
                addUsersParticipationTable();
                target.add( usersParticipationTable );
                addAssigning();
                target.add( assignmentContainer );
                Change change = new Change( Change.Type.Added, getCommunityService().getPlanCommunity(), "participation" );
                change.setMessage( addedParticipation != null
                        ? "Added " + addedParticipation.asString( getCommunityService() )
                        : "Failed to add participation" );
                addedParticipation = null;
                assignmentAgency = null;
                assignmentUser = null;
                assignmentAgent = null;
                update( target, change );
            }
        };
        participateButton.setOutputMarkupId( true );
        participateButton.setEnabled( canAssign() );
        assignmentContainer.addOrReplace( participateButton );
    }

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof UserParticipationWrapper ) {
            UserParticipationWrapper wrapper = (UserParticipationWrapper) object;
            if ( action.equals( "unassign" ) ) {
                String userParticipationString = wrapper.toString();
                boolean success = wrapper.unassign();
                resetUserParticipationWrappers();
                addUsersParticipationTable();
                target.add( usersParticipationTable );
                addAssigning();
                target.add( assignmentContainer );
                Change change = new Change( Change.Type.Removed, getCommunityService().getPlanCommunity(), "participation" );
                change.setMessage( success
                        ? "Removed " + userParticipationString
                        : "Failed to remove " + userParticipationString );
                update( target, change );
            }
        }
    }

    public String getAssignmentUserFullNameAndEmail() {
        return assignmentUser == null
                ? null
                : assignmentUser.getFullName() + " (" + assignmentUser.getEmail() + ")";
    }

    public void setAssignmentUserFullNameAndEmail( String val ) {
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
                        ? val.substring( openParIndex + 1 )
                        : val.substring( openParIndex + 1, closedParIndex ) ).trim();
            }
            UserRecordService userDao = getCommunityService().getUserRecordService();
            if ( email != null )
                assignmentUser = userDao.getUserWithIdentity( email );
            if ( assignmentUser == null ) {
                assignmentUser = userDao.getUserWithIdentity( identifier );
            }
            if ( assignmentUser == null ) {
                List<ChannelsUser> users = userDao.findAllUsersWithFullName( identifier, getCommunityService().getPlanCommunity().getUri() );
                if ( users.size() == 1 )
                    assignmentUser = users.get( 0 );
            }
        } else {
            assignmentUser = null;
        }
        assignmentAgency = null;
    }

    public Agency getAssignmentAgency() {
        return assignmentAgency;
    }

    public void setAssignmentAgency( Agency assignmentAgency ) {
        this.assignmentAgency = assignmentAgency;
    }

    public Agent getAssignmentAgent() {
        return assignmentAgent;
    }

    public void setAssignmentAgent( Agent agent ) {
        assignmentAgent = agent;
    }

    private void assignParticipation() {
        if ( canAssign() ) {
            CommunityService communityService = getCommunityService();
            ParticipationManager participationManager = communityService.getParticipationManager();
            if ( participationManager.isParticipationAvailable( assignmentAgent, assignmentUser, communityService ) ) {
                if ( getUser().equals( assignmentUser ) ) {
                    addedParticipation = communityService.getUserParticipationService().addAcceptedParticipation(
                            getUsername(),
                            assignmentUser,
                            assignmentAgent,
                            communityService );
                } else {
                    addedParticipation = communityService.getUserParticipationService().addParticipation(
                            getUsername(),
                            assignmentUser,
                            assignmentAgent,
                            communityService );
                }
            }
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
                ChannelsUser user = getUser();
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


        public boolean unassign() {
            if ( userParticipation != null && isUserInCharge() ) {
                CommunityService communityService = getCommunityService();
                return communityService.getUserParticipationService().deleteParticipation(
                        new ChannelsUser( userParticipation.getParticipant( communityService ), communityService ),
                        userParticipation.getAgent( communityService ),
                        communityService );
            }
            return false;
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

        public ChannelsUser getUserIfInCharge() {
            return isUserInCharge()
                    ? getUser()
                    : null;
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
            columns.add( makeActionLinkColumn( "",
                    "unassign",
                    "unassign",
                    "Delete participation?",
                    "userIfInCharge",
                    "more",
                    UsersParticipationPanel.this ) );
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
