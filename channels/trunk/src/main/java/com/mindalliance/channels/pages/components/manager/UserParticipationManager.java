package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationConfirmationService;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User participation manager.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/7/13
 * Time: 1:51 PM
 */
public class UserParticipationManager extends AbstractUpdatablePanel {

    @SpringBean
    private ParticipationManager participationManager;

    @SpringBean
    private UserParticipationService userParticipationService;

    @SpringBean
    private ChannelsUserDao userDao;

    @SpringBean
    private UserParticipationConfirmationService userParticipationConfirmationService;

    private boolean onlyShowUnassignedAgencies = false;
    private boolean onlyShowUnassignedAgents = false;
    private Agency selectedAgency;
    private Agent selectedAgent;
    private WebMarkupContainer agenciesContainer;
    private WebMarkupContainer agentsContainer;
    private WebMarkupContainer participantsContainer;
    private Label summaryLabel;
    private Set<ChannelsUser> addedParticipants;
    private Set<ChannelsUser> removedParticipants;
    private WebMarkupContainer agenciesListContainer;
    private WebMarkupContainer agentsListContainer;
    private String participantsFilter;

    public UserParticipationManager( String id ) {
        super( id );
        init();
    }

    private void init() {
        resetSelectionsAndChanges();
        addAgencies();
        addAgents();
        addParticipants();
        addSummary();
        addCancelAndSubmitButtons();
    }

    private void resetSelectionsAndChanges() {
        selectedAgency = null;
        selectedAgent = null;
        resetPendingParticipationChanges();
    }

    private void resetPendingParticipationChanges() {
        addedParticipants = new HashSet<ChannelsUser>();
        removedParticipants = new HashSet<ChannelsUser>();
    }

    private void addAgencies() {
        agenciesContainer = new WebMarkupContainer( "agenciesContainer" );
        agenciesContainer.setOutputMarkupId( true );
        addOrReplace( agenciesContainer );
        addAgenciesFilter();
        agenciesListContainer = new WebMarkupContainer( "agenciesListContainer" );
        agenciesListContainer.setOutputMarkupId( true );
        agenciesContainer.addOrReplace( agenciesListContainer );
        addAgenciesList();
    }

    private void addAgenciesFilter() {
        AjaxCheckBox onlyUnassignedAgenciesCheckBox = new AjaxCheckBox(
                "onlyWithUnassignedAgents",
                new PropertyModel<Boolean>( this, "onlyShowUnassignedAgencies" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addAgenciesList();
                target.add( agenciesListContainer );
                addAgents();
                target.add( agentsContainer );
                addParticipants();
                target.add( participantsContainer );
                addSummary();
                target.add( summaryLabel );
            }
        };
        agenciesContainer.add( onlyUnassignedAgenciesCheckBox );
    }

    private void addAgenciesList() {
        ListView<Agency> agencyList = new ListView<Agency>(
                "agencies",
                new PropertyModel<List<Agency>>( this, "filteredAgencies" )
        ) {
            @Override
            protected void populateItem( final ListItem<Agency> item ) {
                final Agency agency = item.getModelObject();
                boolean selected = selectedAgency != null && selectedAgency.equals( agency );
                // selector image
                final WebMarkupContainer selectorImage = new WebMarkupContainer( "selector" );
                selectorImage.add( new AttributeModifier(
                        "src",
                        selected
                                ? "images/selected.png"
                                : "images/not_selected.png"
                ) );
                selectorImage.add( new AjaxEventBehavior( "onclick" ) {
                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        selectAgency( agency );
                        addAgenciesList();
                        target.add( agenciesListContainer );
                        addAgents();
                        target.add( agentsContainer );
                        addParticipants();
                        target.add( participantsContainer );
                        addSummary();
                        target.add( summaryLabel );
                    }
                } );
                if ( selected ) item.add( new AttributeModifier( "class", "selected" ) );
                item.add( selectorImage );
                // name
                item.add( new Label( "agencyName", agency.getName() ) );
                // metrics
                item.add( new Label( "metrics", getAgencyMetrics( agency ) ) );
            }
        };
        agencyList.setOutputMarkupId( true );
        agenciesListContainer.addOrReplace( agencyList );
    }

    private String getAgencyMetrics( Agency agency ) {
        int count = listUnassignedAgents( agency ).size();
        return "("
                + count
                + " unassigned "
                + ( count > 1 ? "agents" : "agent" )
                + ")";
    }

    private void selectAgency( Agency agency ) {
        if ( selectedAgency != null && selectedAgency.equals( agency ) ) {
            selectedAgency = null;
        } else {
            selectedAgency = agency;
        }
        addedParticipants = new HashSet<ChannelsUser>();
        removedParticipants = new HashSet<ChannelsUser>();
    }

    public List<Agency> getFilteredAgencies() {
        List<Agency> filteredAgencies = new ArrayList<Agency>();
        for ( Agency agency : participationManager.getAllKnownAgencies( getCommunityService() ) ) {
            if ( onlyShowUnassignedAgencies ) {
                List<Agent> unassignedAgents = listUnassignedAgents( agency );
                if ( unassignedAgents.isEmpty() )
                    filteredAgencies.add( agency );
            } else {
                filteredAgencies.add( agency );
            }
        }
        Collections.sort( filteredAgencies, new Comparator<Agency>() {
            @Override
            public int compare( Agency a1, Agency a2 ) {
                return a1.getName().compareTo( a2.getName() );
            }
        } );
        return filteredAgencies;
    }

    @SuppressWarnings( "unchecked" )
    private List<Agent> listUnassignedAgents( Agency agency ) {
        final List<Agent> unassignedAgents = participationManager.findAllUnassignedAgents( getCommunityService() );
        return (List<Agent>) CollectionUtils.select(
                agency.getAgents( getCommunityService() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return unassignedAgents.contains( (Agent) object );
                    }
                }
        );
    }

    private void addAgents() {
        agentsContainer = new WebMarkupContainer( "agentsContainer" );
        agentsContainer.setOutputMarkupId( true );
        addOrReplace( agentsContainer );
        agentsContainer.add( new Label( "agencyName", selectedAgency == null ? "" : selectedAgency.getName() ) );
        addAgentsFilter();
        agentsListContainer = new WebMarkupContainer( "agentsListContainer" );
        agentsListContainer.setOutputMarkupId( true );
        agentsContainer.addOrReplace( agentsListContainer );
        addAgentsList();
        makeVisible( agentsContainer, selectedAgency != null );
    }

    private void addAgentsFilter() {
        AjaxCheckBox onlyUnassignedAgentsCheckBox = new AjaxCheckBox(
                "onlyWithUnassignedAgents",
                new PropertyModel<Boolean>( this, "onlyShowUnassignedAgents" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                selectedAgent = null;
                resetPendingParticipationChanges();
                addAgents();
                target.add( agentsContainer );
                addParticipants();
                target.add( participantsContainer );
                addSummary();
                target.add( summaryLabel );
            }
        };
        agentsContainer.add( onlyUnassignedAgentsCheckBox );
    }

    private void addAgentsList() {
        ListView<Agent> agentList = new ListView<Agent>(
                "agents",
                new PropertyModel<List<Agent>>( this, "filteredAgents" )
        ) {
            @Override
            protected void populateItem( ListItem<Agent> item ) {
                final Agent agent = item.getModelObject();
                boolean selected = selectedAgent != null && selectedAgent.equals( agent );
                // selector image
                WebMarkupContainer selectorImage = new WebMarkupContainer( "selector" );
                selectorImage.add( new AttributeModifier(
                        "src",
                        selected
                                ? "images/selected.png"
                                : "images/not_selected.png"
                ) );
                selectorImage.add( new AjaxEventBehavior( "onclick" ) {
                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        selectAgent( agent );
                        addAgentsList();
                        target.add( agentsListContainer );
                        addParticipants();
                        target.add( participantsContainer );
                        addSummary();
                        target.add( summaryLabel );
                    }
                } );
                if ( selected ) item.add( new AttributeModifier( "class", "selected" ) );
                item.add( selectorImage );
                // name
                item.add( new Label( "agentName", agent.getName() ) );
                // metrics
                item.add( new Label( "metrics", getAgentMetrics( agent ) ) );
            }
        };
        agentList.setOutputMarkupId( true );
        agentsListContainer.addOrReplace( agentList );
    }

    public List<Agent> getFilteredAgents() {
        List<Agent> filteredAgents = new ArrayList<Agent>();
        if ( selectedAgency != null ) {
            List<Agent> unassignedAgents = listUnassignedAgents( selectedAgency );
            for ( Agent agent : selectedAgency.getAgents( getCommunityService() ) ) {
                if ( onlyShowUnassignedAgents ) {
                    if ( unassignedAgents.contains( agent ) )
                        filteredAgents.add( agent );
                } else {
                    filteredAgents.add( agent );
                }
            }
        }
        Collections.sort( filteredAgents, new Comparator<Agent>() {
            @Override
            public int compare( Agent a1, Agent a2 ) {
                return a1.getName().compareTo( a2.getName() );
            }
        } );
        return filteredAgents;
    }

    private void selectAgent( Agent agent ) {
        if ( selectedAgent != null && selectedAgent.equals( agent ) ) {
            selectedAgent = null;
        } else {
            selectedAgent = agent;
        }
        resetPendingParticipationChanges();
    }

    private String getAgentMetrics( Agent agent ) {
        int count = selectedAgent != null && selectedAgent.equals( agent )
                ? getRegisteredAndPendingParticipants().size()
                : getRegisteredParticipants( agent ).size();
        Actor actor = agent.getActor();
        StringBuilder sb = new StringBuilder();
        sb.append( "(" )
                .append( count )
                .append( count > 1 ? " participants" : " participant" );
        if ( actor.getMaxParticipation() > 0 ) {
            sb.append( " - " )
                    .append( actor.getMaxParticipation() )
                    .append( " max" );
        }
        sb.append( ")" );
        return sb.toString();
    }

    private List<ChannelsUser> getRegisteredAndPendingParticipants() {
        List<ChannelsUser> allParticipants = new ArrayList<ChannelsUser>();
        allParticipants.addAll( getRegisteredParticipants( selectedAgent ) );
        allParticipants.addAll( addedParticipants );
        allParticipants.removeAll( removedParticipants );
        return allParticipants;
    }

    private List<ChannelsUser> getRegisteredParticipants( Agent agent ) {
        List<ChannelsUser> participants = new ArrayList<ChannelsUser>();
        if ( agent != null ) {
            for ( UserParticipation userParticipation
                    : userParticipationService.getParticipationsAsAgent( agent, getCommunityService() ) ) {
                participants.add( new ChannelsUser( userParticipation.getParticipant() ) );
            }
            Collections.sort( participants, new Comparator<ChannelsUser>() {
                @Override
                public int compare( ChannelsUser u1, ChannelsUser u2 ) {
                    return u1.getNormalizedFullName().compareTo( u2.getNormalizedFullName() );
                }
            } );
        }
        return participants;
    }

    private void addParticipants() {
        participantsContainer = new WebMarkupContainer( "participantsContainer" );
        participantsContainer.setOutputMarkupId( true );
        addOrReplace( participantsContainer );
        participantsContainer.add( new Label( "agentName", selectedAgent == null ? "" : selectedAgent.getName() ) );
        addParticipantsFilter();
        addParticipantsList();
        makeVisible( participantsContainer, selectedAgent != null );
    }

    private void addParticipantsFilter() {
        participantsFilter = null;
        TextField<String> userNameFilter = new TextField<String>(
                "userNameFilter",
                new PropertyModel<String>( this, "participantsFilter" )
        );
        userNameFilter.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addParticipantsList();
                target.add( participantsContainer );
            }
        } );
        participantsContainer.add( userNameFilter );
    }

    public String getParticipantsFilter() {
        return participantsFilter;
    }

    public void setParticipantsFilter( String participantsFilter ) {
        this.participantsFilter = participantsFilter;
    }

    private void addParticipantsList() {
        final List<ChannelsUser> participants = getRegisteredParticipants( selectedAgent );
        ListView<ChannelsUser> participantsListView = new ListView<ChannelsUser>(
                "participants",
                getFilteredUsers()
        ) {
            @Override
            protected void populateItem( ListItem<ChannelsUser> item ) {
                final ChannelsUser participant = item.getModelObject();
                boolean participating = participants.contains( participant );
                // participating
                AjaxCheckBox participatingCheckBox = new AjaxCheckBox(
                        "participating",
                        new Model<Boolean>( participating )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        toggleParticipationsAs( participant );
                        addSummary();
                        target.add( summaryLabel );
                    }
                };
                boolean participationAvailable = selectedAgent != null &&
                        ( participating ||
                        participationManager.isParticipationAvailable(
                                selectedAgent,
                                participant,
                                getCommunityService() ) );
                boolean userHasAuthority = selectedAgent != null &&
                        ( getCommunityService().isCommunityPlanner( getUser() )
                            || participationManager.hasAuthorityOverParticipation(
                                getCommunityService(),
                                getUser(),
                                participant.getUserInfo(),
                                selectedAgent ) );
                // UserParticipation userParticipation = userParticipationService.getParticipation( user,  )
                participatingCheckBox.setEnabled( participationAvailable && userHasAuthority );
                item.add( participatingCheckBox );
                // user name
                Label userNameLabel = new Label( "userName", participant.getNormalizedFullName() );
                String tooltip = "";
                if ( selectedAgent != null && !participationAvailable )
                    tooltip += "Participation as " + selectedAgent.getName() + " is not available to this user. ";
                if ( selectedAgent != null && !userHasAuthority )
                    tooltip += "You are not authorized to assign this user as " + selectedAgent.getName();
                if ( !tooltip.isEmpty() ) addTipTitle( userNameLabel, tooltip );
                item.add( userNameLabel );
                // accepted
                item.add( new Label( "accepted", isAcceptedParticipation( participant ) ? "Yes" : "No" ) );
                // confirmed
                item.add( new Label( "confirmed", isConfirmedParticipation( participant ) ? "Yes" : "No" ) );
            }
        };
        participantsListView.setOutputMarkupId( true );
        participantsContainer.addOrReplace( participantsListView );
    }

    private boolean isAcceptedParticipation( ChannelsUser user ) {
        return selectedAgent != null
                && userParticipationService.isUserParticipatingAs( user, selectedAgent, getCommunityService() );
    }

    private boolean isConfirmedParticipation( ChannelsUser user ) {
        if ( selectedAgent == null ) return false;
        UserParticipation userParticipation = userParticipationService.getParticipation(
                user,
                selectedAgent,
                getCommunityService() );
        return userParticipation != null &&
                userParticipationConfirmationService
                        .isConfirmedByAllSupervisors( userParticipation, getCommunityService() );
    }

    private void toggleParticipationsAs( ChannelsUser user ) {
        // delay until submit
        if ( addedParticipants.contains( user ) ) {
            addedParticipants.remove( user );
        } else if ( removedParticipants.contains( user ) ) {
            removedParticipants.remove( user );
        } else if ( getRegisteredParticipants( selectedAgent ).contains( user ) ) {
            removedParticipants.add( user );
        } else if ( !getRegisteredParticipants( selectedAgent ).contains( user ) ) {
            addedParticipants.add( user );
        }

    }

    private List<ChannelsUser> getFilteredUsers() {
        List<ChannelsUser> participants = getRegisteredAndPendingParticipants();
        Collections.sort( participants, new Comparator<ChannelsUser>() {
            @Override
            public int compare( ChannelsUser u1, ChannelsUser u2 ) {
                return u1.getNormalizedFullName().compareTo( u2.getNormalizedFullName() );
            }
        } );
        List<ChannelsUser> allUsers = new ArrayList<ChannelsUser>();
        for ( ChannelsUser participant : participants ) {
            if ( !isFilteredOut( participant ) ) {
                allUsers.add( participant );
            }
        }
        List<ChannelsUser> nonParticipants = new ArrayList<ChannelsUser>();
        for ( ChannelsUser user : userDao.getUsers() ) {
            if ( !participants.contains( user ) && !isFilteredOut( user ) )
                nonParticipants.add( user );
        }
        Collections.sort( nonParticipants, new Comparator<ChannelsUser>() {
            @Override
            public int compare( ChannelsUser u1, ChannelsUser u2 ) {
                return u1.getNormalizedFullName().compareTo( u2.getNormalizedFullName() );
            }
        } );
        allUsers.addAll( nonParticipants );
        return allUsers;
    }

    private boolean isFilteredOut( ChannelsUser user ) {
        return participantsFilter != null
                && !participantsFilter.isEmpty()
                && !Matcher.matches( user.getNormalizedFullName(), participantsFilter );
    }

    private void addSummary() {
        summaryLabel = new Label( "summary", summarizeParticipation() );
        summaryLabel.setOutputMarkupId( true );
        addOrReplace( summaryLabel );
    }

    private String summarizeParticipation() {
        if ( selectedAgency == null ) {
            return "Select an organization";
        } else if ( selectedAgent == null ) {
            return "Select an agent";
        } else {
            StringBuilder sb = new StringBuilder();
            List<ChannelsUser> registeredParticipants = getRegisteredParticipants( selectedAgent );
            if ( registeredParticipants.isEmpty() ) {
                sb.append( "No one currently participates as " )
                        .append( selectedAgent.getName() )
                        .append( "." );
            } else {
                int size = registeredParticipants.size();
                for ( int i = 1; i <= registeredParticipants.size(); i++ ) {
                    sb.append( registeredParticipants.get( i - 1 ).getNormalizedFullName() );
                    if ( i == size - 1 )
                        sb.append( " and " );
                    else if ( i < size - 1 )
                        sb.append( ", " );
                }
                sb.append( " already" );
                if ( size > 1 )
                    sb.append( " participate as" );
                else
                    sb.append( " participates as" );
                sb.append( " " )
                        .append( selectedAgent.getName() )
                        .append( "." );
            }
            // new participation
            List<ChannelsUser> added = new ArrayList<ChannelsUser>( addedParticipants );
            added.removeAll( removedParticipants );
            if ( !added.isEmpty() ) {
                sb.append( " " );
                int size = added.size();
                for ( int i = 1; i <= added.size(); i++ ) {
                    sb.append( added.get( i - 1 ).getNormalizedFullName() );
                    if ( i == size - 1 )
                        sb.append( " and " );
                    else if ( i < size - 1 )
                        sb.append( ", " );
                }
                if ( !registeredParticipants.isEmpty() ) {
                    sb.append( " will also participate." );
                } else {
                    sb.append( "will participate as " )
                            .append( selectedAgent.getName() )
                            .append( "." );
                }

            }
            List<ChannelsUser> removed = new ArrayList<ChannelsUser>( removedParticipants );
            if ( !removed.isEmpty() ) {
                sb.append( " " );
                int size = removed.size();
                for ( int i = 1; i <= removed.size(); i++ ) {
                    sb.append( removed.get( i - 1 ).getNormalizedFullName() );
                    if ( i == size - 2 )
                        sb.append( " and " );
                    else if ( i < size - 2 )
                        sb.append( ", " );
                }
                sb.append( "will no longer participate as " )
                        .append( selectedAgent.getName() )
                        .append( "." );
            }
            return sb.toString();
        }
    }

    private void addCancelAndSubmitButtons() {
        // cancel
        AjaxLink<String> cancelLink = new AjaxLink<String>(
                "cancel"
        ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetPendingParticipationChanges();
                addAgenciesList();
                addAgentsList();
                addParticipantsList();
                addSummary();
                target.add( agenciesContainer );
                target.add( agentsContainer );
                target.add( participantsContainer );
                target.add( summaryLabel );
                if ( selectedAgent != null ) {
                    update( target, Change.message(
                            "Changes to participation as "
                                    + selectedAgent
                                    + " are cancelled" ) );
                }
            }
        };
        add( cancelLink );
        // submit
        AjaxLink<String> submitLink = new AjaxLink<String>(
                "submit"
        ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                executePendingParticipationChanges();
                resetPendingParticipationChanges();
                addAgenciesList();
                addAgentsList();
                addParticipantsList();
                addSummary();
                target.add( agenciesListContainer );
                target.add( agentsListContainer );
                target.add( participantsContainer );
                target.add( summaryLabel );
                if ( selectedAgent != null ) {
                    update( target, Change.message(
                            "Changes to participation as "
                                    + selectedAgent
                                    + " were submitted" ) );
                }
            }
        };
        add( submitLink );
    }

    private void executePendingParticipationChanges() {
        CommunityService communityService = getCommunityService();
        for ( ChannelsUser user : addedParticipants ) {
            userParticipationService.addParticipation( getUsername(), user, selectedAgent, communityService );
        }
        for ( ChannelsUser user : removedParticipants ) {
            userParticipationService.deleteParticipation( user, selectedAgent, communityService );
        }
    }

    public boolean isOnlyShowUnassignedAgencies() {
        return onlyShowUnassignedAgencies;
    }

    public void setOnlyShowUnassignedAgencies( boolean onlyShowUnassignedAgencies ) {
        this.onlyShowUnassignedAgencies = onlyShowUnassignedAgencies;
    }

    public boolean isOnlyShowUnassignedAgents() {
        return onlyShowUnassignedAgents;
    }

    public void setOnlyShowUnassignedAgents( boolean onlyShowUnassignedAgents ) {
        this.onlyShowUnassignedAgents = onlyShowUnassignedAgents;
    }
}
