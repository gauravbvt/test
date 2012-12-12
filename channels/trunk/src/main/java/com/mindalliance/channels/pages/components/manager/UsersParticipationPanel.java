package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationConfirmationService;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.util.NameRange;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Users participation panel in participation manager.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/12
 * Time: 11:14 AM
 */
public class UsersParticipationPanel extends AbstractUpdatablePanel implements NameRangeable, Filterable {

    private static final String ALL_USERS = "All users";
    private static final String COLLABORATORS = "Users I collaborate with";
    private static final String SUPERVISED = "All users I supervise";
    private static final String SUPERVISORS = "All users that supervise me";
    private static final String COLLEAGUES = "All users who are my co-employees";
    private static final String SELF = "Only myself";

    private static final String[] USER_CHOICES = {ALL_USERS, COLLABORATORS, SUPERVISED, SUPERVISORS, COLLEAGUES, SELF};

    @SpringBean
    private ParticipationManager participationManager;

    /**
     * Name index panel.
     */
    private NameRangePanel nameRangePanel;
    /**
     * Selected name range.
     */
    private NameRange nameRange = new NameRange();
    /**
     * Model objects filtered on (show only where so and so is the actor etc.)
     */
    private List<Identifiable> filters = new ArrayList<Identifiable>();

    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_ROWS = 12;

    private String userRelationship = ALL_USERS;
    private UsersParticipationTable usersParticipationTable;
    private List<UserParticipationWrapper> userParticipationWrappers;
    private WebMarkupContainer notesContainer;
    private WebMarkupContainer assignmentContainer;
    private ChannelsUser assignmentUser;
    private Agency assignmentAgency;

    public UsersParticipationPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model );
        init();
    }

    private void init() {
        resetUserParticipationWrappers();
        addUsersDropDown();
        addUsersParticipationTable();
        addNotes();
        addAssigning();
    }

    private void addUsersDropDown() {
        DropDownChoice<String> usersChoice = new DropDownChoice<String>(
                "users",
                new PropertyModel<String>( this, "userRelationship" ),
                Arrays.asList( USER_CHOICES )
        );
        usersChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                resetUserParticipationWrappers();
                nameRange = new NameRange();
                addNameRangePanel();
                addUsersParticipationTable();
                target.add( usersParticipationTable );
            }
        } );
        add( usersChoice );
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
        add( usersParticipationTable );
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
        addUsersParticipationTable();
        target.add( usersParticipationTable );
    }


    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return filters.contains( identifiable );
    }

    @Override
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        // Property ignored since no two properties filtered are ambiguous on type.
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( identifiable );
        } else {
            filters.add( identifiable );
        }
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
        ChannelsUserDao userDao = getPlanCommunity().getUserDao();
        List<String> normalizedUserNames = new ArrayList<String>();
        for ( ChannelsUser user : userDao.getUsers( getPlanCommunity().getUri() ) ) {
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
        PlanCommunity planCommunity = getPlanCommunity();
        ChannelsUserDao userDao = getPlanCommunity().getUserDao();
        UserParticipationService userParticipationService = planCommunity.getUserParticipationService();
        userParticipationWrappers = new ArrayList<UserParticipationWrapper>();
        for ( ChannelsUser user : userDao.getUsers( planCommunity.getUri() ) ) {
            if ( nameRange.contains( user.getNormalizedFullName() )
                    && hasUserRelationship( user, userRelationship ) ) {
                List<UserParticipation> userParticipationList = userParticipationService.getUserParticipations(
                        user,
                        planCommunity
                );
                for ( UserParticipation participation : userParticipationList ) {
                    if ( !isFilteredOut( participation ) ) {
                        userParticipationWrappers.add( new UserParticipationWrapper( user, participation ) );
                    }
                }
                if ( userParticipationList.isEmpty() ) {
                    userParticipationWrappers.add( new UserParticipationWrapper( user ) );
                }
            }
        }
    }

    private boolean hasUserRelationship( ChannelsUser user, String userRelationship ) {
        boolean isSelf = user.getUsername().equals( getUsername() );
        if ( userRelationship.equals( ALL_USERS ) ) return true;
        else if ( userRelationship.equals( SELF ) ) return isSelf;
        else if ( isSelf ) return false;
        else {
            PlanCommunity planCommunity = getPlanCommunity();
            if ( userRelationship.equals( COLLABORATORS ) )
                return participationManager.areCollaborators( planCommunity, user, getUser() ); // todo - implement
            else if ( userRelationship.equals( SUPERVISED ) )
                return participationManager.isSupervisedBy( planCommunity, user, getUser() );
            else if ( userRelationship.equals( SUPERVISORS ) )
                return participationManager.isSupervisorOf( planCommunity, user, getUser() );
            else if ( userRelationship.equals( COLLEAGUES ) )
                return participationManager.areColleagues( planCommunity, user, getUser() );
            else throw new RuntimeException( "Unknown relationship" );
        }
    }

    private boolean isFilteredOut( UserParticipation participation ) {
        Agent agent = participation.getAgent( getPlanCommunity() );
        return !filters.isEmpty() && !filters.contains( agent );
    }

    private void addNotes() {
        List<String> participationNotes = getParticipationNotes();
        notesContainer = new WebMarkupContainer( "notesContainer" );
        notesContainer.setOutputMarkupId( true );
        notesContainer.setVisible( !participationNotes.isEmpty() );
        addOrReplace( notesContainer );
        ListView<String> notesListView = new ListView<String>(
                "notes",
                participationNotes
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                item.add( new Label("note", item.getModelObject()) );
            }
        };
        notesContainer.add( notesListView );
    }

    private List<String> getParticipationNotes() {
        final PlanCommunity planCommunity = getPlanCommunity();
        UserParticipationService userParticipationService = planCommunity.getUserParticipationService();
        final UserParticipationConfirmationService userParticipationConfirmationService
                = planCommunity.getUserParticipationConfirmationService();
        List<String> notes = new ArrayList<String>(  );
        int unacceptedCount = CollectionUtils.select(
                userParticipationService.getUserParticipations( getUser(), planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !((UserParticipation)object).isAccepted();
                    }
                }
        ).size();
        if ( unacceptedCount > 0 ) {
            notes.add( "You are requested to accept "
                    + unacceptedCount
                    + ( unacceptedCount > 1 ? " assignments." : " assignment.") );
        }
        int confirmationCount = CollectionUtils.select(
                userParticipationService.getParticipationsSupervisedByUser( getUser(), planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation userParticipation = (UserParticipation)object;
                        return userParticipationConfirmationService.isConfirmationByUserRequired(
                                userParticipation,
                                getUser(),
                                planCommunity );
                    }
                }).size();
        if ( confirmationCount > 0 ) {
            notes.add( confirmationCount
                    + ( confirmationCount > 1 ? " assignments" : " assignment")
                    + " await your confirmation as supervisor.");
        }
        return notes;
    }

    private void addAssigning() {
        assignmentContainer = new WebMarkupContainer( "assignmentContainer" );
        assignmentContainer.setOutputMarkupId( true );
        addOrReplace( assignmentContainer );
        addAssignmentUser( );
        addAssignmentAgency( );
        addAssignmentAgent( );
    }

    private void addAssignmentUser() {
        final List<String> choices = getUserFullNamesAndEmails();
        AutoCompleteTextField<String> assignmentUserField = new AutoCompleteTextField<String>(
                "assignedUser",
                new PropertyModel<String>( this, "assignmentUserName" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                if ( choices != null ) {
                    for ( String choice : choices ) {
                        if ( Matcher.matches( choice, input) )
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
                target.add( assignmentContainer );
            }
        } );
        assignmentContainer.addOrReplace( assignmentUserField );
    }

    private List<String> getUserFullNamesAndEmails() {
        PlanCommunity planCommunity = getPlanCommunity();
        List<String> fullNamesAndEmails = new ArrayList<String>();
        for ( ChannelsUser user : planCommunity.getUserDao().getUsers( planCommunity.getUri() ) ) {
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
                target.add( assignmentContainer );
            }
        } );
        container.add( agencyChoice );
        container.setVisible( !agenciesWithAvailableParticipation.isEmpty() );
        assignmentContainer.addOrReplace( container );
    }

    @SuppressWarnings( "unchecked" )
    private List<Agency> agenciesWithAvailableParticipationFor( final ChannelsUser assignmentUser ) {
        List<Agency> sortedAgencies = (List<Agency>) CollectionUtils.select(
                participationManager.getAllKnownAgencies( getPlanCommunity() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Agency agency = (Agency) object;
                        return CollectionUtils.exists(
                                agency.getAgents( getPlanCommunity() ),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        final Agent agent = (Agent) object;
                                        return participationManager.isParticipationAvailable(
                                                agent,
                                                assignmentUser,
                                                getPlanCommunity() );
                                    }
                                }
                        );
                    }
                }
        );        Collections.sort( sortedAgencies,
                new Comparator<Agency>() {
            @Override
            public int compare( Agency a1, Agency a2 ) {
                return a1.getName().compareToIgnoreCase( a2.getName() );
            }
        } );
        return sortedAgencies;
    }

    private void addAssignmentAgent() {
        WebMarkupContainer container = new WebMarkupContainer( "agentsContainer" );
        container.setOutputMarkupId( true );
        makeVisible( container, assignmentAgency != null );
        assignmentContainer.addOrReplace( container );
        List<Agent> availableParticipationAgents = agentsWithAvailableParticipation(  );
        DropDownChoice<Agent> agentChoice = new DropDownChoice<Agent>(
                "agents",
                new PropertyModel<Agent>( this, "assignmentAgent" ),
                availableParticipationAgents,
                new ChoiceRenderer<Agent>() {
                    @Override
                    public Object getDisplayValue( Agent actor ) {
                        return actor.getName();
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
                addAssigning();
                target.add( assignmentContainer );
            }
        } );
        container.add( agentChoice );
    }

    private List<Agent> agentsWithAvailableParticipation() {
        List<Agent> agents = new ArrayList<Agent>();
        if ( assignmentUser != null && assignmentAgency != null ) {
            for ( final Agent agent : assignmentAgency.getAgents( getPlanCommunity() ) ) {
                if ( participationManager.isParticipationAvailable( agent, assignmentUser, getPlanCommunity() ) ) {
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

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof UserParticipationWrapper ) {
            UserParticipationWrapper wrapper = (UserParticipationWrapper) object;
            if ( action.equals( "unassign" ) ) {
                wrapper.unassign();
                addUsersParticipationTable();
                target.add( usersParticipationTable );
                addNotes();
                target.add( notesContainer );
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
                    : val.substring( openParIndex + 1, closedParIndex )).trim();
            }
            ChannelsUserDao userDao = getPlanCommunity().getUserDao();
            if ( email != null )
                assignmentUser = userDao.getUserNamed( email );
            if ( assignmentUser == null) {
                assignmentUser = userDao.getUserNamed( identifier );
            }
            if ( assignmentUser == null) {
                List<ChannelsUser> users = userDao.findAllUsersWithFullName( identifier, getPlanCommunity().getUri() );
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
        return null;
    }

    public void setAssignmentAgent( Agent agent ) {
        PlanCommunity planCommunity = getPlanCommunity();
        if (  participationManager.isParticipationAvailable( agent, assignmentUser, getPlanCommunity() ) ) {
            planCommunity.getUserParticipationService().addParticipation(
                    getUsername(),
                    assignmentUser,
                    agent,
                    planCommunity );
        }
        assignmentAgency = null;
    }

     /**
     * User participation wrapper.
     */
    private class UserParticipationWrapper implements Serializable {

        private ChannelsUser user;
        private UserParticipation userParticipation;

        public UserParticipationWrapper( ChannelsUser user ) {
            this.user = user;
        }

        public UserParticipationWrapper( ChannelsUser user, UserParticipation userParticipation ) {
            this.user = user;
            this.userParticipation = userParticipation;
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
            return user.getNormalizedFullName();
        }

        public String getEmail() {
            return user.getEmail();
        }

        public String getAgentName() {
            Agent agent = getAgent();
            return agent == null ? null : agent.getName();
        }

        private Agent getAgent() {
            return userParticipation != null
                    ? userParticipation.getAgent( getPlanCommunity() )
                    : null;
        }

        private String getAgentDescription() {
            Agent agent = getAgent();
            return agent != null
                    ? agent.getDescription()
                    : null;
        }

        private String getRelationshipsToUser() {
            List<String> rels = new ArrayList<String>();
            if ( userParticipation != null ) {
                PlanCommunity planCommunity = getPlanCommunity();
                ChannelsUser user = getUser();
                ChannelsUser otherUser = new ChannelsUser( userParticipation.getParticipant(), planCommunity );
                if ( user.equals( otherUser ) ) {
                    rels.add( "me" );
                } else {
                    if ( participationManager.areCollaborators( planCommunity, user, otherUser ) ) {
                        rels.add( "collaborator" );
                    }
                    if ( participationManager.isSupervisorOf( planCommunity, otherUser, user ) ) {
                        rels.add( "supervisor" );
                    }
                    if ( participationManager.isSupervisedBy( planCommunity, otherUser, user ) ) {
                        rels.add( "direct report" );
                    }
                    if ( participationManager.areColleagues( planCommunity, otherUser, user ) ) {
                        rels.add( "colleague" );
                    }
                }
            }
            return rels.isEmpty() ? null : ChannelsUtils.listToString( rels, " and " );
        }

        public void unassign() {
            if ( isUserInCharge() ) {
                getPlanCommunity().getUserParticipationService().refuse( userParticipation );
            }
        }

        public boolean isUserInCharge() {
            if ( userParticipation != null ) {
                ChannelsUser user = getUser();
                PlanCommunity planCommunity = getPlanCommunity();
                return participationManager.hasAuthorityOverParticipation( planCommunity, user, userParticipation );
            } else {
                return false;
            }
        }
    }


    /**
     * User participation table.
     */
    private class UsersParticipationTable extends AbstractTablePanel<UserParticipationWrapper> {

        IModel<List<UserParticipationWrapper>> userParticipationModel;

        public UsersParticipationTable( String id, IModel<List<UserParticipationWrapper>> userParticipationModel ) {
            super( id, MAX_ROWS );
            this.userParticipationModel = userParticipationModel;
            initTable();
        }

        private void initTable() {
            final List<IColumn<UserParticipationWrapper>> columns = new ArrayList<IColumn<UserParticipationWrapper>>();
            columns.add( makeColumn( "User", "normalizedFullName", EMPTY ) );
            columns.add( makeColumn( "Email", "email", EMPTY ) );
            columns.add( makeColumn( "Participates as", "agentName", null, EMPTY, "agentDescription" ) );
            columns.add( makeColumn( "Is my", "relationshipsToUser", EMPTY ) );
            columns.add( makeColumn( "Accepted?", "accepted", EMPTY ) );
            columns.add( makeColumn( "As of", "whenAccepted", EMPTY ) );
            columns.add( makeColumn( "Confirmed?", "confirmed", EMPTY ) );
            columns.add( makeActionLinkColumn( "",
                    "unassign",
                    "unassign",
                    "userInCharge",
                    "more",
                    UsersParticipationPanel.this ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable<UserParticipationWrapper>(
                    "userParticipationTable",
                    columns,
                    new SortableBeanProvider<UserParticipationWrapper>(
                            userParticipationModel.getObject(),
                            "userInfo.normalizedFullName" ),
                    getPageSize() ) );
        }
    }

}
