package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.services.communities.UserParticipationConfirmationService;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.pages.components.ChannelsModalWindow;
import com.mindalliance.channels.pages.components.community.UserCommandChainsPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
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

    private static final String PREFIX_DOM_IDENTIFIER = "#commandChains";

    private static final int COMMAND_CHAINS_DIALOG_WIDTH = 1000;

    private static final int COMMAND_CHAINS_DIALOG_HEIGHT = 800;

    private static final double[] COMMAND_CHAINS_DIAGRAM_SIZE = { COMMAND_CHAINS_DIALOG_WIDTH - 10 , COMMAND_CHAINS_DIALOG_HEIGHT - 30 };

    @SpringBean
    private ChannelsUser user;

    @SpringBean
    private ParticipationManager participationManager;

    private ModalWindow commandChainsDialog;

    private WebMarkupContainer userParticipationContainer;

    private Agency selectedAvailableParticipationAgency;
    private WebMarkupContainer newAgentContainer;
    private WebMarkupContainer privilegesContainer;


    public UserParticipationPanel( String id, SocialPanel socialPanel, boolean collapsible ) {
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
        addCommandChainsDialog();
        addUserParticipationContainer();
        addUserRole();
        addParticipatesInPlan();
        addOpenAndConfirmedParticipation();
        addNewParticipation();
        addToBeConfirmedParticipation();
    }

    private void addCommandChainsDialog() {
        commandChainsDialog = new ChannelsModalWindow( "commandChainsDialog" );
        commandChainsDialog.setInitialHeight( COMMAND_CHAINS_DIALOG_HEIGHT );
        commandChainsDialog.setInitialWidth( COMMAND_CHAINS_DIALOG_WIDTH );
        commandChainsDialog.setTitle( "Chains of command" );
        commandChainsDialog.setCookieName( "channels-command-chains" );
        commandChainsDialog.setWindowClosedCallback( new ModalWindow.WindowClosedCallback() {
            public void onClose( AjaxRequestTarget target ) {
                target.add( commandChainsDialog );
                commandChainsDialog = null;
            }
        } );
        UserCommandChainsPanel userCommandChainsPanel = new UserCommandChainsPanel(
                commandChainsDialog.getContentId(),
                new Model<ChannelsUser>( getUser() ),
                getExpansions(),
                PREFIX_DOM_IDENTIFIER
        );
        commandChainsDialog.setContent( userCommandChainsPanel );
        addOrReplace( commandChainsDialog );
    }

    private void addUserRole() {
        privilegesContainer = new WebMarkupContainer( "myPrivileges" );
        privilegesContainer.add( new Label( "userRole", getUserRole() ) );
        privilegesContainer.add( makeHelpIcon( "helpPrivileges", "participant", "what-i-do", "my-privileges", "images/help_guide_gray.png" ) );
        userParticipationContainer.add( privilegesContainer );
    }

    private void addUserParticipationContainer() {
        userParticipationContainer = new WebMarkupContainer( "userParticipation" );
        userParticipationContainer.setOutputMarkupId( true );
        addOrReplace( userParticipationContainer );
    }

    private void addParticipatesInPlan() {
        String uri = getPlanCommunity().getUri();
        boolean isPlanner = getUser().isCommunityPlanner( uri );
        boolean participatesAsAgents = participationManager.isUserParticipatingAsAgents( getUser(), getCommunityService() );
        WebMarkupContainer participatesInPlanContainer = new WebMarkupContainer( "participatesInPlanContainer" );
        userParticipationContainer.add( participatesInPlanContainer );
        String whyDisabled = isPlanner
                ? "You can not leave this plan while you are a planner"
                : participatesAsAgents
                ? "You can not leave this plan while you participate in one or more positions"
                : "";
        if ( !whyDisabled.isEmpty() ) addTipTitle( participatesInPlanContainer, whyDisabled );
        AjaxCheckBox participatesCheckBox = new AjaxCheckBox(
                "participatesInPlan",
                new PropertyModel<Boolean>( this, "participatesInPlan" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                resetAll();
                target.add( userParticipationContainer );
                update( target, new Change( Change.Type.Updated, getPlanCommunity(), "participation" ) );
            }
        };
        participatesCheckBox.setEnabled( !isParticipatesInPlan() || !isPlanner && !participatesAsAgents );
        participatesInPlanContainer.add(
                makeHelpIcon(
                        "helpParticipatesInPlan",
                        "what-i-do",
                        "participating-in-plan",
                        "images/help_guide_gray.png" ) );
        participatesInPlanContainer.setVisible( !getPlanCommunity().isDomainCommunity() );
        participatesInPlanContainer.add( participatesCheckBox );
    }

    private void addOpenAndConfirmedParticipation() {
        List<ParticipationWrapper> participationWrappers = openAndConfirmedParticipationWrappers();
        Collections.sort(
                participationWrappers,
                new Comparator<ParticipationWrapper>() {
                    @Override
                    public int compare( ParticipationWrapper pw1, ParticipationWrapper pw2 ) {
                        return ( pw1.isLinked() && !pw2.isLinked() )
                                ? 1
                                : ( pw2.isLinked() && !pw1.isLinked() )
                                ? -1
                                : pw1.getAgent( getCommunityService() ).getName().compareTo(
                                pw1.getAgent( getCommunityService() ).getName() );
                    }
                } );
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
                String assignation = getAssignation( participationWrapper.getAgent( getCommunityService() ) );
                AjaxCheckBox acceptedCheckBox = new AjaxCheckBox(
                        "accepted",
                        new PropertyModel<Boolean>( participationWrapper, "accepted" )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        resetAll();
                        // getPlanManager().clearCache();
                        target.add( userParticipationContainer );
                        update( target, new Change( Change.Type.Updated, getPlanCommunity(), "participation" ) );
                    }
                };
                acceptedCheckBox.setEnabled( !participationWrapper.isLinked() );
                item.add( acceptedCheckBox );
                item.add( new Label( "participation", assignation ) );
                addTipTitle( item, participationWrapper.getRequirementsDescription( getCommunityService() ) );
                item.add( ( new Label(
                        "requester",
                        participationWrapper.isRequested()
                                ? ( " - Requested by " + participationWrapper.getRequesterFullName() )
                                : ""
                ) ) );
            }
        };
        openAndConfirmedParticipationContainer.add( participationList );
        openAndConfirmedParticipationContainer.add( makeCommandChainIcon() );
        openAndConfirmedParticipationContainer.add( makeHelpIcon( "helpParticipateAs", "what-i-do", "my-participation", "images/help_guide_gray.png" ) );
        openAndConfirmedParticipationContainer.setVisible( !getPlanCommunity().isDomainCommunity() && isUserParticipating() );
    }

    private WebMarkupContainer makeCommandChainIcon() {
        WebMarkupContainer commandChainIcon = new WebMarkupContainer( "commandChain" );
        commandChainIcon.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                showCommandChainDialog( target );
            }
        } );
        addTipTitle( commandChainIcon, "My chains of command" );
        return commandChainIcon;
    }

    private void showCommandChainDialog( AjaxRequestTarget target ) {
        addCommandChainsDialog();
        commandChainsDialog.show( target );
    }

    private void addNewParticipation() {
        List<ParticipationWrapper> participationWrappers = openAndConfirmedParticipationWrappers();
        WebMarkupContainer newParticipationContainer = new WebMarkupContainer( "newParticipationContainer" );
        newParticipationContainer.setVisible( !getPlanCommunity().isDomainCommunity() && isUserParticipating() );
        userParticipationContainer.add( newParticipationContainer );
        newParticipationContainer.add(
                new Label(
                        "newParticipationLabel",
                        participationWrappers.isEmpty()
                                ? "I want to participate"
                                : "I also want to participate" )
        );
        WebMarkupContainer newParticipationControls = new WebMarkupContainer( "newParticipation" );
        newParticipationControls.setOutputMarkupId( true );
        newParticipationContainer.add( newParticipationControls );
        addPartipationAgencyChoice( newParticipationControls, participationWrappers );
        addParticipationAgentChoice( newParticipationControls, participationWrappers );
        newParticipationContainer.add( makeHelpIcon( "helpNewParticipation", "what-i-do", "adding-participation", "images/help_guide_gray.png" ) );
    }

    private List<ParticipationWrapper> openAndConfirmedParticipationWrappers() {
        List<ParticipationWrapper> wrappers = new ArrayList<ParticipationWrapper>();
        if ( !getPlanCommunity().isDomainCommunity() ) {
            Set<UserParticipation> participationSet = new HashSet<UserParticipation>();
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
            for ( UserParticipation participation : participationSet ) {
                wrappers.add( new ParticipationWrapper( participation ) );
            }
            Collections.sort(
                    wrappers,
                    new Comparator<ParticipationWrapper>() {
                        @Override
                        public int compare( ParticipationWrapper p1, ParticipationWrapper p2 ) {
                            boolean p1Open = p1.isOpen( getCommunityService() );
                            boolean p2Open = p2.isOpen( getCommunityService() );
                            if ( !p1Open && p2Open ) return -1;
                            if ( p1Open && !p2Open ) return 1;
                            return p1.getAgent( getCommunityService() ).getName()
                                    .compareTo( p2.getAgent( getCommunityService() ).getName() );
                        }
                    } );
        }
        return wrappers;
    }

    private List<UserParticipation> unconstrainedUnacceptedParticipations() {
        Set<UserParticipation> participations = new HashSet<UserParticipation>();
        PlanCommunity planCommunity = getPlanCommunity();
        if ( !planCommunity.isDomainCommunity() ) {
            for ( Agent agent : participationManager.findSelfAssignableOpenAgents( getCommunityService(), getUser() ) ) {
                if ( agent.isUnconstrainedParticipation() ) {
                    if ( !getCommunityService().getParticipationManager().isUserActivelyParticipatingAs(
                            getUser(),
                            agent,
                            getCommunityService() ) )
                        participations.add( new UserParticipation( getUsername(), getUser(), agent, planCommunity ) );
                }
            }
        }
        return new ArrayList<UserParticipation>( participations );
    }

    @SuppressWarnings("unchecked")
    private List<UserParticipation> unsupervisedParticipations() {
        return (List<UserParticipation>) CollectionUtils.select(
                getCommunityService().getParticipationManager().getUserParticipations(
                        getUser(),
                        getCommunityService() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (UserParticipation) object ).isSupervised( getCommunityService() );
                    }
                } );
    }

    @SuppressWarnings("unchecked")
    private List<UserParticipation> confirmedSupervisedParticipations() {
        final UserParticipationConfirmationService userParticipationConfirmationService
                = getCommunityService().getUserParticipationConfirmationService();
        return (List<UserParticipation>) CollectionUtils.select(
                getCommunityService().getParticipationManager().getUserParticipations(
                        getUser(),
                        getCommunityService() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation participation = (UserParticipation) object;
                        return ( participation.isSupervised( getCommunityService() )
                                && userParticipationConfirmationService.isConfirmedByAllSupervisors( participation, getCommunityService() ) );
                    }
                } );
    }


    private String getUserRole() {
        String userRole = null;
        PlanCommunity planCommunity = getPlanCommunity();
        if ( planCommunity.isDomainCommunity() ) { // context is a plan
            if ( user.isAdmin() )
                userRole = "administrator";
            else {
                userRole = user.isPlannerOrAdmin( getPlan().getUri() )
                        ? "template developer"
                        : "template guest";
            }
        } else { // context is a plan community
            userRole = getCommunityService().isCommunityPlanner( user )
                    ? "collaboration planner"
                    : getCommunityService().getParticipationManager().userHasJoinedCommunity( user, getCommunityService() )
                    ? "participant"
                    : "potential participant";
        }
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
                String assignation = getAssignation( participationWrapper.getAgent( getCommunityService() ) );
                item.add( new Label( "participation", assignation ) );
                addTipTitle( item, participationWrapper.getRequirementsDescription( getCommunityService() ) );
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
        unconfirmedSupervisedParticipationContainer.setVisible( !getPlanCommunity().isDomainCommunity()
                && !participationWrappers.isEmpty() );
        unconfirmedSupervisedParticipationContainer
                .add( makeHelpIcon( "helpConfirmation", "what-i-do", "my-to-be-confirmed", "images/help_guide_gray.png" ) );
    }

    private void resetAllAndUpdate( AjaxRequestTarget target ) {
        resetAll();
        // getPlanManager().clearCache();
        target.add( userParticipationContainer );
        update( target, new Change( Change.Type.Updated, getPlanCommunity(), "participation" ) );
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
                        return selectedAvailableParticipationAgency == null // should never happen
                                ? agent.getActorName()
                                : selectedAvailableParticipationAgency.getJobTitleOf( agent, false, getCommunityService() );
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
            for ( final Agent agent : selectedAvailableParticipationAgency.getAgents( getCommunityService() ) ) {
                if ( isAgentAvailableForParticipation( agent )
                        && !CollectionUtils.exists(
                        participationWrappers,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (ParticipationWrapper) object ).getParticipation()
                                        .getAgent( getCommunityService() ).equals( agent );
                            }
                        }
                ) ) {
                    agents.add( agent );
                }
            }
            final CommunityService communityService = getCommunityService();
            Collections.sort( agents, new Comparator<Agent>() {
                @Override
                public int compare( Agent a1, Agent a2 ) {
                    return selectedAvailableParticipationAgency.getJobTitleOf( a1, true, communityService )
                            .compareToIgnoreCase( selectedAvailableParticipationAgency.getJobTitleOf( a2, true, communityService ) );
                }
            } );
        }
        return agents;
    }

    @SuppressWarnings("unchecked")
    private List<Agency> agenciesWithAvailableParticipation(
            final List<ParticipationWrapper> participationWrappers ) {
        List<Agency> agencies = (List<Agency>) CollectionUtils.select(
                participationManager.getAllKnownAgencies( getCommunityService() ),
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
                                        return isAgentAvailableForParticipation( agent )
                                                && !CollectionUtils.exists(
                                                participationWrappers,
                                                new Predicate() {
                                                    @Override
                                                    public boolean evaluate( Object object ) {
                                                        return ( (ParticipationWrapper) object ).getParticipation()
                                                                .getAgent( getCommunityService() ).equals( agent );
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
                        return p1.getAgent( getCommunityService() ).getName()
                                .compareTo( p2.getAgent( getCommunityService() ).getName() );
                    }
                } );
        return wrappers;
    }

    @SuppressWarnings("unchecked")
    private List<UserParticipation> unconfirmedSupervisedParticipations() {
        final UserParticipationConfirmationService userParticipationConfirmationService =
                getCommunityService().getUserParticipationConfirmationService();
        return (List<UserParticipation>) CollectionUtils.select( getCommunityService().getParticipationManager()
                .getUserParticipations(
                        getUser(),
                        getCommunityService() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation participation = (UserParticipation) object;
                        return ( participation ).isSupervised( getCommunityService() )
                                && !userParticipationConfirmationService.isConfirmedByAllSupervisors( participation, getCommunityService() );
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
        CommunityService communityService = getCommunityService();
        if ( isAgentAvailableForParticipation( agent ) ) {
            getCommunityService().getUserParticipationService().addAcceptedParticipation(
                    getUsername(),
                    getUser(),
                    agent,
                    communityService );
        }
        selectedAvailableParticipationAgency = null;
    }

    private boolean isAgentAvailableForParticipation( Agent agent ) {
        return participationManager.isParticipationSelfAssignable( agent, getUser(), getCommunityService() );
    }

    public boolean isParticipatesInPlan() {
        return getCommunityService().getParticipationManager()
                .userHasJoinedCommunity( getUser(), getCommunityService() );
    }

    public void setParticipatesInPlan( boolean val ) {
        if ( val ) {
            getCommunityService().getParticipationManager().joinCommunity( getUser(), getCommunityService() );
        } else {
            getCommunityService().getParticipationManager().leaveCommunity( getUser(), getCommunityService() );
        }
    }

    private boolean isUserParticipating() {
        return getCommunityService().getParticipationManager()
                .userHasJoinedCommunity( getUser(), getCommunityService() );
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
            CommunityService communityService = getCommunityService();
            UserParticipationService userParticipationService = communityService.getUserParticipationService();
            if ( accepted ) {
                userParticipationService.accept( participation, communityService );
            } else {
                if ( isRequested() ) {
                    userParticipationService.refuse( participation, communityService );
                } else {
                    userParticipationService.deleteParticipation(
                            new ChannelsUser( participation.getParticipant( getCommunityService() ), communityService ),
                            participation.getAgent( communityService ),
                            communityService );
                }
            }
            resetAll();
            selectedAvailableParticipationAgency = null;
            // getPlanManager().clearCache(); // Must manually clear the cache
        }


        public boolean isOpen( CommunityService communityService ) {
            return getAgent( communityService ).isOpenParticipation();
        }

        public boolean isRequested() {
            return !participation.isLinked() && !participation.getUsername().equals( getUser().getUsername() );
        }

        public String getRequesterFullName() {
            ChannelsUser requestingUser = getQueryService().getUserInfoService().getUserWithIdentity( participation.getUsername() );
            return requestingUser == null
                    ? "?"
                    : requestingUser.getFullName();
        }

        public Agent getAgent( CommunityService communityService ) {
            return participation == null
                    ? null
                    : participation.getAgent( communityService );
        }

        public String getRequirementsDescription( CommunityService communityService ) {
            Agent agent = getAgent( communityService );
            return agent == null ? "" : agent.getRequirementsDescription( communityService );
        }

        public boolean isLinked() {
            return participation.isLinked();
        }
    }

}
