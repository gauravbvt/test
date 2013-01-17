package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationConfirmationService;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/12
 * Time: 4:34 PM
 */
public class UserParticipationPanel extends AbstractSocialListPanel {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserParticipationPanel.class );

    @SpringBean
    private ChannelsUser user;

    @SpringBean
    private ParticipationManager participationManager;

    private WebMarkupContainer userParticipationContainer;

    private Agency selectedAvailableParticipationAgency;
    private WebMarkupContainer newAgentContainer;


    public UserParticipationPanel( String id, SocialPanel socialPanel, boolean collapsible ) {
        super( id, collapsible );
        init();
    }

    protected void init() {
        super.init();
        resetAll();
    }

    private void resetAll() {
        addUserParticipationContainer();
        addUserRole();
        addOpenAndConfirmedParticipation();
        addToBeConfirmedParticipation();
    }

    private void addUserRole() {
        userParticipationContainer.add( new Label( "userRole", getUserRole() ) );
    }

    private void addUserParticipationContainer() {
        userParticipationContainer = new WebMarkupContainer( "userParticipation" );
        userParticipationContainer.setOutputMarkupId( true );
        addOrReplace( userParticipationContainer );
    }


    private void addOpenAndConfirmedParticipation() {
        List<ParticipationWrapper> participationWrappers = openAndConfirmedParticipationWrappers();
        WebMarkupContainer openAndConfirmedParticipationContainer = new WebMarkupContainer( "openAndConfirmedParticipation" );
        // openAndConfirmedParticipationContainer.setVisible( !participationWrappers.isEmpty() );
        userParticipationContainer.add( openAndConfirmedParticipationContainer );
        Label noParticipationLabel = new Label( "noParticipation", "No one yet" );
        openAndConfirmedParticipationContainer.add( noParticipationLabel );
        noParticipationLabel.setVisible( participationWrappers.isEmpty() );
        ListView<ParticipationWrapper> participationList = new ListView<ParticipationWrapper>(
                "participations",
                participationWrappers ) {
            @Override
            protected void populateItem( ListItem<ParticipationWrapper> item ) {
                ParticipationWrapper participationWrapper = item.getModelObject();
                String assignation = getAssignation( participationWrapper.getAgent( getPlanCommunity() ) );
                AjaxCheckBox acceptedCheckBox = new AjaxCheckBox(
                        "accepted",
                        new PropertyModel<Boolean>( participationWrapper, "accepted" )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        resetAll();
                        // getPlanManager().clearCache();
                        target.add( userParticipationContainer );
                        update( target, new Change( Change.Type.Updated, getPlan(), "participation" ) );
                    }
                };
                item.add( acceptedCheckBox );
                item.add( new Label( "participation", assignation ) );
                addTipTitle( item, participationWrapper.getRequirementsDescription( getPlanCommunity()) );
                item.add( ( new Label(
                        "requester",
                        participationWrapper.isRequested()
                                ? ( " - Requested by " + participationWrapper.getRequesterFullName() )
                                : ""
                ) ) );
            }
        };
        openAndConfirmedParticipationContainer.add( participationList );
        // new participation
        WebMarkupContainer newParticipationContainer = new WebMarkupContainer( "newParticipation" );
        newParticipationContainer.setOutputMarkupId( true );
        openAndConfirmedParticipationContainer.add( newParticipationContainer );
        newParticipationContainer.add(
                new Label(
                        "newParticipationLabel",
                        participationWrappers.isEmpty()
                                ? "I participate in"
                                : "I also participate in" )
        );
        addPartipationAgencyChoice( newParticipationContainer, participationWrappers );
        addParticipationAgentChoice( newParticipationContainer, participationWrappers );
    }


    private List<ParticipationWrapper> openAndConfirmedParticipationWrappers() {
        Set<UserParticipation> participationSet = new HashSet<UserParticipation>(  );
        for ( UserParticipation participation : unconstrainedUnacceptedParticipations() ) {
            participationSet.add( participation );
        }
        final List<UserParticipation> unsupervisedParticipations = unsupervisedParticipations();
        for ( UserParticipation participation : unsupervisedParticipations ) {
            participationSet.add( participation );
        }
        final List<UserParticipation> confirmedSupervisedParticipations = confirmedSupervisedParticipations();
        for ( UserParticipation participation : confirmedSupervisedParticipations ) {
            participationSet.add( participation );
        }
        List<UserParticipation> list = new ArrayList<UserParticipation>( participationSet );
        List<ParticipationWrapper> wrappers = new ArrayList<ParticipationWrapper>();
        for ( UserParticipation participation : participationSet ) {
            wrappers.add(  new ParticipationWrapper( participation ) );
        }
        Collections.sort(
                wrappers,
                new Comparator<ParticipationWrapper>() {
                    @Override
                    public int compare( ParticipationWrapper p1, ParticipationWrapper p2 ) {
                        boolean p1Open = p1.isOpen( getPlanCommunity() );
                        boolean p2Open = p2.isOpen( getPlanCommunity() );
                        if ( !p1Open && p2Open ) return -1;
                        if ( p1Open && !p2Open ) return 1;
                        return p1.getAgent( getPlanCommunity() ).getName()
                                .compareTo( p2.getAgent( getPlanCommunity() ).getName() );
                    }
                } );
        return wrappers;
    }

    private List<UserParticipation> unconstrainedUnacceptedParticipations() {
        Set<UserParticipation> participations = new HashSet<UserParticipation>(  );
        PlanCommunity planCommunity = getPlanCommunity();
        for ( Agent agent : participationManager.findSelfAssignableOpenAgents( planCommunity, getUser() ) ) {
            if ( agent.isUnconstrainedParticipation() ) {
                participations.add( new UserParticipation(  getUsername(),  getUser(),  agent, planCommunity ) );
            }
        }
        return new ArrayList<UserParticipation>( participations );
    }

    @SuppressWarnings( "unchecked" )
    private List<UserParticipation> unsupervisedParticipations() {
        return (List<UserParticipation>) CollectionUtils.select(
                getPlanCommunity().getUserParticipationService().getUserParticipations(
                        getUser(),
                        getPlanCommunity() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (UserParticipation) object ).isSupervised( getPlanCommunity() );
                    }
                } );
    }

    @SuppressWarnings( "unchecked" )
    private List<UserParticipation> confirmedSupervisedParticipations() {
         final UserParticipationConfirmationService userParticipationConfirmationService
                = getPlanCommunity().getUserParticipationConfirmationService();
        return (List<UserParticipation>) CollectionUtils.select(
                getPlanCommunity().getUserParticipationService().getUserParticipations(
                        getUser(),
                        getPlanCommunity() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation participation = (UserParticipation) object;
                        return ( participation.isSupervised( getPlanCommunity() )
                                && userParticipationConfirmationService.isConfirmedByAllSupervisors( participation, getPlanCommunity() ) );
                    }
                } );
    }


    private String getUserRole() {
        String userRole = user.isAdmin()
                ? "administrator"
                : user.isPlanner()
                ? "planner"
                : "participant";
        return ( ChannelsUtils.startsWithVowel( userRole ) ? " an " : " a " ) + userRole + ".";
    }


    private String getAssignation( Agent agent ) {
        return agent != null ? agent.getName() : "?";
    }

    private void addToBeConfirmedParticipation() {
        List<ParticipationWrapper> participationWrappers = unconfirmedSupervisedParticipationWrappers();
        WebMarkupContainer unconfirmedSupervisedParticipationContainer =
                new WebMarkupContainer( "unconfirmedSupervisedParticipation" );
        unconfirmedSupervisedParticipationContainer.setOutputMarkupId( true );
        userParticipationContainer.add( unconfirmedSupervisedParticipationContainer );
        ListView<ParticipationWrapper> participationList = new ListView<ParticipationWrapper>(
                "participations",
                participationWrappers ) {
            @Override
            protected void populateItem( ListItem<ParticipationWrapper> item ) {
                ParticipationWrapper participationWrapper = item.getModelObject();
                String assignation = getAssignation( participationWrapper.getAgent( getPlanCommunity() ) );
                item.add( new Label( "participation", assignation ) );
                addTipTitle( item, participationWrapper.getRequirementsDescription( getPlanCommunity()) );
                AjaxCheckBox acceptedCheckBox = new AjaxCheckBox(
                        "accepted",
                        new PropertyModel<Boolean>( participationWrapper, "accepted" )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        resetAllAndUpdate( target );
                    }
                };
                item.add( acceptedCheckBox );
                item.add( ( new Label(
                        "requester",
                        participationWrapper.isRequested()
                                ? ( " - Requested by " + participationWrapper.getRequesterFullName() )
                                : ""
                ) ) );
            }
        };
        unconfirmedSupervisedParticipationContainer.add( participationList );
        unconfirmedSupervisedParticipationContainer.setVisible( !participationWrappers.isEmpty() );
    }

    private void resetAllAndUpdate( AjaxRequestTarget target ) {
        resetAll();
        // getPlanManager().clearCache();
        target.add( userParticipationContainer );
        update( target, new Change( Change.Type.Updated, getPlan(), "participation" ) );
    }

    private void addPartipationAgencyChoice(
            final WebMarkupContainer container,
            final List<ParticipationWrapper> participationWrappers ) {
        List<Agency> agenciesWithAvailableParticipation = agenciesWithAvailableParticipation( participationWrappers );
        DropDownChoice<Agency> orgChoice = new DropDownChoice<Agency>(
                "agencies",
                new PropertyModel<Agency>( this, "selectedAvailableParticipationAgency" ),
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
        orgChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addParticipationAgentChoice( container, participationWrappers );
                target.add( container );
            }
        } );
        orgChoice.setOutputMarkupId( true );
        container.setVisible( !agenciesWithAvailableParticipation.isEmpty() );
        container.addOrReplace( orgChoice );
    }

    private void addParticipationAgentChoice( WebMarkupContainer container,
                                              List<ParticipationWrapper> participationWrappers ) {
        newAgentContainer = new WebMarkupContainer( "asAgent" );
        newAgentContainer.setOutputMarkupId( true );
        makeVisible( newAgentContainer, selectedAvailableParticipationAgency != null );
        container.addOrReplace( newAgentContainer );
        List<Agent> availableParticipationAgents = agentsWithAvailableParticipation( participationWrappers );
        DropDownChoice<Agent> agentChoice = new DropDownChoice<Agent>(
                "agents",
                new PropertyModel<Agent>( this, "participationAsAvailableAgent" ),
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
                resetAllAndUpdate( target );
            }
        } );
        newAgentContainer.add( agentChoice );
    }

    private List<Agent> agentsWithAvailableParticipation( List<ParticipationWrapper> participationWrappers ) {
        List<Agent> agents = new ArrayList<Agent>();
        if ( selectedAvailableParticipationAgency != null ) {
            for ( final Agent agent : selectedAvailableParticipationAgency.getAgents( getPlanCommunity() ) ) {
                if ( isAgentAvailableForParticipation( agent )
                        && !CollectionUtils.exists(
                        participationWrappers,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (ParticipationWrapper) object ).getParticipation()
                                        .getAgent( getPlanCommunity() ).equals( agent );
                            }
                        }
                ) ) {
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

    @SuppressWarnings( "unchecked" )
    private List<Agency> agenciesWithAvailableParticipation(
            final List<ParticipationWrapper> participationWrappers ) {
        List<Agency> agencies = (List<Agency>) CollectionUtils.select(
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
                                        return isAgentAvailableForParticipation( agent )
                                                && !CollectionUtils.exists(
                                                participationWrappers,
                                                new Predicate() {
                                                    @Override
                                                    public boolean evaluate( Object object ) {
                                                        return ( (ParticipationWrapper) object ).getParticipation()
                                                                .getAgent( getPlanCommunity() ).equals( agent );
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
        Collections.sort( agencies, new Comparator<Agency>() {
            @Override
            public int compare( Agency agency1, Agency agency2 ) {
                return agency1.getName().compareToIgnoreCase( agency2.getName() );

            }
        } );
        return agencies;
    }

    private List<ParticipationWrapper> unconfirmedSupervisedParticipationWrappers() {
        List<ParticipationWrapper> wrappers = new ArrayList<ParticipationWrapper>();
        final List<UserParticipation> participations = unconfirmedSupervisedParticipations();
        for ( UserParticipation participation : participations ) {
            wrappers.add( new ParticipationWrapper( participation ) );
        }
        Collections.sort(
                wrappers,
                new Comparator<ParticipationWrapper>() {
                    @Override
                    public int compare( ParticipationWrapper p1, ParticipationWrapper p2 ) {
                        return p1.getAgent( getPlanCommunity() ).getName()
                                .compareTo( p2.getAgent( getPlanCommunity() ).getName() );
                    }
                } );
        return wrappers;
    }

    @SuppressWarnings( "unchecked" )
    private List<UserParticipation> unconfirmedSupervisedParticipations() {
        final UserParticipationConfirmationService userParticipationConfirmationService =
                getPlanCommunity().getUserParticipationConfirmationService();
        return (List<UserParticipation>) CollectionUtils.select( getPlanCommunity().getUserParticipationService()
                .getUserParticipations(
                        getUser(),
                        getPlanCommunity() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation participation = (UserParticipation) object;
                        return ( participation ).isSupervised( getPlanCommunity() )
                                && !userParticipationConfirmationService.isConfirmedByAllSupervisors( participation, getPlanCommunity() );
                    }
                } );
    }

    public Agency getSelectedAvailableParticipationAgency() {
        return selectedAvailableParticipationAgency;
    }

    public void setSelectedAvailableParticipationAgency( Agency selectedAvailableParticipationAgency ) {
        this.selectedAvailableParticipationAgency = selectedAvailableParticipationAgency;
    }

    public Agent getParticipationAsAvailableAgent() {
        return null;
    }

    public void setParticipationAsAvailableAgent( Agent agent ) {
        PlanCommunity planCommunity = getPlanCommunity();
        if ( isAgentAvailableForParticipation( agent ) ) {
            planCommunity.getUserParticipationService().addAcceptedParticipation(
                    getUsername(),
                    getUser(),
                    agent,
                    planCommunity );
        }
        selectedAvailableParticipationAgency = null;
    }

    private boolean isAgentAvailableForParticipation( Agent agent ) {
        return participationManager.isParticipationSelfAssignable( agent, getUser(), getPlanCommunity() );
    }

    public class ParticipationWrapper implements Serializable {

        private UserParticipation participation;

        public ParticipationWrapper( UserParticipation participation ) {
            this.participation = participation;
        }

        public UserParticipation getParticipation() {
            return participation;
        }

        public void setParticipation( UserParticipation participation ) {
            this.participation = participation;
        }

        public boolean isAccepted() {
            return participation.isAccepted();
        }

        public void setAccepted( boolean accepted ) {
            PlanCommunity planCommunity = getPlanCommunity();
            UserParticipationService userParticipationService = planCommunity.getUserParticipationService();
            if ( accepted ) {
                userParticipationService.accept( participation, planCommunity );
            } else {
                if ( isRequested() ) {
                    userParticipationService.refuse( participation, planCommunity );
                } else {
                    userParticipationService.deleteParticipation(
                            new ChannelsUser( participation.getParticipant(), planCommunity ),
                            participation.getAgent( planCommunity ),
                            planCommunity );
                }
            }
            resetAll();
            selectedAvailableParticipationAgency = null;
            // getPlanManager().clearCache(); // Must manually clear the cache
        }


        public boolean isOpen( PlanCommunity planCommunity ) {
            return getAgent( planCommunity ).isOpenParticipation();
        }

        public boolean isRequested() {
            return !participation.getUsername().equals( getUser().getUsername() );
        }

        public String getRequesterFullName() {
            ChannelsUser requestingUser = getQueryService().getUserDao().getUserNamed( participation.getUsername() );
            return requestingUser == null
                    ? "?"
                    : requestingUser.getFullName();
        }

        public Agent getAgent( PlanCommunity planCommunity ) {
            return participation == null
                    ? null
                    : participation.getAgent( planCommunity );
        }

        public String getRequirementsDescription( PlanCommunity planCommunity ) {
            Agent agent = getAgent( planCommunity );
            return agent == null ? "" : agent.getRequirementsDescription( planCommunity );
        }
    }

}
